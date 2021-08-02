package com.motict.app.verifier;

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
import com.motict.app.verifier.state.VerifierFailed;
import com.motict.app.verifier.state.VerifierInitial;
import com.motict.app.verifier.state.VerifierReceived;
import com.motict.app.verifier.state.VerifierSucceed;
import com.motict.sdk.exception.PermissionDeniedException;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;


public class PinCodeVerifierFragment extends Fragment {
    private EditText edtPinCode;

    private CircularProgressButton btnNext;
    private Button btnCancel;

    private VerifierViewModel verifierViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pin_code_verifier, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        verifierViewModel = new ViewModelProvider(requireActivity()).get(VerifierViewModel.class);

        initView(view);
        initViewListener();
        initVerifierListener();
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

            verifierViewModel.verifyPinCode(edtPinCode.getText().toString());
        });

        btnCancel.setOnClickListener(view -> {
            verifierViewModel.cancel();
        });


        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                verifierViewModel.cancel();
            }
        });
    }

    private void initVerifierListener() {
        verifierViewModel.state().observe(getViewLifecycleOwner(), verifierState -> {
            if (verifierState instanceof VerifierInitial)
                Navigation.findNavController(requireActivity(), R.id.fragmentContainer)
                        .popBackStack();

            if (verifierState instanceof VerifierReceived)
                edtPinCode.setText(((VerifierReceived) verifierState).getReceived().getReceivedFourPinCode());

            if (verifierState instanceof VerifierSucceed)
                Navigation.findNavController(requireActivity(), R.id.fragmentContainer)
                        .navigate(R.id.action_pinCodeVerifierFragment_to_authenticatedFragment);

            if (verifierState instanceof VerifierFailed) {
                VerifierFailed verifierFailed = (VerifierFailed) verifierState;
                if (!(verifierFailed.getException() instanceof PermissionDeniedException))
                    Toast.makeText(requireActivity(), verifierFailed.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}