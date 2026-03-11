package com.bloodrouter.donor;

import com.bloodrouter.common.enums.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonorProfileRepository extends JpaRepository<DonorProfile, Long> {

    List<DonorProfile> findByBloodGroupAndAvailableTrue(BloodGroup bloodGroup);

}