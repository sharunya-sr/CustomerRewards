# Customer Rewards Program API

A Spring Boot RESTful API that calculates reward points earned by customers based on their purchase transactions over a three-month period.


---

## Implementation Rules

A customer earns reward points per transaction as follows:

| Purchase Amount      | Points Earned                                              |
|----------------------|------------------------------------------------------------|
| ≤ $50                | 0 points                                                   |
| $50.01 – $100.00     | 1 point per dollar above $50                               |
| > $100               | 1 pt per dollar ($50–$100) + 2 pts per dollar above $100   |

**Example:** $120 purchase = 2×$20 + 1×$50 = **90 points**

---

## Project Structure

```
customer-rewards/
├── build.gradle
├── settings.gradle
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/rewards/customerrewards/
    │   │   ├── CustomerRewardsApplication.java   # App entry point
    │   │   ├── controller/
    │   │   │   └── RewardsController.java        # REST endpoints
    │   │   ├── service/
    │   │   │   ├── RewardsCalculatorService.java # Core business logic
    │   │   │   └── DataInitializerService.java   # Sample dataset
    │   │   ├── model/
    │   │   │   └── Transaction.java              # Transaction domain model
    │   │   ├── dto/
    │   │   │   ├── RewardSummary.java            # Response DTO
    │   │   │   └── ErrorResponse.java            # Error DTO
    │   │   └── exception/
    │   │       ├── GlobalExceptionHandler.java   # Centralized error handling
    │   │       ├── TransactionNotFoundException.java
    │   │       └── InvalidTransactionException.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/rewards/customerrewards/
            ├── 
            │   └── RewardsCalculatorServiceTest.java      # Unit tests
            └── 
                └── RewardsControllerIntegrationTest.java  # Integration tests
```

---


## API Endpoints

### 1. GET `/api/rewards/calculate`
Returns reward summaries for all customers in the built-in sample dataset.

**Response:**
```json
[
  {
    "customerId": "C001",
    "customerName": "Alice",
    "monthlyPoints": {
      "FEBRUARY 2024": 25,
      "MARCH 2024": 160
    },
    "totalPoints": 185
  }
]
```

---

### 2. POST `/api/rewards/calculate`
Calculates reward summaries from a caller-supplied transaction list.

**Request Body:**
```json
[
  {
    "customerId": "C001",
    "customerName": "Alice",
    "transactionDate": "2024-01-15",
    "amount": 120.00
  },
  {
    "customerId": "C001",
    "customerName": "Alice",
    "transactionDate": "2024-02-10",
    "amount": 75.00
  }
]
```

**Response:** Same structure as GET endpoint above.

---

### 3. GET `/api/rewards/points?amount={amount}`
Returns points earned for a single transaction amount.

**Example:** `GET /api/rewards/points?amount=120` → `90`

---