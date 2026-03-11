package com.bloodrouter.request;

import com.bloodrouter.common.entity.User;
import com.bloodrouter.common.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-requests")
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;
    private final UserRepository userRepository;

    public BloodRequestController(BloodRequestService bloodRequestService,
                                  UserRepository userRepository) {
        this.bloodRequestService = bloodRequestService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public BloodRequest createBloodRequest(@RequestBody BloodRequestDto requestDto) {
        // Get current logged-in user (hospital)
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User hospital = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        // Create new blood request
        BloodRequest request = new BloodRequest();
        request.setHospital(hospital);
        request.setBloodType(requestDto.getBloodType());
        request.setUnitsNeeded(requestDto.getUnitsNeeded());
        request.setUrgency(requestDto.getUrgency());
        request.setHospitalLocation(requestDto.getHospitalLocation());

        return bloodRequestService.createBloodRequest(request);
    }

    @GetMapping("/{requestId}/matches")
    public List<DonorMatchResult> findMatchingDonors(@PathVariable Long requestId) {
        return bloodRequestService.findMatchingDonors(requestId);
    }


    @PostMapping("/{requestId}/notify-donor/{donorId}")
    public DonorResponse notifyDonor(@PathVariable Long requestId,
                                     @PathVariable Long donorId) {
        return bloodRequestService.sendRequestToDonor(requestId, donorId);
    }

    @PutMapping("/donor-response/{responseId}")
    public DonorResponse donorRespond(@PathVariable Long responseId,
                                      @RequestParam String status,
                                      @RequestParam(required = false) String estimatedArrival) {
        return bloodRequestService.respondToRequest(responseId, status, estimatedArrival);
    }

    @GetMapping("/{requestId}/responses")
    public List<DonorResponse> getRequestResponses(@PathVariable Long requestId) {
        return bloodRequestService.getDonorResponses(requestId);
    }

    @GetMapping("/{requestId}/matches-with-status")
    public List<DonorMatchResult> findMatchingDonorsWithStatus(@PathVariable Long requestId) {
        return bloodRequestService.findMatchingDonorsWithStatus(requestId);
    }

    @PutMapping("/{requestId}/confirm-donation/{donorId}")
    public BloodRequest confirmDonation(@PathVariable Long requestId,
                                        @PathVariable Long donorId) {
        return bloodRequestService.confirmDonation(requestId, donorId);
    }


    @GetMapping("/open")
    public List<BloodRequest> getOpenRequests() {
        return bloodRequestService.getOpenRequests();
    }

    @GetMapping("/hospital/{hospitalId}")
    public List<BloodRequest> getHospitalRequests(@PathVariable Long hospitalId) {
        return bloodRequestService.getRequestsByHospital(hospitalId);
    }

    @PutMapping("/{requestId}/status")
    public BloodRequest updateStatus(@PathVariable Long requestId,
                                     @RequestParam String status) {
        return bloodRequestService.updateRequestStatus(requestId, status);
    }
}