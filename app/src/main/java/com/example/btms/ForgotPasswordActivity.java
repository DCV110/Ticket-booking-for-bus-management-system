package com.example.btms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String userEmail;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        dbHelper = new DatabaseHelper(this);

        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etVerificationCode = findViewById(R.id.etVerificationCode);
        TextInputEditText etNewPassword = findViewById(R.id.etNewPassword);
        TextInputEditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnSendCode = findViewById(R.id.btnSendCode);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Initially hide reset password section
        findViewById(R.id.tvEnterCode).setVisibility(View.GONE);
        findViewById(R.id.tilVerificationCode).setVisibility(View.GONE);
        findViewById(R.id.tilNewPassword).setVisibility(View.GONE);
        findViewById(R.id.tilConfirmPassword).setVisibility(View.GONE);
        btnResetPassword.setVisibility(View.GONE);

        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!dbHelper.emailExists(email)) {
                Toast.makeText(this, "Email not found. Please check your email address.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get verification code (in real app, send via email)
            verificationCode = dbHelper.getVerificationCode(email);
            userEmail = email;
            
            // Show reset password section
            findViewById(R.id.tvEnterCode).setVisibility(View.VISIBLE);
            findViewById(R.id.tilVerificationCode).setVisibility(View.VISIBLE);
            findViewById(R.id.tilNewPassword).setVisibility(View.VISIBLE);
            findViewById(R.id.tilConfirmPassword).setVisibility(View.VISIBLE);
            btnResetPassword.setVisibility(View.VISIBLE);
            
            Toast.makeText(this, "Verification code sent to your email: " + verificationCode, Toast.LENGTH_LONG).show();
        });

        btnResetPassword.setOnClickListener(v -> {
            String code = etVerificationCode.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            
            if (code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!code.equals(verificationCode)) {
                Toast.makeText(this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Reset password
            if (dbHelper.resetPassword(userEmail, newPassword)) {
                Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show();
            }
        });

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
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




