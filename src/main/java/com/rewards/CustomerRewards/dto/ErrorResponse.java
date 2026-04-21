package com.rewards.CustomerRewards.dto;

import java.time.LocalDateTime;

/**
 * Standardized error response object returned by the API
 * when an exception or validation failure occurs.
 */
public class ErrorResponse {

    /** HTTP status code. */
    private int status;

    /** Short error description. */
    private String error;

    /** Detailed message explaining what went wrong. */
    private String message;

    /** Timestamp when the error occurred. */
    private LocalDateTime timestamp;

    // ---- Constructors ----

    /** Default no-arg constructor. */
    public ErrorResponse() {}

    /**
     * All-args constructor.
     *
     * @param status    HTTP status code
     * @param error     short error label
     * @param message   detailed error message
     * @param timestamp when the error occurred
     */
    public ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
    }

    // ---- Getters and Setters ----

    /** @return HTTP status code */
    public int getStatus() { return status; }

    /** @param status HTTP status code to set */
    public void setStatus(int status) { this.status = status; }

    /** @return short error label */
    public String getError() { return error; }

    /** @param error short error label to set */
    public void setError(String error) { this.error = error; }

    /** @return detailed error message */
    public String getMessage() { return message; }

    /** @param message detailed error message to set */
    public void setMessage(String message) { this.message = message; }

    /** @return timestamp of the error */
    public LocalDateTime getTimestamp() { return timestamp; }

    /** @param timestamp error timestamp to set */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}