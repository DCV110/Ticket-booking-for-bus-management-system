package com.example.btms;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
                tvGreeting.setText("Xin chào " + userName + "!");
            } else {
                tvGreeting.setText("Xin chào!");
            }
        }
        
        // Get locations from intent
        fromLocation = getIntent().getStringExtra("from_location");
        toLocation = getIntent().getStringExtra("to_location");
        
        if (fromLocation == null) fromLocation = "Quận 1";
        if (toLocation == null) toLocation = "Quận 3";

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
        // Use new method to get all schedules for routes from A to B (all route numbers)
        Cursor cursor = dbHelper.getSchedulesForRouteByLocations(fromLocation, toLocation, date);
        
        android.util.Log.d("SelectBusActivity", "Loading schedules for from: " + fromLocation + ", to: " + toLocation + ", date: " + date);
        
        if (cursor != null) {
            android.util.Log.d("SelectBusActivity", "Found " + cursor.getCount() + " schedules");
        } else {
            android.util.Log.e("SelectBusActivity", "Cursor is null");
            android.widget.Toast.makeText(this, "Không tìm thấy chuyến xe. Vui lòng chọn địa điểm hợp lệ.", android.widget.Toast.LENGTH_LONG).show();
        }
        
        // Collect all schedules (no filtering by route_number - show all available schedules)
        java.util.List<java.util.Map<String, Object>> uniqueSchedules = new java.util.ArrayList<>();
        java.util.Set<Long> seenScheduleIds = new java.util.HashSet<>();
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    long scheduleId = cursor.getLong(0);
                    
                    // Only filter by schedule_id to avoid exact duplicates
                    if (!seenScheduleIds.contains(scheduleId)) {
                        seenScheduleIds.add(scheduleId);
                        java.util.Map<String, Object> schedule = new java.util.HashMap<>();
                        schedule.put("schedule_id", scheduleId);
                        schedule.put("route_number", cursor.getInt(1));
                        schedule.put("departure_time", cursor.getString(2));
                        schedule.put("arrival_time", cursor.getString(3));
                        schedule.put("price", cursor.getDouble(4));
                        schedule.put("bus_type", cursor.getString(5));
                        schedule.put("available_seats", cursor.getInt(6));
                        schedule.put("total_seats", cursor.getInt(7));
                        schedule.put("route_id", cursor.getLong(8));
                        uniqueSchedules.add(schedule);
                    }
                } catch (Exception e) {
                    android.util.Log.e("SelectBusActivity", "Error processing schedule: " + e.getMessage(), e);
                }
            } while (cursor.moveToNext());
        }
        
        android.util.Log.d("SelectBusActivity", "Total schedules found: " + uniqueSchedules.size());
        
        // Update bus cards with database data - show all available schedules
        // Use all 4 fixed cards to display schedules
        int[] cardIds = {R.id.cardBus1, R.id.cardBus2, R.id.cardBus3, R.id.cardBus4};
        int[] buttonIds = {R.id.btnSelectBus1, R.id.btnSelectBus2, R.id.btnSelectBus3, R.id.btnSelectBus4};
        
        int scheduleCount = uniqueSchedules.size();
        android.util.Log.d("SelectBusActivity", "Total unique schedules: " + scheduleCount);
        
        // Show/hide cards based on available schedules
        int maxCards = Math.min(cardIds.length, scheduleCount);
        for (int i = 0; i < maxCards; i++) {
            try {
                View cardView = findViewById(cardIds[i]);
                if (cardView != null && i < uniqueSchedules.size()) {
                    cardView.setVisibility(View.VISIBLE);
                    updateBusCardFromMap(cardIds[i], buttonIds[i], uniqueSchedules.get(i));
                }
            } catch (Exception e) {
                android.util.Log.e("SelectBusActivity", "Error showing card " + i + ": " + e.getMessage(), e);
            }
        }
        
        // Hide remaining cards if we have less schedules
        for (int i = maxCards; i < cardIds.length; i++) {
            View cardView = findViewById(cardIds[i]);
            if (cardView != null) {
                cardView.setVisibility(View.GONE);
            }
        }
        
        // Close cursor after processing
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        
        // Show message if no schedules found
        if (scheduleCount == 0) {
            android.widget.Toast.makeText(this, "Không tìm thấy chuyến xe cho tuyến này và ngày này. Vui lòng thử ngày khác hoặc tuyến khác.", android.widget.Toast.LENGTH_LONG).show();
        }
    }

    // Refresh schedules: ensure routes exist, create schedules if needed, then load
    private void refreshSchedules() {
        android.util.Log.d("SelectBusActivity", "refreshSchedules: From: " + fromLocation + ", To: " + toLocation + ", Date: " + selectedDate);

        try {
            // Check if any routes exist from A to B
            android.database.Cursor routeCheck = dbHelper.getSchedulesForRouteByLocations(fromLocation, toLocation, selectedDate);
            boolean hasSchedules = routeCheck != null && routeCheck.getCount() > 0;
            if (routeCheck != null) {
                routeCheck.close();
            }

            if (!hasSchedules) {
                android.util.Log.d("SelectBusActivity", "No schedules found, creating new routes and schedules");
                
                // Get or create routes (multiple routes for same A-B pair)
                java.util.List<Long> routeIds = dbHelper.getAllRouteIdsForPair(fromLocation, toLocation);
                
                if (routeIds.isEmpty()) {
                    android.util.Log.w("SelectBusActivity", "No routes found, creating new routes");
                    long firstRouteId = dbHelper.createRouteIfNotExists(fromLocation, toLocation);

                    if (firstRouteId == -1) {
                        android.widget.Toast.makeText(this, "Không thể tạo tuyến. Vui lòng chọn địa điểm hợp lệ.", android.widget.Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        android.util.Log.d("SelectBusActivity", "Created new routes, first route ID: " + firstRouteId);
                        // Get all route IDs again after creation
                        routeIds = dbHelper.getAllRouteIdsForPair(fromLocation, toLocation);
                    }
                }
                
                // Create schedules for all routes (each route will have different schedules)
                for (Long routeId : routeIds) {
                    android.util.Log.d("SelectBusActivity", "Creating schedules for route ID: " + routeId);
                    dbHelper.insertSampleSchedulesForDate(selectedDate, routeId);
                }
            }

            // Load all schedules for routes from A to B (all route numbers)
            loadBusSchedules(-1, selectedDate);
        } catch (Exception e) {
            android.util.Log.e("SelectBusActivity", "Error in refreshSchedules: " + e.getMessage(), e);
            android.widget.Toast.makeText(this, "Lỗi khi tải chuyến xe: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }
    
    private void updateBusCardFromMap(int cardId, int buttonId, java.util.Map<String, Object> schedule) {
        View cardView = findViewById(cardId);
        if (cardView == null) return;
        
        try {
            long scheduleId = (Long) schedule.get("schedule_id");
            int routeNumber = (Integer) schedule.get("route_number");
            String departureTime = (String) schedule.get("departure_time");
            String arrivalTime = (String) schedule.get("arrival_time");
            double price = (Double) schedule.get("price");
            String busType = (String) schedule.get("bus_type");
            int availableSeats = (Integer) schedule.get("available_seats");
            
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
            String timeDisplay = DateTimeHelper.formatTime12Hour(departureTime) + " - " + DateTimeHelper.formatTime12Hour(arrivalTime);
            
            // Update card content using findViewById within the card
            CardView card = (CardView) cardView;
            LinearLayout cardLayout = (LinearLayout) card.getChildAt(0);
            if (cardLayout != null) {
                // Find the first TextView (route/company name) - it's the first child
                View firstChild = cardLayout.getChildAt(0);
                if (firstChild instanceof TextView) {
                    TextView tvCompany = (TextView) firstChild;
                    tvCompany.setText("Tuyến số " + routeNumber);
                    android.util.Log.d("SelectBusActivity", "Updated route name to: Tuyến số " + routeNumber);
                } else {
                    // Fallback: try finding by searching through children
                    TextView tvCompany = findTextViewInLayout(cardLayout, 0);
                    if (tvCompany != null) {
                        tvCompany.setText("Tuyến số " + routeNumber);
                        android.util.Log.d("SelectBusActivity", "Updated route name (fallback) to: Tuyến số " + routeNumber);
                    } else {
                        android.util.Log.e("SelectBusActivity", "Could not find TextView for route name in card");
                    }
                }
                
                // Also try to find TextView by searching all children recursively
                TextView routeNameView = findTextViewByName(cardLayout, "Perera Travels", "Gayan Express", "Shehan Travels");
                if (routeNameView != null) {
                    routeNameView.setText("Tuyến số " + routeNumber);
                    android.util.Log.d("SelectBusActivity", "Updated route name by text search to: Tuyến số " + routeNumber);
                }
                
                LinearLayout priceLayout = (LinearLayout) cardLayout.getChildAt(1);
                if (priceLayout != null) {
                    TextView tvPrice = (TextView) priceLayout.getChildAt(0);
                    if (tvPrice != null) {
                        tvPrice.setText(CurrencyHelper.formatPrice(price));
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
                
                // Update seats left - look for the text containing "Ghế còn lại" or "Seats left"
                TextView tvSeats = findTextViewByName(cardLayout, "Ghế còn lại", "Seats left");
                if (tvSeats != null) {
                    tvSeats.setText(availableSeats + " Ghế còn lại");
                    if (availableSeats < 5) {
                        tvSeats.setTextColor(getColor(R.color.red));
                    } else if (availableSeats < 15) {
                        tvSeats.setTextColor(getColor(R.color.yellow));
                    } else {
                        tvSeats.setTextColor(getColor(R.color.green));
                    }
                }
            }
            
            // Set button click listener - find button within the card
            Button btn = null;
            
            // Method 1: Find in cardView
            btn = cardView.findViewById(buttonId);
            
            // Method 2: If not found, search in cardLayout
            if (btn == null && cardLayout != null) {
                btn = findButtonInLayout(cardLayout, buttonId);
            }
            
            // Method 3: If still not found, try root view
            if (btn == null) {
                View rootView = findViewById(android.R.id.content);
                if (rootView != null) {
                    btn = rootView.findViewById(buttonId);
                }
            }
            
            if (btn != null) {
                // Remove any existing listeners
                btn.setOnClickListener(null);
                btn.setText("Chọn"); // Consistently use Vietnamese
                
                btn.setOnClickListener(v -> {
                    try {
                        android.util.Log.d("SelectBusActivity", "Button clicked! Schedule ID: " + scheduleId + ", Time: " + departureTime + ", Seats: " + availableSeats);
                        Intent intent = new Intent(SelectBusActivity.this, ChooseSeatActivity.class);
                        intent.putExtra("from_location", fromLocation);
                        intent.putExtra("to_location", toLocation);
                        intent.putExtra("schedule_id", scheduleId);
                        intent.putExtra("route_number", routeNumber);
                        intent.putExtra("price", price);
                        intent.putExtra("departure_time", departureTime);
                        intent.putExtra("arrival_time", arrivalTime);
                        intent.putExtra("bus_type", busType);
                        intent.putExtra("available_seats", availableSeats);
                        intent.putExtra("schedule_date", selectedDate);
                        startActivity(intent);
                    } catch (Exception e) {
                        android.util.Log.e("SelectBusActivity", "Error starting ChooseSeatActivity: " + e.getMessage(), e);
                        android.widget.Toast.makeText(this, "Lỗi khi mở màn hình chọn ghế: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
                android.util.Log.d("SelectBusActivity", "Button listener set for: Tuyến " + routeNumber + " (" + departureTime + ")");
            } else {
                android.util.Log.e("SelectBusActivity", "Button not found with ID: " + buttonId);
            }
        } catch (Exception e) {
            android.util.Log.e("SelectBusActivity", "Error updating bus card: " + e.getMessage(), e);
            cardView.setVisibility(View.GONE);
        }
    }
    
    private void updateBusCard(int cardId, int buttonId, Cursor cursor, int position) {
        View cardView = findViewById(cardId);
        if (cardView == null) return;
        
        try {
            if (cursor != null && !cursor.isClosed() && cursor.getCount() > position && cursor.moveToPosition(position)) {
            long scheduleId = cursor.getLong(0);
            int routeNumber = cursor.getInt(1); // Route number instead of company name
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
            String timeDisplay = DateTimeHelper.formatTime12Hour(departureTime) + " - " + DateTimeHelper.formatTime12Hour(arrivalTime);
            
            // Update card content using findViewById within the card
            CardView card = (CardView) cardView;
            LinearLayout cardLayout = (LinearLayout) card.getChildAt(0);
            if (cardLayout != null) {
                // Find the first TextView (route/company name) - it's the first child
                View firstChild = cardLayout.getChildAt(0);
                if (firstChild instanceof TextView) {
                    TextView tvCompany = (TextView) firstChild;
                    tvCompany.setText("Tuyến số " + routeNumber);
                    android.util.Log.d("SelectBusActivity", "Updated route name to: Tuyến số " + routeNumber);
                } else {
                    // Fallback: try finding by searching through children
                    TextView tvCompany = findTextViewInLayout(cardLayout, 0);
                    if (tvCompany != null) {
                        tvCompany.setText("Tuyến số " + routeNumber);
                        android.util.Log.d("SelectBusActivity", "Updated route name (fallback) to: Tuyến số " + routeNumber);
                    } else {
                        android.util.Log.e("SelectBusActivity", "Could not find TextView for route name in card");
                    }
                }
                
                // Also try to find TextView by searching all children recursively
                TextView routeNameView = findTextViewByName(cardLayout, "Perera Travels", "Gayan Express", "Shehan Travels");
                if (routeNameView != null) {
                    routeNameView.setText("Tuyến số " + routeNumber);
                    android.util.Log.d("SelectBusActivity", "Updated route name by text search to: Tuyến số " + routeNumber);
                }
                
                LinearLayout priceLayout = (LinearLayout) cardLayout.getChildAt(1);
                if (priceLayout != null) {
                    TextView tvPrice = (TextView) priceLayout.getChildAt(0);
                    if (tvPrice != null) {
                        tvPrice.setText(CurrencyHelper.formatPrice(price));
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
                
                // Update seats left - look for the text containing "Ghế còn lại" or "Seats left"
                TextView tvSeats = findTextViewByName(cardLayout, "Ghế còn lại", "Seats left");
                if (tvSeats != null) {
                    tvSeats.setText(availableSeats + " Ghế còn lại");
                    if (availableSeats < 5) {
                        tvSeats.setTextColor(getColor(R.color.red));
                    } else if (availableSeats < 15) {
                        tvSeats.setTextColor(getColor(R.color.yellow));
                    } else {
                        tvSeats.setTextColor(getColor(R.color.green));
                    }
                }
            }
            
            // Set button click listener - find button within the card
            // Try multiple ways to find the button
            Button btn = null;
            
            // Method 1: Find in cardView
            btn = cardView.findViewById(buttonId);
            
            // Method 2: If not found, search in cardLayout
            if (btn == null && cardLayout != null) {
                btn = findButtonInLayout(cardLayout, buttonId);
            }
            
            // Method 3: If still not found, try root view
            if (btn == null) {
                View rootView = findViewById(android.R.id.content);
                if (rootView != null) {
                    btn = rootView.findViewById(buttonId);
                }
            }
            
            if (btn != null) {
                // Remove any existing listeners
                btn.setOnClickListener(null);
                
                btn.setOnClickListener(v -> {
                    try {
                        android.util.Log.d("SelectBusActivity", "Button clicked for schedule ID: " + scheduleId + ", Route: " + routeNumber);
                        Intent intent = new Intent(SelectBusActivity.this, ChooseSeatActivity.class);
                        intent.putExtra("from_location", fromLocation);
                        intent.putExtra("to_location", toLocation);
                        intent.putExtra("schedule_id", scheduleId);
                        intent.putExtra("route_number", routeNumber); // Pass route number instead of company name
                        intent.putExtra("price", price);
                        intent.putExtra("departure_time", departureTime);
                        intent.putExtra("arrival_time", arrivalTime);
                        intent.putExtra("bus_type", busType);
                        intent.putExtra("available_seats", availableSeats);
                        intent.putExtra("schedule_date", selectedDate);
                        startActivity(intent);
                    } catch (Exception e) {
                        android.util.Log.e("SelectBusActivity", "Error starting ChooseSeatActivity: " + e.getMessage(), e);
                        android.widget.Toast.makeText(this, "Lỗi khi mở màn hình chọn ghế: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
                android.util.Log.d("SelectBusActivity", "Button listener set for button ID: " + buttonId + " at position " + position);
            } else {
                android.util.Log.e("SelectBusActivity", "Button not found with ID: " + buttonId + " at position " + position);
            }
            } else {
                // Hide card if no data
                cardView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            android.util.Log.e("SelectBusActivity", "Error updating bus card at position " + position + ": " + e.getMessage(), e);
            cardView.setVisibility(View.GONE);
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
    
    private TextView findTextViewByName(ViewGroup parent, String... searchTexts) {
        if (parent == null) return null;
        
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;
                String text = tv.getText().toString();
                for (String searchText : searchTexts) {
                    if (text.contains(searchText)) {
                        return tv;
                    }
                }
            } else if (child instanceof ViewGroup) {
                TextView found = findTextViewByName((ViewGroup) child, searchTexts);
                if (found != null) return found;
            }
        }
        return null;
    }
    
    private Button findButtonInLayout(ViewGroup parent, int buttonId) {
        if (parent == null) return null;
        
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getId() == buttonId && child instanceof Button) {
                return (Button) child;
            } else if (child instanceof ViewGroup) {
                Button found = findButtonInLayout((ViewGroup) child, buttonId);
                if (found != null) return found;
            }
        }
        return null;
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

