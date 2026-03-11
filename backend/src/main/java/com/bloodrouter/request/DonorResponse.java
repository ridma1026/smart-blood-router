package com.bloodrouter.request;

import com.bloodrouter.donor.DonorProfile;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donor_responses")
public class DonorResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private BloodRequest bloodRequest;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private DonorProfile donor;

    @Column(nullable = false)
    private String status; // PENDING, ACCEPTED, DECLINED, COMPLETED

    private LocalDateTime respondedAt;

    private String estimatedArrivalTime;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public DonorResponse() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    // GETTERS
    public Long getId() {
        return id;
    }

    public BloodRequest getBloodRequest() {
        return bloodRequest;
    }

    public DonorProfile getDonor() {
        return donor;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public String getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // SETTERS
    public void setId(Long id) {
        this.id = id;
    }

    public void setBloodRequest(BloodRequest bloodRequest) {
        this.bloodRequest = bloodRequest;
    }

    public void setDonor(DonorProfile donor) {
        this.donor = donor;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public void setEstimatedArrivalTime(String estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}