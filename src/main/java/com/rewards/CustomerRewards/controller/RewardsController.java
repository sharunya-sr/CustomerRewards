package com.rewards.CustomerRewards.controller;

import com.rewards.CustomerRewards.dto.RewardSummary;
import com.rewards.CustomerRewards.model.Transaction;
import com.rewards.CustomerRewards.service.DataInitializerService;
import com.rewards.CustomerRewards.service.RewardsCalculatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing endpoints to calculate and retrieve
 * customer reward points.
 */
@RestController
@RequestMapping("/api/rewards")
public class RewardsController {

    private final RewardsCalculatorService rewardsCalculatorService;
    private final DataInitializerService dataInitializerService;

    /**
     * Constructor injection for service dependencies.
     *
     * @param rewardsCalculatorService service containing the points calculation logic
     * @param dataInitializerService   service providing sample transaction data
     */
    public RewardsController(RewardsCalculatorService rewardsCalculatorService,
                             DataInitializerService dataInitializerService) {
        this.rewardsCalculatorService = rewardsCalculatorService;
        this.dataInitializerService = dataInitializerService;
    }

    /**
     * Calculates reward points from the built-in sample dataset.
     *
     * Endpoint: {@code GET /api/rewards/calculate}
     *
     * Returns reward summaries for all customers in the sample dataset,
     * including monthly breakdowns and totals. Months are derived dynamically
     * from the transaction dates — never hard-coded.
     *
     * @return 200 OK with a list of {@link RewardSummary} objects
     */
    @GetMapping("/calculate")
    public ResponseEntity<List<RewardSummary>> calculateSampleRewards() {
        List<Transaction> transactions = dataInitializerService.getSampleTransactions();
        List<RewardSummary> summaries = rewardsCalculatorService.calculateRewards(transactions);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Calculates reward points from a caller-supplied list of transactions.
     *
     * Endpoint: {@code POST /api/rewards/calculate}
     *
     * Accepts a JSON array of {@link Transaction} objects and returns reward
     * summaries for all customers found in the provided data.
     *
     * @param transactions list of transactions supplied in the request body
     * @return 200 OK with a list of {@link RewardSummary} objects
     */
    @PostMapping("/calculate")
    public ResponseEntity<List<RewardSummary>> calculateCustomRewards(
            @Valid @RequestBody List<Transaction> transactions) {
        List<RewardSummary> summaries = rewardsCalculatorService.calculateRewards(transactions);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Calculates reward points for a single transaction amount (utility endpoint).
     *
     * Endpoint: {@code GET /api/rewards/points?amount=120.0}
     *
     * Useful for quickly verifying how many points a given purchase amount earns.
     *
     * @param amount the purchase amount in dollars (must be non-negative)
     * @return 200 OK with the number of points earned as a plain long
     */
    @GetMapping("/points")
    public ResponseEntity<Long> getPointsForAmount(@RequestParam double amount) {
        long points = rewardsCalculatorService.calculatePointsForTransaction(amount);
        return ResponseEntity.ok(points);
    }
}