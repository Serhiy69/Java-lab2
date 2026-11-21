package org.example.dto;

public class ReliabilityResponse {

    private Long readerId;
    private boolean reliable;
    private int maxActiveLoans;
    private int returnedLast30Days;
    private boolean hasOverdueActiveLoans;

    public ReliabilityResponse() {
    }

    public ReliabilityResponse(Long readerId, boolean reliable, int maxActiveLoans,
                               int returnedLast30Days, boolean hasOverdueActiveLoans) {
        this.readerId = readerId;
        this.reliable = reliable;
        this.maxActiveLoans = maxActiveLoans;
        this.returnedLast30Days = returnedLast30Days;
        this.hasOverdueActiveLoans = hasOverdueActiveLoans;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }

    public boolean isReliable() {
        return reliable;
    }

    public void setReliable(boolean reliable) {
        this.reliable = reliable;
    }

    public int getMaxActiveLoans() {
        return maxActiveLoans;
    }

    public void setMaxActiveLoans(int maxActiveLoans) {
        this.maxActiveLoans = maxActiveLoans;
    }

    public int getReturnedLast30Days() {
        return returnedLast30Days;
    }

    public void setReturnedLast30Days(int returnedLast30Days) {
        this.returnedLast30Days = returnedLast30Days;
    }

    public boolean isHasOverdueActiveLoans() {
        return hasOverdueActiveLoans;
    }

    public void setHasOverdueActiveLoans(boolean hasOverdueActiveLoans) {
        this.hasOverdueActiveLoans = hasOverdueActiveLoans;
    }
}