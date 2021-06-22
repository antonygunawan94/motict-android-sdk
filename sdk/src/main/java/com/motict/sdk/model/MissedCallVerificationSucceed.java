package com.motict.sdk.model;

public class MissedCallVerificationSucceed {
    private final String verifiedPhoneNumber;

    public MissedCallVerificationSucceed(String verifiedPhoneNumber) {
        this.verifiedPhoneNumber = verifiedPhoneNumber;
    }

    public String getVerifiedPhoneNumber() {
        return verifiedPhoneNumber;
    }
}
