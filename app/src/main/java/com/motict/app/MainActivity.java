package com.motict.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.motict.app.example_one.ExampleOneActivity;


public class MainActivity extends AppCompatActivity {

    Button btnExampleOne;
    Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
        initView();
        initViewListener();
    }

    private void initView() {
        btnExampleOne = findViewById(R.id.btnExampleOne);
        btnExit = findViewById(R.id.btnFinish);
    }

    private void initViewListener() {
        btnExampleOne.setOnClickListener(view ->
                startActivity(new Intent(this, ExampleOneActivity.class)));
        btnExit.setOnClickListener(view -> finish());
    }
}
