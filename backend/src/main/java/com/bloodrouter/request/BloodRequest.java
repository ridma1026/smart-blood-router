package com.bloodrouter.request;
import com.bloodrouter.common.enums.BloodGroup;
import com.bloodrouter.common.entity.User;
import com.bloodrouter.common.enums.UrgencyLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_requests")
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private User hospital;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup bloodType;

    @Column(nullable = false)
    private Integer unitsNeeded;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UrgencyLevel urgency;

    @Column(nullable = false)
    private String hospitalLocation;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private String status = "OPEN";

    private Integer matchedDonors = 0;

    // Constructors
    public BloodRequest() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24);
    }

    //  GETTERS

    public Long getId() {
        return id;
    }

    public User getHospital() {
        return hospital;
    }

    public BloodGroup getBloodType() {
        return bloodType;
    }

    public Integer getUnitsNeeded() {
        return unitsNeeded;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public String getHospitalLocation() {
        return hospitalLocation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getStatus() {
        return status;
    }

    public Integer getMatchedDonors() {
        return matchedDonors;
    }

    //  SETTERS
    public void setId(Long id) {
        this.id = id;
    }

    public void setHospital(User hospital) {
        this.hospital = hospital;
    }

    public void setBloodType(BloodGroup bloodType) {
        this.bloodType = bloodType;
    }

    public void setUnitsNeeded(Integer unitsNeeded) {
        this.unitsNeeded = unitsNeeded;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    public void setHospitalLocation(String hospitalLocation) {
        this.hospitalLocation = hospitalLocation;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMatchedDonors(Integer matchedDonors) {
        this.matchedDonors = matchedDonors;
    }
}