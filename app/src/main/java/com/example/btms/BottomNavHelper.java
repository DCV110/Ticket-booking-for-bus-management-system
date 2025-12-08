package com.example.btms;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class BottomNavHelper {

    public static void setupBottomNav(View rootView, int activeNavId) {
        LinearLayout navHome = rootView.findViewById(R.id.navHome);
        LinearLayout navTicket = rootView.findViewById(R.id.navTicket);
        LinearLayout navWallet = rootView.findViewById(R.id.navWallet);
        LinearLayout navSettings = rootView.findViewById(R.id.navSettings);

        if (navHome == null || navTicket == null || navWallet == null || navSettings == null) {
            return; // Bottom nav not found in this layout
        }

        // Reset all nav items
        resetNavItem(navHome);
        resetNavItem(navTicket);
        resetNavItem(navWallet);
        resetNavItem(navSettings);

        // Set active nav item
        if (activeNavId == R.id.navHome) {
            setActiveNavItem(navHome);
        } else if (activeNavId == R.id.navTicket) {
            setActiveNavItem(navTicket);
        } else if (activeNavId == R.id.navWallet) {
            setActiveNavItem(navWallet);
        } else if (activeNavId == R.id.navSettings) {
            setActiveNavItem(navSettings);
        }
    }

    public static void setupBottomNavListeners(AppCompatActivity activity, View rootView) {
        LinearLayout navHome = rootView.findViewById(R.id.navHome);
        LinearLayout navTicket = rootView.findViewById(R.id.navTicket);
        LinearLayout navWallet = rootView.findViewById(R.id.navWallet);
        LinearLayout navSettings = rootView.findViewById(R.id.navSettings);

        if (navHome == null || navTicket == null || navWallet == null || navSettings == null) {
            return; // Bottom nav not found in this layout
        }

        navHome.setOnClickListener(v -> {
            if (!(activity instanceof HomeActivity)) {
                Intent intent = new Intent(activity, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        navTicket.setOnClickListener(v -> {
            if (!(activity instanceof UpcomingJourneyActivity)) {
                Intent intent = new Intent(activity, UpcomingJourneyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        navWallet.setOnClickListener(v -> {
            if (!(activity instanceof WalletActivity)) {
                Intent intent = new Intent(activity, WalletActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        navSettings.setOnClickListener(v -> {
            if (!(activity instanceof SettingsActivity)) {
                Intent intent = new Intent(activity, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    private static void setActiveNavItem(LinearLayout navItem) {
        if (navItem == null) return;
        navItem.setBackgroundResource(R.drawable.bg_nav_item_active);
        ImageView icon = (ImageView) navItem.getChildAt(0);
        if (icon != null) {
            icon.setColorFilter(0xFF000000); // Black color
        }
    }

    private static void resetNavItem(LinearLayout navItem) {
        if (navItem == null) return;
        navItem.setBackground(null);
        ImageView icon = (ImageView) navItem.getChildAt(0);
        if (icon != null) {
            icon.setColorFilter(0xFFFFFFFF); // White color
        }
    }
}

