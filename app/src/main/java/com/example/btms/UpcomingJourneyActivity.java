package com.example.btms;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpcomingJourneyActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String currentFilter = "all"; // all, upcoming, completed
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_journey);

        dbHelper = new DatabaseHelper(this);
        
        // Get user email from shared preferences
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", "hello@example.com");
        String userName = sharedPreferences.getString("user_name", "User");
        
        // Update greeting
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null && userName != null) {
            tvGreeting.setText("Xin chào " + userName + "!");
        }
        
        // Setup filter tabs
        setupFilterTabs();
        
        loadUpcomingJourneys(userEmail, currentFilter);

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNav(rootView, R.id.navTicket);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }
    
    private void setupFilterTabs() {
        android.widget.Button btnAll = findViewById(R.id.btnAll);
        android.widget.Button btnUpcoming = findViewById(R.id.btnUpcoming);
        android.widget.Button btnCompleted = findViewById(R.id.btnCompleted);
        
        if (btnAll != null) {
            btnAll.setOnClickListener(v -> {
                currentFilter = "all";
                updateFilterButtons(btnAll, btnUpcoming, btnCompleted);
                loadUpcomingJourneys(userEmail, currentFilter);
            });
        }
        
        if (btnUpcoming != null) {
            btnUpcoming.setOnClickListener(v -> {
                currentFilter = "upcoming";
                updateFilterButtons(btnUpcoming, btnAll, btnCompleted);
                loadUpcomingJourneys(userEmail, currentFilter);
            });
        }
        
        if (btnCompleted != null) {
            btnCompleted.setOnClickListener(v -> {
                currentFilter = "completed";
                updateFilterButtons(btnCompleted, btnAll, btnUpcoming);
                loadUpcomingJourneys(userEmail, currentFilter);
            });
        }
    }
    
    private void updateFilterButtons(android.widget.Button activeBtn, android.widget.Button... otherBtns) {
        activeBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.primary)));
        activeBtn.setTextColor(android.graphics.Color.WHITE);
        
        for (android.widget.Button btn : otherBtns) {
            if (btn != null) {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
                btn.setTextColor(getColor(R.color.text_secondary));
            }
        }
    }

    private void loadUpcomingJourneys(String userEmail, String filter) {
        LinearLayout bookingsContainer = findViewById(R.id.llBookingsContainer);
        if (bookingsContainer == null) {
            android.util.Log.e("UpcomingJourneyActivity", "llBookingsContainer is null");
            return;
        }
        
        // Clear existing views
        bookingsContainer.removeAllViews();
        
        Cursor cursor = null;
        try {
            cursor = dbHelper.getUserBookingsWithDetails(userEmail);
            
            if (cursor == null) {
                android.util.Log.e("UpcomingJourneyActivity", "Cursor is null");
                showEmptyMessage(bookingsContainer, "Không thể tải dữ liệu");
                return;
            }
            
            if (!cursor.moveToFirst()) {
                showEmptyMessage(bookingsContainer, "Bạn chưa có chuyến đi nào");
                cursor.close();
                return;
            }
            
            boolean hasVisibleItems = false;
            LayoutInflater inflater = LayoutInflater.from(this);
            
            int terminalCounter = 1;
            do {
                try {
                    View bookingCard = inflater.inflate(R.layout.item_booking_card, bookingsContainer, false);
                    
                    TextView tvTerminalName = bookingCard.findViewById(R.id.tvTerminalName);
                    TextView tvFromLocation = bookingCard.findViewById(R.id.tvFromLocation);
                    TextView tvToLocation = bookingCard.findViewById(R.id.tvToLocation);
                    TextView tvDepartureTime = bookingCard.findViewById(R.id.tvDepartureTime);
                    TextView tvSeatNumbers = bookingCard.findViewById(R.id.tvSeatNumbers);
                    TextView tvBoardingPoint = bookingCard.findViewById(R.id.tvBoardingPoint);
                    TextView tvStatus = bookingCard.findViewById(R.id.tvStatus);
                    android.widget.Button btnViewDetails = bookingCard.findViewById(R.id.btnViewDetails);
                    
                    // Get booking ID for click listener - safely
                    long bookingId = -1;
                    try {
                        int bookingIdIndex = cursor.getColumnIndex("booking_id");
                        if (bookingIdIndex >= 0) {
                            bookingId = cursor.getLong(bookingIdIndex);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting booking_id: " + e.getMessage());
                    }
                    
                    if (bookingId == -1) {
                        android.util.Log.w("UpcomingJourneyActivity", "Skipping booking with invalid ID");
                        continue;
                    }
                    
                    // Get data from cursor - safely
                    String companyName = null;
                    String fromLocation = null;
                    String toLocation = null;
                    String departureTime = null;
                    String arrivalTime = null;
                    String scheduleDate = null;
                    String boardingPoint = null;
                    String seatNumbers = null;
                    
                    try {
                        int companyNameIndex = cursor.getColumnIndex("company_name");
                        if (companyNameIndex >= 0) companyName = cursor.getString(companyNameIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting company_name: " + e.getMessage());
                    }
                    
                    try {
                        int fromIndex = cursor.getColumnIndex("from_location");
                        if (fromIndex >= 0) fromLocation = cursor.getString(fromIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting from_location: " + e.getMessage());
                    }
                    
                    try {
                        int toIndex = cursor.getColumnIndex("to_location");
                        if (toIndex >= 0) toLocation = cursor.getString(toIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting to_location: " + e.getMessage());
                    }
                    
                    try {
                        int depTimeIndex = cursor.getColumnIndex("departure_time");
                        if (depTimeIndex >= 0) departureTime = cursor.getString(depTimeIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting departure_time: " + e.getMessage());
                    }
                    
                    try {
                        int arrTimeIndex = cursor.getColumnIndex("arrival_time");
                        if (arrTimeIndex >= 0) arrivalTime = cursor.getString(arrTimeIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting arrival_time: " + e.getMessage());
                    }
                    
                    try {
                        int dateIndex = cursor.getColumnIndex("date");
                        if (dateIndex >= 0) scheduleDate = cursor.getString(dateIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting date: " + e.getMessage());
                    }
                    
                    try {
                        int boardingIndex = cursor.getColumnIndex("boarding_point");
                        if (boardingIndex >= 0) boardingPoint = cursor.getString(boardingIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting boarding_point: " + e.getMessage());
                    }
                    
                    try {
                        int seatIndex = cursor.getColumnIndex("seat_numbers");
                        if (seatIndex >= 0) seatNumbers = cursor.getString(seatIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting seat_numbers: " + e.getMessage());
                    }
                    
                    // Get status if available (may not be in old query)
                    String status = null;
                    try {
                        int statusIndex = cursor.getColumnIndex("status");
                        if (statusIndex >= 0) {
                            status = cursor.getString(statusIndex);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting status: " + e.getMessage());
                    }
                    
                    // If status is null, calculate from date/time
                    if (status == null || status.isEmpty()) {
                        status = determineJourneyStatus(scheduleDate, departureTime, arrivalTime);
                    }
                    
                    // Set terminal name (use company name or "Bến xe X")
                    if (tvTerminalName != null) {
                        if (companyName != null && !companyName.isEmpty()) {
                            tvTerminalName.setText(companyName);
                        } else {
                            tvTerminalName.setText("Bến xe " + terminalCounter);
                        }
                    }
                    
                    // Set route locations
                    if (tvFromLocation != null) {
                        tvFromLocation.setText(fromLocation != null ? fromLocation : "");
                    }
                    if (tvToLocation != null) {
                        tvToLocation.setText(toLocation != null ? toLocation : "");
                    }
                    
                    // Format departure time and date
                    if (tvDepartureTime != null) {
                        try {
                            String timeStr = departureTime != null ? formatTime(departureTime) : "Chưa có";
                            String dayStr = scheduleDate != null ? getDayOfWeek(scheduleDate) : "";
                            String dateStr = scheduleDate != null ? formatDate(scheduleDate) : "Chưa có";
                            tvDepartureTime.setText(timeStr + ", " + dayStr + "\n" + dateStr);
                        } catch (Exception e) {
                            android.util.Log.e("UpcomingJourneyActivity", "Error formatting time/date: " + e.getMessage());
                            tvDepartureTime.setText("Chưa có");
                        }
                    }
                    
                    // Set seat numbers
                    if (tvSeatNumbers != null) {
                        if (seatNumbers != null && !seatNumbers.isEmpty()) {
                            tvSeatNumbers.setText("Ghế: " + seatNumbers);
                        } else {
                            tvSeatNumbers.setText("Ghế: Chưa chọn");
                        }
                    }
                    
                    // Set boarding point
                    if (tvBoardingPoint != null) {
                        if (boardingPoint != null && !boardingPoint.isEmpty()) {
                            tvBoardingPoint.setText("Điểm đón: " + boardingPoint);
                        } else {
                            tvBoardingPoint.setText("Điểm đón: Chưa chọn");
                        }
                    }
                    
                    // Set status view
                    setStatusView(tvStatus, status);
                    
                    // Filter logic
                    boolean shouldShow = false;
                    if (filter.equals("all")) {
                        shouldShow = true;
                    } else if (filter.equals("upcoming") && status != null && (status.equals("upcoming") || status.equals("ongoing"))) {
                        shouldShow = true;
                    } else if (filter.equals("completed") && status != null && status.equals("completed")) {
                        shouldShow = true;
                    }
                    
                    if (shouldShow) {
                        // Create final variable for lambda
                        final long finalBookingId = bookingId;
                        
                        // Set click listeners
                        bookingCard.setOnClickListener(v -> {
                            try {
                                android.content.Intent intent = new android.content.Intent(UpcomingJourneyActivity.this, BookingDetailActivity.class);
                                intent.putExtra("booking_id", finalBookingId);
                                startActivity(intent);
                            } catch (Exception e) {
                                android.util.Log.e("UpcomingJourneyActivity", "Error opening booking details: " + e.getMessage(), e);
                                android.widget.Toast.makeText(UpcomingJourneyActivity.this, "Không thể mở chi tiết vé", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                        if (btnViewDetails != null) {
                            btnViewDetails.setOnClickListener(v -> {
                                try {
                                    android.content.Intent intent = new android.content.Intent(UpcomingJourneyActivity.this, BookingDetailActivity.class);
                                    intent.putExtra("booking_id", finalBookingId);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    android.util.Log.e("UpcomingJourneyActivity", "Error opening booking details: " + e.getMessage(), e);
                                    android.widget.Toast.makeText(UpcomingJourneyActivity.this, "Không thể mở chi tiết vé", android.widget.Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        
                        bookingsContainer.addView(bookingCard);
                        hasVisibleItems = true;
                        terminalCounter++;
                    }
                } catch (Exception e) {
                    android.util.Log.e("UpcomingJourneyActivity", "Error processing booking card: " + e.getMessage(), e);
                    // Continue to next booking
                }
                
            } while (cursor.moveToNext());
            
            // Show message if no items
            if (!hasVisibleItems) {
                showEmptyMessage(bookingsContainer, "Không có chuyến đi nào");
            }
        } catch (Exception e) {
            android.util.Log.e("UpcomingJourneyActivity", "Error loading bookings: " + e.getMessage(), e);
            showEmptyMessage(bookingsContainer, "Lỗi khi tải dữ liệu");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
    
    private void showEmptyMessage(LinearLayout container, String message) {
        if (container == null) return;
        TextView tvEmpty = new TextView(this);
        tvEmpty.setText(message);
        tvEmpty.setTextSize(16);
        tvEmpty.setTextColor(getColor(R.color.text_secondary));
        tvEmpty.setGravity(android.view.Gravity.CENTER);
        tvEmpty.setPadding(0, 48, 0, 48);
        container.addView(tvEmpty);
    }
    
    private String formatTime(String time24h) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(time24h);
            if (date != null) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time24h;
    }
    
    private String determineJourneyStatus(String scheduleDate, String departureTime, String arrivalTime) {
        try {
            // Parse schedule date and time
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String dateTimeStr = scheduleDate + " " + departureTime;
            Date departureDateTime = dateTimeFormat.parse(dateTimeStr);
            
            if (departureDateTime == null) {
                return "upcoming";
            }
            
            Calendar now = Calendar.getInstance();
            Calendar departure = Calendar.getInstance();
            departure.setTime(departureDateTime);
            
            // Calculate arrival time
            Calendar arrival = Calendar.getInstance();
            if (arrivalTime != null && !arrivalTime.isEmpty()) {
                String arrivalDateTimeStr = scheduleDate + " " + arrivalTime;
                Date arrivalDateTime = dateTimeFormat.parse(arrivalDateTimeStr);
                if (arrivalDateTime != null) {
                    arrival.setTime(arrivalDateTime);
                } else {
                    arrival = (Calendar) departure.clone();
                    arrival.add(Calendar.HOUR_OF_DAY, 2); // Default 2 hours if no arrival time
                }
            } else {
                arrival = (Calendar) departure.clone();
                arrival.add(Calendar.HOUR_OF_DAY, 2);
            }
            
            // Determine status
            if (now.before(departure)) {
                // Before departure
                long diffMinutes = (departure.getTimeInMillis() - now.getTimeInMillis()) / (1000 * 60);
                if (diffMinutes <= 30) {
                    return "upcoming"; // Sắp khởi hành (< 30 phút)
                }
                return "upcoming";
            } else if (now.after(departure) && now.before(arrival)) {
                return "ongoing"; // Đang diễn ra
            } else {
                return "completed"; // Đã hoàn thành
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "upcoming";
        }
    }
    
    private void setStatusView(TextView tvStatus, String status) {
        if (tvStatus == null) return;
        
        switch (status) {
            case "upcoming":
                tvStatus.setText("Sắp khởi hành");
                tvStatus.setBackgroundResource(R.drawable.bg_status_upcoming);
                break;
            case "ongoing":
                tvStatus.setText("Đang diễn ra");
                tvStatus.setBackgroundResource(R.drawable.bg_status_ongoing);
                break;
            case "completed":
                tvStatus.setText("Đã hoàn thành");
                tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                break;
            default:
                tvStatus.setText("Sắp khởi hành");
                tvStatus.setBackgroundResource(R.drawable.bg_status_upcoming);
        }
    }
    
    private String getDayOfWeek(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            if (date != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                String[] days = {"", "CN", "T2", "T3", "T4", "T5", "T6", "T7"};
                return days[dayOfWeek];
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}

