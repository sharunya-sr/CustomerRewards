package com.rewards.CustomerRewards.service;

import com.rewards.CustomerRewards.dto.RewardSummary;
import com.rewards.CustomerRewards.exception.InvalidTransactionException;
import com.rewards.CustomerRewards.exception.TransactionNotFoundException;
import com.rewards.CustomerRewards.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for calculating reward points from customer transactions.
 *
 * Reward rules:
 *
 *   2 points for every dollar spent <b>over $100</b> in a single transaction.
 *   1 point for every dollar spent <b>between $50 and $100</b> (inclusive) in a single transaction.
 *   No points are awarded for amounts below $50.
 *
 * Example: $120 purchase = (2 × $20) + (1 × $50) = 90 points.
 */
@Service
public class RewardsCalculatorService {

    /** Threshold above which 2 points per dollar are awarded. */
    private static final double UPPER_THRESHOLD = 100.0;

    /** Threshold above which 1 point per dollar is awarded. */
    private static final double LOWER_THRESHOLD = 50.0;

    /**
     * Calculates reward points for a single transaction amount.
     *
     * Logic:
     * 
     *   Amount &gt; 100: points = 2*(amount - 100) + 50
     *   Amount between 50 and 100: points = (amount - 50)
     *   Amount &lt;= 50: 0 points
     * 
     *
     * @param amount the purchase amount in dollars; must not be negative
     * @return reward points earned as a long value
     * @throws InvalidTransactionException if the amount is negative
     */
    public long calculatePointsForTransaction(double amount) {
        if (amount < 0) {
            throw new InvalidTransactionException(
                    "Transaction amount cannot be negative: " + amount);
        }
        long points = 0;
        if (amount > UPPER_THRESHOLD) {
            points += (long) (2 * (amount - UPPER_THRESHOLD));
            points += (long) (UPPER_THRESHOLD - LOWER_THRESHOLD); // 50 points for the $50–$100 band
        } else if (amount > LOWER_THRESHOLD) {
            points += (long) (amount - LOWER_THRESHOLD);
        }
        return points;
    }

    /**
     * Calculates monthly and total reward points for each customer
     * from a list of transactions.
     *
     * Transactions are grouped first by customer, then by "MONTH YEAR"
     * (e.g. "JANUARY 2024"), so the month labels are derived dynamically
     * from the transaction dates — months are never hard-coded.
     *
     * @param transactions list of transactions to process; must not be null or empty
     * @return list of {@link RewardSummary} objects, one per customer
     * @throws TransactionNotFoundException if the transaction list is null or empty
     * @throws InvalidTransactionException  if any transaction has a null date or customer ID
     */
    public List<RewardSummary> calculateRewards(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new TransactionNotFoundException(
                    "No transactions found. Please provide at least one transaction.");
        }

        // Validate every transaction before processing
        transactions.forEach(this::validateTransaction);

        // Group transactions by customerId
        Map<String, List<Transaction>> byCustomer = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCustomerId));

        return byCustomer.entrySet().stream()
                .map(entry -> buildRewardSummary(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Validates that a transaction has all required non-null fields.
     *
     * @param t the transaction to validate
     * @throws InvalidTransactionException if customerId or transactionDate is null/blank
     */
    private void validateTransaction(Transaction t) {
        if (t.getCustomerId() == null || t.getCustomerId().isBlank()) {
            throw new InvalidTransactionException("Customer ID must not be null or blank.");
        }
        if (t.getTransactionDate() == null) {
            throw new InvalidTransactionException(
                    "Transaction date must not be null for customer: " + t.getCustomerId());
        }
        if (t.getAmount() == null || t.getAmount() < 0) {
            throw new InvalidTransactionException(
                    "Transaction amount must be non-negative for customer: " + t.getCustomerId());
        }
    }

    /**
     * Builds a {@link RewardSummary} for one customer by grouping their
     * transactions per month and summing points.
     *
     * @param customerId   the customer's unique ID
     * @param transactions all transactions belonging to this customer
     * @return populated {@link RewardSummary}
     */
    private RewardSummary buildRewardSummary(String customerId, List<Transaction> transactions) {
        // Use display name from the first available transaction
        String customerName = transactions.stream()
                .map(Transaction::getCustomerName)
                .filter(n -> n != null && !n.isBlank())
                .findFirst()
                .orElse(customerId);

        // Group by "MONTH YEAR" derived dynamically from the transaction date
        Map<String, Long> monthlyPoints = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().getMonth().name()
                                + " " + t.getTransactionDate().getYear(),
                        LinkedHashMap::new,
                        Collectors.summingLong(t -> calculatePointsForTransaction(t.getAmount()))
                ));

        long totalPoints = monthlyPoints.values().stream().mapToLong(Long::longValue).sum();

        return new RewardSummary(customerId, customerName, monthlyPoints, totalPoints);
    }
}