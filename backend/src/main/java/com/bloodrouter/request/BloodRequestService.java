package com.bloodrouter.request;

import com.bloodrouter.common.entity.User;
import com.bloodrouter.common.enums.BloodGroup;
import com.bloodrouter.donor.DonorProfile;
import com.bloodrouter.donor.DonorProfileRepository;
import com.bloodrouter.notification.WebSocketService;
import com.bloodrouter.notification.EmailService;
import com.bloodrouter.notification.SmsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

@Service
public class BloodRequestService {

    private final BloodRequestRepository bloodRequestRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final DonorResponseRepository donorResponseRepository;
    private final WebSocketService webSocketService;
    private final EmailService emailService;
    private final SmsService smsService;

    public BloodRequestService(BloodRequestRepository bloodRequestRepository,
                               DonorProfileRepository donorProfileRepository,
                               DonorResponseRepository donorResponseRepository,
                               WebSocketService webSocketService,
                               EmailService emailService,
                               SmsService smsService) {
        this.bloodRequestRepository = bloodRequestRepository;
        this.donorProfileRepository = donorProfileRepository;
        this.donorResponseRepository = donorResponseRepository;
        this.webSocketService = webSocketService;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    @Transactional
    public BloodRequest createBloodRequest(BloodRequest request) {
        if (request.getCreatedAt() == null) {
            request.setCreatedAt(LocalDateTime.now());
        }
        if (request.getExpiresAt() == null) {
            request.setExpiresAt(LocalDateTime.now().plusHours(24));
        }
        request.setStatus("OPEN");
        request.setMatchedDonors(0);
        return bloodRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<DonorMatchResult> findMatchingDonors(Long requestId) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Blood request not found"));

        // Get all available donors with the same blood group
        List<DonorProfile> potentialDonors = donorProfileRepository
                .findByBloodGroupAndAvailableTrue(request.getBloodType());

        // Calculate match score for each donor
        List<DonorMatchResult> matchResults = new ArrayList<>();

        for (DonorProfile donor : potentialDonors) {
            double score = calculateMatchScore(donor, request);

            // Only include donors with score > 0
            if (score > 0) {
                String reason = getMatchReason(donor, request, score);
                matchResults.add(new DonorMatchResult(donor, score, reason));
            }
        }

        // Sort by score (highest first)
        matchResults.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return matchResults;
    }

    private double calculateMatchScore(DonorProfile donor, BloodRequest request) {
        double score = 100.0; // Start with base score

        // 1. Blood type compatibility check
        if (!isBloodTypeCompatible(donor.getBloodGroup(), request.getBloodType())) {
            return 0; // Incompatible blood type
        }

        // 2. Urgency multiplier
        switch (request.getUrgency()) {
            case CRITICAL:
                score *= 1.5;
                break;
            case HIGH:
                score *= 1.3;
                break;
            case MEDIUM:
                score *= 1.1;
                break;
            case LOW:
                // No multiplier
                break;
        }

        // 3. Reliability score (0-100)
        score += donor.getReliabilityScore() * 0.5;

        // 4. Recent donation penalty
        if (donor.getLastDonationDate() != null) {
            long daysSinceLastDonation = LocalDate.now()
                    .toEpochDay() - donor.getLastDonationDate().toEpochDay();

            if (daysSinceLastDonation < 90) {
                score -= (90 - daysSinceLastDonation) * 0.5; // Penalize recent donors
            }
        }

        return Math.max(0, score); // Ensure score is not negative
    }

    private boolean isBloodTypeCompatible(BloodGroup donorBlood, BloodGroup requiredBlood) {
        // Universal donor O_NEG can donate to anyone
        if (donorBlood == BloodGroup.O_NEG) {
            return true;
        }

        // Exact match
        if (donorBlood == requiredBlood) {
            return true;
        }

        // O_POS can donate to A_POS, B_POS, AB_POS
        if (donorBlood == BloodGroup.O_POS) {
            return requiredBlood == BloodGroup.O_POS ||
                    requiredBlood == BloodGroup.A_POS ||
                    requiredBlood == BloodGroup.B_POS ||
                    requiredBlood == BloodGroup.AB_POS;
        }

        // A_NEG can donate to A_NEG, A_POS, AB_NEG, AB_POS
        if (donorBlood == BloodGroup.A_NEG) {
            return requiredBlood == BloodGroup.A_NEG ||
                    requiredBlood == BloodGroup.A_POS ||
                    requiredBlood == BloodGroup.AB_NEG ||
                    requiredBlood == BloodGroup.AB_POS;
        }

        // B_NEG can donate to B_NEG, B_POS, AB_NEG, AB_POS
        if (donorBlood == BloodGroup.B_NEG) {
            return requiredBlood == BloodGroup.B_NEG ||
                    requiredBlood == BloodGroup.B_POS ||
                    requiredBlood == BloodGroup.AB_NEG ||
                    requiredBlood == BloodGroup.AB_POS;
        }

        // AB_NEG can donate to AB_NEG, AB_POS
        if (donorBlood == BloodGroup.AB_NEG) {
            return requiredBlood == BloodGroup.AB_NEG ||
                    requiredBlood == BloodGroup.AB_POS;
        }

        // A_POS can donate to A_POS, AB_POS
        if (donorBlood == BloodGroup.A_POS) {
            return requiredBlood == BloodGroup.A_POS ||
                    requiredBlood == BloodGroup.AB_POS;
        }

        // B_POS can donate to B_POS, AB_POS
        if (donorBlood == BloodGroup.B_POS) {
            return requiredBlood == BloodGroup.B_POS ||
                    requiredBlood == BloodGroup.AB_POS;
        }

        // AB_POS can only donate to AB_POS
        if (donorBlood == BloodGroup.AB_POS) {
            return requiredBlood == BloodGroup.AB_POS;
        }

        return false;
    }

    private String getMatchReason(DonorProfile donor, BloodRequest request, double score) {
        StringBuilder reason = new StringBuilder();

        reason.append("Blood type compatible. ");

        if (score > 150) {
            reason.append("Excellent match - ");
        } else if (score > 120) {
            reason.append("Good match - ");
        } else {
            reason.append("Potential match - ");
        }

        if (donor.getReliabilityScore() > 90) {
            reason.append("Highly reliable donor. ");
        }

        if (donor.getLastDonationDate() != null) {
            long daysSinceLastDonation = LocalDate.now()
                    .toEpochDay() - donor.getLastDonationDate().toEpochDay();
            if (daysSinceLastDonation > 90) {
                reason.append("Eligible to donate. ");
            }
        }

        return reason.toString();
    }

    @Transactional
    public DonorResponse sendRequestToDonor(Long requestId, Long donorId) {
        System.out.println("===== SENDING REQUEST TO DONOR =====");
        System.out.println("Request ID: " + requestId);
        System.out.println("Donor ID: " + donorId);

        // Find the blood request
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Blood request not found"));

        // Check if request is still open
        if (!"OPEN".equals(request.getStatus())) {
            throw new RuntimeException("Blood request is not open. Current status: " + request.getStatus());
        }

        // Find the donor
        DonorProfile donor = donorProfileRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        System.out.println("Found donor: " + donor.getFullName() + " with ID: " + donor.getUserId());

        // Check if donor is available
        if (!donor.getAvailable()) {
            throw new RuntimeException("Donor is not available for donation");
        }

        // Check if donor has donated recently (within 90 days)
        if (donor.getLastDonationDate() != null) {
            long daysSinceLastDonation = LocalDate.now()
                    .toEpochDay() - donor.getLastDonationDate().toEpochDay();
            if (daysSinceLastDonation < 90) {
                throw new RuntimeException("Donor donated " + daysSinceLastDonation +
                        " days ago. Must wait " + (90 - daysSinceLastDonation) + " more days.");
            }
        }

        // Check if already sent to this donor
        Optional<DonorResponse> existing = donorResponseRepository
                .findByBloodRequestIdAndDonorUserId(requestId, donorId);

        DonorResponse response;

        if (existing.isPresent()) {
            DonorResponse existingResponse = existing.get();
            System.out.println("Found existing response with status: " + existingResponse.getStatus());

            // If the donor already responded (accepted/declined), don't allow resending
            if ("ACCEPTED".equals(existingResponse.getStatus()) ||
                    "DECLINED".equals(existingResponse.getStatus()) ||
                    "COMPLETED".equals(existingResponse.getStatus())) {
                throw new RuntimeException("Donor has already " + existingResponse.getStatus() +
                        " this request");
            }

            // If it's still PENDING, resend the notification
            System.out.println("Donor already has PENDING request. Resending notification...");
            response = existingResponse;

        } else {
            // Create new response record
            response = new DonorResponse();
            response.setBloodRequest(request);
            response.setDonor(donor);
            response.setStatus("PENDING");
            response = donorResponseRepository.save(response);
            System.out.println("Created new response with ID: " + response.getId());
        }

        // ===== SEND NOTIFICATIONS (always send, even for resend) =====
        User donorUser = donor.getUser();
        User hospital = request.getHospital();
        boolean isResend = existing.isPresent();

        try {
            // Send WebSocket notification to donor
            Map<String, Object> donorNotification = new HashMap<>();
            donorNotification.put("type", "NEW_REQUEST");
            donorNotification.put("requestId", requestId);
            donorNotification.put("hospitalName", hospital.getEmail());
            donorNotification.put("bloodType", request.getBloodType().name());
            donorNotification.put("urgency", request.getUrgency().name());
            donorNotification.put("responseId", response.getId());
            donorNotification.put("isResend", isResend);
            donorNotification.put("message", isResend ? "Reminder: You have a pending blood request" : "New blood request available");
            donorNotification.put("timestamp", LocalDateTime.now().toString());

            System.out.println("Sending WebSocket to donor ID: " + donorId);
            System.out.println("Notification payload: " + donorNotification);

            webSocketService.sendDonorNotification(donorId, donorNotification);
            System.out.println("✅ WebSocket notification sent to donor " + donorId);

        } catch (Exception e) {
            System.out.println("❌ WebSocket notification failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Send Email (if email exists)
        if (donorUser.getEmail() != null && !donorUser.getEmail().isEmpty()) {
            try {
                emailService.sendBloodRequestNotification(
                        donorUser.getEmail(),
                        donor.getFullName(),
                        hospital.getEmail(),
                        request.getBloodType().name(),
                        request.getUrgency().name(),
                        requestId
                );
                System.out.println("✅ Email " + (isResend ? "reminder" : "sent") +
                        " to: " + donorUser.getEmail());
            } catch (Exception e) {
                System.out.println("❌ Email failed: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ No email for donor " + donorId);
        }

        // Send SMS (if phone exists)
        if (donorUser.getPhone() != null && !donorUser.getPhone().isEmpty()) {
            try {
                smsService.sendBloodRequestSms(
                        donorUser.getPhone(),
                        donor.getFullName(),
                        hospital.getEmail(),
                        request.getBloodType().name(),
                        request.getUrgency().name(),
                        requestId
                );
                System.out.println("✅ SMS " + (isResend ? "reminder" : "sent") +
                        " to: " + donorUser.getPhone());
            } catch (Exception e) {
                System.out.println("❌ SMS failed: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ No phone for donor " + donorId);
        }

        System.out.println("===== REQUEST SENT SUCCESSFULLY =====");
        return response;
    }

    @Transactional
    public DonorResponse respondToRequest(Long responseId, String status, String estimatedArrival) {
        System.out.println("===== DONOR RESPONDING TO REQUEST =====");
        System.out.println("Response ID: " + responseId);
        System.out.println("Status: " + status);
        System.out.println("Estimated Arrival: " + estimatedArrival);

        // Find the response
        DonorResponse response = donorResponseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response not found"));

        // Check if response is still pending
        if (!"PENDING".equals(response.getStatus())) {
            throw new RuntimeException("Response already " + response.getStatus());
        }

        // Update response
        response.setStatus(status);
        response.setRespondedAt(LocalDateTime.now());

        DonorProfile donor = response.getDonor();
        BloodRequest request = response.getBloodRequest();

        if ("ACCEPTED".equals(status)) {
            response.setEstimatedArrivalTime(estimatedArrival);

            // Increase reliability score for accepting
            donor.setReliabilityScore(donor.getReliabilityScore() + 1);
            donorProfileRepository.save(donor);
            System.out.println("✅ Donor accepted. Reliability score increased to: " + donor.getReliabilityScore());

        } else if ("DECLINED".equals(status)) {
            // Slightly decrease reliability score for declining
            donor.setReliabilityScore(Math.max(0, donor.getReliabilityScore() - 1));
            donorProfileRepository.save(donor);
            System.out.println("⚠️ Donor declined. Reliability score decreased to: " + donor.getReliabilityScore());
        }

        DonorResponse savedResponse = donorResponseRepository.save(response);

        // ===== SEND NOTIFICATION TO HOSPITAL =====
        try {
            User hospital = request.getHospital();

            Map<String, Object> hospitalNotification = new HashMap<>();
            hospitalNotification.put("type", "DONOR_RESPONSE");
            hospitalNotification.put("responseId", savedResponse.getId());
            hospitalNotification.put("requestId", request.getId());
            hospitalNotification.put("donorName", donor.getFullName());
            hospitalNotification.put("donorId", donor.getUserId());
            hospitalNotification.put("status", status);
            hospitalNotification.put("estimatedArrival", estimatedArrival != null ? estimatedArrival : "");
            hospitalNotification.put("timestamp", LocalDateTime.now().toString());

            System.out.println("Sending WebSocket notification to hospital ID: " + hospital.getId());
            webSocketService.sendDonorResponse(hospital.getId(), hospitalNotification);
            System.out.println("✅ WebSocket notification sent to hospital");

            // Send Email to hospital
            if (hospital.getEmail() != null && !hospital.getEmail().isEmpty()) {
                emailService.sendDonorResponseNotification(
                        hospital.getEmail(),
                        hospital.getEmail(),
                        donor.getFullName(),
                        status,
                        request.getId()
                );
                System.out.println("✅ Email sent to hospital: " + hospital.getEmail());
            }

            // Send SMS to hospital
            if (hospital.getPhone() != null && !hospital.getPhone().isEmpty()) {
                smsService.sendDonorResponseSms(
                        hospital.getPhone(),
                        hospital.getEmail(),
                        donor.getFullName(),
                        status,
                        request.getId()
                );
                System.out.println("✅ SMS sent to hospital: " + hospital.getPhone());
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to notify hospital: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("===== DONOR RESPONSE PROCESSED =====");
        return savedResponse;
    }

    @Transactional
    public BloodRequest confirmDonation(Long requestId, Long donorId) {
        System.out.println("===== CONFIRMING DONATION =====");
        System.out.println("Request ID: " + requestId);
        System.out.println("Donor ID: " + donorId);

        // Find the response
        DonorResponse response = donorResponseRepository
                .findByBloodRequestIdAndDonorUserId(requestId, donorId)
                .orElseThrow(() -> new RuntimeException("No response found for this donor and request"));

        // Check if response was accepted
        if (!"ACCEPTED".equals(response.getStatus())) {
            throw new RuntimeException("Cannot confirm donation: donor has not accepted this request");
        }

        // Update response status
        response.setStatus("COMPLETED");
        donorResponseRepository.save(response);

        // Update donor's last donation date
        DonorProfile donor = response.getDonor();
        donor.setLastDonationDate(LocalDate.now());
        donor.setAvailable(false); // Temporarily unavailable (cooldown period)
        donorProfileRepository.save(donor);
        System.out.println("✅ Donor updated: Last donation date set to today, availability set to false");

        // Update request matched donors count
        BloodRequest request = response.getBloodRequest();
        request.setMatchedDonors(request.getMatchedDonors() + 1);

        // If enough donors matched, close the request
        if (request.getMatchedDonors() >= request.getUnitsNeeded()) {
            request.setStatus("FULFILLED");
            System.out.println("✅ Request fulfilled! Required units reached.");
        }

        BloodRequest updatedRequest = bloodRequestRepository.save(request);

        // Notify hospital via WebSocket
        try {
            User hospital = request.getHospital();
            Map<String, Object> donationNotification = new HashMap<>();
            donationNotification.put("type", "DONATION_CONFIRMED");
            donationNotification.put("requestId", requestId);
            donationNotification.put("donorId", donorId);
            donationNotification.put("donorName", donor.getFullName());
            donationNotification.put("matchedDonors", request.getMatchedDonors());
            donationNotification.put("unitsNeeded", request.getUnitsNeeded());
            donationNotification.put("timestamp", LocalDateTime.now().toString());

            webSocketService.sendDonorResponse(hospital.getId(), donationNotification);
            System.out.println("✅ Donation confirmation sent to hospital");

            // Send email confirmation
            if (hospital.getEmail() != null && !hospital.getEmail().isEmpty()) {
                // You can add email confirmation here if needed
                System.out.println("✅ Email confirmation would be sent to: " + hospital.getEmail());
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to send donation confirmation: " + e.getMessage());
        }

        System.out.println("===== DONATION CONFIRMED =====");
        return updatedRequest;
    }

    public List<DonorResponse> getDonorResponses(Long requestId) {
        return donorResponseRepository.findByBloodRequestId(requestId);
    }

    public List<DonorMatchResult> findMatchingDonorsWithStatus(Long requestId) {
        // Get regular matches
        List<DonorMatchResult> matches = findMatchingDonors(requestId);

        // Add response status to each match
        for (DonorMatchResult match : matches) {
            Optional<DonorResponse> response = donorResponseRepository
                    .findByBloodRequestIdAndDonorUserId(requestId,
                            match.getDonor().getUserId());

            if (response.isPresent()) {
                match.setResponseStatus(response.get().getStatus());

                // Add estimated arrival time if available
                if ("ACCEPTED".equals(response.get().getStatus()) &&
                        response.get().getEstimatedArrivalTime() != null) {
                    match.setEstimatedArrival(response.get().getEstimatedArrivalTime());
                }
            } else {
                match.setResponseStatus("NOT_CONTACTED");
            }
        }

        return matches;
    }

    @Transactional
    public BloodRequest updateRequestStatus(Long requestId, String status) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Blood request not found"));
        request.setStatus(status);

        // If request is cancelled or expired, notify all pending donors
        if ("CANCELLED".equals(status) || "EXPIRED".equals(status)) {
            List<DonorResponse> pendingResponses = donorResponseRepository
                    .findByBloodRequestIdAndStatus(requestId, "PENDING");

            for (DonorResponse response : pendingResponses) {
                response.setStatus("CANCELLED");
                donorResponseRepository.save(response);

                // Notify donor that request is no longer needed
                try {
                    User donor = response.getDonor().getUser();
                    Map<String, Object> cancelNotification = new HashMap<>();
                    cancelNotification.put("type", "REQUEST_CANCELLED");
                    cancelNotification.put("requestId", requestId);
                    cancelNotification.put("message", "Blood request has been " + status);
                    cancelNotification.put("timestamp", LocalDateTime.now().toString());

                    webSocketService.sendDonorNotification(donor.getId(), cancelNotification);
                } catch (Exception e) {
                    System.out.println("❌ Failed to notify donor of cancellation: " + e.getMessage());
                }
            }
        }

        return bloodRequestRepository.save(request);
    }

    public List<BloodRequest> getOpenRequests() {
        return bloodRequestRepository.findByStatus("OPEN");
    }

    public List<BloodRequest> getRequestsByHospital(Long hospitalId) {
        return bloodRequestRepository.findByHospitalId(hospitalId);
    }
}