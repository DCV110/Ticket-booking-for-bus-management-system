package com.example.btms;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SelectBusActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String fromLocation;
    private String toLocation;
    private LinearLayout busListContainer;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bus);

        dbHelper = new DatabaseHelper(this);
        
        // Get user name from SharedPreferences or database
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", null);
        String userName = sharedPreferences.getString("user_name", null);
        
        // If name not in SharedPreferences, get from database
        if (userName == null && userEmail != null) {
            android.content.ContentValues userInfo = dbHelper.getUserInfo(userEmail);
            if (userInfo != null && userInfo.containsKey("name")) {
                userName = userInfo.getAsString("name");
                // Save to SharedPreferences for future use
                if (userName != null) {
                    sharedPreferences.edit().putString("user_name", userName).apply();
                }
            }
        }
        
        // Update greeting with user name
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null) {
            if (userName != null && !userName.isEmpty()) {
                tvGreeting.setText("Hello " + userName + "!");
            } else {
                tvGreeting.setText("Hello!");
            }
        }
        
        // Get locations from intent
        fromLocation = getIntent().getStringExtra("from_location");
        toLocation = getIntent().getStringExtra("to_location");
        
        if (fromLocation == null) fromLocation = "New York, NY";
        if (toLocation == null) toLocation = "Los Angeles, CA";

        // Get selected date from intent (must be declared before use in lambda)
        selectedDate = getIntent().getStringExtra("selected_date");
        if (selectedDate == null || selectedDate.isEmpty()) {
            // Default to today if not provided
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        // Update route display in red card
        TextView tvFromLocation = findViewById(R.id.tvFromLocation);
        TextView tvToLocation = findViewById(R.id.tvToLocation);
        if (tvFromLocation != null) {
            // Extract city name (remove state if present)
            String fromCity = fromLocation.split(",")[0].trim();
            tvFromLocation.setText(fromCity);
        }
        if (tvToLocation != null) {
            String toCity = toLocation.split(",")[0].trim();
            tvToLocation.setText(toCity);
        }
        
        // Setup swap button
        android.widget.ImageButton btnSwapRoute = findViewById(R.id.btnSwapRoute);
        if (btnSwapRoute != null) {
            btnSwapRoute.setOnClickListener(v -> {
                String temp = fromLocation;
                fromLocation = toLocation;
                toLocation = temp;
                
                if (tvFromLocation != null) {
                    tvFromLocation.setText(fromLocation.split(",")[0].trim());
                }
                if (tvToLocation != null) {
                    tvToLocation.setText(toLocation.split(",")[0].trim());
                }
                
                // Reload schedules with swapped route
                refreshSchedules();
            });
        }
        
        // Ensure sample schedules exist for the selected date
        dbHelper.ensureSampleSchedulesForDate(selectedDate);
        
        // Format date for display and update date button
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd'th' - MMM - yyyy | EEEE", Locale.getDefault());
            String displayDate = displayFormat.format(date);
            
            android.widget.Button btnDate = findViewById(R.id.btnDate);
            if (btnDate != null) {
                btnDate.setText(displayDate);
                btnDate.setOnClickListener(v -> {
                    // Open date picker to change date
                    android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {
                            Calendar selected = Calendar.getInstance();
                            selected.set(year, month, dayOfMonth);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            selectedDate = dateFormat.format(selected.getTime());
                            
                            // Reload schedules with new date
                            refreshSchedules();
                            
                            // Update button text
                            SimpleDateFormat displayFormat2 = new SimpleDateFormat("dd'th' - MMM - yyyy | EEEE", Locale.getDefault());
                            btnDate.setText(displayFormat2.format(selected.getTime()));
                        },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.show();
                });
            }
        } catch (Exception e) {
            android.util.Log.e("SelectBusActivity", "Error formatting date: " + e.getMessage());
        }
        
        refreshSchedules();

        // Setup bottom navigation (no active state for this screen)
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void loadBusSchedules(long routeId, String date) {
        Cursor cursor = null;
        
        android.util.Log.d("SelectBusActivity", "Loading schedules for routeId: " + routeId + ", date: " + date);
        
        if (routeId != -1) {
            cursor = dbHelper.getSchedulesForRoute(routeId, date);
            if (cursor != null) {
                android.util.Log.d("SelectBusActivity", "Found " + cursor.getCount() + " schedules");
            } else {
                android.util.Log.e("SelectBusActivity", "Cursor is null");
            }
        } else {
            android.util.Log.e("SelectBusActivity", "Route ID is -1, route not found");
            android.widget.Toast.makeText(this, "Route not found. Please select valid locations.", android.widget.Toast.LENGTH_LONG).show();
        }
        
        // Update bus cards with database data - show all available schedules
        int[] cardIds = {R.id.cardBus1, R.id.cardBus2, R.id.cardBus3};
        int[] buttonIds = {R.id.btnSelectBus1, R.id.btnSelectBus2, R.id.btnSelectBus3};
        
        int scheduleCount = cursor != null ? cursor.getCount() : 0;
        android.util.Log.d("SelectBusActivity", "Total schedules found: " + scheduleCount);
        
        // Update all available cards (show up to 3, but we create 8 schedules)
        for (int i = 0; i < cardIds.length && i < scheduleCount; i++) {
            updateBusCard(cardIds[i], buttonIds[i], cursor, i);
        }
        
        // Hide remaining cards if we have less than 3 schedules
        for (int i = scheduleCount; i < cardIds.length; i++) {
            View cardView = findViewById(cardIds[i]);
            if (cardView != null) {
                cardView.setVisibility(View.GONE);
            }
        }
        
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        
        // Show message if no schedules found
        if (scheduleCount == 0) {
            android.widget.Toast.makeText(this, "No bus schedules found for this route and date. Please try a different date or route.", android.widget.Toast.LENGTH_LONG).show();
        } else {
            android.widget.Toast.makeText(this, "Found " + scheduleCount + " bus schedules", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    // Refresh schedules: ensure route exists, create schedules if needed, then load
    private void refreshSchedules() {
        // Get route ID, create if doesn't exist
        long routeId = dbHelper.getRouteId(fromLocation, toLocation);

        android.util.Log.d("SelectBusActivity", "From: " + fromLocation + ", To: " + toLocation + ", RouteId: " + routeId + ", Date: " + selectedDate);

        if (routeId == -1) {
            android.util.Log.w("SelectBusActivity", "Route not found, creating new route");
            routeId = dbHelper.createRouteIfNotExists(fromLocation, toLocation);

            if (routeId == -1) {
                android.widget.Toast.makeText(this, "Could not create route. Please select valid locations.", android.widget.Toast.LENGTH_LONG).show();
                return;
            } else {
                android.util.Log.d("SelectBusActivity", "Created new route with ID: " + routeId);
                dbHelper.insertSampleSchedulesForDate(selectedDate, routeId);
            }
        } else {
            // Ensure schedules for this route and date
            dbHelper.insertSampleSchedulesForDate(selectedDate, routeId);
        }

        loadBusSchedules(routeId, selectedDate);
    }
    
    private void updateBusCard(int cardId, int buttonId, Cursor cursor, int position) {
        View cardView = findViewById(cardId);
        if (cardView == null) return;
        
        if (cursor != null && cursor.getCount() > position && cursor.moveToPosition(position)) {
            long scheduleId = cursor.getLong(0);
            String companyName = cursor.getString(1);
            String departureTime = cursor.getString(2);
            String arrivalTime = cursor.getString(3);
            double price = cursor.getDouble(4);
            String busType = cursor.getString(5);
            int availableSeats = cursor.getInt(6);
            
            // Calculate duration in minutes
            String[] depParts = departureTime.split(":");
            String[] arrParts = arrivalTime.split(":");
            int depHour = Integer.parseInt(depParts[0]);
            int depMin = depParts.length > 1 ? Integer.parseInt(depParts[1]) : 0;
            int arrHour = Integer.parseInt(arrParts[0]);
            int arrMin = arrParts.length > 1 ? Integer.parseInt(arrParts[1]) : 0;
            
            int depTotalMinutes = depHour * 60 + depMin;
            int arrTotalMinutes = arrHour * 60 + arrMin;
            int durationMinutes = arrTotalMinutes - depTotalMinutes;
            if (durationMinutes < 0) {
                durationMinutes += 24 * 60; // Add 24 hours if crossing midnight
            }
            if (durationMinutes == 0) durationMinutes = 1;
            
            // Format duration
            String durationDisplay;
            if (durationMinutes < 60) {
                durationDisplay = durationMinutes + " Min";
            } else {
                int hours = durationMinutes / 60;
                int minutes = durationMinutes % 60;
                if (minutes == 0) {
                    durationDisplay = hours + " Hr";
                } else {
                    durationDisplay = hours + " Hr " + minutes + " Min";
                }
            }
            
            // Format time for display
            String timeDisplay = formatTime(departureTime) + " - " + formatTime(arrivalTime);
            
            // Update card content using findViewById within the card
            CardView card = (CardView) cardView;
            LinearLayout cardLayout = (LinearLayout) card.getChildAt(0);
            if (cardLayout != null) {
                // Find TextViews by their position in layout
                TextView tvCompany = findTextViewInLayout(cardLayout, 0);
                if (tvCompany != null) {
                    tvCompany.setText(companyName);
                }
                
                LinearLayout priceLayout = (LinearLayout) cardLayout.getChildAt(1);
                if (priceLayout != null) {
                    TextView tvPrice = (TextView) priceLayout.getChildAt(0);
                    if (tvPrice != null) {
                        tvPrice.setText("LKR " + String.format("%.0f", price));
                    }
                    TextView tvBusType = (TextView) priceLayout.getChildAt(1);
                    if (tvBusType != null) {
                        tvBusType.setText(busType);
                    }
                }
                
                LinearLayout timeLayout = (LinearLayout) cardLayout.getChildAt(2);
                if (timeLayout != null) {
                    TextView tvDuration = (TextView) timeLayout.getChildAt(0);
                    if (tvDuration != null) {
                        tvDuration.setText(durationDisplay);
                    }
                    TextView tvTime = (TextView) timeLayout.getChildAt(1);
                    if (tvTime != null) {
                        tvTime.setText(timeDisplay);
                    }
                }
                
                TextView tvSeats = findTextViewInLayout(cardLayout, 3);
                if (tvSeats != null) {
                    tvSeats.setText(availableSeats + " Seats left");
                    if (availableSeats < 5) {
                        tvSeats.setTextColor(getColor(R.color.red));
                    } else if (availableSeats < 15) {
                        tvSeats.setTextColor(getColor(R.color.yellow));
                    } else {
                        tvSeats.setTextColor(getColor(R.color.green));
                    }
                }
            }
            
            // Set button click listener
            Button btn = findViewById(buttonId);
            if (btn != null) {
                btn.setOnClickListener(v -> {
                    Intent intent = new Intent(SelectBusActivity.this, ChooseSeatActivity.class);
                    intent.putExtra("from_location", fromLocation);
                    intent.putExtra("to_location", toLocation);
                    intent.putExtra("schedule_id", scheduleId);
                    intent.putExtra("company_name", companyName);
                    intent.putExtra("price", price);
                    intent.putExtra("departure_time", departureTime);
                    intent.putExtra("arrival_time", arrivalTime);
                    intent.putExtra("bus_type", busType);
                    startActivity(intent);
                });
            }
        } else {
            // Hide card if no data
            cardView.setVisibility(View.GONE);
            if (position == 0 && (cursor == null || cursor.getCount() == 0)) {
                // Show message if no schedules found
                android.widget.Toast.makeText(this, "No bus schedules found for this route and date.", android.widget.Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private TextView findTextViewInLayout(LinearLayout layout, int index) {
        if (layout != null && index < layout.getChildCount()) {
            View child = layout.getChildAt(index);
            if (child instanceof TextView) {
                return (TextView) child;
            }
        }
        return null;
    }
    
    private String formatTime(String time) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            String period = hour >= 12 ? "PM" : "AM";
            int displayHour = hour > 12 ? hour - 12 : (hour == 0 ? 12 : hour);
            return String.format("%d:%02d %s", displayHour, minute, period);
        } catch (Exception e) {
            return time;
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

