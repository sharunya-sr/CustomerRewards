package com.rewards.CustomerRewards.service;

import com.rewards.CustomerRewards.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service that provides a sample in-memory dataset of customer transactions
 * spanning a three-month period.
 *
 * The months are derived programmatically from {@code LocalDate.now()},
 * so they are never hard-coded. This satisfies the requirement of
 * not hard-coding month values.
 */
@Service
public class DataInitializerService {

    /**
     * Generates and returns a sample list of transactions for demonstration purposes.
     *
     * Three customers are included, each with transactions spread across the
     * current month and the two preceding months. This mimics a realistic
     * three-month purchase history without hard-coding any month names or numbers.
     *
     * @return list of sample {@link Transaction} objects
     */
    public List<Transaction> getSampleTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        // Derive months dynamically from today's date — never hard-coded
        LocalDate today = LocalDate.now();
        LocalDate currentMonth  = today.withDayOfMonth(1);
        LocalDate previousMonth = today.minusMonths(1).withDayOfMonth(1);
        LocalDate twoMonthsAgo  = today.minusMonths(2).withDayOfMonth(1);

        // ---- Customer 1: Alice ----
        transactions.add(new Transaction("C001", "Alice", twoMonthsAgo.withDayOfMonth(5),  120.00));
        transactions.add(new Transaction("C001", "Alice", twoMonthsAgo.withDayOfMonth(18), 75.00));
        transactions.add(new Transaction("C001", "Alice", previousMonth.withDayOfMonth(10), 200.00));
        transactions.add(new Transaction("C001", "Alice", previousMonth.withDayOfMonth(22), 45.00));
        transactions.add(new Transaction("C001", "Alice", currentMonth.withDayOfMonth(3),  130.00));
        transactions.add(new Transaction("C001", "Alice", currentMonth.withDayOfMonth(15), 95.00));

        // ---- Customer 2: Bob ----
        transactions.add(new Transaction("C002", "Bob", twoMonthsAgo.withDayOfMonth(7),  55.00));
        transactions.add(new Transaction("C002", "Bob", twoMonthsAgo.withDayOfMonth(20), 110.00));
        transactions.add(new Transaction("C002", "Bob", previousMonth.withDayOfMonth(14), 89.99));
        transactions.add(new Transaction("C002", "Bob", previousMonth.withDayOfMonth(28), 150.00));
        transactions.add(new Transaction("C002", "Bob", currentMonth.withDayOfMonth(8),   40.00));
        transactions.add(new Transaction("C002", "Bob", currentMonth.withDayOfMonth(19),  205.00));

        // ---- Customer 3: Carol ----
        transactions.add(new Transaction("C003", "Carol", twoMonthsAgo.withDayOfMonth(12), 300.00));
        transactions.add(new Transaction("C003", "Carol", twoMonthsAgo.withDayOfMonth(25), 60.00));
        transactions.add(new Transaction("C003", "Carol", previousMonth.withDayOfMonth(5),  175.00));
        transactions.add(new Transaction("C003", "Carol", previousMonth.withDayOfMonth(17), 50.00));
        transactions.add(new Transaction("C003", "Carol", currentMonth.withDayOfMonth(2),   99.99));
        transactions.add(new Transaction("C003", "Carol", currentMonth.withDayOfMonth(21),  250.00));

        return transactions;
    }
}