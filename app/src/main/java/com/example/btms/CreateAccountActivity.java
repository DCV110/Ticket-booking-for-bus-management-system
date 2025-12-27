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
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ email hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email already exists
            try {
                if (dbHelper.emailExists(email)) {
                    Toast.makeText(this, "Email đã được đăng ký. Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                android.util.Log.e("CreateAccountActivity", "Error checking email: " + e.getMessage(), e);
                // Continue with registration attempt
            }

            // Register user
            try {
                long userId = dbHelper.registerUser(name, email, password);
                
                if (userId > 0) {
                    // Save user name to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_email", email);
                    editor.putString("user_name", name);
                    editor.apply();
                    
                    // Get verification code (user is auto-verified, but we still show verification screen)
                    String verificationCode = null;
                    try {
                        verificationCode = dbHelper.getVerificationCode(email);
                    } catch (Exception e) {
                        android.util.Log.e("CreateAccountActivity", "Error getting verification code: " + e.getMessage(), e);
                        // Continue even if verification code is null
                    }
                    
                    // Go to verification screen where user can click "Sign in here" to login
                    Intent intent = new Intent(CreateAccountActivity.this, VerificationActivity.class);
                    intent.putExtra("email", email);
                    if (verificationCode != null) {
                        intent.putExtra("verification_code", verificationCode);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Đăng ký thất bại. Email có thể đã tồn tại.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                android.util.Log.e("CreateAccountActivity", "Error during registration: " + e.getMessage(), e);
                Toast.makeText(this, "Lỗi khi đăng ký: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

