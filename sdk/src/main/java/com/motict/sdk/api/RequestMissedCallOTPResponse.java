package com.motict.sdk.api;

import com.google.gson.annotations.SerializedName;


public class RequestMissedCallOTPResponse {
    private String id;

    @SerializedName("token")
    private String tokenFourDigit;

    @SerializedName("token_6_digit")
    private String tokenSixDigit;

    @SerializedName("full_token")
    private String fullPhoneNumber;

    private String message;

    public RequestMissedCallOTPResponse() {
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
