package com.bloodrouter.hospital;

import com.bloodrouter.common.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "hospital_profiles")
public class HospitalProfile {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String hospitalName;

    private String branchName;

    @Column(nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private Boolean verified = false;

    // Getters & Setters

    public Long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}