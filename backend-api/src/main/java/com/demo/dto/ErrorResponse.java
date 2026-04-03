package com.demo.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private String message;
    private List<FieldError> fieldErrors;

    public ErrorResponse(int status, String error, String message) {
        this.timestamp   = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status      = status;
        this.error       = error;
        this.message     = message;
    }

    public ErrorResponse(int status, String error, String message, List<FieldError> fieldErrors) {
        this(status, error, message);
        this.fieldErrors = fieldErrors;
    }

    // ── Getters ────────────────────────────────────────────────
    public String getTimestamp()              { return timestamp; }
    public int    getStatus()                 { return status; }
    public String getError()                  { return error; }
    public String getMessage()                { return message; }
    public List<FieldError> getFieldErrors()  { return fieldErrors; }

    // ── Nested: individual field violation ─────────────────────
    public static class FieldError {
        private final String field;
        private final String message;

        public FieldError(String field, String message) {
            this.field   = field;
            this.message = message;
        }

        public String getField()   { return field; }
        public String getMessage() { return message; }
    }
}
