package com.example.btms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TravellerInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveller_info);

        Button btnProceedToBook = findViewById(R.id.btnProceedToBook);

        btnProceedToBook.setOnClickListener(v -> {
            // Get data from intent
            String fromLocation = getIntent().getStringExtra("from_location");
            String toLocation = getIntent().getStringExtra("to_location");
            long scheduleId = getIntent().getLongExtra("schedule_id", -1);
            String companyName = getIntent().getStringExtra("company_name");
            double price = getIntent().getDoubleExtra("price", 0);
            String boardingPoint = getIntent().getStringExtra("boarding_point");
            String dropPoint = getIntent().getStringExtra("drop_point");
            
            Intent intent = new Intent(TravellerInfoActivity.this, PaymentActivity.class);
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
}

