package com.motict.sdk;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.motict.sdk.listener.MissedCallVerificationFailedListener;
import com.motict.sdk.listener.MissedCallVerificationReceivedListener;
import com.motict.sdk.listener.MissedCallVerificationRejectedListener;
import com.motict.sdk.listener.MissedCallVerificationRingingListener;
import com.motict.sdk.listener.MissedCallVerificationSucceedListener;
import com.motict.sdk.model.MissedCallVerificationReceived;
import com.motict.sdk.model.MissedCallVerificationSucceed;

import java.lang.reflect.Method;
import java.util.List;

class MissedCallReceiver extends BroadcastReceiver {
    private final String verifiedPhoneNumber;
    private final String listenedPhoneNumber;
    private final List<MissedCallVerificationRingingListener> missedCallVerificationRingingListeners;
    private final List<MissedCallVerificationReceivedListener> missedCallVerificationReceivedListeners;
    private final List<MissedCallVerificationSucceedListener> missedCallVerificationSucceedListeners;
    private final List<MissedCallVerificationRejectedListener> missedCallVerificationRejectedListeners;
    private final List<MissedCallVerificationFailedListener> missedCallVerificationFailedListeners;

    private MissedCallPhoneStateListener missedCallPhoneStateListener;


    MissedCallReceiver(
            String verifiedPhoneNumber,
            String listenedPhoneNumber,
            List<MissedCallVerificationRingingListener> missedCallVerificationRingingListeners,
            List<MissedCallVerificationReceivedListener> missedCallVerificationReceivedListeners,
            List<MissedCallVerificationSucceedListener> missedCallVerificationSucceedListeners,
            List<MissedCallVerificationRejectedListener> missedCallVerificationRejectedListeners,
            List<MissedCallVerificationFailedListener> missedCallVerificationFailedListeners) {
        this.verifiedPhoneNumber = verifiedPhoneNumber;
        this.listenedPhoneNumber = listenedPhoneNumber;
        this.missedCallVerificationRingingListeners = missedCallVerificationRingingListeners;
        this.missedCallVerificationReceivedListeners = missedCallVerificationReceivedListeners;
        this.missedCallVerificationSucceedListeners = missedCallVerificationSucceedListeners;
        this.missedCallVerificationRejectedListeners = missedCallVerificationRejectedListeners;
        this.missedCallVerificationFailedListeners = missedCallVerificationFailedListeners;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (missedCallPhoneStateListener == null) {
            missedCallPhoneStateListener = new MissedCallPhoneStateListener(
                    context,
                    verifiedPhoneNumber,
                    listenedPhoneNumber,
                    missedCallVerificationRingingListeners,
                    missedCallVerificationReceivedListeners,
                    missedCallVerificationSucceedListeners,
                    missedCallVerificationRejectedListeners,
                    missedCallVerificationFailedListeners);
            telephonyManager.listen(
                    missedCallPhoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE
            );
        }
    }

    private static class MissedCallPhoneStateListener extends PhoneStateListener {
        private final Context context;
        private final String verifiedPhoneNumber;
        private final String listenedPhoneNumber;
        private final List<MissedCallVerificationRingingListener> missedCallVerificationRingingListeners;
        private final List<MissedCallVerificationReceivedListener> missedCallVerificationReceivedListeners;
        private final List<MissedCallVerificationSucceedListener> missedCallVerificationSucceedListeners;
        private final List<MissedCallVerificationRejectedListener> missedCallVerificationRejectedListeners;
        private final List<MissedCallVerificationFailedListener> missedCallVerificationFailedListeners;

        private boolean isRinging = false;
        private boolean isTerminatedBySystem = false;


        MissedCallPhoneStateListener(
                Context context,
                String verifiedPhoneNumber,
                String listenedPhoneNumber,
                List<MissedCallVerificationRingingListener> missedCallVerificationRingingListeners,
                List<MissedCallVerificationReceivedListener> missedCallVerificationReceivedListeners,
                List<MissedCallVerificationSucceedListener> missedCallVerificationSucceedListeners,
                List<MissedCallVerificationRejectedListener> missedCallVerificationRejectedListeners,
                List<MissedCallVerificationFailedListener> missedCallVerificationFailedListeners) {
            this.context = context;
            this.verifiedPhoneNumber = verifiedPhoneNumber;
            this.listenedPhoneNumber = listenedPhoneNumber;
            this.missedCallVerificationRingingListeners = missedCallVerificationRingingListeners;
            this.missedCallVerificationReceivedListeners = missedCallVerificationReceivedListeners;
            this.missedCallVerificationSucceedListeners = missedCallVerificationSucceedListeners;
            this.missedCallVerificationRejectedListeners = missedCallVerificationRejectedListeners;
            this.missedCallVerificationFailedListeners = missedCallVerificationFailedListeners;
        }


        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            if (!phoneNumber.replaceAll("\\+", "")
                    .equals(listenedPhoneNumber.replaceAll("\\+", ""))) return;

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    handlePhoneStateRinging(phoneNumber);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    handlePhoneStateIdle();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
            }

        }

        private void handlePhoneStateRinging(String phoneNumber) {
            isRinging = true;

            if (!missedCallVerificationRingingListeners.isEmpty())
                for (MissedCallVerificationRingingListener listener : missedCallVerificationRingingListeners) {
                    listener.onMissedCallVerificationRinging(
                            phoneNumber
                    );
                }

            try {
                endCall();
            } catch (Exception exception) {
                for (MissedCallVerificationFailedListener listener : missedCallVerificationFailedListeners) {
                    listener.onMissedCallVerificationFailed(exception);
                }
                return;
            }


            if (!missedCallVerificationReceivedListeners.isEmpty())
                for (MissedCallVerificationReceivedListener listener : missedCallVerificationReceivedListeners) {
                    listener.onMissedCallVerificationReceived(
                            new MissedCallVerificationReceived(
                                    phoneNumber.substring(phoneNumber.length() - 4),
                                    phoneNumber.substring(phoneNumber.length() - 6),
                                    phoneNumber
                            )
                    );
                }


            if (!missedCallVerificationSucceedListeners.isEmpty())
                for (MissedCallVerificationSucceedListener listener : missedCallVerificationSucceedListeners) {
                    listener.onMissedCallVerificationSucceed(
                            new MissedCallVerificationSucceed(verifiedPhoneNumber)
                    );
                }
        }

        private void handlePhoneStateIdle() {
            if (isRinging && !isTerminatedBySystem)
                for (MissedCallVerificationRejectedListener listener : missedCallVerificationRejectedListeners) {
                    listener.onMissedCallVerificationRejected();
                }
        }

        private void endCall() throws Exception {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

                if (tm != null) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    tm.endCall();
                }
            } else {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);

                Class c = Class.forName(tm.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                Object telephonyService = m.invoke(tm);
                c = Class.forName(telephonyService.getClass().getName());
                m = c.getDeclaredMethod("endCall");
                m.setAccessible(true);
                m.invoke(telephonyService);
            }

            isTerminatedBySystem = true;
        }
    }
}