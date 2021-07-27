package com.motict.sdk.api;

public interface LogEventCallback {
    void onLogEventSuccess(String message);

    void onLogEventFailed(Exception error);
}
