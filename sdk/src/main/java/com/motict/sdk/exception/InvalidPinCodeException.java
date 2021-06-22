package com.motict.sdk.exception;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class InvalidPinCodeException extends Exception {

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "Sorry, your pin code is not correct";
    }
}
