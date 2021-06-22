package com.motict.app.otp;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.motict.sdk.MotictMissedCallVerifier;
import com.motict.sdk.exception.RequiredPermissionDeniedException;
import com.motict.sdk.model.MissedCallVerificationReceived;

public class OtpViewModel extends ViewModel {
    private final MotictMissedCallVerifier verifier;

    private final MutableLiveData<Boolean> isVerificationStartedLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isVerificationStarted = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isVerificationSucceed = new MutableLiveData<>(false);

    private final MutableLiveData<MissedCallVerificationReceived> missedCallVerificationReceived = new MutableLiveData<>(null);

    private final MutableLiveData<Exception> exception = new MutableLiveData<>(null);
    private String lastRequestedPhoneNumber;

    private OtpViewModel(MotictMissedCallVerifier verifier) {
        this.verifier = verifier;
        this.verifier.addMissedCallVerificationStartedListener(start -> {
            isVerificationStartedLoading.postValue(false);
            isVerificationStarted.postValue(true);
        });
        this.verifier.addMissedCallVerificationFailedListener(exception::postValue);
        this.verifier.addMissedCallVerificationReceivedListener(missedCallVerificationReceived::postValue);
        this.verifier.addMissedCallVerificationSucceedListener(succeed -> isVerificationSucceed.postValue(true));
    }

    public LiveData<Boolean> isVerificationStartedLoading() {
        return isVerificationStartedLoading;
    }

    public LiveData<Boolean> isVerificationStarted() {
        return isVerificationStarted;
    }

    public LiveData<Boolean> isVerificationSucceed() {
        return isVerificationSucceed;
    }

    public LiveData<MissedCallVerificationReceived> missedCallVerificationReceived() {
        return missedCallVerificationReceived;
    }

    public LiveData<Exception> exception() {
        return exception;
    }


    public void startVerification(String phoneNumber) {
        isVerificationStartedLoading.postValue(true);
        try {
            verifier.startVerification(phoneNumber);
        } catch (RequiredPermissionDeniedException e) {
            lastRequestedPhoneNumber = phoneNumber;
            isVerificationStartedLoading.postValue(false);
            exception.postValue(e);
        } catch (Exception e) {
            isVerificationStartedLoading.postValue(false);
            exception.postValue(e);
        }
    }

    public void retryVerification() {
        isVerificationStartedLoading.postValue(true);
        try {
            verifier.startVerification(lastRequestedPhoneNumber);
        } catch (RequiredPermissionDeniedException e) {
            isVerificationStartedLoading.postValue(false);
            exception.postValue(e);

        } catch (Exception e) {
            isVerificationStartedLoading.postValue(false);
            exception.postValue(e);
        }
    }


    public void verifyPinCode(String pinCode) {
        verifier.verifyPinCode(pinCode);
    }

    public void reset() {
        isVerificationStartedLoading.postValue(false);
        isVerificationStarted.postValue(false);
        isVerificationSucceed.postValue(false);
        exception.postValue(null);
    }


    public MotictMissedCallVerifier getVerifier() {
        return verifier;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final MotictMissedCallVerifier verifier;

        public Factory(MotictMissedCallVerifier verifier) {
            this.verifier = verifier;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new OtpViewModel(verifier);
        }
    }
}
