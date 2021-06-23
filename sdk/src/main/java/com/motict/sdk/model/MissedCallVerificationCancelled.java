package com.motict.sdk.model;

public class MissedCallVerificationCancelled {
    private final String cancelledVerifierPhoneNumber;

    public MissedCallVerificationCancelled(String cancelledVerifierPhoneNumber) {
        this.cancelledVerifierPhoneNumber = cancelledVerifierPhoneNumber;
    }

    public String getCancelledVerifierPhoneNumber() {
        return cancelledVerifierPhoneNumber;
    }
}
