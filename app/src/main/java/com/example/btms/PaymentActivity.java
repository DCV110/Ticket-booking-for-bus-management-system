package com.example.btms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private String[] paymentMethods = {"VISA", "DISCOVER", "MASTERCARD", "AMEX"};
    private String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private String[] years = {"2024", "2025", "2026", "2027", "2028", "2029", "2030"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        AutoCompleteTextView actvPaymentMethod = findViewById(R.id.actvPaymentMethod);
        AutoCompleteTextView actvMonth = findViewById(R.id.actvMonth);
        AutoCompleteTextView actvYear = findViewById(R.id.actvYear);
        Button btnPayNow = findViewById(R.id.btnPayNow);

        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, paymentMethods);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, months);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, years);

        actvPaymentMethod.setAdapter(paymentAdapter);
        actvMonth.setAdapter(monthAdapter);
        actvYear.setAdapter(yearAdapter);

        btnPayNow.setOnClickListener(v -> {
            // Get all data from intent
            String fromLocation = getIntent().getStringExtra("from_location");
            String toLocation = getIntent().getStringExtra("to_location");
            long scheduleId = getIntent().getLongExtra("schedule_id", -1);
            String companyName = getIntent().getStringExtra("company_name");
            double price = getIntent().getDoubleExtra("price", 0);
            String boardingPoint = getIntent().getStringExtra("boarding_point");
            String dropPoint = getIntent().getStringExtra("drop_point");
            
            // Get passenger info from fields
            com.google.android.material.textfield.TextInputEditText etPassenger1Name = findViewById(R.id.etPassenger1Name);
            com.google.android.material.textfield.TextInputEditText etPassenger1Age = findViewById(R.id.etPassenger1Age);
            android.widget.RadioGroup rgPassenger1Gender = findViewById(R.id.rgPassenger1Gender);
            
            String passengerName = etPassenger1Name != null ? etPassenger1Name.getText().toString() : "";
            String passengerAge = etPassenger1Age != null ? etPassenger1Age.getText().toString() : "";
            int selectedGenderId = rgPassenger1Gender != null ? rgPassenger1Gender.getCheckedRadioButtonId() : -1;
            String gender = selectedGenderId == R.id.rbPassenger1Male ? "Male" : "Female";
            
            // Save booking to database
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            
            // Get user email from shared preferences
            android.content.SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("user_email", "hello@example.com");
            
            // Get seat numbers (in real app, get from seat selection)
            String seatNumbers = "17, 18"; // TODO: Get from ChooseSeatActivity
            
            // Format booking date
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            String bookingDate = sdf.format(new java.util.Date());
            
            long bookingId = dbHelper.insertBooking(
                    scheduleId,
                    userEmail,
                    passengerName,
                    passengerAge,
                    gender,
                    seatNumbers,
                    boardingPoint,
                    dropPoint,
                    price,
                    "confirmed",
                    bookingDate
            );
            dbHelper.close();
            
            Intent intent = new Intent(PaymentActivity.this, OrderConfirmationActivity.class);
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("from_location", fromLocation);
            intent.putExtra("to_location", toLocation);
            startActivity(intent);
            finish();
        });

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNav(rootView, R.id.navWallet);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }
}

