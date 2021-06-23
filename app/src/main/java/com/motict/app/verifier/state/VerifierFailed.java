package com.motict.app.verifier.state;

public class VerifierFailed extends VerifierState {
    private final Exception exception;

    public VerifierFailed(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
