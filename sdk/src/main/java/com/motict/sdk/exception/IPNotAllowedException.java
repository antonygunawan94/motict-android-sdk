package com.motict.sdk.exception;

import androidx.annotation.NonNull;

public class IPNotAllowedException extends Exception {
    @NonNull
    @Override
    public String toString() {
        return "IP not allowed";
    }
}
