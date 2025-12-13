package com.example.btms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class FeedbackActivity extends AppCompatActivity {

    private TextInputEditText etFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        etFeedback = findViewById(R.id.etFeedback);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> submitFeedback());
        }

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void submitFeedback() {
        String feedback = etFeedback != null ? etFeedback.getText().toString().trim() : "";

        if (feedback.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập phản hồi của bạn", Toast.LENGTH_SHORT).show();
            return;
        }

        // In a real app, this would send to a server
        // For now, we'll just send via email
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"feedback@easybus.vn"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Phản hồi từ ứng dụng EASYBUS");
        intent.putExtra(Intent.EXTRA_TEXT, feedback);
        
        try {
            startActivity(Intent.createChooser(intent, "Gửi phản hồi qua email"));
            Toast.makeText(this, "Cảm ơn bạn đã gửi phản hồi!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Không tìm thấy ứng dụng email", Toast.LENGTH_SHORT).show();
        }
    }
}

