package com.bloodrouter.request;

import com.bloodrouter.common.enums.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    List<BloodRequest> findByStatus(String status);

    List<BloodRequest> findByBloodTypeAndStatus(BloodGroup bloodType, String status);

    List<BloodRequest> findByHospitalId(Long hospitalId);
}