package com.example.btms;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class TravellerInfoActivity extends AppCompatActivity {

    private List<View> passengerViews = new ArrayList<>();
    private java.util.ArrayList<String> selectedSeatsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveller_info);

        // Get selected seats from intent
        selectedSeatsList = getIntent().getStringArrayListExtra("selected_seats");
        if (selectedSeatsList == null) {
            selectedSeatsList = new ArrayList<>();
        }

        // Create passenger forms based on number of selected seats
        createPassengerForms();

        Button btnProceedToBook = findViewById(R.id.btnProceedToBook);

        btnProceedToBook.setOnClickListener(v -> {
            // Validate all passenger forms
            if (!validatePassengerForms()) {
                android.widget.Toast.makeText(this, "Vui lòng điền đầy đủ thông tin hành khách", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            // Get data from intent
            String fromLocation = getIntent().getStringExtra("from_location");
            String toLocation = getIntent().getStringExtra("to_location");
            long scheduleId = getIntent().getLongExtra("schedule_id", -1);
            int routeNumber = getIntent().getIntExtra("route_number", 0);
            double price = getIntent().getDoubleExtra("price", 0);
            String boardingPoint = getIntent().getStringExtra("boarding_point");
            String dropPoint = getIntent().getStringExtra("drop_point");
            
            // Get selected seats
            String selectedSeats = "";
            if (selectedSeatsList != null && !selectedSeatsList.isEmpty()) {
                selectedSeats = String.join(", ", selectedSeatsList);
            }
            
            Intent intent = new Intent(TravellerInfoActivity.this, PaymentActivity.class);
            intent.putExtra("from_location", fromLocation);
            intent.putExtra("to_location", toLocation);
            intent.putExtra("schedule_id", scheduleId);
            intent.putExtra("route_number", routeNumber); // Pass route number instead of company name
            intent.putExtra("price", price);
            intent.putExtra("boarding_point", boardingPoint);
            intent.putExtra("drop_point", dropPoint);
            intent.putExtra("selected_seats", selectedSeats);
            intent.putExtra("departure_time", getIntent().getStringExtra("departure_time"));
            intent.putExtra("arrival_time", getIntent().getStringExtra("arrival_time"));
            intent.putExtra("bus_type", getIntent().getStringExtra("bus_type"));
            intent.putExtra("schedule_date", getIntent().getStringExtra("schedule_date"));
            intent.putExtra("total_fare", getIntent().getDoubleExtra("total_fare", price));
            
            // Pass passenger data
            intent.putStringArrayListExtra("passenger_names", getPassengerNames());
            intent.putStringArrayListExtra("passenger_ages", getPassengerAges());
            intent.putStringArrayListExtra("passenger_genders", getPassengerGenders());
            
            startActivity(intent);
        });

        // Setup bottom navigation (no active state for this screen)
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void createPassengerForms() {
        LinearLayout passengersContainer = findViewById(R.id.llPassengersContainer);
        if (passengersContainer == null || selectedSeatsList == null || selectedSeatsList.isEmpty()) {
            return;
        }

        passengersContainer.removeAllViews();
        passengerViews.clear();

        LayoutInflater inflater = LayoutInflater.from(this);
        int seatCount = selectedSeatsList.size();

        for (int i = 0; i < seatCount; i++) {
            View passengerCard = inflater.inflate(R.layout.item_passenger_card, passengersContainer, false);
            
            TextView tvPassengerLabel = passengerCard.findViewById(R.id.tvPassengerLabel);
            if (tvPassengerLabel != null) {
                String seatNumber = selectedSeatsList.get(i);
                tvPassengerLabel.setText("Hành khách " + (i + 1) + " (Ghế " + seatNumber + ")");
            }

            passengersContainer.addView(passengerCard);
            passengerViews.add(passengerCard);
        }
    }

    private boolean validatePassengerForms() {
        for (View passengerView : passengerViews) {
            TextInputEditText etName = passengerView.findViewById(R.id.etPassengerName);
            TextInputEditText etAge = passengerView.findViewById(R.id.etPassengerAge);
            RadioGroup rgGender = passengerView.findViewById(R.id.rgPassengerGender);

            if (etName == null || etName.getText() == null || etName.getText().toString().trim().isEmpty()) {
                return false;
            }
            if (etAge == null || etAge.getText() == null || etAge.getText().toString().trim().isEmpty()) {
                return false;
            }
            if (rgGender == null || rgGender.getCheckedRadioButtonId() == -1) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> getPassengerNames() {
        ArrayList<String> names = new ArrayList<>();
        for (View passengerView : passengerViews) {
            TextInputEditText etName = passengerView.findViewById(R.id.etPassengerName);
            if (etName != null && etName.getText() != null) {
                names.add(etName.getText().toString().trim());
            } else {
                names.add("");
            }
        }
        return names;
    }

    private ArrayList<String> getPassengerAges() {
        ArrayList<String> ages = new ArrayList<>();
        for (View passengerView : passengerViews) {
            TextInputEditText etAge = passengerView.findViewById(R.id.etPassengerAge);
            if (etAge != null && etAge.getText() != null) {
                ages.add(etAge.getText().toString().trim());
            } else {
                ages.add("");
            }
        }
        return ages;
    }

    private ArrayList<String> getPassengerGenders() {
        ArrayList<String> genders = new ArrayList<>();
        for (View passengerView : passengerViews) {
            RadioGroup rgGender = passengerView.findViewById(R.id.rgPassengerGender);
            if (rgGender != null) {
                int selectedId = rgGender.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedButton = passengerView.findViewById(selectedId);
                    if (selectedButton != null) {
                        genders.add(selectedButton.getText().toString());
                    } else {
                        genders.add("");
                    }
                } else {
                    genders.add("");
                }
            } else {
                genders.add("");
            }
        }
        return genders;
    }
}

