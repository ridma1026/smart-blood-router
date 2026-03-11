package com.bloodrouter.donor;

import java.time.LocalDate;
import com.bloodrouter.common.entity.User;
import com.bloodrouter.common.enums.BloodGroup;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "donor_profiles")
public class DonorProfile {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup bloodGroup;

    @Column(nullable = false)
    private Boolean available = true;

    private LocalDate lastDonationDate;

    private Integer reliabilityScore = 100;

    // Getters & Setters

    public Long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public String getFullName() {
        return fullName;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public Boolean getAvailable() {
        return available;
    }

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }

    public Integer getReliabilityScore() {
        return reliabilityScore;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public void setReliabilityScore(Integer reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }
}