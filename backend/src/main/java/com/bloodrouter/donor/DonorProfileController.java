package com.bloodrouter.donor;

import com.bloodrouter.common.enums.BloodGroup;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donor")
public class DonorProfileController {

    private final DonorProfileService donorProfileService;

    public DonorProfileController(DonorProfileService donorProfileService) {
        this.donorProfileService = donorProfileService;
    }

    // Create donor profile
    @PostMapping("/profile")
    public DonorProfile createProfile(@RequestBody DonorProfile donorProfile) {
        return donorProfileService.createDonorProfile(donorProfile);
    }

    // Get all donors
    @GetMapping("/all")
    public List<DonorProfile> getAllDonors() {
        return donorProfileService.getAllDonors();
    }

    // Find donors by blood group
    @GetMapping("/available")
    public List<DonorProfile> findAvailableDonors(
            @RequestParam BloodGroup bloodGroup
    ) {
        return donorProfileService.findAvailableDonors(bloodGroup);
    }

    // Update donor availability
    @PutMapping("/availability")
    public DonorProfile updateAvailability(
            @RequestParam Long userId,
            @RequestParam Boolean available
    ) {
        return donorProfileService.updateAvailability(userId, available);
    }
}