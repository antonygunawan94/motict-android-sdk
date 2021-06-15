package com.motict.sdk.callback;

import com.motict.sdk.model.MissedCallOTPResponse;

public interface RequestMissedCallOTPCallback {
    void onRequestMissedCallOTPSuccess(MissedCallOTPResponse response);
    void onRequestMissedCallOTPFailed(Exception error);
}
