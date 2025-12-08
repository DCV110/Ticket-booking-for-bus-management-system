package com.example.btms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private GoogleSignInHelper googleSignInHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        googleSignInHelper = new GoogleSignInHelper(this, dbHelper, sharedPreferences);

        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> {
            try {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper == null) {
                    dbHelper = new DatabaseHelper(this);
                }

                // Validate login
                if (dbHelper.loginUser(email, password)) {
                    // Save user email to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_email", email);
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Check if email exists
                    if (dbHelper.emailExists(email)) {
                        Toast.makeText(this, "Invalid password or email not verified. Please check your email for verification.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Email not found. Please create an account first.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("LoginActivity", "Error during login: " + e.getMessage(), e);
                Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoogleSignIn.setOnClickListener(v -> {
            // Handle Google sign in
            googleSignInHelper.signIn();
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == GoogleSignInHelper.getSignInRequestCode()) {
            if (googleSignInHelper.handleSignInResult(requestCode, data)) {
                Toast.makeText(this, "Signed in with Google successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}

