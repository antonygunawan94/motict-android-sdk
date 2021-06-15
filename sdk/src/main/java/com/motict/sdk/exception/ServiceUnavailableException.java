package com.motict.sdk.exception;

import androidx.annotation.NonNull;

public class ServiceUnavailableException extends Exception {
    @NonNull
    @Override
    public String toString() {
        return "Service unavailable";
    }
}
