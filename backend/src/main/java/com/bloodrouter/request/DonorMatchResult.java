package com.bloodrouter.request;

import com.bloodrouter.donor.DonorProfile;

public class DonorMatchResult {

    private DonorProfile donor;
    private double score;
    private String matchReason;
    private String responseStatus;
    private String estimatedArrival;

    // Constructor
    public DonorMatchResult(DonorProfile donor, double score, String matchReason) {
        this.donor = donor;
        this.score = score;
        this.matchReason = matchReason;
        this.responseStatus = "NOT_CONTACTED";
        this.estimatedArrival = null;
    }

    // ============ GETTERS ============
    public DonorProfile getDonor() {
        return donor;
    }

    public double getScore() {
        return score;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public String getEstimatedArrival() {
        return estimatedArrival;
    }

    // ============ SETTERS ============
    public void setDonor(DonorProfile donor) {
        this.donor = donor;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setEstimatedArrival(String estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }
}