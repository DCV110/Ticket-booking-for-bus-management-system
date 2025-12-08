package com.example.btms;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            dbHelper = new DatabaseHelper(this);
            
            AutoCompleteTextView actvFrom = findViewById(R.id.actvFrom);
            AutoCompleteTextView actvTo = findViewById(R.id.actvTo);
            Button btnSearch = findViewById(R.id.btnSearch);
            ImageButton btnSwap = findViewById(R.id.btnSwap);
            Button btnToday = findViewById(R.id.btnToday);
            Button btnTomorrow = findViewById(R.id.btnTomorrow);
            Button btnOther = findViewById(R.id.btnOther);

            // Get all locations from database
            List<String> locations = dbHelper.getAllLocations();
            if (locations == null || locations.isEmpty()) {
                // If no locations, use default list
                locations = new java.util.ArrayList<>();
                locations.add("New York, NY");
                locations.add("Los Angeles, CA");
                locations.add("Chicago, IL");
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
            if (hasFocus && actvFrom.getText().toString().isEmpty()) {
                actvFrom.showDropDown();
            }
        });
        
        actvTo.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && actvTo.getText().toString().isEmpty()) {
                actvTo.showDropDown();
            }
        });
        
        // Show dropdown when clicked
        actvFrom.setOnClickListener(v -> {
            if (actvFrom.getText().toString().isEmpty()) {
                actvFrom.showDropDown();
            }
        });
        
        actvTo.setOnClickListener(v -> {
            if (actvTo.getText().toString().isEmpty()) {
                actvTo.showDropDown();
            }
        });

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

        // Date selection buttons
        btnToday.setOnClickListener(v -> {
            // Set today's date
            btnToday.setBackgroundTintList(getColorStateList(R.color.red));
            btnToday.setTextColor(getColor(R.color.white));
            btnTomorrow.setBackgroundTintList(null);
            btnTomorrow.setTextColor(getColor(R.color.red));
            btnOther.setBackgroundTintList(null);
            btnOther.setTextColor(getColor(R.color.red));
        });

        btnTomorrow.setOnClickListener(v -> {
            // Set tomorrow's date
            btnTomorrow.setBackgroundTintList(getColorStateList(R.color.red));
            btnTomorrow.setTextColor(getColor(R.color.white));
            btnToday.setBackgroundTintList(null);
            btnToday.setTextColor(getColor(R.color.red));
            btnOther.setBackgroundTintList(null);
            btnOther.setTextColor(getColor(R.color.red));
        });

        btnOther.setOnClickListener(v -> {
            // Show date picker
            btnOther.setBackgroundTintList(getColorStateList(R.color.red));
            btnOther.setTextColor(getColor(R.color.white));
            btnToday.setBackgroundTintList(null);
            btnToday.setTextColor(getColor(R.color.red));
            btnTomorrow.setBackgroundTintList(null);
            btnTomorrow.setTextColor(getColor(R.color.red));
        });

        btnSearch.setOnClickListener(v -> {
            String from = actvFrom.getText().toString().trim();
            String to = actvTo.getText().toString().trim();
            
            if (from.isEmpty() || to.isEmpty()) {
                // Show error message
                return;
            }
            
            Intent intent = new Intent(HomeActivity.this, SelectBusActivity.class);
            intent.putExtra("from_location", from);
            intent.putExtra("to_location", to);
            startActivity(intent);
        });

            // Setup bottom navigation
            View rootView = findViewById(android.R.id.content);
            if (rootView != null) {
                BottomNavHelper.setupBottomNav(rootView, R.id.navHome);
                BottomNavHelper.setupBottomNavListeners(this, rootView);
            }
        } catch (Exception e) {
            android.util.Log.e("HomeActivity", "Error in onCreate: " + e.getMessage(), e);
            android.widget.Toast.makeText(this, "Error loading home screen. Please try again.", android.widget.Toast.LENGTH_LONG).show();
            finish();
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

