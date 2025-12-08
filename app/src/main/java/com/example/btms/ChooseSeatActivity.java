package com.example.btms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ChooseSeatActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String fromLocation;
    private String toLocation;
    private long scheduleId;
    private String companyName;
    private double price;

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
        
        // Update route display
        android.widget.TextView tvRoute = findViewById(R.id.tvRoute);
        if (tvRoute != null && fromLocation != null && toLocation != null) {
            tvRoute.setText(fromLocation + "\n" + toLocation);
        }
        
        // Update bus info
        android.widget.TextView tvBusInfo = findViewById(R.id.cardBusInfo);
        if (tvBusInfo != null && companyName != null) {
            // Update bus company name in the card
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

        // Create seat buttons
        for (int i = 1; i <= 40; i++) {
            ToggleButton seatButton = new ToggleButton(this);
            seatButton.setText(String.valueOf(i));
            seatButton.setTextOff(String.valueOf(i));
            seatButton.setTextOn(String.valueOf(i));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % 4, 1f);
            params.rowSpec = GridLayout.spec(i / 4);
            params.setMargins(4, 4, 4, 4);
            seatButton.setLayoutParams(params);
            gridSeats.addView(seatButton);
        }

        btnProceed.setOnClickListener(v -> {
            String boardingPoint = actvBoardingPoint.getText().toString().trim();
            String dropPoint = actvDropPoint.getText().toString().trim();
            
            if (boardingPoint.isEmpty() || dropPoint.isEmpty()) {
                // Show error
                return;
            }
            
            Intent intent = new Intent(ChooseSeatActivity.this, TravellerInfoActivity.class);
            intent.putExtra("from_location", fromLocation);
            intent.putExtra("to_location", toLocation);
            intent.putExtra("schedule_id", scheduleId);
            intent.putExtra("company_name", companyName);
            intent.putExtra("price", price);
            intent.putExtra("boarding_point", boardingPoint);
            intent.putExtra("drop_point", dropPoint);
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
}

