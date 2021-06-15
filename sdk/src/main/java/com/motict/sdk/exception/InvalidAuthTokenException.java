package com.motict.sdk.exception;

import androidx.annotation.NonNull;

public class InvalidAuthTokenException extends Exception {
    @NonNull
    @Override
    public String toString() {
        return "Invalid auth token";
    }
}
