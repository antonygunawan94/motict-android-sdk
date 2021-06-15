package com.motict.sdk.exception;

import org.jetbrains.annotations.NotNull;

public class InvalidPhoneNumberException extends Exception {

    @NotNull
    @Override
    public String toString() {
        return "The phone number format is invalid, please make sure to use +E164 format and only number for allowed country";
    }
}
