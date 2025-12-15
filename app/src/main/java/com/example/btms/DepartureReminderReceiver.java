package com.example.btms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DepartureReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long bookingId = intent.getLongExtra("booking_id", -1);
        String fromLocation = intent.getStringExtra("from_location");
        String toLocation = intent.getStringExtra("to_location");
        String departureTime = intent.getStringExtra("departure_time");
        String date = intent.getStringExtra("date");

        Log.d("DepartureReminderReceiver", "Reminder triggered for booking: " + bookingId);

        if (fromLocation != null && toLocation != null && departureTime != null && date != null) {
            NotificationHelper.showDepartureReminderNotification(context, bookingId, 
                    fromLocation, toLocation, departureTime, date);
        }
    }
}



