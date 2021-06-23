package com.motict.app.verifier;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.motict.app.verifier.state.VerifierFailed;
import com.motict.app.verifier.state.VerifierInitial;
import com.motict.app.verifier.state.VerifierLoading;
import com.motict.app.verifier.state.VerifierReceived;
import com.motict.app.verifier.state.VerifierStarted;
import com.motict.app.verifier.state.VerifierState;
import com.motict.app.verifier.state.VerifierSucceed;
import com.motict.sdk.MotictMissedCallVerifier;
import com.motict.sdk.exception.RequiredPermissionDeniedException;

public class VerifierViewModel extends ViewModel {
    private final MotictMissedCallVerifier verifier;


    private final MutableLiveData<VerifierState> state = new MutableLiveData<>();

    private String lastRequestedPhoneNumber;

    public VerifierViewModel(MotictMissedCallVerifier verifier) {
        this.verifier = verifier;
        this.verifier.addMissedCallVerificationStartedListener(start -> {
            state.postValue(new VerifierStarted());
        });
        this.verifier.addMissedCallVerificationFailedListener(error -> state.postValue(new VerifierFailed(error)));
        this.verifier.addMissedCallVerificationReceivedListener(received -> state.postValue(new VerifierReceived(received)));
        this.verifier.addMissedCallVerificationSucceedListener(succeed -> state.postValue(new VerifierSucceed()));
    }

    public LiveData<VerifierState> state() {
        return state;
    }

    public void startVerification(String phoneNumber) {
        state.postValue(new VerifierLoading());
        try {
            verifier.startVerification(phoneNumber);
        } catch (RequiredPermissionDeniedException e) {
            lastRequestedPhoneNumber = phoneNumber;
            state.postValue(new VerifierFailed(e));
        } catch (Exception e) {
            state.postValue(new VerifierFailed(e));
        }
    }

    public void retryVerification() {
        state.postValue(new VerifierLoading());
        try {
            verifier.startVerification(lastRequestedPhoneNumber);
        } catch (RequiredPermissionDeniedException e) {
            state.postValue(new VerifierFailed(e));
        } catch (Exception e) {
            state.postValue(new VerifierFailed(e));
        }
    }


    public void verifyPinCode(String pinCode) {
        verifier.verifyPinCode(pinCode);
    }

    public void cancel() {
        verifier.cancelVerification();
        state.postValue(new VerifierInitial());
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
            return (T) new VerifierViewModel(verifier);
        }
    }
}
