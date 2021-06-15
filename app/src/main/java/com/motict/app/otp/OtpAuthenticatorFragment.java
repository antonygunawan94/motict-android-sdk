package com.motict.app.otp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.motict.app.R;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;


public class OtpAuthenticatorFragment extends Fragment {
    private NavController navController;

    private EditText edtPinCode;

    private CircularProgressButton btnNext;
    private Button btnCancel;

    private OtpViewModel otpViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_otp_authenticator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        otpViewModel = new ViewModelProvider(requireActivity()).get(OtpViewModel.class);

        initView(view);
        initViewListener();
        initOtpListener();

    }

    private void initView(View view) {
        navController = Navigation.findNavController(view);

        edtPinCode = view.findViewById(R.id.edtPinCode);
        btnNext = view.findViewById(R.id.btnNext);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void initViewListener() {
        btnNext.setOnClickListener(view -> {
            if (TextUtils.isEmpty(edtPinCode.getText())) {
                Toast.makeText(requireActivity(), "Please enter your otp code", Toast.LENGTH_SHORT).show();
                return;
            }

            otpViewModel.verifyMissedCallOTP(edtPinCode.getText().toString());
        });

        btnCancel.setOnClickListener(view -> {
            otpViewModel.reset();
            navController.navigateUp();
        });

        otpViewModel.getSdk().addOtpMissedCallReceivedCallback((fullPhoneNumber, tokenFourDigit, tokenSixDigit) -> {
//            edtPinCode.setText(fullPhoneNumber);
//            edtPinCode.setText(tokenFourDigit);
            edtPinCode.setText(tokenSixDigit);
        });
    }

    private void initOtpListener() {
        otpViewModel.isAuthenticationSuccessful().observe(requireActivity(), isSuccess -> {
            if (isSuccess)
                navController.navigate(R.id.action_otpAuthenticatorFragment_to_otpAuthenticatedFragment);
        });
        otpViewModel.exception().observe(requireActivity(), error -> {
            if (error != null)
                Toast.makeText(requireActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
    }
}