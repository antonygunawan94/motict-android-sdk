package com.motict.sdk.listener;

import com.motict.sdk.model.MissedCallVerificationReceived;

public interface MissedCallVerificationReceivedListener {
    void onMissedCallVerificationReceived(MissedCallVerificationReceived received);
}
