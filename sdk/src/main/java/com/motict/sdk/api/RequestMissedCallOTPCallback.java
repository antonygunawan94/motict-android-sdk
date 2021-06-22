package com.motict.sdk.api;

import com.motict.sdk.api.RequestMissedCallOTPResponse;

public interface RequestMissedCallOTPCallback {
    void onRequestMissedCallOTPSuccess(RequestMissedCallOTPResponse response);
    void onRequestMissedCallOTPFailed(Exception error);
}
