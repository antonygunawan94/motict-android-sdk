package com.motict.sdk.listener;

import com.motict.sdk.model.MissedCallVerificationCancelled;

public interface MissedCallVerificationCancelledListener {
    void onMissedCallVerificationCancelled(MissedCallVerificationCancelled cancelled);
}
