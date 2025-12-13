package com.example.btms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private String[] paymentMethods = {"VISA", "MoMo", "Credit/Debit Card", "PayPal", "Cash"};
    private String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private String[] years = {"2024", "2025", "2026", "2027", "2028", "2029", "2030"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Get user name from SharedPreferences or database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
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
        android.widget.TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null) {
            if (userName != null && !userName.isEmpty()) {
                tvGreeting.setText("Xin chào " + userName + "!");
            } else {
                tvGreeting.setText("Xin chào!");
            }
        }

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
            int routeNumber = getIntent().getIntExtra("route_number", 0);
            double price = getIntent().getDoubleExtra("price", 0);
            String boardingPoint = getIntent().getStringExtra("boarding_point");
            String dropPoint = getIntent().getStringExtra("drop_point");
            
            // Get passenger info from intent (multiple passengers)
            java.util.ArrayList<String> passengerNames = getIntent().getStringArrayListExtra("passenger_names");
            java.util.ArrayList<String> passengerAges = getIntent().getStringArrayListExtra("passenger_ages");
            java.util.ArrayList<String> passengerGenders = getIntent().getStringArrayListExtra("passenger_genders");
            
            // Use first passenger for main booking record (or combine all if needed)
            String passengerName = (passengerNames != null && !passengerNames.isEmpty()) ? passengerNames.get(0) : "";
            String passengerAge = (passengerAges != null && !passengerAges.isEmpty()) ? passengerAges.get(0) : "";
            String gender = (passengerGenders != null && !passengerGenders.isEmpty()) ? passengerGenders.get(0) : "";
            
            // If multiple passengers, combine names (for display purposes)
            if (passengerNames != null && passengerNames.size() > 1) {
                StringBuilder namesBuilder = new StringBuilder();
                for (int i = 0; i < passengerNames.size(); i++) {
                    if (i > 0) namesBuilder.append(", ");
                    namesBuilder.append(passengerNames.get(i));
                }
                passengerName = namesBuilder.toString();
            }
            
            // Save booking to database (reuse existing dbHelper)
            // Get user email from shared preferences (reuse existing sharedPreferences)
            String bookingUserEmail = sharedPreferences.getString("user_email", "hello@example.com");
            
            // Get seat numbers from intent
            String seatNumbers = getIntent().getStringExtra("selected_seats");
            if (seatNumbers == null || seatNumbers.isEmpty()) {
                seatNumbers = "";
            }
            
            // Format booking date
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            String bookingDate = sdf.format(new java.util.Date());
            
            // Get total fare from intent (if multiple seats)
            double totalFare = getIntent().getDoubleExtra("total_fare", price);
            
            long bookingId = dbHelper.insertBooking(
                    scheduleId,
                    bookingUserEmail,
                    passengerName,
                    passengerAge,
                    gender,
                    seatNumbers,
                    boardingPoint,
                    dropPoint,
                    totalFare,
                    "confirmed",
                    bookingDate
            );
            
            // Get schedule details for notification
            String departureTime = getIntent().getStringExtra("departure_time");
            String scheduleDate = getIntent().getStringExtra("schedule_date");
            
            // If schedule date not in intent, try to get from database
            if (scheduleDate == null || scheduleDate.isEmpty()) {
                android.database.Cursor scheduleCursor = dbHelper.getScheduleDetails(scheduleId);
                if (scheduleCursor != null && scheduleCursor.moveToFirst()) {
                    scheduleDate = scheduleCursor.getString(scheduleCursor.getColumnIndexOrThrow("date"));
                    if (departureTime == null || departureTime.isEmpty()) {
                        departureTime = scheduleCursor.getString(scheduleCursor.getColumnIndexOrThrow("departure_time"));
                    }
                    scheduleCursor.close();
                }
            }
            
            dbHelper.close();
            
            // Show booking confirmation notification
            if (departureTime != null && scheduleDate != null) {
                NotificationHelper.showBookingConfirmationNotification(
                        this, fromLocation, toLocation, departureTime, scheduleDate);
                
                // Schedule reminder notification 30 minutes before departure
                NotificationHelper.scheduleDepartureReminder(
                        this, bookingId, fromLocation, toLocation, departureTime, scheduleDate);
            }
            
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

