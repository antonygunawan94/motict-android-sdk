package com.motict.sdk.model;


public class MissedCallVerificationStart {
    private final String verifiedPhoneNumber;

    private final String receivedFourPinCode;
    private final String receivedSixPinCode;
    private final String receivedPhoneNumber;

    public MissedCallVerificationStart(String verifiedPhoneNumber,
                                       String receivedFourPinCode,
                                       String receivedSixPinCode,
                                       String receivedPhoneNumber) {
        this.verifiedPhoneNumber = verifiedPhoneNumber;
        this.receivedFourPinCode = receivedFourPinCode;
        this.receivedSixPinCode = receivedSixPinCode;
        this.receivedPhoneNumber = receivedPhoneNumber;
    }


    public String getVerifiedPhoneNumber() {
        return verifiedPhoneNumber;
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
