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
    private final List<String> bookedSeats = new ArrayList<>(); // Actual booked seats from database
    private final List<String> virtualBookedSeats = new ArrayList<>(); // Virtual booked seats to match available_seats count
    private final Map<String, View> seatViews = new HashMap<>(); // Map seat ID to view
    private final List<String> allSeatIds = new ArrayList<>(); // All seat IDs in order

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
        String scheduleDate = getIntent().getStringExtra("schedule_date");
        
        android.util.Log.d("ChooseSeatActivity", "Received from Intent: ID=" + scheduleId + ", Seats: " + availableSeats + ", Date: " + scheduleDate);
        
        // Refresh availableSeats from database to ensure sync
        if (scheduleId != -1) {
            int latestAvailable = dbHelper.getScheduleAvailableSeats(scheduleId);
            android.util.Log.d("ChooseSeatActivity", "Latest available from DB: " + latestAvailable);
            if (latestAvailable > 0) {
                availableSeats = latestAvailable;
            }
        }
        
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
        android.widget.TextView tvDate = findViewById(R.id.tvDate);

        if (tvDate != null && scheduleDate != null) {
            tvDate.setText(DateTimeHelper.formatDateForDisplay(scheduleDate) + " | " + DateTimeHelper.getDayOfWeekFull(scheduleDate));
        }

        if (tvCompanyName != null) {
            tvCompanyName.setText("Tuyến số " + routeNumber);
        }
        if (tvPrice != null) {
            tvPrice.setText(CurrencyHelper.formatPrice(price));
        }
        if (tvBusType != null && busType != null) {
            tvBusType.setText(busType);
        }
        if (tvDurationTime != null) {
            String timeDisplay = DateTimeHelper.formatTime12Hour(departureTime) + " - " + DateTimeHelper.formatTime12Hour(arrivalTime);
            tvDurationTime.setText(timeDisplay);
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
        
        updateSummary(); // Initial summary update
        
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

        // Get actual booked seats from database for this schedule
        loadBookedSeats();
        
        // Calculate virtual booked seats to match available_seats count
        calculateVirtualBookedSeats();
        
        // Create seats according to new design - 28 seats total (14 per floor)
        // Tầng 1: VIP seats (A1, B1, C1, A3, B3, C3, A5, B5, C5) and standard seats (A7, B7, C7, last row: A9, B9)
        String[] floor1MainSeats = {
            "A1", "B1", "C1",  // Row 1 - VIP
            "A3", "B3", "C3",  // Row 2 - VIP
            "A5", "B5", "C5",  // Row 3 - VIP
            "A7", "B7", "C7"   // Row 4 - Standard
        };
        String[] floor1LastRow = {"A9", "B9"}; // Row 5 - Standard (2 seats)
        LinearLayout lastRowFloor1 = findViewById(R.id.lastRowFloor1);
        createSeats(gridSeatsFloor1, lastRowFloor1, floor1MainSeats, floor1LastRow, true);
        
        // Tầng 2: All seats (A2-C8, last row: A10, B10)
        String[] floor2MainSeats = {
            "A2", "B2", "C2",  // Row 1
            "A4", "B4", "C4",  // Row 2
            "A6", "B6", "C6",  // Row 3
            "A8", "B8", "C8"   // Row 4
        };
        String[] floor2LastRow = {"A10", "B10"}; // Row 5 (2 seats)
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

    private void calculateVirtualBookedSeats() {
        virtualBookedSeats.clear();
        
        // Calculate total seats and required booked seats
        final int TOTAL_SEATS = 28;
        int requiredBookedSeats = TOTAL_SEATS - availableSeats; // If 34 available, need 6 booked
        int actualBookedInDb = bookedSeats.size();
        
        android.util.Log.d("ChooseSeatActivity", "Total seats: " + TOTAL_SEATS + ", Available: " + availableSeats + 
            ", Required booked: " + requiredBookedSeats + ", Actual booked in DB: " + actualBookedInDb);
        
        // If DB doesn't have enough booked seats, we need to "virtually book" some seats
        // to match the available_seats count
        int virtualBookedNeeded = Math.max(0, requiredBookedSeats - actualBookedInDb);
        
        if (virtualBookedNeeded > 0) {
            // Collect ALL seat IDs (both floors) - 28 seats total
            List<String> allSeatIdsList = new ArrayList<>();
            String[] floor1MainSeats = {"A1", "B1", "C1", "A3", "B3", "C3", "A5", "B5", "C5", "A7", "B7", "C7"};
            String[] floor1LastRow = {"A9", "B9"};
            String[] floor2MainSeats = {"A2", "B2", "C2", "A4", "B4", "C4", "A6", "B6", "C6", "A8", "B8", "C8"};
            String[] floor2LastRow = {"A10", "B10"};
            
            for (String seatId : floor1MainSeats) allSeatIdsList.add(seatId);
            for (String seatId : floor1LastRow) allSeatIdsList.add(seatId);
            for (String seatId : floor2MainSeats) allSeatIdsList.add(seatId);
            for (String seatId : floor2LastRow) allSeatIdsList.add(seatId);
            
            // Create a list of unbooked seats that we can "virtually book"
            List<String> unbookedSeats = new ArrayList<>();
            for (String seatId : allSeatIdsList) {
                if (!isSeatBooked(seatId)) {
                    unbookedSeats.add(seatId);
                }
            }
            
            // Randomly select seats to "virtually book" to match the count
            java.util.Collections.shuffle(unbookedSeats);
            for (int i = 0; i < Math.min(virtualBookedNeeded, unbookedSeats.size()); i++) {
                virtualBookedSeats.add(unbookedSeats.get(i));
            }
            
            android.util.Log.d("ChooseSeatActivity", "Virtual booked seats needed: " + virtualBookedNeeded + 
                ", Actually virtual booking: " + virtualBookedSeats.size() + " seats: " + virtualBookedSeats);
        }
    }
    
    private void createSeats(GridLayout gridLayout, LinearLayout lastRowLayout, String[] mainSeatIds, String[] lastRowSeatIds, boolean isFloor1) {
        LayoutInflater inflater = LayoutInflater.from(this);
        
        // Helper function to check if a seat is booked (either in DB or virtually)
        java.util.function.Function<String, Boolean> isSeatBookedOrVirtual = (seatId) -> {
            return isSeatBooked(seatId) || virtualBookedSeats.contains(seatId);
        };
        
        // Create main seats (4 rows x 3 columns = 12 seats)
        for (int i = 0; i < mainSeatIds.length; i++) {
            String seatId = mainSeatIds[i];
            boolean isBooked = isSeatBookedOrVirtual.apply(seatId);
            boolean isSelectable = !isBooked; // All non-booked seats are selectable
            
            View seatView = createSeatView(inflater, seatId, isFloor1, i < 9, isSelectable, isBooked); // First 9 seats (3 rows) are VIP
            
            // Set GridLayout params
            int row = i / 3;
            int col = i % 3;
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(col, 1f);
            params.rowSpec = GridLayout.spec(row);
            params.setMargins(2, 2, 2, 2);
            seatView.setLayoutParams(params);
            
            gridLayout.addView(seatView);
            seatViews.put(seatId, seatView);
        }
        
        // Create last row seats (2 seats in horizontal LinearLayout)
        if (lastRowSeatIds.length > 0) {
            lastRowLayout.setVisibility(View.VISIBLE);
            for (String seatId : lastRowSeatIds) {
                boolean isBooked = isSeatBookedOrVirtual.apply(seatId);
                boolean isSelectable = !isBooked;
                
                View seatView = createSeatView(inflater, seatId, isFloor1, false, isSelectable, isBooked);
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                params.setMargins(2, 2, 2, 2);
                seatView.setLayoutParams(params);
                
                lastRowLayout.addView(seatView);
                seatViews.put(seatId, seatView);
            }
        }
    }

    private View createSeatView(LayoutInflater inflater, String seatId, boolean isFloor1, boolean isVip, boolean isAvailable, boolean isBooked) {
        View seatView = inflater.inflate(R.layout.item_seat, null);
        
        LinearLayout container = seatView.findViewById(R.id.seatContainer);
        TextView tvSeatNumber = seatView.findViewById(R.id.tvSeatNumber);
        TextView tvVipLabel = seatView.findViewById(R.id.tvVipLabel);
        View seatIndicator = seatView.findViewById(R.id.seatIndicator);
        
        tvSeatNumber.setText(seatId);
        
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
        
        // Set click listener - only allow clicking if seat is available (not booked)
        if (!isBooked && isAvailable) {
            seatView.setOnClickListener(v -> {
                toggleSeatSelection(seatId);
            });
        } else {
            seatView.setEnabled(false);
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
    
    private void loadBookedSeats() {
        bookedSeats.clear();
        if (scheduleId <= 0) {
            android.util.Log.w("ChooseSeatActivity", "Invalid schedule ID, cannot load booked seats");
            return;
        }
        
        try {
            List<String> seats = dbHelper.getBookedSeatsForSchedule(scheduleId);
            if (seats != null) {
                bookedSeats.addAll(seats);
                android.util.Log.d("ChooseSeatActivity", "Actually loaded " + bookedSeats.size() + " booked seats from DB: " + bookedSeats);
            }
        } catch (Exception e) {
            android.util.Log.e("ChooseSeatActivity", "Error loading booked seats: " + e.getMessage(), e);
        }
    }
    
    private boolean isSeatBooked(String seatId) {
        for (String booked : bookedSeats) {
            if (booked.equals(seatId)) return true;
        }
        return false;
    }
    
    private boolean shouldShowSeat(String seatId, int currentAvailableCount) {
        // If seat is booked, don't show it
        if (isSeatBooked(seatId)) {
            return false;
        }
        
        // Count how many available seats we've shown so far
        int shownAvailableCount = 0;
        for (String seat : allSeatIds) {
            if (seat.equals(seatId)) {
                // We've reached this seat, check if we should show it
                return shownAvailableCount < availableSeats;
            }
            if (!isSeatBooked(seat)) {
                shownAvailableCount++;
            }
        }
        
        return false;
    }

    private void updateSummary() {
        android.widget.TextView tvSeatsValue = findViewById(R.id.tvSeatsValue);
        android.widget.TextView tvTotalFare = findViewById(R.id.tvTotalFare);
        android.widget.TextView tvSeatsLeft = findViewById(R.id.tvSeatsLeft);
        
        if (tvSeatsValue != null) {
            tvSeatsValue.setText(selectedSeats.isEmpty() ? "-" : selectedSeats.toString().replace("[", "").replace("]", ""));
        }
        
        if (tvTotalFare != null) {
            double total = selectedSeats.size() * price;
            tvTotalFare.setText(CurrencyHelper.formatPrice(total));
        }
        
        if (tvSeatsLeft != null) {
            int currentLeft = availableSeats - selectedSeats.size();
            if (currentLeft >= 0) {
                tvSeatsLeft.setText(currentLeft + " Seats left");
                tvSeatsLeft.setTextColor(getColor(R.color.green));
            } else {
                tvSeatsLeft.setText("0 Seats left");
                tvSeatsLeft.setTextColor(getColor(R.color.red));
            }
        }
    }
}

