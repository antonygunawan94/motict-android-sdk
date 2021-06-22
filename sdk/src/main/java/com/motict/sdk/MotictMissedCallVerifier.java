package com.motict.sdk;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import com.motict.sdk.api.RequestMissedCallOTPApi;
import com.motict.sdk.api.RequestMissedCallOTPCallback;
import com.motict.sdk.api.RequestMissedCallOTPResponse;
import com.motict.sdk.exception.AirplaneModeActiveException;
import com.motict.sdk.exception.InvalidPinCodeException;
import com.motict.sdk.exception.RequiredPermissionDeniedException;
import com.motict.sdk.listener.MissedCallVerificationFailedListener;
import com.motict.sdk.listener.MissedCallVerificationReceivedListener;
import com.motict.sdk.listener.MissedCallVerificationStartedListener;
import com.motict.sdk.listener.MissedCallVerificationSucceedListener;
import com.motict.sdk.model.MissedCallVerificationStart;
import com.motict.sdk.model.MissedCallVerificationSucceed;

import java.util.ArrayList;
import java.util.List;

public class MotictMissedCallVerifier implements RequestMissedCallOTPCallback {
    private final Context context;

    private final List<MissedCallVerificationStartedListener> missedCallVerificationStartedListeners;
    private final List<MissedCallVerificationReceivedListener> missedCallVerificationReceivedListeners;
    private final List<MissedCallVerificationSucceedListener> missedCallVerificationSucceedListeners;
    private final List<MissedCallVerificationFailedListener> missedCallVerificationFailedListeners;

    private final RequestMissedCallOTPApi requestMissedCallOTPApi;

    private String verifiedPhoneNumber;
    private MissedCallReceiver missedCallReceiver;
    private MissedCallVerificationStart missedCallVerificationStart;


    private MotictMissedCallVerifier(Context context,
                                     String apiKey,
                                     List<MissedCallVerificationStartedListener> missedCallVerificationStartedListeners,
                                     List<MissedCallVerificationReceivedListener> missedCallVerificationReceivedListeners,
                                     List<MissedCallVerificationSucceedListener> missedCallVerificationSucceedListeners,
                                     List<MissedCallVerificationFailedListener> missedCallVerificationFailedListeners) {


        this.context = context;
        this.missedCallVerificationStartedListeners = missedCallVerificationStartedListeners;
        this.missedCallVerificationReceivedListeners = missedCallVerificationReceivedListeners;
        this.missedCallVerificationSucceedListeners = missedCallVerificationSucceedListeners;
        this.missedCallVerificationFailedListeners = missedCallVerificationFailedListeners;
        requestMissedCallOTPApi = new RequestMissedCallOTPApi(apiKey, this);
    }


    public void startVerification(String phoneNumber) throws Exception {
        if (isAirplaneModeOn(context)) throw new AirplaneModeActiveException();

        checkNecessaryPermissions();
        

        requestMissedCallOTPApi.execute(phoneNumber);

        verifiedPhoneNumber = phoneNumber;
    }

    public void verifyPinCode(String pinCode) {
        if (pinCode.equals(missedCallVerificationStart.getReceivedFourPinCode()) ||
                pinCode.equals(missedCallVerificationStart.getReceivedSixPinCode()) ||
                pinCode.equals(missedCallVerificationStart.getReceivedPhoneNumber()))
            for (MissedCallVerificationSucceedListener listener :
                    missedCallVerificationSucceedListeners) {
                listener.onMissedCallVerificationSucceed(new MissedCallVerificationSucceed(verifiedPhoneNumber));
            }
        else
            for (MissedCallVerificationFailedListener listener :
                    missedCallVerificationFailedListeners) {
                listener.onMissedCallVerificationFailed(new InvalidPinCodeException());
            }
    }

    public void addMissedCallVerificationStartedListener(MissedCallVerificationStartedListener listener) {
        this.missedCallVerificationStartedListeners.add(listener);

    }

    public void addMissedCallVerificationReceivedListener(MissedCallVerificationReceivedListener listener) {
        this.missedCallVerificationReceivedListeners.add(listener);

    }

    public void addMissedCallVerificationSucceedListener(MissedCallVerificationSucceedListener listener) {
        this.missedCallVerificationSucceedListeners.add(listener);

    }

    public void addMissedCallVerificationFailedListener(MissedCallVerificationFailedListener listener) {
        this.missedCallVerificationFailedListeners.add(listener);

    }


    public void finish() {
        if (missedCallReceiver != null) context.unregisterReceiver(missedCallReceiver);
    }


    private void checkNecessaryPermissions() throws Exception {
        List<String> permissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ANSWER_PHONE_CALLS
            ) != PackageManager.PERMISSION_GRANTED
            )
                permissions.add(Manifest.permission.ANSWER_PHONE_CALLS);
        }

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED
        )
            permissions.add(Manifest.permission.CALL_PHONE);

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


    @Override
    public void onRequestMissedCallOTPSuccess(RequestMissedCallOTPResponse response) {
        if (missedCallReceiver != null)
            context.unregisterReceiver(missedCallReceiver);

        missedCallReceiver = new MissedCallReceiver(
                verifiedPhoneNumber,
                response.getFullPhoneNumber(),
                missedCallVerificationReceivedListeners,
                missedCallVerificationSucceedListeners,
                missedCallVerificationFailedListeners
        );

        context.registerReceiver(missedCallReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));

        missedCallVerificationStart = new MissedCallVerificationStart(
                verifiedPhoneNumber,
                response.getTokenFourDigit(),
                response.getTokenSixDigit(),
                response.getFullPhoneNumber()
        );

        for (MissedCallVerificationStartedListener listener : missedCallVerificationStartedListeners) {
            listener.onMissedCallVerificationStarted(missedCallVerificationStart);
        }
    }

    @Override
    public void onRequestMissedCallOTPFailed(Exception error) {
        for (MissedCallVerificationFailedListener listener : missedCallVerificationFailedListeners) {
            listener.onMissedCallVerificationFailed(error);
        }
    }

    public static class Builder {
        private final Context context;
        private final List<MissedCallVerificationStartedListener> missedCallVerificationStartedListeners = new ArrayList<>();
        private final List<MissedCallVerificationReceivedListener> missedCallVerificationReceivedListeners = new ArrayList<>();
        private final List<MissedCallVerificationSucceedListener> missedCallVerificationSucceedListeners = new ArrayList<>();
        private final List<MissedCallVerificationFailedListener> missedCallVerificationFailedListeners = new ArrayList<>();
        private String apiKey;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder withApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }


        public Builder addMissedCallVerificationStartedListener(MissedCallVerificationStartedListener listener) {
            this.missedCallVerificationStartedListeners.add(listener);
            return this;
        }

        public Builder addMissedCallVerificationReceivedListener(MissedCallVerificationReceivedListener listener) {
            this.missedCallVerificationReceivedListeners.add(listener);
            return this;
        }

        public Builder addMissedCallVerificationSucceedListener(MissedCallVerificationSucceedListener listener) {
            this.missedCallVerificationSucceedListeners.add(listener);
            return this;
        }

        public Builder addMissedCallVerificationFailedListener(MissedCallVerificationFailedListener listener) {
            this.missedCallVerificationFailedListeners.add(listener);
            return this;
        }

        public MotictMissedCallVerifier build() {
            return new MotictMissedCallVerifier(context, apiKey,
                    missedCallVerificationStartedListeners,
                    missedCallVerificationReceivedListeners,
                    missedCallVerificationSucceedListeners,
                    missedCallVerificationFailedListeners);
        }
    }


}
