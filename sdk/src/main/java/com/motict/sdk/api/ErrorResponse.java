package com.motict.sdk.api;

import com.google.gson.annotations.SerializedName;
import com.motict.sdk.exception.ApiResponseException;
import com.motict.sdk.exception.BlockedUnusualActivityException;
import com.motict.sdk.exception.IPNotAllowedException;
import com.motict.sdk.exception.InvalidAuthException;
import com.motict.sdk.exception.InvalidAuthTokenException;
import com.motict.sdk.exception.InvalidPhoneNumberException;
import com.motict.sdk.exception.MaxAttemptExceededException;
import com.motict.sdk.exception.MissingAuthTokenException;
import com.motict.sdk.exception.ServiceUnavailableException;

class ErrorResponse {
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

    Exception toException() {
        switch (getErrorCode()) {
            case 110:
                return new InvalidPhoneNumberException();
            case 111:
                return new MaxAttemptExceededException();
            case 112:
                return new BlockedUnusualActivityException();
            case 113:
                return new MissingAuthTokenException();
            case 114:
                return new InvalidAuthTokenException();
            case 115:
                return new ServiceUnavailableException();
            case 117:
                return new InvalidAuthException();
            case 118:
                return new IPNotAllowedException();
            default:
                return new ApiResponseException(getMessage());
        }
    }
}
