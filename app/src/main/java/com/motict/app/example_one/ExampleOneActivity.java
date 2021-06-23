package com.motict.app.example_one;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.motict.app.R;
import com.motict.app.verifier.VerifierViewModel;
import com.motict.sdk.MotictMissedCallVerifier;
import com.motict.sdk.exception.RequiredPermissionDeniedException;

import java.util.List;

public class ExampleOneActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private MotictMissedCallVerifier verifier;
    private VerifierViewModel verifierViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_one);

        getSupportActionBar().hide();


        verifier = new MotictMissedCallVerifier.Builder(this)
                .withApiKey("1y8Bhut35ANt4WUuIVoAaW5TlfMp0DhCMDZTQXbGUGy36jgNHj3Kr4ewQj1V2oUe")
                .addMissedCallVerificationStartedListener(start ->
                        runOnUiThread(() -> Toast.makeText(this,
                                String.format("Missed Call Verification Started: %s %s %s %s",
                                        start.getVerifiedPhoneNumber(),
                                        start.getToReceivedFourPinCode(),
                                        start.getToReceivedSixPinCode(),
                                        start.getToReceivedPhoneNumber()),
                                Toast.LENGTH_SHORT)
                                .show())
                )
                .addMissedCallVerificationReceivedListener(received ->
                        runOnUiThread(() -> Toast.makeText(this,
                                String.format("Missed Call Verification Received: %s %s %s",
                                        received.getReceivedFourPinCode(),
                                        received.getReceivedSixPinCode(),
                                        received.getReceivedPhoneNumber()), Toast.LENGTH_SHORT)
                                .show())

                )
                .addMissedCallVerificationSucceedListener(succeed ->
                        runOnUiThread(() -> Toast.makeText(this,
                                String.format("Missed Call Verification Succeed: %s",
                                        succeed.getVerifiedPhoneNumber()), Toast.LENGTH_SHORT)
                                .show())
                )
                .addMissedCallVerificationCancelledListener(cancelled ->
                        runOnUiThread(() -> Toast.makeText(this,
                                String.format("Missed Call Verification Cancelled: %s",
                                        cancelled.getCancelledVerifierPhoneNumber()), Toast.LENGTH_SHORT)
                                .show())
                )
                .addMissedCallVerificationFailedListener(failed ->
                        runOnUiThread(() -> {
                            if (failed instanceof RequiredPermissionDeniedException)
                                return;

                            Toast.makeText(this,
                                    String.format("Missed Call Verification Failed: %s",
                                            failed.toString()), Toast.LENGTH_SHORT)
                                    .show();
                        })
                )
                .build();


        initViewModel();
    }


    @Override
    protected void onDestroy() {
        verifier.clear();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            verifierViewModel.retryVerification();
        }
    }

    public void requestPermission(List<String> permissions) {
        ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
    }

    private void initViewModel() {
        verifierViewModel = new ViewModelProvider(this, new VerifierViewModel.Factory(verifier))
                .get(VerifierViewModel.class);
    }
}