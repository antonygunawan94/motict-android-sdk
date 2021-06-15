package com.motict.app.otp;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.motict.sdk.MotictSDK;
import com.motict.sdk.callback.RequestMissedCallOTPCallback;
import com.motict.sdk.exception.RequiredPermissionDeniedException;
import com.motict.sdk.model.MissedCallOTPResponse;

public class OtpViewModel extends ViewModel {
    private final MotictSDK sdk;

    private final MutableLiveData<Boolean> isRequestingMissedCallOTP = new MutableLiveData<>(false);
    private final MutableLiveData<Exception> exception = new MutableLiveData<>(null);
    private final MutableLiveData<MissedCallOTPResponse> missedCallOTPResponse = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> isAuthenticationSuccessful = new MutableLiveData<>(false);
    private String lastRequestedPhoneNumber;

    private OtpViewModel(MotictSDK sdk) {
        this.sdk = sdk;
    }

    public LiveData<Boolean> isRequestingMissedCallOTP() {
        return isRequestingMissedCallOTP;
    }

    public LiveData<Exception> exception() {
        return exception;
    }

    public LiveData<MissedCallOTPResponse> missedCallOTPResponse() {
        return missedCallOTPResponse;
    }

    public LiveData<Boolean> isAuthenticationSuccessful() {
        return isAuthenticationSuccessful;
    }

    public void requestMissedCallOTP(String phoneNumber) {
        isRequestingMissedCallOTP.postValue(true);
        try {
            sdk.requestMissedCallOTP(phoneNumber, new RequestMissedCallOTPCallback() {
                @Override
                public void onRequestMissedCallOTPSuccess(MissedCallOTPResponse response) {
                    isRequestingMissedCallOTP.postValue(false);
                    missedCallOTPResponse.postValue(response);
                }

                @Override
                public void onRequestMissedCallOTPFailed(Exception error) {
                    isRequestingMissedCallOTP.postValue(false);
                    exception.postValue(error);
                }
            });
        } catch (RequiredPermissionDeniedException e) {
            lastRequestedPhoneNumber = phoneNumber;
            isRequestingMissedCallOTP.postValue(false);
            exception.postValue(e);

        } catch (Exception e) {
            isRequestingMissedCallOTP.postValue(false);
            exception.postValue(e);
        }
    }

    public void retryMissedCallOTP() {
        isRequestingMissedCallOTP.postValue(true);
        try {
            sdk.requestMissedCallOTP(lastRequestedPhoneNumber, new RequestMissedCallOTPCallback() {
                @Override
                public void onRequestMissedCallOTPSuccess(MissedCallOTPResponse response) {
                    isRequestingMissedCallOTP.postValue(false);
                    missedCallOTPResponse.postValue(response);
                }

                @Override
                public void onRequestMissedCallOTPFailed(Exception error) {
                    isRequestingMissedCallOTP.postValue(false);
                    exception.postValue(error);
                }
            });
        } catch (RequiredPermissionDeniedException e) {
            isRequestingMissedCallOTP.postValue(false);
            exception.postValue(e);

        } catch (Exception e) {
            isRequestingMissedCallOTP.postValue(false);
            exception.postValue(e);
        }
    }

    public void verifyMissedCallOTP(String code) {
        try {
            sdk.verifyMissedCallOTP(code);
            isAuthenticationSuccessful.postValue(true);
        } catch (Exception e) {
            exception.postValue(e);
        }
    }

    public void reset() {
        isRequestingMissedCallOTP.postValue(false);
        exception.postValue(null);
        missedCallOTPResponse.postValue(null);
        isAuthenticationSuccessful.postValue(false);
    }


    public MotictSDK getSdk() {
        return sdk;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final MotictSDK sdk;

        public Factory(MotictSDK sdk) {
            this.sdk = sdk;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new OtpViewModel(sdk);
        }
    }
}
