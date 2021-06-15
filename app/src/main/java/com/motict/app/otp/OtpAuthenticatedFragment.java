package com.motict.app.otp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.motict.app.R;

public class OtpAuthenticatedFragment extends Fragment {

    private Button btnFinish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_otp_authenticated, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initViewListener();
    }

    private void initView(View view) {
        btnFinish = view.findViewById(R.id.btnFinish);
    }

    private void initViewListener() {
        btnFinish.setOnClickListener(view -> requireActivity().finish());
    }
}