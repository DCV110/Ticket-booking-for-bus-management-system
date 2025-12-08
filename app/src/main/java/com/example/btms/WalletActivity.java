package com.example.btms;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNav(rootView, R.id.navWallet);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }
}

