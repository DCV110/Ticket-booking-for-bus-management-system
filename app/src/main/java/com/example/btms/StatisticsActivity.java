package com.example.btms;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DatabaseHelper(this);
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", null);

        loadStatistics();

        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void loadStatistics() {
        if (userEmail == null) return;

        new Thread(() -> {
            try {
                Cursor cursor = dbHelper.getAllUserBookings(userEmail);

                int totalBookings = 0;
                double totalSpent = 0;
                int confirmedCount = 0;
                int cancelledCount = 0;
                Map<String, Integer> routeCounts = new HashMap<>();

                if (cursor != null) {
                    totalBookings = cursor.getCount();
                    if (cursor.moveToFirst()) {
                        do {
                            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                            double fare = cursor.getDouble(cursor.getColumnIndexOrThrow("total_fare"));
                            String fromLocation = cursor.getString(cursor.getColumnIndexOrThrow("from_location"));
                            String toLocation = cursor.getString(cursor.getColumnIndexOrThrow("to_location"));

                            totalSpent += fare;

                            if (status != null) {
                                if (status.equalsIgnoreCase("confirmed")) {
                                    confirmedCount++;
                                } else if (status.equalsIgnoreCase("cancelled")) {
                                    cancelledCount++;
                                }
                            }

                            String route = fromLocation + " → " + toLocation;
                            routeCounts.put(route, routeCounts.getOrDefault(route, 0) + 1);

                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                final int finalTotalBookings = totalBookings;
                final double finalTotalSpent = totalSpent;
                final int finalConfirmedCount = confirmedCount;
                final int finalCancelledCount = cancelledCount;
                final Map<String, Integer> finalRouteCounts = routeCounts;

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    TextView tvTotalBookings = findViewById(R.id.tvTotalBookings);
                    TextView tvTotalSpent = findViewById(R.id.tvTotalSpent);
                    TextView tvConfirmedBookings = findViewById(R.id.tvConfirmedBookings);
                    TextView tvCancelledBookings = findViewById(R.id.tvCancelledBookings);

                    if (tvTotalBookings != null) {
                        tvTotalBookings.setText(String.valueOf(finalTotalBookings));
                    }
                    if (tvTotalSpent != null) {
                        tvTotalSpent.setText(CurrencyHelper.formatPrice(finalTotalSpent));
                    }
                    if (tvConfirmedBookings != null) {
                        tvConfirmedBookings.setText(String.valueOf(finalConfirmedCount));
                    }
                    if (tvCancelledBookings != null) {
                        tvCancelledBookings.setText(String.valueOf(finalCancelledCount));
                    }

                    displayPopularRoutes(finalRouteCounts);
                });
            } catch (Exception e) {
                android.util.Log.e("StatisticsActivity", "Error loading statistics: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                });
            }
        }).start();
    }

    private void displayPopularRoutes(Map<String, Integer> routeCounts) {
        LinearLayout container = findViewById(R.id.llPopularRoutes);
        if (container == null) return;

        container.removeAllViews();

        if (routeCounts.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Chưa có dữ liệu");
            tvEmpty.setTextSize(14);
            tvEmpty.setTextColor(getColor(R.color.text_secondary));
            container.addView(tvEmpty);
            return;
        }

        java.util.List<Map.Entry<String, Integer>> sortedRoutes = new java.util.ArrayList<>(routeCounts.entrySet());
        java.util.Collections.sort(sortedRoutes, (a, b) -> b.getValue().compareTo(a.getValue()));
        
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedRoutes) {
            if (count >= 5) break;
            
            View routeItem = LayoutInflater.from(this).inflate(
                    android.R.layout.simple_list_item_2, container, false);
            TextView tvRoute = routeItem.findViewById(android.R.id.text1);
            TextView tvCount = routeItem.findViewById(android.R.id.text2);

            if (tvRoute != null) {
                tvRoute.setText(entry.getKey());
                tvRoute.setTextSize(16);
            }
            if (tvCount != null) {
                tvCount.setText(entry.getValue() + " chuyến");
                tvCount.setTextSize(14);
                tvCount.setTextColor(getColor(R.color.text_secondary));
            }

            container.addView(routeItem);
            count++;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

