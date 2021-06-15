package com.motict.sdk.exception;

import androidx.annotation.NonNull;

public class MissingAuthTokenException extends Exception {
    @NonNull
    @Override
    public String toString() {
        return "Missing auth token";
    }
}
