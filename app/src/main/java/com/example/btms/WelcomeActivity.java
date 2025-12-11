package com.example.btms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);

        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });
    }
}




