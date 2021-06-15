package com.motict.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.motict.sdk.callback.OTPMissedCallReceivedCallback;

import java.util.List;

class MissedCallReceiver extends BroadcastReceiver {
    private final String listenedPhoneNumber;
    private final List<OTPMissedCallReceivedCallback> otpMissedCallReceivedCallbacks;

    private MissedCallPhoneStateListener missedCallPhoneStateListener;

    MissedCallReceiver(String listenedPhoneNumber, List<OTPMissedCallReceivedCallback> otpMissedCallReceivedCallbacks) {
        this.listenedPhoneNumber = listenedPhoneNumber;
        this.otpMissedCallReceivedCallbacks = otpMissedCallReceivedCallbacks;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (missedCallPhoneStateListener == null) {
            missedCallPhoneStateListener = new MissedCallPhoneStateListener(listenedPhoneNumber, otpMissedCallReceivedCallbacks);
            telephonyManager.listen(
                    missedCallPhoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE
            );
        }
    }

    private static class MissedCallPhoneStateListener extends PhoneStateListener {
        private final String listenedPhoneNumber;
        private final List<OTPMissedCallReceivedCallback> otpMissedCallReceivedCallbacks;

        MissedCallPhoneStateListener(String listenedPhoneNumber, List<OTPMissedCallReceivedCallback> otpMissedCallReceivedCallbacks) {
            this.listenedPhoneNumber = listenedPhoneNumber;
            this.otpMissedCallReceivedCallbacks = otpMissedCallReceivedCallbacks;
        }

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            if (!phoneNumber.equals(listenedPhoneNumber)) return;


            if (state == TelephonyManager.CALL_STATE_RINGING && !otpMissedCallReceivedCallbacks.isEmpty())
                for (OTPMissedCallReceivedCallback otpMissedCallReceivedCallback : otpMissedCallReceivedCallbacks) {
                    otpMissedCallReceivedCallback.onOTPMissedCallReceived(
                            phoneNumber,
                            phoneNumber.substring(phoneNumber.length() - 4),
                            phoneNumber.substring(phoneNumber.length() - 6)
                    );
                }
        }
    }
}