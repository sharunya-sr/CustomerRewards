package com.rewards.CustomerRewards.dto;

import java.util.Map;

/**
 * Data Transfer Object representing a customer's reward points summary.
 * Holds monthly breakdowns and a total for the entire period.
 */
public class RewardSummary {

    /**
     * Unique identifier of the customer.
     */
    private String customerId;

    /**
     * Display name of the customer.
     */
    private String customerName;

    /**
     * Map of month-year label (e.g., "JANUARY 2024") to points earned that month.
     */
    private Map<String, Long> monthlyPoints;

    /**
     * Total reward points earned across all months.
     */
    private long totalPoints;

    // ---- Constructors ----

    /** Default no-arg constructor. */
    public RewardSummary() {}

    /**
     * All-args constructor.
     *
     * @param customerId    unique customer identifier
     * @param customerName  display name
     * @param monthlyPoints map of month label to points
     * @param totalPoints   total points across all months
     */
    public RewardSummary(String customerId, String customerName,
                         Map<String, Long> monthlyPoints, long totalPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthlyPoints = monthlyPoints;
        this.totalPoints = totalPoints;
    }

    // ---- Getters and Setters ----

    /** @return the customer ID */
    public String getCustomerId() { return customerId; }

    /** @param customerId the customer ID to set */
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    /** @return the customer name */
    public String getCustomerName() { return customerName; }

    /** @param customerName the customer name to set */
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    /** @return map of month label to points */
    public Map<String, Long> getMonthlyPoints() { return monthlyPoints; }

    /** @param monthlyPoints map of month label to points to set */
    public void setMonthlyPoints(Map<String, Long> monthlyPoints) { this.monthlyPoints = monthlyPoints; }

    /** @return total reward points */
    public long getTotalPoints() { return totalPoints; }

    /** @param totalPoints total points to set */
    public void setTotalPoints(long totalPoints) { this.totalPoints = totalPoints; }

    @Override
    public String toString() {
        return "RewardSummary{" +
                "customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", monthlyPoints=" + monthlyPoints +
                ", totalPoints=" + totalPoints +
                '}';
    }
}