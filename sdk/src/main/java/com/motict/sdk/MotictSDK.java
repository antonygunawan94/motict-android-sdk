package com.motict.sdk;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.motict.sdk.callback.OTPMissedCallReceivedCallback;
import com.motict.sdk.callback.RequestMissedCallOTPCallback;
import com.motict.sdk.exception.AirplaneModeActiveException;
import com.motict.sdk.exception.ApiResponseException;
import com.motict.sdk.exception.BlockedUnusualActivityException;
import com.motict.sdk.exception.IPNotAllowedException;
import com.motict.sdk.exception.InvalidAuthException;
import com.motict.sdk.exception.InvalidAuthTokenException;
import com.motict.sdk.exception.InvalidPhoneNumberException;
import com.motict.sdk.exception.MaxAttemptExceededException;
import com.motict.sdk.exception.MissingAuthTokenException;
import com.motict.sdk.exception.RequiredPermissionDeniedException;
import com.motict.sdk.exception.ServiceUnavailableException;
import com.motict.sdk.exception.VerifyInvalidOTPCodeException;
import com.motict.sdk.exception.VerifyUnrequestedOTPException;
import com.motict.sdk.model.ErrorResponse;
import com.motict.sdk.model.MissedCallOTPRequest;
import com.motict.sdk.model.MissedCallOTPResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MotictSDK {
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    private final String apiKey;
    private final Context context;

    private final OkHttpClient httpClient;
    private final List<OTPMissedCallReceivedCallback> otpMissedCallReceivedCallbacks;
    private MissedCallReceiver missedCallReceiver;
    private MissedCallOTPResponse currentMissedCallOTPResponse;


    private MotictSDK(Context context, String apiKey,
                      List<OTPMissedCallReceivedCallback> otpMissedCallReceivedCallbacks) {
        this.context = context;
        this.apiKey = apiKey;
        this.otpMissedCallReceivedCallbacks = otpMissedCallReceivedCallbacks;
        httpClient = new OkHttpClient();
    }


    public void requestMissedCallOTP(String phoneNumber, RequestMissedCallOTPCallback callback) throws Exception {
        if (isAirplaneModeOn(context)) throw new AirplaneModeActiveException();

        checkNecessaryPermissions();

        Gson gson = new Gson();

        final String requestBody = gson.toJson(new MissedCallOTPRequest(phoneNumber));


        Request request = new Request.Builder()
                .url("https://api.motict.com/v1/calls")
                .addHeader("Authorization", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, JSON))
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onRequestMissedCallOTPFailed(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    final String body = response.body().string();

                    if (response.code() >= 400) {
                        callback.onRequestMissedCallOTPFailed(errorResponseToException(gson.fromJson(body, ErrorResponse.class)));
                        return;
                    }


                    final MissedCallOTPResponse missedCallOTPResponse = gson.fromJson(body, MissedCallOTPResponse.class);

                    if (missedCallReceiver != null)
                        context.unregisterReceiver(missedCallReceiver);

                    missedCallReceiver = new MissedCallReceiver(missedCallOTPResponse.getFullPhoneNumber(), otpMissedCallReceivedCallbacks);


                    context.registerReceiver(missedCallReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));

                    currentMissedCallOTPResponse = missedCallOTPResponse;

                    callback.onRequestMissedCallOTPSuccess(currentMissedCallOTPResponse);
                } catch (IOException e) {
                    callback.onRequestMissedCallOTPFailed(e);
                }
            }
        });
    }

    public void verifyMissedCallOTP(String code) throws Exception {
        if (currentMissedCallOTPResponse == null)
            throw new VerifyUnrequestedOTPException();


        if (!code.equals(currentMissedCallOTPResponse.getTokenFourDigit()) &&
                !code.equals(currentMissedCallOTPResponse.getTokenSixDigit()) &&
                !code.equals(currentMissedCallOTPResponse.getFullPhoneNumber()))
            throw new VerifyInvalidOTPCodeException();
    }


    public void finish() {
        if (missedCallReceiver != null) context.unregisterReceiver(missedCallReceiver);
    }


    public void addOtpMissedCallReceivedCallback(OTPMissedCallReceivedCallback callback) {
        this.otpMissedCallReceivedCallbacks.add(callback);
    }

    private void checkNecessaryPermissions() throws Exception {
        List<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED
        )
            permissions.add(Manifest.permission.READ_PHONE_STATE);

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALL_LOG
        ) != PackageManager.PERMISSION_GRANTED
        )
            permissions.add((Manifest.permission.READ_CALL_LOG));


        if (!permissions.isEmpty()) throw new RequiredPermissionDeniedException(permissions);

    }


    private boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        else
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;

    }

    private Exception errorResponseToException(ErrorResponse errorResponse) {
        switch (errorResponse.getErrorCode()) {
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
                return new ApiResponseException(errorResponse.getMessage());
        }
    }

    public static class Builder {
        private final Context context;
        private final List<OTPMissedCallReceivedCallback> otpMissedCallReceivedCallbacks = new ArrayList<>();
        private String apiKey;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder withApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }


        public Builder addOtpMissedCallReceivedCallback(OTPMissedCallReceivedCallback callback) {
            this.otpMissedCallReceivedCallbacks.add(callback);
            return this;
        }

        public MotictSDK build() {
            return new MotictSDK(context, apiKey, otpMissedCallReceivedCallbacks);
        }
    }


}
