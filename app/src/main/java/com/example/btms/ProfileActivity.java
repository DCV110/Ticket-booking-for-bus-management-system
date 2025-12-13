package com.example.btms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private String userEmail;
    private TextInputEditText etName, etEmail, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", null);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        Button btnSave = findViewById(R.id.btnSave);

        loadUserInfo();

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveUserInfo());
        }

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void loadUserInfo() {
        if (userEmail == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        android.content.ContentValues userInfo = dbHelper.getUserInfo(userEmail);
        if (userInfo != null) {
            if (etName != null && userInfo.containsKey("name")) {
                etName.setText(userInfo.getAsString("name"));
            }
            if (etEmail != null && userInfo.containsKey("email")) {
                etEmail.setText(userInfo.getAsString("email"));
            }
            if (etPhone != null && userInfo.containsKey("phone")) {
                String phone = userInfo.getAsString("phone");
                etPhone.setText(phone != null ? phone : "");
            }
        }
    }

    private void saveUserInfo() {
        if (userEmail == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName != null ? etName.getText().toString().trim() : "";
        String phone = etPhone != null ? etPhone.getText().toString().trim() : "";

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update in database using helper method
        boolean success = dbHelper.updateUserInfo(userEmail, name, phone);

        if (success) {
            // Update SharedPreferences
            sharedPreferences.edit().putString("user_name", name).apply();
            Toast.makeText(this, "Đã lưu thông tin thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra khi lưu thông tin", Toast.LENGTH_SHORT).show();
        }
    }
}

