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
            // User can login directly from here after account creation
            // Since users are auto-verified on registration, we can login them directly
            if (email != null) {
                // Verify user (even though already verified, this ensures consistency)
                if (verificationCode != null) {
                    dbHelper.verifyUser(email, verificationCode);
                }
                
                // Get user info from database
                android.content.ContentValues userInfo = dbHelper.getUserInfo(email);
                String userName = null;
                if (userInfo != null && userInfo.containsKey("name")) {
                    userName = userInfo.getAsString("name");
                }
                
                // Save user email and name to shared preferences and login
                SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_email", email);
                editor.putBoolean("is_logged_in", true);
                if (userName != null) {
                    editor.putString("user_name", userName);
                }
                editor.apply();

                Toast.makeText(this, "Login successful! Welcome!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VerificationActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // If email is missing, go to login screen
                Toast.makeText(this, "Please login with your email and password", Toast.LENGTH_SHORT).show();
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

