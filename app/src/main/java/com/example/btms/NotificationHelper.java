package com.example.btms;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {
    private static final String CHANNEL_ID = "BTMS_NOTIFICATION_CHANNEL";
    private static final String CHANNEL_NAME = "Thông báo đặt vé";
    private static final String CHANNEL_DESCRIPTION = "Thông báo về đặt vé và lịch trình xe buýt";
    private static final int NOTIFICATION_ID_BOOKING = 1000;
    private static final int NOTIFICATION_ID_REMINDER = 2000;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // Thông báo sau khi đặt vé thành công
    public static void showBookingConfirmationNotification(Context context, String fromLocation, 
                                                           String toLocation, String departureTime, 
                                                           String date) {
        createNotificationChannel(context);

        String title = "Đặt vé thành công!";
        String message = String.format("Chuyến đi từ %s đến %s\nKhởi hành: %s ngày %s", 
                fromLocation, toLocation, departureTime, DateTimeHelper.formatDateForDisplay(date));

        Intent intent = new Intent(context, BookingDetailActivity.class);
        intent.putExtra("booking_id", -1); // Will be set by caller if needed
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) 
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID_BOOKING, builder.build());
        }
    }

    // Lên lịch thông báo 30 phút trước giờ khởi hành
    public static void scheduleDepartureReminder(Context context, long bookingId, 
                                                 String fromLocation, String toLocation,
                                                 String departureTime, String date) {
        try {
            // Parse date and time
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String dateTimeStr = date + " " + departureTime;
            Date departureDateTime = dateTimeFormat.parse(dateTimeStr);

            if (departureDateTime == null) {
                Log.e("NotificationHelper", "Failed to parse departure date/time: " + dateTimeStr);
                return;
            }

            // Calculate reminder time (30 minutes before departure)
            Calendar reminderCal = Calendar.getInstance();
            reminderCal.setTime(departureDateTime);
            reminderCal.add(Calendar.MINUTE, -30);

            // Check if reminder time is in the past
            if (reminderCal.getTimeInMillis() <= System.currentTimeMillis()) {
                Log.d("NotificationHelper", "Reminder time is in the past, skipping");
                return;
            }

            // Create intent for reminder notification
            Intent reminderIntent = new Intent(context, DepartureReminderReceiver.class);
            reminderIntent.putExtra("booking_id", bookingId);
            reminderIntent.putExtra("from_location", fromLocation);
            reminderIntent.putExtra("to_location", toLocation);
            reminderIntent.putExtra("departure_time", departureTime);
            reminderIntent.putExtra("date", date);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 
                    (int) bookingId, reminderIntent, 
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 
                            reminderCal.getTimeInMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, 
                            reminderCal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, 
                            reminderCal.getTimeInMillis(), pendingIntent);
                }
                Log.d("NotificationHelper", "Reminder scheduled for: " + reminderCal.getTime().toString());
            }
        } catch (ParseException e) {
            Log.e("NotificationHelper", "Error parsing date/time", e);
        }
    }

    // Hiển thị thông báo nhắc nhở
    public static void showDepartureReminderNotification(Context context, long bookingId,
                                                         String fromLocation, String toLocation,
                                                         String departureTime, String date) {
        createNotificationChannel(context);

        String title = "Nhắc nhở: Chuyến xe sắp khởi hành!";
        String message = String.format("Chuyến đi từ %s đến %s sẽ khởi hành lúc %s ngày %s\nCòn 30 phút nữa!", 
                fromLocation, toLocation, departureTime, DateTimeHelper.formatDateForDisplay(date));

        Intent intent = new Intent(context, BookingDetailActivity.class);
        intent.putExtra("booking_id", bookingId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) bookingId, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) 
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID_REMINDER + (int) bookingId, builder.build());
        }
    }

    // Use DateTimeHelper utility methods instead of duplicate code
}




