package com.bloodrouter.donor;

import com.bloodrouter.common.enums.BloodGroup;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonorProfileService {

    private final DonorProfileRepository donorProfileRepository;

    public DonorProfileService(DonorProfileRepository donorProfileRepository) {
        this.donorProfileRepository = donorProfileRepository;
    }

    public DonorProfile createDonorProfile(DonorProfile donorProfile) {
        return donorProfileRepository.save(donorProfile);
    }

    public List<DonorProfile> getAllDonors() {
        return donorProfileRepository.findAll();
    }

    public List<DonorProfile> findAvailableDonors(BloodGroup bloodGroup) {
        return donorProfileRepository
                .findByBloodGroupAndAvailableTrue(bloodGroup);
    }

    public DonorProfile updateAvailability(Long userId, Boolean available) {

        DonorProfile donor =
                donorProfileRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Donor not found"));

        donor.setAvailable(available);

        return donorProfileRepository.save(donor);
    }
}