package com.motict.app.example_one;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.motict.app.R;
import com.motict.app.otp.OtpViewModel;
import com.motict.sdk.MotictSDK;

import java.util.List;

public class ExampleOneActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private MotictSDK sdk;
    private OtpViewModel otpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_one);

        getSupportActionBar().hide();


        sdk = new MotictSDK.Builder(this)
                .withApiKey("1y8Bhut35ANt4WUuIVoAaW5TlfMp0DhCMDZTQXbGUGy36jgNHj3Kr4ewQj1V2oUe")
                .addOtpMissedCallReceivedCallback((fullPhoneNumber, tokenFourDigit, tokenSixDigit) ->
                        Toast.makeText(this,
                                String.format(
                                        "OTP Missed Call Received %s %s %s",
                                        fullPhoneNumber, tokenFourDigit, tokenSixDigit),
                                Toast.LENGTH_LONG
                        ).show()
                )
                .build();


        initViewModel();
    }


    @Override
    protected void onDestroy() {
        sdk.finish();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            otpViewModel.retryMissedCallOTP();
        }
    }

    public void requestPermission(List<String> permissions) {
        ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
    }

    private void initViewModel() {
        otpViewModel = new ViewModelProvider(this, new OtpViewModel.Factory(sdk))
                .get(OtpViewModel.class);
    }
}