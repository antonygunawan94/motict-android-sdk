package com.motict.sdk.exception;

public class ApiResponseException extends Exception {
    private String message;

    public ApiResponseException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
