package com.motict.sdk.exception;

import androidx.annotation.NonNull;

public class MaxAttemptExceededException extends Exception {
    @NonNull
    @Override
    public String toString() {
        return "The phone number has been temporary block because of too many requests in the last few minutes, please try again in next few minutes";
    }
}
