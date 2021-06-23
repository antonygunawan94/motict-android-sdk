package com.motict.sdk.exception;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class PinCodeVerificationNotStartedException extends Exception {
    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "Please start verification flow first before verifying pin code";
    }
}
