package com.motict.sdk.model;


public class MissedCallVerificationStart {
    private final String id;
    private final String verifiedPhoneNumber;

    private final String toReceivedFourPinCode;
    private final String toReceivedSixPinCode;
    private final String toReceivedPhoneNumber;

    public MissedCallVerificationStart(
            String id,
            String verifiedPhoneNumber,
            String toReceivedFourPinCode,
            String toReceivedSixPinCode,
            String toReceivedPhoneNumber) {
        this.id = id;
        this.verifiedPhoneNumber = verifiedPhoneNumber;
        this.toReceivedFourPinCode = toReceivedFourPinCode;
        this.toReceivedSixPinCode = toReceivedSixPinCode;
        this.toReceivedPhoneNumber = toReceivedPhoneNumber;
    }


    public String getId() {
        return id;
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
