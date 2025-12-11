package com.example.btms;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ChooseSeatActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String fromLocation;
    private String toLocation;
    private long scheduleId;
    private String companyName;
    private double price;
    private String departureTime;
    private String arrivalTime;
    private String busType;
    private int availableSeats;

    private final List<Integer> selectedSeats = new ArrayList<>();
    private final int[] bookedSeats = {2, 5, 8, 17, 20, 25}; // sample booked seats

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seat);

        dbHelper = new DatabaseHelper(this);
        
        // Get data from intent
        fromLocation = getIntent().getStringExtra("from_location");
        toLocation = getIntent().getStringExtra("to_location");
        scheduleId = getIntent().getLongExtra("schedule_id", -1);
        companyName = getIntent().getStringExtra("company_name");
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

        if (tvCompanyName != null && companyName != null) {
            tvCompanyName.setText(companyName);
        }
        if (tvPrice != null) {
            tvPrice.setText("LKR " + String.format("%.0f", price));
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
        
        // Get terminals/stops for the route
        List<String> terminals = dbHelper.getTerminalsForRoute(fromLocation, toLocation);
        if (terminals.isEmpty()) {
            terminals.add("Main Terminal");
            terminals.add("Downtown Terminal");
            terminals.add("Airport Terminal");
        }
        
        AutoCompleteTextView actvBoardingPoint = findViewById(R.id.actvBoardingPoint);
        AutoCompleteTextView actvDropPoint = findViewById(R.id.actvDropPoint);
        Button btnProceed = findViewById(R.id.btnProceed);
        GridLayout gridSeats = findViewById(R.id.gridSeats);

        ArrayAdapter<String> boardingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, terminals);
        ArrayAdapter<String> dropAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, terminals);

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

        // Create seat buttons with states: booked (red), available (gray), selected (blue)
        final int totalSeats = 40;
        for (int i = 1; i <= totalSeats; i++) {
            ToggleButton seatButton = new ToggleButton(this);
            seatButton.setText(String.valueOf(i));
            seatButton.setTextOff(String.valueOf(i));
            seatButton.setTextOn(String.valueOf(i));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec((i - 1) % 4, 1f);
            params.rowSpec = GridLayout.spec((i - 1) / 4);
            params.setMargins(8, 8, 8, 8);
            seatButton.setLayoutParams(params);

            boolean isBooked = isSeatBooked(i);
            if (isBooked) {
                seatButton.setEnabled(false);
                seatButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.red)));
                seatButton.setTextColor(getColor(R.color.white));
            } else {
                seatButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.LTGRAY));
                seatButton.setTextColor(getColor(R.color.text_primary));
                seatButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int seatNumber = Integer.parseInt(buttonView.getText().toString());
                    if (isChecked) {
                        if (!selectedSeats.contains(seatNumber)) {
                            selectedSeats.add(seatNumber);
                        }
                        buttonView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.primary)));
                        buttonView.setTextColor(getColor(R.color.white));
                    } else {
                        selectedSeats.remove(Integer.valueOf(seatNumber));
                        buttonView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.LTGRAY));
                        buttonView.setTextColor(getColor(R.color.text_primary));
                    }
                    updateSummary();
                });
            }

            gridSeats.addView(seatButton);
        }

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
            intent.putExtra("company_name", companyName);
            intent.putExtra("price", price);
            intent.putExtra("departure_time", departureTime);
            intent.putExtra("arrival_time", arrivalTime);
            intent.putExtra("bus_type", busType);
            intent.putExtra("boarding_point", boardingPoint);
            intent.putExtra("drop_point", dropPoint);
            intent.putIntegerArrayListExtra("selected_seats", new ArrayList<>(selectedSeats));
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

    private boolean isSeatBooked(int seatNumber) {
        for (int booked : bookedSeats) {
            if (booked == seatNumber) return true;
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
            tvTotalFare.setText("LKR " + String.format("%.0f", total));
        }
    }
}

