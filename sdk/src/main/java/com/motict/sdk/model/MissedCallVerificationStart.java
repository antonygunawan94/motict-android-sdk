package com.motict.sdk.model;


public class MissedCallVerificationStart {
    private final String verifiedPhoneNumber;

    private final String toReceivedFourPinCode;
    private final String toReceivedSixPinCode;
    private final String toReceivedPhoneNumber;

    public MissedCallVerificationStart(String verifiedPhoneNumber,
                                       String toReceivedFourPinCode,
                                       String toReceivedSixPinCode,
                                       String toReceivedPhoneNumber) {
        this.verifiedPhoneNumber = verifiedPhoneNumber;
        this.toReceivedFourPinCode = toReceivedFourPinCode;
        this.toReceivedSixPinCode = toReceivedSixPinCode;
        this.toReceivedPhoneNumber = toReceivedPhoneNumber;
    }


    public String getVerifiedPhoneNumber() {
        return verifiedPhoneNumber;
    }

    public String getToReceivedFourPinCode() {
        return toReceivedFourPinCode;
    }

    public String getToReceivedSixPinCode() {
        return toReceivedSixPinCode;
    }

    public String getToReceivedPhoneNumber() {
        return toReceivedPhoneNumber;
    }
}
