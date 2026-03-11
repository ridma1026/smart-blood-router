package com.bloodrouter.request;
import com.bloodrouter.common.enums.BloodGroup;
import com.bloodrouter.common.enums.UrgencyLevel;

public class BloodRequestDto {

    private BloodGroup bloodType;
    private Integer unitsNeeded;
    private UrgencyLevel urgency;
    private String hospitalLocation;

    // Getters and Setters
    public BloodGroup getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodGroup bloodType) {
        this.bloodType = bloodType;
    }

    public Integer getUnitsNeeded() {
        return unitsNeeded;
    }

    public void setUnitsNeeded(Integer unitsNeeded) {
        this.unitsNeeded = unitsNeeded;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    public String getHospitalLocation() {
        return hospitalLocation;
    }

    public void setHospitalLocation(String hospitalLocation) {
        this.hospitalLocation = hospitalLocation;
    }
}