package com.example.btms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class CreateAccountActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private GoogleSignInHelper googleSignInHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        googleSignInHelper = new GoogleSignInHelper(this, dbHelper, sharedPreferences);

        TextInputEditText etName = findViewById(R.id.etName);
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnGoogleSignUp = findViewById(R.id.btnGoogleSignUp);

        btnSignUp.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email already exists
            if (dbHelper.emailExists(email)) {
                Toast.makeText(this, "Email already registered. Please login instead.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Register user
            long userId = dbHelper.registerUser(name, email, password);
            
            if (userId > 0) {
                // Get verification code
                String verificationCode = dbHelper.getVerificationCode(email);
                
                Intent intent = new Intent(CreateAccountActivity.this, VerificationActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("verification_code", verificationCode);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoogleSignUp.setOnClickListener(v -> {
            // Handle Google sign up
            googleSignInHelper.signIn();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == GoogleSignInHelper.getSignInRequestCode()) {
            if (googleSignInHelper.handleSignInResult(requestCode, data)) {
                Toast.makeText(this, "Signed up with Google successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
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

