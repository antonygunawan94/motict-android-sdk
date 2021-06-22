package com.motict.sdk.model;

public class MissedCallVerificationReceived {
    private final String receivedFourPinCode;
    private final String receivedSixPinCode;
    private final String receivedPhoneNumber;

    public MissedCallVerificationReceived(String receivedFourPinCode, String receivedSixPinCode, String receivedPhoneNumber) {
        this.receivedFourPinCode = receivedFourPinCode;
        this.receivedSixPinCode = receivedSixPinCode;
        this.receivedPhoneNumber = receivedPhoneNumber;
    }

    public String getReceivedFourPinCode() {
        return receivedFourPinCode;
    }

    public String getReceivedSixPinCode() {
        return receivedSixPinCode;
    }

    public String getReceivedPhoneNumber() {
        return receivedPhoneNumber;
    }
}
