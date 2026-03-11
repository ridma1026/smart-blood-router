package com.bloodrouter.request;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DonorResponseRepository extends JpaRepository<DonorResponse, Long> {

    // Find all responses for a specific blood request
    List<DonorResponse> findByBloodRequestId(Long requestId);

    // Find all responses by a specific donor with a given status
    List<DonorResponse> findByDonorUserIdAndStatus(Long donorId, String status);

    // Find specific response for a donor to a request
    Optional<DonorResponse> findByBloodRequestIdAndDonorUserId(Long requestId, Long donorId);

    // Count how many donors have responded with a specific status to a request
    long countByBloodRequestIdAndStatus(Long requestId, String status);

    // Find all responses for a request with a specific status
    List<DonorResponse> findByBloodRequestIdAndStatus(Long requestId, String status);
}