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
import java.util.Date;
import java.util.Locale;

public class SelectBusActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String fromLocation;
    private String toLocation;
    private LinearLayout busListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bus);

        dbHelper = new DatabaseHelper(this);
        
        // Get locations from intent
        fromLocation = getIntent().getStringExtra("from_location");
        toLocation = getIntent().getStringExtra("to_location");
        
        if (fromLocation == null) fromLocation = "New York, NY";
        if (toLocation == null) toLocation = "Los Angeles, CA";

        // Update route display
        TextView tvRoute = findViewById(R.id.tvRoute);
        if (tvRoute != null) {
            tvRoute.setText(fromLocation + "\n" + toLocation);
        }

        // Ensure sample schedules exist
        dbHelper.ensureSampleSchedules();
        
        // Get current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        // Format date for display
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd'th' - MMM - yyyy | EEEE", Locale.getDefault());
        String displayDate = displayFormat.format(new Date());
        
        TextView tvDate = findViewById(R.id.tvDate);
        if (tvDate != null) {
            tvDate.setText(displayDate);
        }
        
        // Get route ID
        long routeId = dbHelper.getRouteId(fromLocation, toLocation);
        
        // Load bus schedules from database
        loadBusSchedules(routeId, currentDate);

        // Setup bottom navigation (no active state for this screen)
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void loadBusSchedules(long routeId, String date) {
        Cursor cursor = null;
        
        if (routeId != -1) {
            cursor = dbHelper.getSchedulesForRoute(routeId, date);
        }
        
        // Update bus cards with database data
        updateBusCard(R.id.cardBus1, R.id.btnSelectBus1, cursor, 0);
        updateBusCard(R.id.cardBus2, R.id.btnSelectBus2, cursor, 1);
        updateBusCard(R.id.cardBus3, R.id.btnSelectBus3, cursor, 2);
        
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
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
            
            // Calculate duration
            String[] depParts = departureTime.split(":");
            String[] arrParts = arrivalTime.split(":");
            int depHour = Integer.parseInt(depParts[0]);
            int arrHour = Integer.parseInt(arrParts[0]);
            int duration = (arrHour - depHour + 24) % 24;
            if (duration == 0) duration = 1;
            
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
                        tvPrice.setText("$" + String.format("%.0f", price));
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
                        tvDuration.setText(duration + " Hr");
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

