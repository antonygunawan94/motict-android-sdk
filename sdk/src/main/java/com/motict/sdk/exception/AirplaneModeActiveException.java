package com.motict.sdk.exception;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class AirplaneModeActiveException extends Exception {
    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "Please turn off your airplane mode to use missed call otp authentication";
    }
}
