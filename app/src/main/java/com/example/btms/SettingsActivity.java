package com.example.btms;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNav(rootView, R.id.navSettings);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }
}

