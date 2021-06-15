package com.motict.sdk.model;

import com.google.gson.annotations.SerializedName;


public class MissedCallOTPResponse {
    private String id;

    @SerializedName("token")
    private String tokenFourDigit;

    @SerializedName("token_6_digit")
    private String tokenSixDigit;

    @SerializedName("full_token")
    private String fullPhoneNumber;

    private String message;

    public MissedCallOTPResponse() {
    }

    public String getId() {
        return id;
    }

    public String getTokenFourDigit() {
        return tokenFourDigit;
    }

    public String getTokenSixDigit() {
        return tokenSixDigit;
    }

    public String getFullPhoneNumber() {
        return fullPhoneNumber;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "MissedCallOTPResponse{" +
                "id='" + id + '\'' +
                ", tokenFourDigit='" + tokenFourDigit + '\'' +
                ", tokenSixDigit='" + tokenSixDigit + '\'' +
                ", fullPhoneNumber='" + fullPhoneNumber + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
