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
import androidx.navigation.Navigation;

import com.motict.app.R;
import com.motict.app.example_one.ExampleOneActivity;
import com.motict.sdk.exception.RequiredPermissionDeniedException;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class InputPhoneFragment extends Fragment {
    private EditText edtPhone;
    private Button btnCancel;
    private CircularProgressButton btnNext;

    private OtpViewModel otpViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_phone, container, false);
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
        edtPhone = view.findViewById(R.id.edtPhone);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnNext = view.findViewById(R.id.btnNext);
    }

    private void initViewListener() {
        btnCancel.setOnClickListener(view -> requireActivity().finish());
        btnNext.setOnClickListener(view -> {
            if (TextUtils.isEmpty(edtPhone.getText())) {
                Toast.makeText(getContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            otpViewModel.startVerification(edtPhone.getText().toString());
        });
    }


    private void initOtpListener() {
        otpViewModel.isVerificationStartedLoading().observe(requireActivity(), isRequesting -> {
            if (isRequesting) showLoading();
            else hideLoading();
        });


        otpViewModel.isVerificationStarted().observe(requireActivity(),
                isStarted -> {
                    if (isStarted)
                        Navigation.findNavController(requireActivity(), R.id.fragmentContainer)
                                .navigate(R.id.action_inputPhoneFragment_to_otpAuthenticatorFragment);
                }
        );

        otpViewModel.exception().observe(requireActivity(),
                exception -> {
                    if (exception != null) {
                        if (exception instanceof RequiredPermissionDeniedException) {
                            final RequiredPermissionDeniedException e = (RequiredPermissionDeniedException) exception;

                            if (getActivity() instanceof ExampleOneActivity) {
                                ((ExampleOneActivity) getActivity()).requestPermission(e.getDeniedPermissions());
                            }
                            return;
                        }

                        Toast.makeText(requireActivity(), exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showLoading() {
        btnNext.startAnimation();
    }

    private void hideLoading() {
        btnNext.revertAnimation();
    }
}