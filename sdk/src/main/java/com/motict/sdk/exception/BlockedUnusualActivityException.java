package com.motict.sdk.exception;

import androidx.annotation.NonNull;

public class BlockedUnusualActivityException extends Exception {
    @NonNull
    @Override
    public String toString() {
        return "The phone number has been blocked due to unusual activities, the phone number will be unblocked in next 24 hours";
    }
}
