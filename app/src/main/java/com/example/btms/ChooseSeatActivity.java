package com.example.btms;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseSeatActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String fromLocation;
    private String toLocation;
    private long scheduleId;
    private int routeNumber;
    private double price;
    private String departureTime;
    private String arrivalTime;
    private String busType;
    private int availableSeats;

    private final List<String> selectedSeats = new ArrayList<>();
    private final String[] bookedSeats = {"B3", "C5", "A8", "B10", "C12"}; // sample booked seats
    private final Map<String, View> seatViews = new HashMap<>(); // Map seat ID to view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seat);

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
        
        // Get data from intent
        fromLocation = getIntent().getStringExtra("from_location");
        toLocation = getIntent().getStringExtra("to_location");
        scheduleId = getIntent().getLongExtra("schedule_id", -1);
        routeNumber = getIntent().getIntExtra("route_number", 0);
        price = getIntent().getDoubleExtra("price", 0);
        departureTime = getIntent().getStringExtra("departure_time");
        arrivalTime = getIntent().getStringExtra("arrival_time");
        busType = getIntent().getStringExtra("bus_type");
        availableSeats = getIntent().getIntExtra("available_seats", 0);
        
        // Update route display
        android.widget.TextView tvRoute = findViewById(R.id.tvRoute);
        if (tvRoute != null && fromLocation != null && toLocation != null) {
            tvRoute.setText(fromLocation + "\n" + toLocation);
        }
        
        // Update bus info
        android.widget.TextView tvCompanyName = findViewById(R.id.tvCompanyName);
        android.widget.TextView tvPrice = findViewById(R.id.tvPrice);
        android.widget.TextView tvBusType = findViewById(R.id.tvBusType);
        android.widget.TextView tvDurationTime = findViewById(R.id.tvDurationTime);
        android.widget.TextView tvSeatsLeft = findViewById(R.id.tvSeatsLeft);

        if (tvCompanyName != null) {
            tvCompanyName.setText("Tuyến số " + routeNumber);
        }
        if (tvPrice != null) {
            tvPrice.setText(String.format("%.0f", price) + " VNĐ");
        }
        if (tvBusType != null && busType != null) {
            tvBusType.setText(busType);
        }
        if (tvDurationTime != null) {
            String durationText = "";
            if (departureTime != null && arrivalTime != null) {
                durationText = departureTime + " - " + arrivalTime;
            }
            tvDurationTime.setText(durationText.isEmpty() ? "Schedule" : durationText);
        }
        if (tvSeatsLeft != null) {
            if (availableSeats > 0) {
                tvSeatsLeft.setText(availableSeats + " Seats left");
                tvSeatsLeft.setTextColor(getColor(R.color.green));
            } else {
                tvSeatsLeft.setText("Seats availability");
                tvSeatsLeft.setTextColor(getColor(R.color.text_secondary));
            }
        }
        
        // Get street names for boarding point (from district) and drop point (to district)
        String fromCity = fromLocation.split(",")[0].trim();
        String toCity = toLocation.split(",")[0].trim();
        
        List<String> boardingStreets = dbHelper.getStreetsForDistrict(fromCity);
        List<String> dropStreets = dbHelper.getStreetsForDistrict(toCity);
        
        // Fallback if no streets found
        if (boardingStreets.isEmpty()) {
            boardingStreets.add("Đường " + fromCity);
        }
        if (dropStreets.isEmpty()) {
            dropStreets.add("Đường " + toCity);
        }
        
        AutoCompleteTextView actvBoardingPoint = findViewById(R.id.actvBoardingPoint);
        AutoCompleteTextView actvDropPoint = findViewById(R.id.actvDropPoint);
        Button btnProceed = findViewById(R.id.btnProceed);
        GridLayout gridSeatsFloor1 = findViewById(R.id.gridSeatsFloor1);
        GridLayout gridSeatsFloor2 = findViewById(R.id.gridSeatsFloor2);

        ArrayAdapter<String> boardingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, boardingStreets);
        ArrayAdapter<String> dropAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, dropStreets);

        actvBoardingPoint.setAdapter(boardingAdapter);
        actvBoardingPoint.setThreshold(1);
        actvBoardingPoint.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) actvBoardingPoint.showDropDown();
        });
        
        actvDropPoint.setAdapter(dropAdapter);
        actvDropPoint.setThreshold(1);
        actvDropPoint.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) actvDropPoint.showDropDown();
        });

        // Create seats according to new design
        // Tầng 1: VIP seats (A1, B1, C1, A3, B3, C3, A5, B5, C5) and standard seats (A7-C9, last row: A11, A13, B11, C11, C13)
        String[] floor1MainSeats = {
            "A1", "B1", "C1",  // Row 1 - VIP
            "A3", "B3", "C3",  // Row 2 - VIP
            "A5", "B5", "C5",  // Row 3 - VIP
            "A7", "B7", "C7",  // Row 4 - Standard
            "A9", "B9", "C9"   // Row 5 - Standard
        };
        String[] floor1LastRow = {"A11", "A13", "B11", "C11", "C13"}; // Row 6 - Standard (5 seats)
        LinearLayout lastRowFloor1 = findViewById(R.id.lastRowFloor1);
        createSeats(gridSeatsFloor1, lastRowFloor1, floor1MainSeats, floor1LastRow, true);
        
        // Tầng 2: All seats (A2-C10, last row: A12, A14, B12, C12, C14)
        String[] floor2MainSeats = {
            "A2", "B2", "C2",  // Row 1
            "A4", "B4", "C4",  // Row 2
            "A6", "B6", "C6",  // Row 3
            "A8", "B8", "C8",  // Row 4
            "A10", "B10", "C10"  // Row 5
        };
        String[] floor2LastRow = {"A12", "A14", "B12", "C12", "C14"}; // Row 6 (5 seats)
        LinearLayout lastRowFloor2 = findViewById(R.id.lastRowFloor2);
        createSeats(gridSeatsFloor2, lastRowFloor2, floor2MainSeats, floor2LastRow, false);

        btnProceed.setOnClickListener(v -> {
            String boardingPoint = actvBoardingPoint.getText().toString().trim();
            String dropPoint = actvDropPoint.getText().toString().trim();
            
            if (boardingPoint.isEmpty() || dropPoint.isEmpty()) {
                android.widget.Toast.makeText(this, "Please select boarding and drop points", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedSeats.isEmpty()) {
                android.widget.Toast.makeText(this, "Please select at least one seat", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            
            String scheduleDate = getIntent().getStringExtra("schedule_date");
            
            Intent intent = new Intent(ChooseSeatActivity.this, TravellerInfoActivity.class);
            intent.putExtra("from_location", fromLocation);
            intent.putExtra("to_location", toLocation);
            intent.putExtra("schedule_id", scheduleId);
            intent.putExtra("route_number", routeNumber); // Pass route number instead of company name
            intent.putExtra("price", price);
            intent.putExtra("departure_time", departureTime);
            intent.putExtra("arrival_time", arrivalTime);
            intent.putExtra("bus_type", busType);
            intent.putExtra("schedule_date", scheduleDate);
            intent.putExtra("boarding_point", boardingPoint);
            intent.putExtra("drop_point", dropPoint);
            intent.putStringArrayListExtra("selected_seats", new ArrayList<>(selectedSeats));
            intent.putExtra("total_fare", selectedSeats.size() * price);
            startActivity(intent);
        });

        // Setup bottom navigation (no active state for this screen)
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }
    
    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }

    private void createSeats(GridLayout gridLayout, LinearLayout lastRowLayout, String[] mainSeatIds, String[] lastRowSeatIds, boolean isFloor1) {
        LayoutInflater inflater = LayoutInflater.from(this);
        
        // Create main seats (5 rows x 3 columns = 15 seats)
        for (int i = 0; i < mainSeatIds.length; i++) {
            String seatId = mainSeatIds[i];
            View seatView = createSeatView(inflater, seatId, isFloor1, i < 9); // First 9 are VIP on floor 1
            
            // Set GridLayout params
            int row = i / 3;
            int col = i % 3;
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(col, 1f);
            params.rowSpec = GridLayout.spec(row);
            params.setMargins(4, 4, 4, 4);
            seatView.setLayoutParams(params);
            
            gridLayout.addView(seatView);
            seatViews.put(seatId, seatView);
        }
        
        // Create last row seats (5 seats in horizontal LinearLayout)
        if (lastRowSeatIds.length > 0) {
            lastRowLayout.setVisibility(View.VISIBLE);
            for (String seatId : lastRowSeatIds) {
                View seatView = createSeatView(inflater, seatId, isFloor1, false); // Last row seats are not VIP
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                params.setMargins(4, 4, 4, 4);
                seatView.setLayoutParams(params);
                
                lastRowLayout.addView(seatView);
                seatViews.put(seatId, seatView);
            }
        }
    }
    
    private View createSeatView(LayoutInflater inflater, String seatId, boolean isFloor1, boolean isVip) {
        View seatView = inflater.inflate(R.layout.item_seat, null);
        
        LinearLayout container = seatView.findViewById(R.id.seatContainer);
        TextView tvSeatNumber = seatView.findViewById(R.id.tvSeatNumber);
        TextView tvVipLabel = seatView.findViewById(R.id.tvVipLabel);
        View seatIndicator = seatView.findViewById(R.id.seatIndicator);
        
        tvSeatNumber.setText(seatId);
        
        // Check if seat is booked
        boolean isBooked = isSeatBooked(seatId);
        
        // Apply color based on floor and status
        if (isBooked) {
            container.setBackgroundResource(R.drawable.bg_seat_pair_booked);
            tvSeatNumber.setTextColor(getColor(R.color.white));
            if (tvVipLabel != null) {
                tvVipLabel.setTextColor(getColor(R.color.white));
            }
            seatView.setEnabled(false);
            seatView.setAlpha(0.6f);
        } else if (isFloor1) {
            // Floor 1: gray color scheme
            container.setBackgroundResource(R.drawable.bg_seat_standard);
            tvSeatNumber.setTextColor(getColor(R.color.text_primary));
        } else {
            // Floor 2: orange color scheme
            container.setBackgroundResource(R.drawable.bg_seat_floor2);
            tvSeatNumber.setTextColor(getColor(R.color.orange_dark));
        }
        
        // Handle VIP seats: show VIP label, hide indicator
        if (isVip && !isBooked) {
            if (tvVipLabel != null) {
                tvVipLabel.setVisibility(View.VISIBLE);
            }
            if (seatIndicator != null) {
                seatIndicator.setVisibility(View.GONE);
            }
        } else {
            if (tvVipLabel != null) {
                tvVipLabel.setVisibility(View.GONE);
            }
            if (seatIndicator != null) {
                seatIndicator.setVisibility(View.VISIBLE);
                if (isFloor1) {
                    seatIndicator.setBackgroundResource(R.drawable.bg_seat_standard);
                } else {
                    seatIndicator.setBackgroundResource(R.drawable.bg_seat_floor2);
                }
            }
        }
        
        // Set click listener
        if (!isBooked) {
            seatView.setOnClickListener(v -> {
                toggleSeatSelection(seatId);
            });
        }
        
        return seatView;
    }
    
    private void toggleSeatSelection(String seatId) {
        View seatView = seatViews.get(seatId);
        if (seatView == null) return;
        
        View selectedIndicator = seatView.findViewById(R.id.selectedIndicator);
        
        if (selectedSeats.contains(seatId)) {
            // Deselect
            selectedSeats.remove(seatId);
            selectedIndicator.setVisibility(View.GONE);
        } else {
            // Select
            selectedSeats.add(seatId);
            selectedIndicator.setVisibility(View.VISIBLE);
        }
        
        updateSummary();
    }
    
    private boolean isSeatBooked(String seatId) {
        for (String booked : bookedSeats) {
            if (booked.equals(seatId)) return true;
        }
        return false;
    }

    private void updateSummary() {
        android.widget.TextView tvSeatsValue = findViewById(R.id.tvSeatsValue);
        android.widget.TextView tvTotalFare = findViewById(R.id.tvTotalFare);
        if (tvSeatsValue != null) {
            tvSeatsValue.setText(selectedSeats.isEmpty() ? "-" : selectedSeats.toString().replace("[", "").replace("]", ""));
        }
        if (tvTotalFare != null) {
            double total = selectedSeats.size() * price;
            tvTotalFare.setText(String.format("%.0f", total) + " VNĐ");
        }
    }
}

