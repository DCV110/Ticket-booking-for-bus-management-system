package com.example.btms;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private String selectedDate; // Format: yyyy-MM-dd
    private int selectedDateType; // 0 = today, 1 = tomorrow, 2 = other

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            dbHelper = new DatabaseHelper(this);
            
            // Use AutoCompleteTextView from layout
            AutoCompleteTextView actvFrom = findViewById(R.id.actvFrom);
            AutoCompleteTextView actvTo = findViewById(R.id.actvTo);
            Button btnSearch = findViewById(R.id.btnSearch);
            ImageButton btnSwap = findViewById(R.id.btnSwap);
            Button btnToday = findViewById(R.id.btnToday);
            Button btnTomorrow = findViewById(R.id.btnTomorrow);
            Button btnOther = findViewById(R.id.btnOther);
            
            // Check if views are found
            if (actvFrom == null || actvTo == null || btnSearch == null || btnSwap == null) {
                android.util.Log.e("HomeActivity", "Some views are null");
                android.widget.Toast.makeText(this, "Lỗi tải giao diện. Vui lòng khởi động lại ứng dụng.", android.widget.Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            // Update greeting with user name
            android.content.SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("user_email", null);
            String userName = sharedPreferences.getString("user_name", null);
            
            // If name not in SharedPreferences, get from database
            if (userName == null && userEmail != null && dbHelper != null) {
                try {
                    android.content.ContentValues userInfo = dbHelper.getUserInfo(userEmail);
                    if (userInfo != null && userInfo.containsKey("name")) {
                        userName = userInfo.getAsString("name");
                        // Save to SharedPreferences for future use
                        if (userName != null) {
                            sharedPreferences.edit().putString("user_name", userName).apply();
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("HomeActivity", "Error getting user info: " + e.getMessage(), e);
                    // Continue without user name
                }
            }
            
            // Update greeting TextView
            TextView tvGreeting = findViewById(R.id.tvGreeting);
            if (tvGreeting != null) {
                if (userName != null && !userName.isEmpty()) {
                    tvGreeting.setText("Xin chào " + userName + "!");
                } else {
                    tvGreeting.setText("Xin chào!");
                }
            }

            // Get all locations from database
            List<String> locations = null;
            try {
                if (dbHelper != null) {
                    locations = dbHelper.getAllLocations();
                }
            } catch (Exception e) {
                android.util.Log.e("HomeActivity", "Error getting locations: " + e.getMessage(), e);
            }
            
            if (locations == null || locations.isEmpty()) {
                // If no locations, use default list of HCMC districts
                locations = new java.util.ArrayList<>();
                locations.add("Quận 1");
                locations.add("Quận 2");
                locations.add("Quận 3");
            }
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, locations);

        actvFrom.setAdapter(adapter);
        actvTo.setAdapter(adapter);
        
        // Set threshold to show dropdown after 1 character
        actvFrom.setThreshold(1);
        actvTo.setThreshold(1);
        
        // Show dropdown when focused
        actvFrom.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actvFrom.showDropDown();
            }
        });
        
        actvTo.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actvTo.showDropDown();
            }
        });
        
        // Show dropdown when clicked
        actvFrom.setOnClickListener(v -> actvFrom.showDropDown());
        actvTo.setOnClickListener(v -> actvTo.showDropDown());

        // Add text change listeners for search functionality
        actvFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() > 0 && dbHelper != null) {
                        List<String> filtered = dbHelper.searchLocations(s.toString());
                        if (filtered == null || filtered.isEmpty()) {
                            filtered = dbHelper.getAllLocations();
                            if (filtered == null) filtered = new java.util.ArrayList<>();
                        }
                        ArrayAdapter<String> filteredAdapter = new ArrayAdapter<>(HomeActivity.this,
                                android.R.layout.simple_dropdown_item_1line, filtered);
                        actvFrom.setAdapter(filteredAdapter);
                        actvFrom.showDropDown();
                    } else {
                        actvFrom.setAdapter(adapter);
                        actvFrom.showDropDown();
                    }
                } catch (Exception e) {
                    android.util.Log.e("HomeActivity", "Error in text watcher: " + e.getMessage(), e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        actvTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() > 0 && dbHelper != null) {
                        List<String> filtered = dbHelper.searchLocations(s.toString());
                        if (filtered == null || filtered.isEmpty()) {
                            filtered = dbHelper.getAllLocations();
                            if (filtered == null) filtered = new java.util.ArrayList<>();
                        }
                        ArrayAdapter<String> filteredAdapter = new ArrayAdapter<>(HomeActivity.this,
                                android.R.layout.simple_dropdown_item_1line, filtered);
                        actvTo.setAdapter(filteredAdapter);
                        actvTo.showDropDown();
                    } else {
                        actvTo.setAdapter(adapter);
                        actvTo.showDropDown();
                    }
                } catch (Exception e) {
                    android.util.Log.e("HomeActivity", "Error in text watcher: " + e.getMessage(), e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Swap button functionality
        btnSwap.setOnClickListener(v -> {
            String from = actvFrom.getText().toString();
            String to = actvTo.getText().toString();
            actvFrom.setText(to);
            actvTo.setText(from);
        });

        // Initialize with today's date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = dateFormat.format(calendar.getTime());
        selectedDateType = 0; // Default to today
        
        // Date selection buttons
        btnToday.setOnClickListener(v -> {
            // Set today's date
            Calendar today = Calendar.getInstance();
            selectedDate = dateFormat.format(today.getTime());
            selectedDateType = 0;
            
            btnToday.setBackgroundTintList(getColorStateList(R.color.red));
            btnToday.setTextColor(getColor(R.color.white));
            btnTomorrow.setBackgroundTintList(null);
            btnTomorrow.setTextColor(getColor(R.color.red));
            btnOther.setBackgroundTintList(null);
            btnOther.setTextColor(getColor(R.color.red));
        });

        btnTomorrow.setOnClickListener(v -> {
            // Set tomorrow's date
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            selectedDate = dateFormat.format(tomorrow.getTime());
            selectedDateType = 1;
            
            btnTomorrow.setBackgroundTintList(getColorStateList(R.color.red));
            btnTomorrow.setTextColor(getColor(R.color.white));
            btnToday.setBackgroundTintList(null);
            btnToday.setTextColor(getColor(R.color.red));
            btnOther.setBackgroundTintList(null);
            btnOther.setTextColor(getColor(R.color.red));
        });

        btnOther.setOnClickListener(v -> {
            // Show date picker
            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.DAY_OF_MONTH, -1); // Allow today and future dates
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    selectedDate = dateFormat.format(selected.getTime());
                    selectedDateType = 2;
                    
                    btnOther.setBackgroundTintList(getColorStateList(R.color.red));
                    btnOther.setTextColor(getColor(R.color.white));
                    btnToday.setBackgroundTintList(null);
                    btnToday.setTextColor(getColor(R.color.red));
                    btnTomorrow.setBackgroundTintList(null);
                    btnTomorrow.setTextColor(getColor(R.color.red));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            
            // Set minimum date to today
            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
            datePickerDialog.show();
        });

        btnSearch.setOnClickListener(v -> {
            String from = actvFrom.getText().toString().trim();
            String to = actvTo.getText().toString().trim();
            
            if (from.isEmpty() || to.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn điểm đi và điểm đến", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (from.equals(to)) {
                Toast.makeText(this, "Điểm đi và điểm đến không thể giống nhau", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Ensure selected date is set
            if (selectedDate == null || selectedDate.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                selectedDate = dateFormat.format(cal.getTime());
            }
            
            Intent intent = new Intent(HomeActivity.this, SelectBusActivity.class);
            intent.putExtra("from_location", from);
            intent.putExtra("to_location", to);
            intent.putExtra("selected_date", selectedDate);
            intent.putExtra("date_type", selectedDateType);
            startActivity(intent);
        });

            // Setup bottom navigation
            View rootView = findViewById(android.R.id.content);
            if (rootView != null) {
                BottomNavHelper.setupBottomNav(rootView, R.id.navHome);
                BottomNavHelper.setupBottomNavListeners(this, rootView);
            }
            
            // Load suggested journeys - use runOnUiThread to ensure we're on main thread
            // Use postDelayed to ensure everything is ready and database is initialized
            try {
                // Use handler to delay execution and ensure we're on main thread
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    try {
                        // Double check activity is still valid and not finishing
                        if (isFinishing() || isDestroyed()) {
                            android.util.Log.d("HomeActivity", "Activity is finishing or destroyed, skipping loadSuggestedJourneys");
                            return;
                        }
                        
                        // Ensure we're on main thread
                        runOnUiThread(() -> {
                            try {
                                if (isFinishing() || isDestroyed()) {
                                    return;
                                }
                                
                                if (dbHelper == null) {
                                    android.util.Log.w("HomeActivity", "dbHelper is null, recreating");
                                    dbHelper = new DatabaseHelper(HomeActivity.this);
                                }
                                
                                // Ensure database is ready by doing a simple query
                                try {
                                    java.util.List<String> testLocations = dbHelper.getAllLocations();
                                    if (testLocations == null || testLocations.isEmpty()) {
                                        android.util.Log.w("HomeActivity", "Database not ready yet, skipping loadSuggestedJourneys");
                                        return;
                                    }
                                } catch (Exception dbTestException) {
                                    android.util.Log.e("HomeActivity", "Database test failed: " + dbTestException.getMessage(), dbTestException);
                                    return;
                                }
                                
                                // Now safe to load suggested journeys
                                if (dbHelper != null && !isFinishing() && !isDestroyed()) {
                                    try {
                                        loadSuggestedJourneys();
                                    } catch (Exception e) {
                                        android.util.Log.e("HomeActivity", "Error in loadSuggestedJourneys: " + e.getMessage(), e);
                                        // Don't crash, just log
                                    }
                                }
                            } catch (Exception e) {
                                android.util.Log.e("HomeActivity", "Error in runOnUiThread: " + e.getMessage(), e);
                            }
                        });
                    } catch (Exception e) {
                        android.util.Log.e("HomeActivity", "Error loading suggested journeys: " + e.getMessage(), e);
                        // Don't crash the app, just log the error
                    }
                }, 2000); // Delay 2 seconds to ensure database is fully ready
            } catch (Exception e) {
                android.util.Log.e("HomeActivity", "Error setting up suggested journeys loader: " + e.getMessage(), e);
                // Don't crash, just skip loading suggested journeys
            }
        } catch (Exception e) {
            android.util.Log.e("HomeActivity", "Error in onCreate: " + e.getMessage(), e);
            android.widget.Toast.makeText(this, "Lỗi tải màn hình chính. Vui lòng thử lại.", android.widget.Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void loadSuggestedJourneys() {
        // Ensure this runs on main thread
        if (android.os.Looper.myLooper() != android.os.Looper.getMainLooper()) {
            runOnUiThread(this::loadSuggestedJourneys);
            return;
        }
        
        android.database.Cursor cursor = null;
        try {
            // Double check activity state
            if (isFinishing() || isDestroyed()) {
                android.util.Log.d("HomeActivity", "Activity finishing, skipping loadSuggestedJourneys");
                return;
            }
            
            if (dbHelper == null) {
                android.util.Log.e("HomeActivity", "dbHelper is null");
                return;
            }
            
            // Get 2 suggested journeys for home page
            try {
                cursor = dbHelper.getSuggestedJourneys(2);
            } catch (Exception dbException) {
                android.util.Log.e("HomeActivity", "Error getting suggested journeys from database: " + dbException.getMessage(), dbException);
                return;
            }
            
            if (cursor == null) {
                android.util.Log.e("HomeActivity", "Cursor is null");
                return;
            }
            
            if (!cursor.moveToFirst()) {
                android.util.Log.d("HomeActivity", "No suggested journeys found");
                return;
            }
            
            // Update journey cards
            int cardIndex = 0;
            do {
                try {
                    // Get data from cursor
                    String fromLocation = null;
                    String toLocation = null;
                    String departureTime = null;
                    String scheduleDate = null;
                    int routeNumber = 0;
                    long scheduleId = -1;
                    double price = 0;
                    String busType = null;
                    String arrivalTime = null;
                    int availableSeats = 0;
                    
                    try {
                        int fromIndex = cursor.getColumnIndex("from_location");
                        if (fromIndex >= 0) fromLocation = cursor.getString(fromIndex);
                        
                        int toIndex = cursor.getColumnIndex("to_location");
                        if (toIndex >= 0) toLocation = cursor.getString(toIndex);
                        
                        int depTimeIndex = cursor.getColumnIndex("departure_time");
                        if (depTimeIndex >= 0) departureTime = cursor.getString(depTimeIndex);
                        
                        int dateIndex = cursor.getColumnIndex("date");
                        if (dateIndex >= 0) scheduleDate = cursor.getString(dateIndex);
                        
                        int routeNumberIndex = cursor.getColumnIndex("route_number");
                        if (routeNumberIndex >= 0) routeNumber = cursor.getInt(routeNumberIndex);
                        
                        int scheduleIdIndex = cursor.getColumnIndex("schedule_id");
                        if (scheduleIdIndex >= 0) scheduleId = cursor.getLong(scheduleIdIndex);
                        
                        int priceIndex = cursor.getColumnIndex("price");
                        if (priceIndex >= 0) price = cursor.getDouble(priceIndex);
                        
                        int busTypeIndex = cursor.getColumnIndex("bus_type");
                        if (busTypeIndex >= 0) busType = cursor.getString(busTypeIndex);
                        
                        int arrivalTimeIndex = cursor.getColumnIndex("arrival_time");
                        if (arrivalTimeIndex >= 0) arrivalTime = cursor.getString(arrivalTimeIndex);
                        
                        int availableSeatsIndex = cursor.getColumnIndex("available_seats");
                        if (availableSeatsIndex >= 0) availableSeats = cursor.getInt(availableSeatsIndex);
                    } catch (Exception e) {
                        android.util.Log.e("HomeActivity", "Error getting cursor data: " + e.getMessage(), e);
                    }
                    
                    // Double check activity state before updating UI
                    if (isFinishing() || isDestroyed()) {
                        android.util.Log.d("HomeActivity", "Activity finishing, stopping loadSuggestedJourneys");
                        break;
                    }
                    
                    // Update views based on card index
                    TextView tvTerminalName = null;
                    TextView tvFromLocation = null;
                    TextView tvToLocation = null;
                    TextView tvTime = null;
                    TextView tvDate = null;
                    androidx.cardview.widget.CardView journeyButton = null;
                    
                    try {
                        if (cardIndex == 0) {
                            tvTerminalName = findViewById(R.id.tvJourney1Terminal);
                            tvFromLocation = findViewById(R.id.tvJourney1From);
                            tvToLocation = findViewById(R.id.tvJourney1To);
                            tvTime = findViewById(R.id.tvJourney1Time);
                            tvDate = findViewById(R.id.tvJourney1Date);
                            journeyButton = findViewById(R.id.btnJourney1);
                        } else if (cardIndex == 1) {
                            tvTerminalName = findViewById(R.id.tvJourney2Terminal);
                            tvFromLocation = findViewById(R.id.tvJourney2From);
                            tvToLocation = findViewById(R.id.tvJourney2To);
                            tvTime = findViewById(R.id.tvJourney2Time);
                            tvDate = findViewById(R.id.tvJourney2Date);
                            journeyButton = findViewById(R.id.btnJourney2);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("HomeActivity", "Error finding views: " + e.getMessage(), e);
                        continue; // Skip this card if views not found
                    }
                    
                    // Skip if views are null
                    if (tvTerminalName == null || journeyButton == null) {
                        android.util.Log.w("HomeActivity", "Views are null for card index: " + cardIndex);
                        continue;
                    }
                    
                    // Double check views are still valid
                    if (isFinishing() || isDestroyed()) {
                        break;
                    }
                    
                    // Update terminal name (route number)
                    if (tvTerminalName != null) {
                        if (routeNumber > 0) {
                            tvTerminalName.setText("Tuyến số " + routeNumber);
                        } else {
                            tvTerminalName.setText("Bến xe " + (cardIndex + 1));
                        }
                    }
                    
                    // Update from location
                    if (tvFromLocation != null && fromLocation != null) {
                        tvFromLocation.setText("Từ: " + fromLocation);
                    }
                    
                    // Update to location
                    if (tvToLocation != null && toLocation != null) {
                        tvToLocation.setText("Đến: " + toLocation);
                    }
                    
                    // Update departure time
                    if (tvTime != null && departureTime != null && scheduleDate != null) {
                        try {
                            String timeStr = DateTimeHelper.formatTime(departureTime);
                            String dayStr = DateTimeHelper.getDayOfWeek(scheduleDate);
                            tvTime.setText(timeStr + ", " + dayStr);
                        } catch (Exception e) {
                            tvTime.setText(departureTime);
                        }
                    }
                    
                    // Update date
                    if (tvDate != null && scheduleDate != null) {
                        try {
                            tvDate.setText(DateTimeHelper.formatDate(scheduleDate));
                        } catch (Exception e) {
                            tvDate.setText(scheduleDate);
                        }
                    }
                    
                    // Set click listener to open ChooseSeatActivity
                    if (scheduleId > 0 && journeyButton != null) {
                        final long finalScheduleId = scheduleId;
                        final String finalFromLocation = fromLocation;
                        final String finalToLocation = toLocation;
                        final int finalRouteNumber = routeNumber;
                        final double finalPrice = price;
                        final String finalDepartureTime = departureTime;
                        final String finalArrivalTime = arrivalTime;
                        final String finalBusType = busType;
                        final String finalScheduleDate = scheduleDate;
                        final int finalAvailableSeats = availableSeats;
                        
                        journeyButton.setOnClickListener(v -> {
                            try {
                                Intent intent = new Intent(HomeActivity.this, ChooseSeatActivity.class);
                                intent.putExtra("from_location", finalFromLocation);
                                intent.putExtra("to_location", finalToLocation);
                                intent.putExtra("schedule_id", finalScheduleId);
                                intent.putExtra("route_number", finalRouteNumber);
                                intent.putExtra("price", finalPrice);
                                intent.putExtra("departure_time", finalDepartureTime);
                                intent.putExtra("arrival_time", finalArrivalTime);
                                intent.putExtra("bus_type", finalBusType);
                                intent.putExtra("schedule_date", finalScheduleDate);
                                intent.putExtra("available_seats", finalAvailableSeats);
                                startActivity(intent);
                            } catch (Exception e) {
                                android.util.Log.e("HomeActivity", "Error opening ChooseSeatActivity: " + e.getMessage(), e);
                                Toast.makeText(HomeActivity.this, "Không thể mở trang đặt vé", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    
                    cardIndex++;
                } catch (Exception e) {
                    android.util.Log.e("HomeActivity", "Error updating journey card: " + e.getMessage(), e);
                }
            } while (cursor.moveToNext() && cardIndex < 2);
        } catch (Exception e) {
            android.util.Log.e("HomeActivity", "Error loading suggested journeys: " + e.getMessage(), e);
            // Don't crash the app, just log the error
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("HomeActivity", "Error closing cursor: " + e.getMessage(), e);
                }
            }
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

