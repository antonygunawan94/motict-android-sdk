package com.motict.sdk.model;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("error_code")
    private int errorCode;

    @SerializedName("error_code_slug")
    private String errorCodeSlug;


    private String message;

    public ErrorResponse() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorCodeSlug() {
        return errorCodeSlug;
    }

    public String getMessage() {
        return message;
    }
}
