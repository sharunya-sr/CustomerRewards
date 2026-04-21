package CustomerRewards;

import com.rewards.CustomerRewards.dto.RewardSummary;
import com.rewards.CustomerRewards.exception.InvalidTransactionException;
import com.rewards.CustomerRewards.exception.TransactionNotFoundException;
import com.rewards.CustomerRewards.model.Transaction;
import com.rewards.CustomerRewards.service.RewardsCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RewardsCalculatorService}.
 *
 *   Points calculation for various spend amounts
 *   Monthly grouping and total calculation
 *   Exception and negative test scenarios
 *   Multiple customers with multiple transactions
 * 
 */
class RewardsCalculatorServiceTest {

    private RewardsCalculatorService service;

    @BeforeEach
    void setUp() {
        service = new RewardsCalculatorService();
    }

    // ========== calculatePointsForTransaction Tests ==========

    @Test
    @DisplayName("Amount exactly $120 should yield 90 points (example from spec)")
    void testPoints_120Dollars_Equals_90Points() {
        assertEquals(90, service.calculatePointsForTransaction(120.0));
    }

    @Test
    @DisplayName("Amount exactly $100 should yield 50 points")
    void testPoints_100Dollars_Equals_50Points() {
        assertEquals(50, service.calculatePointsForTransaction(100.0));
    }

    @Test
    @DisplayName("Amount exactly $50 should yield 0 points")
    void testPoints_50Dollars_Equals_0Points() {
        assertEquals(0, service.calculatePointsForTransaction(50.0));
    }

    @Test
    @DisplayName("Amount below $50 should yield 0 points")
    void testPoints_BelowFifty_Equals_0Points() {
        assertEquals(0, service.calculatePointsForTransaction(30.0));
        assertEquals(0, service.calculatePointsForTransaction(0.0));
        assertEquals(0, service.calculatePointsForTransaction(49.99));
    }

    @Test
    @DisplayName("Amount between $50 and $100 should yield 1 point per dollar above $50")
    void testPoints_BetweenFiftyAndHundred() {
        // $75 → 75 - 50 = 25 points
        assertEquals(25, service.calculatePointsForTransaction(75.0));
        // $51 → 1 point
        assertEquals(1, service.calculatePointsForTransaction(51.0));
        // $99 → 49 points
        assertEquals(49, service.calculatePointsForTransaction(99.0));
    }

    @Test
    @DisplayName("Amount over $100 should apply both point tiers")
    void testPoints_OverHundred_TwoTiers() {
        // $200 → 2*(200-100) + (100-50) = 200 + 50 = 250
        assertEquals(250, service.calculatePointsForTransaction(200.0));
        // $150 → 2*(150-100) + 50 = 100 + 50 = 150
        assertEquals(150, service.calculatePointsForTransaction(150.0));
    }

    @Test
    @DisplayName("Negative amount should throw InvalidTransactionException")
    void testPoints_NegativeAmount_ThrowsException() {
        assertThrows(InvalidTransactionException.class,
                () -> service.calculatePointsForTransaction(-10.0));
    }

    // ========== calculateRewards Tests ==========

    @Test
    @DisplayName("Null transaction list should throw TransactionNotFoundException")
    void testCalculateRewards_NullList_ThrowsException() {
        assertThrows(TransactionNotFoundException.class,
                () -> service.calculateRewards(null));
    }

    @Test
    @DisplayName("Empty transaction list should throw TransactionNotFoundException")
    void testCalculateRewards_EmptyList_ThrowsException() {
        assertThrows(TransactionNotFoundException.class,
                () -> service.calculateRewards(Collections.emptyList()));
    }

    @Test
    @DisplayName("Transaction with null date should throw InvalidTransactionException")
    void testCalculateRewards_NullDate_ThrowsException() {
        Transaction t = new Transaction("C001", "Alice", null, 120.0);
        assertThrows(InvalidTransactionException.class,
                () -> service.calculateRewards(List.of(t)));
    }

    @Test
    @DisplayName("Transaction with null customerId should throw InvalidTransactionException")
    void testCalculateRewards_NullCustomerId_ThrowsException() {
        Transaction t = new Transaction(null, "Alice", LocalDate.now(), 120.0);
        assertThrows(InvalidTransactionException.class,
                () -> service.calculateRewards(List.of(t)));
    }

    @Test
    @DisplayName("Transaction with blank customerId should throw InvalidTransactionException")
    void testCalculateRewards_BlankCustomerId_ThrowsException() {
        Transaction t = new Transaction("  ", "Alice", LocalDate.now(), 120.0);
        assertThrows(InvalidTransactionException.class,
                () -> service.calculateRewards(List.of(t)));
    }

    @Test
    @DisplayName("Single customer single transaction should return correct summary")
    void testCalculateRewards_SingleCustomer_SingleTransaction() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        Transaction t = new Transaction("C001", "Alice", date, 120.0);

        List<RewardSummary> result = service.calculateRewards(List.of(t));

        assertEquals(1, result.size());
        RewardSummary summary = result.get(0);
        assertEquals("C001", summary.getCustomerId());
        assertEquals("Alice", summary.getCustomerName());
        assertEquals(90L, summary.getTotalPoints());
        assertTrue(summary.getMonthlyPoints().containsKey("JANUARY 2024"));
        assertEquals(90L, summary.getMonthlyPoints().get("JANUARY 2024"));
    }

    @Test
    @DisplayName("Multiple customers should each have correct summaries")
    void testCalculateRewards_MultipleCustomers() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("C001", "Alice", LocalDate.of(2024, 1, 10), 120.0), // 90 pts
                new Transaction("C001", "Alice", LocalDate.of(2024, 2, 10), 75.0),  // 25 pts
                new Transaction("C002", "Bob",   LocalDate.of(2024, 1, 5),  200.0), // 250 pts
                new Transaction("C002", "Bob",   LocalDate.of(2024, 3, 15), 50.0)   // 0 pts
        );

        List<RewardSummary> result = service.calculateRewards(transactions);

        assertEquals(2, result.size());

        RewardSummary alice = result.stream()
                .filter(s -> "C001".equals(s.getCustomerId())).findFirst().orElseThrow();
        assertEquals(115L, alice.getTotalPoints()); // 90 + 25
        assertEquals(90L, alice.getMonthlyPoints().get("JANUARY 2024"));
        assertEquals(25L, alice.getMonthlyPoints().get("FEBRUARY 2024"));

        RewardSummary bob = result.stream()
                .filter(s -> "C002".equals(s.getCustomerId())).findFirst().orElseThrow();
        assertEquals(250L, bob.getTotalPoints()); // 250 + 0
        assertEquals(250L, bob.getMonthlyPoints().get("JANUARY 2024"));
        assertEquals(0L,   bob.getMonthlyPoints().get("MARCH 2024"));
    }

    @Test
    @DisplayName("Transaction amount below $50 contributes 0 points to total")
    void testCalculateRewards_AmountBelowFifty_ZeroPoints() {
        Transaction t = new Transaction("C003", "Carol", LocalDate.of(2024, 2, 20), 30.0);
        List<RewardSummary> result = service.calculateRewards(List.of(t));

        assertEquals(1, result.size());
        assertEquals(0L, result.get(0).getTotalPoints());
    }

    @Test
    @DisplayName("Customer name falls back to customerId when name is null")
    void testCalculateRewards_NullCustomerName_FallsBackToId() {
        Transaction t = new Transaction("C004", null, LocalDate.of(2024, 3, 1), 120.0);
        List<RewardSummary> result = service.calculateRewards(List.of(t));

        assertEquals("C004", result.get(0).getCustomerName());
    }

    @Test
    @DisplayName("Same customer transactions in same month aggregate correctly")
    void testCalculateRewards_SameMonthMultipleTransactions_Aggregated() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("C001", "Alice", LocalDate.of(2024, 1, 5),  120.0), // 90
                new Transaction("C001", "Alice", LocalDate.of(2024, 1, 20), 200.0)  // 250
        );

        List<RewardSummary> result = service.calculateRewards(transactions);
        assertEquals(1, result.size());
        assertEquals(340L, result.get(0).getTotalPoints());
        assertEquals(340L, result.get(0).getMonthlyPoints().get("JANUARY 2024"));
    }
}