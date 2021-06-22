package com.motict.app.otp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.motict.app.R;
import com.motict.sdk.exception.RequiredPermissionDeniedException;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;


public class OtpAuthenticatorFragment extends Fragment {
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

            otpViewModel.verifyPinCode(edtPinCode.getText().toString());
        });

        btnCancel.setOnClickListener(view -> {
            otpViewModel.reset();
            Navigation.findNavController(requireActivity(), R.id.fragmentContainer).navigateUp();
        });


        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                otpViewModel.reset();
                Navigation.findNavController(requireActivity(), R.id.fragmentContainer).navigateUp();
            }
        });
    }

    private void initOtpListener() {
        otpViewModel.missedCallVerificationReceived().observe(requireActivity(), received -> {
            if (received != null)
                edtPinCode.setText(received.getReceivedFourPinCode());
        });
        otpViewModel.isVerificationSucceed().observe(requireActivity(), isSuccess -> {
            if (isSuccess)
                Navigation.findNavController(requireActivity(), R.id.fragmentContainer)
                        .navigate(R.id.action_otpAuthenticatorFragment_to_otpAuthenticatedFragment);
        });
        otpViewModel.exception().observe(requireActivity(), error -> {
            if (error != null && !(error instanceof RequiredPermissionDeniedException))
                Toast.makeText(requireActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
    }
}