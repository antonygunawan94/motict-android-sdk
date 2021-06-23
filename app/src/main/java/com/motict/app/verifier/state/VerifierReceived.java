package com.motict.app.verifier.state;

import com.motict.sdk.model.MissedCallVerificationReceived;

public class VerifierReceived extends VerifierState {
    private final MissedCallVerificationReceived received;

    public VerifierReceived(MissedCallVerificationReceived received) {
        this.received = received;
    }

    public MissedCallVerificationReceived getReceived() {
        return received;
    }
}
