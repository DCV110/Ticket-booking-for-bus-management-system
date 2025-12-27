package com.example.btms;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingHistoryActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String userEmail;
    private String currentFilter = "all"; // all, confirmed, cancelled

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        dbHelper = new DatabaseHelper(this);
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", null);

        setupFilterTabs();
        loadBookingHistory(currentFilter);

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void setupFilterTabs() {
        Button btnAll = findViewById(R.id.btnAllHistory);
        Button btnConfirmed = findViewById(R.id.btnConfirmed);
        Button btnCancelled = findViewById(R.id.btnCancelled);

        if (btnAll != null) {
            btnAll.setOnClickListener(v -> {
                currentFilter = "all";
                updateFilterButtons(btnAll, btnConfirmed, btnCancelled);
                loadBookingHistory(currentFilter);
            });
        }

        if (btnConfirmed != null) {
            btnConfirmed.setOnClickListener(v -> {
                currentFilter = "confirmed";
                updateFilterButtons(btnConfirmed, btnAll, btnCancelled);
                loadBookingHistory(currentFilter);
            });
        }

        if (btnCancelled != null) {
            btnCancelled.setOnClickListener(v -> {
                currentFilter = "cancelled";
                updateFilterButtons(btnCancelled, btnAll, btnConfirmed);
                loadBookingHistory(currentFilter);
            });
        }
    }

    private void updateFilterButtons(Button activeBtn, Button... otherBtns) {
        activeBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.primary)));
        activeBtn.setTextColor(android.graphics.Color.WHITE);

        for (Button btn : otherBtns) {
            if (btn != null) {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
                btn.setTextColor(getColor(R.color.text_secondary));
            }
        }
    }

    private void loadBookingHistory(String filter) {
        if (userEmail == null) return;

        LinearLayout container = findViewById(R.id.llBookingsContainer);
        if (container == null) return;

        container.removeAllViews();

        // Get all bookings (including cancelled)
        Cursor cursor = dbHelper.getAllUserBookings(userEmail);
        
        // Filter by status if needed
        if (!filter.equals("all") && cursor != null) {
            // We'll filter in memory since getAllUserBookings returns all
        }

        if (cursor != null && cursor.moveToFirst()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            do {
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                
                // Apply filter
                if (!filter.equals("all")) {
                    if (filter.equals("confirmed") && !status.equalsIgnoreCase("confirmed")) {
                        continue;
                    }
                    if (filter.equals("cancelled") && !status.equalsIgnoreCase("cancelled")) {
                        continue;
                    }
                }
                
                View card = inflater.inflate(R.layout.item_booking_card, container, false);

                long bookingId = cursor.getLong(cursor.getColumnIndexOrThrow("booking_id"));
                String companyName = cursor.getString(cursor.getColumnIndexOrThrow("company_name"));
                String fromLocation = cursor.getString(cursor.getColumnIndexOrThrow("from_location"));
                String toLocation = cursor.getString(cursor.getColumnIndexOrThrow("to_location"));
                String departureTime = cursor.getString(cursor.getColumnIndexOrThrow("departure_time"));
                String scheduleDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String seatNumbers = cursor.getString(cursor.getColumnIndexOrThrow("seat_numbers"));

                TextView tvTerminalName = card.findViewById(R.id.tvTerminalName);
                TextView tvFromLocation = card.findViewById(R.id.tvFromLocation);
                TextView tvToLocation = card.findViewById(R.id.tvToLocation);
                TextView tvDepartureTime = card.findViewById(R.id.tvDepartureTime);
                TextView tvSeatNumbers = card.findViewById(R.id.tvSeatNumbers);
                TextView tvStatus = card.findViewById(R.id.tvStatus);

                if (tvTerminalName != null) {
                    tvTerminalName.setText(companyName != null ? companyName : "Hãng xe");
                }
                if (tvFromLocation != null) {
                    tvFromLocation.setText(fromLocation);
                }
                if (tvToLocation != null) {
                    tvToLocation.setText(toLocation);
                }
                if (tvDepartureTime != null) {
                    String timeStr = DateTimeHelper.formatTime(departureTime);
                    String dayStr = DateTimeHelper.getDayOfWeek(scheduleDate);
                    String dateStr = DateTimeHelper.formatDate(scheduleDate);
                    tvDepartureTime.setText(timeStr + ", " + dayStr + "\n" + dateStr);
                }
                if (tvSeatNumbers != null) {
                    tvSeatNumbers.setText("Ghế: " + (seatNumbers != null ? seatNumbers : "N/A"));
                }
                if (tvStatus != null) {
                    setStatusView(tvStatus, status);
                }

                card.setOnClickListener(v -> {
                    Intent intent = new Intent(BookingHistoryActivity.this, BookingDetailActivity.class);
                    intent.putExtra("booking_id", bookingId);
                    startActivity(intent);
                });

                container.addView(card);
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        if (container.getChildCount() == 0) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Không có lịch sử đặt vé");
            tvEmpty.setTextSize(16);
            tvEmpty.setTextColor(getColor(R.color.text_secondary));
            tvEmpty.setGravity(android.view.Gravity.CENTER);
            tvEmpty.setPadding(0, 48, 0, 48);
            container.addView(tvEmpty);
        }
    }

    private void setStatusView(TextView tvStatus, String status) {
        if (tvStatus == null) return;

        switch (status != null ? status.toLowerCase() : "") {
            case "confirmed":
                tvStatus.setText("Đã xác nhận");
                tvStatus.setBackgroundResource(R.drawable.bg_status_upcoming);
                break;
            case "cancelled":
                tvStatus.setText("Đã hủy");
                tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                break;
            case "completed":
                tvStatus.setText("Đã hoàn thành");
                tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                break;
            default:
                tvStatus.setText("Đã xác nhận");
                tvStatus.setBackgroundResource(R.drawable.bg_status_upcoming);
        }
    }

    // Use DateTimeHelper utility methods instead of duplicate code

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}

