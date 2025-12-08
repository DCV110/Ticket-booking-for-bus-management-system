package com.example.btms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VerificationActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String email;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        dbHelper = new DatabaseHelper(this);
        
        // Get email and verification code from intent
        email = getIntent().getStringExtra("email");
        verificationCode = getIntent().getStringExtra("verification_code");

        Button btnSignInHere = findViewById(R.id.btnSignInHere);

        btnSignInHere.setOnClickListener(v -> {
            // For demo, auto-verify the user
            // In production, user would enter verification code from email
            if (email != null && verificationCode != null) {
                if (dbHelper.verifyUser(email, verificationCode)) {
                    SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_email", email);
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();

                    Toast.makeText(this, "Email verified successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerificationActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Verification failed. Please try again.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}

