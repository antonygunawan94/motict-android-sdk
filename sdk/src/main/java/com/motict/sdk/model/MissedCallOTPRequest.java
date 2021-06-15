package com.motict.sdk.model;

import com.google.gson.annotations.SerializedName;

public class MissedCallOTPRequest {
    @SerializedName("phone_number")
    private  String phoneNumber;

    public MissedCallOTPRequest() {
    }

    public MissedCallOTPRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
