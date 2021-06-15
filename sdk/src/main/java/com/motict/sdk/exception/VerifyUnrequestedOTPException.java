package com.motict.sdk.exception;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class VerifyUnrequestedOTPException extends Exception{
    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "Please request your missed call otp first before verifying it";
    }
}
