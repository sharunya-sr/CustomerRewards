package com.rewards.CustomerRewards.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

/**
 * Represents a single purchase transaction made by a customer.
 * Each transaction holds the customer identifier, transaction date,
 * and the purchase amount used to calculate reward points.
 */


public class Transaction {

    /**
     * Unique identifier of the customer who made the purchase.
     */
    @NotBlank(message = "Customer ID must not be blank")
    private String customerId;

    /**
     * Name of the customer (optional display field).
     */
    private String customerName;

    /**
     * Date on which the transaction occurred.
     */
    @NotNull(message = "Transaction date must not be null")
    private LocalDate transactionDate;

    /**
     * Amount spent in dollars during this transaction.
     */
    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    // ---- Constructors ----

    /** Default no-arg constructor. */
    public Transaction() {}

    /**
     * All-args constructor.
     *
     * @param customerId      unique customer identifier
     * @param customerName    display name of the customer
     * @param transactionDate date of the transaction
     * @param amount          purchase amount in dollars
     */
    public Transaction(String customerId, String customerName,
                       LocalDate transactionDate, Double amount) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.transactionDate = transactionDate;
        this.amount = amount;
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

    /** @return the transaction date */
    public LocalDate getTransactionDate() { return transactionDate; }

    /** @param transactionDate the transaction date to set */
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    /** @return the transaction amount */
    public Double getAmount() { return amount; }

    /** @param amount the transaction amount to set */
    public void setAmount(Double amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "Transaction{" +
                "customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", transactionDate=" + transactionDate +
                ", amount=" + amount +
                '}';
    }
}