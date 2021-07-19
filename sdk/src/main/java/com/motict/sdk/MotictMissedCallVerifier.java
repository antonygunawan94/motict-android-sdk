package com.motict.sdk;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.motict.sdk.api.LogEventApi;
import com.motict.sdk.api.RequestMissedCallOTPApi;
import com.motict.sdk.api.RequestMissedCallOTPCallback;
import com.motict.sdk.api.RequestMissedCallOTPResponse;
import com.motict.sdk.exception.AirplaneModeActiveException;
import com.motict.sdk.exception.InvalidPinCodeException;
import com.motict.sdk.exception.PinCodeVerificationNotStartedException;
import com.motict.sdk.exception.RequiredPermissionDeniedException;
import com.motict.sdk.listener.MissedCallVerificationCancelledListener;
import com.motict.sdk.listener.MissedCallVerificationFailedListener;
import com.motict.sdk.listener.MissedCallVerificationReceivedListener;
import com.motict.sdk.listener.MissedCallVerificationRejectedListener;
import com.motict.sdk.listener.MissedCallVerificationRingingListener;
import com.motict.sdk.listener.MissedCallVerificationStartedListener;
import com.motict.sdk.listener.MissedCallVerificationSucceedListener;
import com.motict.sdk.model.MissedCallVerificationCancelled;
import com.motict.sdk.model.MissedCallVerificationStart;
import com.motict.sdk.model.MissedCallVerificationSucceed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MotictMissedCallVerifier implements RequestMissedCallOTPCallback {
    private final Context context;

    private final List<MissedCallVerificationRingingListener> missedCallVerificationRingingListeners = new ArrayList<>();
    private final List<MissedCallVerificationStartedListener> missedCallVerificationStartedListeners = new ArrayList<>();
    private final List<MissedCallVerificationReceivedListener> missedCallVerificationReceivedListeners = new ArrayList<>();
    private final List<MissedCallVerificationSucceedListener> missedCallVerificationSucceedListeners = new ArrayList<>();
    private final List<MissedCallVerificationCancelledListener> missedCallVerificationCancelledListeners = new ArrayList<>();
    private final List<MissedCallVerificationRejectedListener> missedCallVerificationRejectedListeners = new ArrayList<>();
    private final List<MissedCallVerificationFailedListener> missedCallVerificationFailedListeners = new ArrayList<>();
    private final RequestMissedCallOTPApi requestMissedCallOTPApi;
    private final LogEventApi logEventApi;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private PackageInfo packageInfo;
    private String verifiedPhoneNumber;
    private MissedCallReceiver missedCallReceiver;
    private MissedCallVerificationStart missedCallVerificationStart;


    private MotictMissedCallVerifier(Context context,
                                     String apiKey,
                                     List<MissedCallVerificationStartedListener> missedCallVerificationStartedListeners,
                                     List<MissedCallVerificationReceivedListener> missedCallVerificationReceivedListeners,
                                     List<MissedCallVerificationSucceedListener> missedCallVerificationSucceedListeners,
                                     List<MissedCallVerificationCancelledListener> missedCallVerificationCancelledListeners,
                                     List<MissedCallVerificationFailedListener> missedCallVerificationFailedListeners) {
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MOTICT_SDK", e.toString());
        }


        requestMissedCallOTPApi = new RequestMissedCallOTPApi(apiKey, this);
        logEventApi = new LogEventApi(apiKey);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        this.context = context;

        this.missedCallVerificationRingingListeners.add(start -> logEvent("ringing"));
        this.missedCallVerificationStartedListeners.add(start -> logEvent("started"));
        this.missedCallVerificationReceivedListeners.add(received -> logEvent("received"));
        this.missedCallVerificationSucceedListeners.add(succeed -> logEvent("succeed"));
        this.missedCallVerificationCancelledListeners.add(cancelled -> logEvent("cancelled"));
        this.missedCallVerificationRejectedListeners.add(() -> logEvent("rejected"));
        this.missedCallVerificationFailedListeners.add(failed -> logEvent("failed"));


        this.missedCallVerificationStartedListeners.addAll(missedCallVerificationStartedListeners);
        this.missedCallVerificationReceivedListeners.addAll(missedCallVerificationReceivedListeners);
        this.missedCallVerificationSucceedListeners.addAll(missedCallVerificationSucceedListeners);
        this.missedCallVerificationCancelledListeners.addAll(missedCallVerificationCancelledListeners);
        this.missedCallVerificationFailedListeners.addAll(missedCallVerificationFailedListeners);
    }


    public void startVerification(String phoneNumber) throws Exception {
        if (isAirplaneModeOn(context)) throw new AirplaneModeActiveException();

        checkNecessaryPermissions();


        requestMissedCallOTPApi.execute(phoneNumber);

        verifiedPhoneNumber = phoneNumber;
    }

    public void cancelVerification() {
        if (missedCallReceiver != null)
            context.unregisterReceiver(missedCallReceiver);

        for (MissedCallVerificationCancelledListener listener :
                missedCallVerificationCancelledListeners) {
            listener.onMissedCallVerificationCancelled(new MissedCallVerificationCancelled(verifiedPhoneNumber));
        }

        verifiedPhoneNumber = "";
        missedCallReceiver = null;
        missedCallVerificationStart = null;
    }

    public void verifyPinCode(String pinCode) {
        if (missedCallVerificationStart == null) {
            for (MissedCallVerificationFailedListener listener :
                    missedCallVerificationFailedListeners) {
                listener.onMissedCallVerificationFailed(new PinCodeVerificationNotStartedException());
            }

            return;
        }

        if (pinCode.equals(missedCallVerificationStart.getToReceivedFourPinCode()) ||
                pinCode.equals(missedCallVerificationStart.getToReceivedSixPinCode()) ||
                pinCode.equals(missedCallVerificationStart.getToReceivedPhoneNumber()))
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

    public void addMissedCallVerificationCancelledListener(MissedCallVerificationCancelledListener listener) {
        this.missedCallVerificationCancelledListeners.add(listener);
    }


    public void addMissedCallVerificationFailedListener(MissedCallVerificationFailedListener listener) {
        this.missedCallVerificationFailedListeners.add(listener);
    }


    public void clear() {
        if (missedCallReceiver != null) context.unregisterReceiver(missedCallReceiver);
    }


    private void logEvent(String eventName) {
        if (missedCallVerificationStart == null) return;

        final String versionName = packageInfo == null ? "" : packageInfo.versionName;
        final String packageName = packageInfo == null ? "" : packageInfo.packageName;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            logEventApi.execute(
                    missedCallVerificationStart.getId(),
                    String.valueOf(Build.VERSION.SDK_INT),
                    versionName,
                    packageName,
                    eventName,
                    new Date(),
                    "",
                    "");
            return;
        }


        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null)
                        logEventApi.execute(
                                missedCallVerificationStart.getId(),
                                String.valueOf(Build.VERSION.SDK_INT),
                                versionName,
                                packageName,
                                eventName,
                                new Date(),
                                String.valueOf(location.getLatitude()),
                                String.valueOf(location.getLongitude()));
                    else
                        logEventApi.execute(
                                missedCallVerificationStart.getId(),
                                String.valueOf(Build.VERSION.SDK_INT),
                                versionName,
                                packageName,
                                eventName,
                                new Date(),
                                "",
                                "");
                })
                .addOnFailureListener(runnable ->
                        logEventApi.execute(
                                missedCallVerificationStart.getId(),
                                String.valueOf(Build.VERSION.SDK_INT),
                                versionName,
                                packageName,
                                eventName,
                                new Date(),
                                "",
                                ""));
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

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        )
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        )
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);


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
                missedCallVerificationRingingListeners,
                missedCallVerificationReceivedListeners,
                missedCallVerificationSucceedListeners,
                missedCallVerificationRejectedListeners,
                missedCallVerificationFailedListeners
        );

        context.registerReceiver(missedCallReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));

        missedCallVerificationStart = new MissedCallVerificationStart(
                response.getId(),
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
        private final List<MissedCallVerificationCancelledListener> missedCallVerificationCancelledListeners = new ArrayList<>();
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

        public Builder addMissedCallVerificationCancelledListener(MissedCallVerificationCancelledListener listener) {
            this.missedCallVerificationCancelledListeners.add(listener);
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
                    missedCallVerificationCancelledListeners,
                    missedCallVerificationFailedListeners);
        }
    }

}
