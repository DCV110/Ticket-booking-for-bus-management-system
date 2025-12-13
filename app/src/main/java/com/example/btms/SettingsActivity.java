package com.example.btms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
        userEmail = sharedPreferences.getString("user_email", null);

        setupAccountSection();
        setupNotificationSettings();
        setupDisplaySettings();
        setupSupportSection();
        setupAppInfoSection();
        loadStatistics();

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNav(rootView, R.id.navSettings);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void setupAccountSection() {
        // Profile
        View llProfile = findViewById(R.id.llProfile);
        if (llProfile != null) {
            llProfile.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }

        // Change Password
        View llChangePassword = findViewById(R.id.llChangePassword);
        if (llChangePassword != null) {
            llChangePassword.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            });
        }

        // Logout
        View llLogout = findViewById(R.id.llLogout);
        if (llLogout != null) {
            llLogout.setOnClickListener(v -> showLogoutDialog());
        }
    }

    private void setupNotificationSettings() {
        // Load saved notification preferences
        boolean bookingNotification = sharedPreferences.getBoolean("notification_booking", true);
        boolean reminderNotification = sharedPreferences.getBoolean("notification_reminder", true);
        boolean promotionNotification = sharedPreferences.getBoolean("notification_promotion", true);
        boolean notificationSound = sharedPreferences.getBoolean("notification_sound", true);

        SwitchCompat switchBooking = findViewById(R.id.switchBookingNotification);
        SwitchCompat switchReminder = findViewById(R.id.switchReminderNotification);
        SwitchCompat switchPromotion = findViewById(R.id.switchPromotionNotification);
        SwitchCompat switchSound = findViewById(R.id.switchNotificationSound);

        if (switchBooking != null) {
            switchBooking.setChecked(bookingNotification);
            switchBooking.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sharedPreferences.edit().putBoolean("notification_booking", isChecked).apply();
            });
        }

        if (switchReminder != null) {
            switchReminder.setChecked(reminderNotification);
            switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sharedPreferences.edit().putBoolean("notification_reminder", isChecked).apply();
            });
        }

        if (switchPromotion != null) {
            switchPromotion.setChecked(promotionNotification);
            switchPromotion.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sharedPreferences.edit().putBoolean("notification_promotion", isChecked).apply();
            });
        }

        if (switchSound != null) {
            switchSound.setChecked(notificationSound);
            switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sharedPreferences.edit().putBoolean("notification_sound", isChecked).apply();
            });
        }
    }

    private void setupDisplaySettings() {
        // Load saved display preferences
        String language = sharedPreferences.getString("language", "vi");
        String timeFormat = sharedPreferences.getString("time_format", "24h");
        String dateFormat = sharedPreferences.getString("date_format", "dd/MM/yyyy");

        TextView tvLanguage = findViewById(R.id.tvLanguageValue);
        TextView tvTimeFormat = findViewById(R.id.tvTimeFormatValue);
        TextView tvDateFormat = findViewById(R.id.tvDateFormatValue);

        if (tvLanguage != null) {
            tvLanguage.setText(language.equals("vi") ? "Tiếng Việt" : "English");
        }

        if (tvTimeFormat != null) {
            tvTimeFormat.setText(timeFormat.equals("24h") ? "24 giờ" : "12 giờ (AM/PM)");
        }

        if (tvDateFormat != null) {
            tvDateFormat.setText(dateFormat);
        }

        // Language
        View llLanguage = findViewById(R.id.llLanguage);
        if (llLanguage != null) {
            llLanguage.setOnClickListener(v -> showLanguageDialog());
        }

        // Time Format
        View llTimeFormat = findViewById(R.id.llTimeFormat);
        if (llTimeFormat != null) {
            llTimeFormat.setOnClickListener(v -> showTimeFormatDialog());
        }

        // Date Format
        View llDateFormat = findViewById(R.id.llDateFormat);
        if (llDateFormat != null) {
            llDateFormat.setOnClickListener(v -> showDateFormatDialog());
        }
    }

    private void setupSupportSection() {
        // FAQ
        View llFAQ = findViewById(R.id.llFAQ);
        if (llFAQ != null) {
            llFAQ.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, FAQActivity.class);
                startActivity(intent);
            });
        }

        // Contact Support
        View llContact = findViewById(R.id.llContact);
        if (llContact != null) {
            llContact.setOnClickListener(v -> showContactDialog());
        }

        // Terms
        View llTerms = findViewById(R.id.llTerms);
        if (llTerms != null) {
            llTerms.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, TermsActivity.class);
                startActivity(intent);
            });
        }

        // Feedback
        View llFeedback = findViewById(R.id.llFeedback);
        if (llFeedback != null) {
            llFeedback.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, FeedbackActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupAppInfoSection() {
        // App Version
        TextView tvAppVersion = findViewById(R.id.tvAppVersion);
        if (tvAppVersion != null) {
            try {
                String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                tvAppVersion.setText(versionName);
            } catch (Exception e) {
                tvAppVersion.setText("1.0.0");
            }
        }

        // App Info
        View llAppInfo = findViewById(R.id.llAppInfo);
        if (llAppInfo != null) {
            llAppInfo.setOnClickListener(v -> showAppInfoDialog());
        }

        // Rate App
        View llRateApp = findViewById(R.id.llRateApp);
        if (llRateApp != null) {
            llRateApp.setOnClickListener(v -> {
                Toast.makeText(this, "Tính năng đánh giá ứng dụng sẽ được thêm sau", Toast.LENGTH_SHORT).show();
            });
        }

        // Share App
        View llShareApp = findViewById(R.id.llShareApp);
        if (llShareApp != null) {
            llShareApp.setOnClickListener(v -> shareApp());
        }
    }

    private void loadStatistics() {
        if (userEmail == null) return;

        android.database.Cursor cursor = dbHelper.getAllUserBookings(userEmail);
        int totalTrips = 0;
        double totalSpent = 0;
        int confirmedCount = 0;
        int cancelledCount = 0;

        if (cursor != null) {
            totalTrips = cursor.getCount();
            if (cursor.moveToFirst()) {
                do {
                    double fare = cursor.getDouble(cursor.getColumnIndexOrThrow("total_fare"));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    totalSpent += fare;
                    
                    if (status != null) {
                        if (status.equalsIgnoreCase("confirmed")) {
                            confirmedCount++;
                        } else if (status.equalsIgnoreCase("cancelled")) {
                            cancelledCount++;
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        TextView tvTotalTrips = findViewById(R.id.tvTotalTrips);
        TextView tvTotalSpent = findViewById(R.id.tvTotalSpent);

        if (tvTotalTrips != null) {
            tvTotalTrips.setText(String.valueOf(totalTrips));
        }

        if (tvTotalSpent != null) {
            tvTotalSpent.setText(String.format(Locale.getDefault(), "%.0f VNĐ", totalSpent));
        }

        // Booking History
        View llBookingHistory = findViewById(R.id.llBookingHistory);
        if (llBookingHistory != null) {
            llBookingHistory.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, BookingHistoryActivity.class);
                startActivity(intent);
            });
        }

        // Statistics
        View llStatistics = findViewById(R.id.llStatistics);
        if (llStatistics != null) {
            llStatistics.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, StatisticsActivity.class);
                startActivity(intent);
            });
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    sharedPreferences.edit()
                            .remove("user_email")
                            .remove("user_name")
                            .putBoolean("is_logged_in", false)
                            .apply();
                    
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showLanguageDialog() {
        String[] languages = {"Tiếng Việt", "English"};
        String currentLanguage = sharedPreferences.getString("language", "vi");
        int selectedIndex = currentLanguage.equals("vi") ? 0 : 1;

        new AlertDialog.Builder(this)
                .setTitle("Chọn ngôn ngữ")
                .setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
                    String language = which == 0 ? "vi" : "en";
                    sharedPreferences.edit().putString("language", language).apply();
                    TextView tvLanguage = findViewById(R.id.tvLanguageValue);
                    if (tvLanguage != null) {
                        tvLanguage.setText(languages[which]);
                    }
                    Toast.makeText(this, "Ngôn ngữ đã thay đổi. Vui lòng khởi động lại ứng dụng.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                })
                .show();
    }

    private void showTimeFormatDialog() {
        String[] formats = {"24 giờ", "12 giờ (AM/PM)"};
        String currentFormat = sharedPreferences.getString("time_format", "24h");
        int selectedIndex = currentFormat.equals("24h") ? 0 : 1;

        new AlertDialog.Builder(this)
                .setTitle("Định dạng thời gian")
                .setSingleChoiceItems(formats, selectedIndex, (dialog, which) -> {
                    String format = which == 0 ? "24h" : "12h";
                    sharedPreferences.edit().putString("time_format", format).apply();
                    TextView tvTimeFormat = findViewById(R.id.tvTimeFormatValue);
                    if (tvTimeFormat != null) {
                        tvTimeFormat.setText(formats[which]);
                    }
                    dialog.dismiss();
                })
                .show();
    }

    private void showDateFormatDialog() {
        String[] formats = {"dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd"};
        String currentFormat = sharedPreferences.getString("date_format", "dd/MM/yyyy");
        int selectedIndex = 0;
        for (int i = 0; i < formats.length; i++) {
            if (formats[i].equals(currentFormat)) {
                selectedIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Định dạng ngày")
                .setSingleChoiceItems(formats, selectedIndex, (dialog, which) -> {
                    String format = formats[which];
                    sharedPreferences.edit().putString("date_format", format).apply();
                    TextView tvDateFormat = findViewById(R.id.tvDateFormatValue);
                    if (tvDateFormat != null) {
                        tvDateFormat.setText(format);
                    }
                    dialog.dismiss();
                })
                .show();
    }

    private void showContactDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Liên hệ hỗ trợ")
                .setMessage("Hotline: 0877086400\nEmail: support@easybus.vn\nThời gian: 8:00 - 22:00 hàng ngày")
                .setPositiveButton("Gọi điện", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(android.net.Uri.parse("tel:0877086400"));
                    startActivity(intent);
                })
                .setNeutralButton("Gửi email", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@easybus.vn"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Yêu cầu hỗ trợ");
                    startActivity(Intent.createChooser(intent, "Chọn ứng dụng email"));
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void showAppInfoDialog() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            new AlertDialog.Builder(this)
                    .setTitle("Giới thiệu ứng dụng")
                    .setMessage("EASYBUS - Hệ thống đặt vé xe buýt\n\n" +
                            "Phiên bản: " + versionName + "\n\n" +
                            "Ứng dụng đặt vé xe buýt tiện lợi, nhanh chóng và an toàn.\n\n" +
                            "Tính năng:\n" +
                            "• Đặt vé dễ dàng\n" +
                            "• Chọn ghế theo sơ đồ\n" +
                            "• Thanh toán đa dạng\n" +
                            "• Quản lý chuyến đi\n" +
                            "• Thông báo thông minh\n\n" +
                            "Hotline: 0877086400")
                    .setPositiveButton("Đóng", null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Không thể lấy thông tin ứng dụng", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApp() {
        String shareText = "Tải ứng dụng EASYBUS - Đặt vé xe buýt tiện lợi!\n\n" +
                "Đặt vé nhanh chóng, chọn ghế dễ dàng, thanh toán an toàn.\n\n" +
                "Hotline: 0877086400\n\n" +
                "Tải ngay tại: [Link tải ứng dụng]";
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Ứng dụng EASYBUS");
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ ứng dụng"));
    }
}
