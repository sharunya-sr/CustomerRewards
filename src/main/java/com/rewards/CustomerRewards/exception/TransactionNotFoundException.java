package com.rewards.CustomerRewards.exception;

/**
 * Exception thrown when no transactions are found
 * for a given customer or within a requested date range.
 */
public class TransactionNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with a descriptive message.
     *
     * @param message the detail message explaining the cause
     */
    public TransactionNotFoundException(String message) {
        super(message);
    }
}