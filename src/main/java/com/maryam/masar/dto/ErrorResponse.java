package com.maryam.masar.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class ErrorResponse {
    private OffsetDateTime timestamp;
    private String path;
    private String code;
    private String message;
    private List<FieldError> fieldErrors;

    public ErrorResponse() {}

    public ErrorResponse(OffsetDateTime timestamp, String path, String code,
                         String message, List<FieldError> fieldErrors) {
        this.timestamp = timestamp;
        this.path = path;
        this.code = code;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<FieldError> getFieldErrors() { return fieldErrors; }
    public void setFieldErrors(List<FieldError> fieldErrors) { this.fieldErrors = fieldErrors; }

    public static class FieldError {
        private String field;
        private String message;

        public FieldError() {}

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}