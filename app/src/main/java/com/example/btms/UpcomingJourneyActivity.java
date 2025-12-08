package com.example.btms;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UpcomingJourneyActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_journey);

        dbHelper = new DatabaseHelper(this);
        
        // Load upcoming journeys from database
        // For demo, we'll use a sample email
        String userEmail = "hello@example.com"; // In real app, get from shared preferences or login
        loadUpcomingJourneys(userEmail);

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNav(rootView, R.id.navTicket);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void loadUpcomingJourneys(String userEmail) {
        Cursor cursor = dbHelper.getUserBookings(userEmail);
        
        // The layout already has sample data, so we'll keep it as is
        // In a real app, you would dynamically populate the cards based on cursor data
        
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
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

