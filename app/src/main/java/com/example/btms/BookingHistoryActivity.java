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
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        dbHelper = new DatabaseHelper(this);
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", null);

        setupFilterTabs();
        loadBookingHistory(currentFilter);

        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void setupFilterTabs() {
        Button btnAll = findViewById(R.id.btnAllHistory);
        Button btnConfirmed = findViewById(R.id.btnConfirmed);
        Button btnCancelled = findViewById(R.id.btnCancelled);

        updateFilterButtons(btnAll, btnConfirmed, btnCancelled);

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
        Button btnAll = findViewById(R.id.btnAllHistory);
        Button btnConfirmed = findViewById(R.id.btnConfirmed);
        Button btnCancelled = findViewById(R.id.btnCancelled);
        if (btnAll != null) {
            btnAll.setBackgroundTintList(null);
            btnAll.setBackgroundResource(R.drawable.bg_filter_button_inactive);
            btnAll.setTextColor(getColor(R.color.text_secondary));
        }
        if (btnConfirmed != null) {
            btnConfirmed.setBackgroundTintList(null);
            btnConfirmed.setBackgroundResource(R.drawable.bg_filter_button_inactive);
            btnConfirmed.setTextColor(getColor(R.color.text_secondary));
        }
        if (btnCancelled != null) {
            btnCancelled.setBackgroundTintList(null);
            btnCancelled.setBackgroundResource(R.drawable.bg_filter_button_inactive);
            btnCancelled.setTextColor(getColor(R.color.text_secondary));
        }

        if (activeBtn != null) {
            activeBtn.setBackgroundTintList(null);
            activeBtn.setBackgroundResource(R.drawable.bg_filter_button_active);
            activeBtn.setTextColor(android.graphics.Color.WHITE);
        }
    }

    private void loadBookingHistory(String filter) {
        if (userEmail == null) return;

        LinearLayout container = findViewById(R.id.llBookingsContainer);
        if (container == null) return;

        container.removeAllViews();
        TextView tvLoading = new TextView(this);
        tvLoading.setText("Đang tải...");
        tvLoading.setTextSize(16);
        tvLoading.setTextColor(getColor(R.color.text_secondary));
        tvLoading.setGravity(android.view.Gravity.CENTER);
        tvLoading.setPadding(0, 48, 0, 48);
        container.addView(tvLoading);

        new Thread(() -> {
            Cursor cursor = null;
            try {
                cursor = dbHelper.getAllUserBookings(userEmail);
                
                if (cursor == null) {
                    android.util.Log.e("BookingHistoryActivity", "Cursor is null");
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        container.removeAllViews();
                        TextView tvError = new TextView(this);
                        tvError.setText("Không thể tải dữ liệu từ database");
                        tvError.setTextSize(16);
                        tvError.setTextColor(getColor(R.color.red));
                        tvError.setGravity(android.view.Gravity.CENTER);
                        tvError.setPadding(0, 48, 0, 48);
                        container.addView(tvError);
                    });
                    return;
                }

                java.util.List<BookingItem> bookingItems = new java.util.ArrayList<>();
                
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                            if (!filter.equals("all")) {
                                if (filter.equals("confirmed") && !status.equalsIgnoreCase("confirmed")) {
                                    continue;
                                }
                                if (filter.equals("cancelled") && !status.equalsIgnoreCase("cancelled")) {
                                    continue;
                                }
                            }
                            
                            BookingItem item = new BookingItem();
                            item.bookingId = cursor.getLong(cursor.getColumnIndexOrThrow("booking_id"));
                            
                            int companyNameIndex = cursor.getColumnIndex("company_name");
                            item.companyName = companyNameIndex >= 0 ? cursor.getString(companyNameIndex) : "EASYBUS";
                            
                            int fromLocationIndex = cursor.getColumnIndex("from_location");
                            item.fromLocation = fromLocationIndex >= 0 ? cursor.getString(fromLocationIndex) : "";
                            
                            int toLocationIndex = cursor.getColumnIndex("to_location");
                            item.toLocation = toLocationIndex >= 0 ? cursor.getString(toLocationIndex) : "";
                            
                            int departureTimeIndex = cursor.getColumnIndex("departure_time");
                            item.departureTime = departureTimeIndex >= 0 ? cursor.getString(departureTimeIndex) : "";
                            
                            int dateIndex = cursor.getColumnIndex("date");
                            item.scheduleDate = dateIndex >= 0 ? cursor.getString(dateIndex) : "";
                            
                            int seatNumbersIndex = cursor.getColumnIndex("seat_numbers");
                            item.seatNumbers = seatNumbersIndex >= 0 ? cursor.getString(seatNumbersIndex) : "";
                            
                            item.status = status != null ? status : "confirmed";
                            
                            bookingItems.add(item);
                        } catch (Exception e) {
                            android.util.Log.e("BookingHistoryActivity", "Error parsing booking item: " + e.getMessage(), e);
                        }
                    } while (cursor.moveToNext());
                }
                
                if (cursor != null) {
                    cursor.close();
                }

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    
                    container.removeAllViews();
                    
                    if (bookingItems.isEmpty()) {
                        TextView tvEmpty = new TextView(this);
                        tvEmpty.setText("Không có lịch sử đặt vé");
                        tvEmpty.setTextSize(16);
                        tvEmpty.setTextColor(getColor(R.color.text_secondary));
                        tvEmpty.setGravity(android.view.Gravity.CENTER);
                        tvEmpty.setPadding(0, 48, 0, 48);
                        container.addView(tvEmpty);
                        return;
                    }

                    LayoutInflater inflater = LayoutInflater.from(this);
                    for (BookingItem item : bookingItems) {
                        View card = inflater.inflate(R.layout.item_booking_card, container, false);

                        TextView tvTerminalName = card.findViewById(R.id.tvTerminalName);
                        TextView tvFromLocation = card.findViewById(R.id.tvFromLocation);
                        TextView tvToLocation = card.findViewById(R.id.tvToLocation);
                        TextView tvDepartureTime = card.findViewById(R.id.tvDepartureTime);
                        TextView tvSeatNumbers = card.findViewById(R.id.tvSeatNumbers);
                        TextView tvStatus = card.findViewById(R.id.tvStatus);

                        if (tvTerminalName != null) {
                            tvTerminalName.setText(item.companyName != null ? item.companyName : "Hãng xe");
                        }
                        if (tvFromLocation != null) {
                            tvFromLocation.setText(item.fromLocation);
                        }
                        if (tvToLocation != null) {
                            tvToLocation.setText(item.toLocation);
                        }
                        if (tvDepartureTime != null) {
                            String timeStr = DateTimeHelper.formatTime(item.departureTime);
                            String dayStr = DateTimeHelper.getDayOfWeek(item.scheduleDate);
                            String dateStr = DateTimeHelper.formatDate(item.scheduleDate);
                            tvDepartureTime.setText(timeStr + ", " + dayStr + "\n" + dateStr);
                        }
                        if (tvSeatNumbers != null) {
                            tvSeatNumbers.setText("Ghế: " + (item.seatNumbers != null ? item.seatNumbers : "N/A"));
                        }
                        if (tvStatus != null) {
                            setStatusView(tvStatus, item.status);
                        }

                        final long bookingId = item.bookingId;
                        card.setOnClickListener(v -> {
                            Intent intent = new Intent(BookingHistoryActivity.this, BookingDetailActivity.class);
                            intent.putExtra("booking_id", bookingId);
                            startActivity(intent);
                        });

                        container.addView(card);
                    }
                });
            } catch (Exception e) {
                android.util.Log.e("BookingHistoryActivity", "Error loading booking history: " + e.getMessage(), e);
                e.printStackTrace();
                if (cursor != null && !cursor.isClosed()) {
                    try {
                        cursor.close();
                    } catch (Exception ce) {
                        android.util.Log.e("BookingHistoryActivity", "Error closing cursor: " + ce.getMessage(), ce);
                    }
                }
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    container.removeAllViews();
                    TextView tvError = new TextView(this);
                    tvError.setText("Có lỗi xảy ra khi tải lịch sử đặt vé: " + e.getMessage());
                    tvError.setTextSize(16);
                    tvError.setTextColor(getColor(R.color.red));
                    tvError.setGravity(android.view.Gravity.CENTER);
                    tvError.setPadding(0, 48, 0, 48);
                    container.addView(tvError);
                });
            }
        }).start();
    }

    private static class BookingItem {
        long bookingId;
        String companyName;
        String fromLocation;
        String toLocation;
        String departureTime;
        String scheduleDate;
        String seatNumbers;
        String status;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

