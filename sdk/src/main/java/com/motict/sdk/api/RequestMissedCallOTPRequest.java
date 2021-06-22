package com.motict.sdk.api;

import com.google.gson.annotations.SerializedName;

class RequestMissedCallOTPRequest {
    @SerializedName("phone_number")
    private String phoneNumber;

    public RequestMissedCallOTPRequest() {
    }

    public RequestMissedCallOTPRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
