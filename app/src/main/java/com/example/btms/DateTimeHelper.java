package com.example.btms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {
    
    /**
     * Format time from 24-hour format (HH:mm) to display format
     * @param time24h Time in 24-hour format (e.g., "14:30")
     * @return Formatted time string (e.g., "14:30")
     */
    public static String formatTime(String time24h) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(time24h);
            if (date != null) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            android.util.Log.e("DateTimeHelper", "Error formatting time: " + e.getMessage(), e);
        }
        return time24h;
    }
    
    /**
     * Format time from 24-hour format to 12-hour format with AM/PM
     * @param time24h Time in 24-hour format (e.g., "14:30")
     * @return Formatted time string (e.g., "2:30 PM")
     */
    public static String formatTime12Hour(String time24h) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(time24h);
            if (date != null) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            android.util.Log.e("DateTimeHelper", "Error formatting time 12h: " + e.getMessage(), e);
        }
        return time24h;
    }
    
    /**
     * Get day of week abbreviation from date string
     * @param dateStr Date string in format "yyyy-MM-dd"
     * @return Day abbreviation (e.g., "CN", "T2", "T3", etc.)
     */
    public static String getDayOfWeek(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            if (date != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                String[] days = {"", "CN", "T2", "T3", "T4", "T5", "T6", "T7"};
                if (dayOfWeek >= 1 && dayOfWeek < days.length) {
                    return days[dayOfWeek];
                }
            }
        } catch (ParseException e) {
            android.util.Log.e("DateTimeHelper", "Error getting day of week: " + e.getMessage(), e);
        }
        return "";
    }
    
    /**
     * Get full day of week name from date string
     * @param dateStr Date string in format "yyyy-MM-dd"
     * @return Full day name (e.g., "Chủ nhật", "Thứ hai", etc.)
     */
    public static String getDayOfWeekFull(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            if (date != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                String[] days = {"", "Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy"};
                if (dayOfWeek >= 1 && dayOfWeek < days.length) {
                    return days[dayOfWeek];
                }
            }
        } catch (ParseException e) {
            android.util.Log.e("DateTimeHelper", "Error getting day of week full: " + e.getMessage(), e);
        }
        return "";
    }
    
    /**
     * Format date from "yyyy-MM-dd" to "dd/MM/yyyy"
     * @param dateStr Date string in format "yyyy-MM-dd"
     * @return Formatted date string (e.g., "17/12/2025")
     */
    public static String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            android.util.Log.e("DateTimeHelper", "Error formatting date: " + e.getMessage(), e);
        }
        return dateStr;
    }
    
    /**
     * Format date for display (alternative format)
     * @param dateStr Date string in format "yyyy-MM-dd"
     * @return Formatted date string
     */
    public static String formatDateForDisplay(String dateStr) {
        return formatDate(dateStr);
    }
}

