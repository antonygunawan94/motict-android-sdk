package com.motict.sdk.exception;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class VerifyInvalidOTPCodeException extends  Exception{

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "Sorry, your phone number doesn't match with otp code";
    }
}
