package com.rewards.CustomerRewards.exception;

/**
 * Exception thrown when a transaction contains invalid data,
 * such as a null date, negative amount, or missing customer ID.
 */
public class InvalidTransactionException extends RuntimeException {

    /**
     * Constructs a new exception with a descriptive message.
     *
     * @param message the detail message explaining the cause
     */
    public InvalidTransactionException(String message) {
        super(message);
    }
}