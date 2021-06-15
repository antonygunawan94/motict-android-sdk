package com.motict.sdk.callback;

public interface OTPMissedCallReceivedCallback {
    void onOTPMissedCallReceived(String fullPhoneNumber, String tokenFourDigit, String tokenSixDigit);
}
