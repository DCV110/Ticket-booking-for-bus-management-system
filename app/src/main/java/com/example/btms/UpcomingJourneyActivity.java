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
    private String currentFilter = "upcoming"; // upcoming, completed, cancelled
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
        
        // Initialize filter button states (set "Chưa khởi hành" as active by default)
        android.widget.Button btnUpcoming = findViewById(R.id.btnUpcoming);
        android.widget.Button btnCompleted = findViewById(R.id.btnCompleted);
        android.widget.Button btnCancelled = findViewById(R.id.btnCancelled);
        if (btnUpcoming != null) {
            updateFilterButtons(btnUpcoming, btnCompleted, btnCancelled);
        }
        
        // Load user bookings (chưa khởi hành by default)
        loadUserBookings();

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNav(rootView, R.id.navTicket);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private void setupFilterTabs() {
        android.widget.Button btnUpcoming = findViewById(R.id.btnUpcoming);
        android.widget.Button btnCompleted = findViewById(R.id.btnCompleted);
        android.widget.Button btnCancelled = findViewById(R.id.btnCancelled);
        
        if (btnUpcoming != null) {
            btnUpcoming.setOnClickListener(v -> {
                currentFilter = "upcoming";
                updateFilterButtons(btnUpcoming, btnCompleted, btnCancelled);
                loadUserBookings(); // Show user's upcoming bookings (chưa khởi hành)
            });
        }
        
        if (btnCompleted != null) {
            btnCompleted.setOnClickListener(v -> {
                currentFilter = "completed";
                updateFilterButtons(btnCompleted, btnUpcoming, btnCancelled);
                loadUserBookings(); // Show user's completed bookings
            });
        }
        
        if (btnCancelled != null) {
            btnCancelled.setOnClickListener(v -> {
                currentFilter = "cancelled";
                updateFilterButtons(btnCancelled, btnUpcoming, btnCompleted);
                loadUserBookings(); // Show user's cancelled bookings
            });
        }
    }
    
    private void updateFilterButtons(android.widget.Button activeBtn, android.widget.Button... otherBtns) {
        // Get all buttons first
        android.widget.Button btnUpcoming = findViewById(R.id.btnUpcoming);
        android.widget.Button btnCompleted = findViewById(R.id.btnCompleted);
        android.widget.Button btnCancelled = findViewById(R.id.btnCancelled);
        
        // Reset ALL buttons first - set all to inactive state
        if (btnUpcoming != null) {
            btnUpcoming.setBackgroundResource(R.drawable.bg_filter_button_inactive);
            btnUpcoming.setTextColor(getColor(R.color.text_secondary));
        }
        if (btnCompleted != null) {
            btnCompleted.setBackgroundResource(R.drawable.bg_filter_button_inactive);
            btnCompleted.setTextColor(getColor(R.color.text_secondary));
        }
        if (btnCancelled != null) {
            btnCancelled.setBackgroundResource(R.drawable.bg_filter_button_inactive);
            btnCancelled.setTextColor(getColor(R.color.text_secondary));
        }
        
        // Then set active button
        if (activeBtn != null) {
            activeBtn.setBackgroundResource(R.drawable.bg_filter_button_active);
            activeBtn.setTextColor(android.graphics.Color.WHITE);
        }
    }

    private void loadUpcomingSchedules() {
        LinearLayout bookingsContainer = findViewById(R.id.llBookingsContainer);
        if (bookingsContainer == null) {
            android.util.Log.e("UpcomingJourneyActivity", "llBookingsContainer is null");
            return;
        }
        
        // Clear existing views
        bookingsContainer.removeAllViews();
        
        Cursor cursor = null;
        try {
            // Get upcoming schedules (available trips to book)
            cursor = dbHelper.getSuggestedJourneys(10); // Get up to 10 upcoming schedules
            
            if (cursor == null) {
                android.util.Log.e("UpcomingJourneyActivity", "Cursor is null");
                showEmptyMessage(bookingsContainer, "Không thể tải dữ liệu");
                return;
            }
            
            if (!cursor.moveToFirst()) {
                showEmptyMessage(bookingsContainer, "Không có chuyến xe sắp tới. Vui lòng thử lại sau!");
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                return;
            }
            
            boolean hasVisibleItems = false;
            LayoutInflater inflater = LayoutInflater.from(this);
            
            int terminalCounter = 1;
            do {
                try {
                    View scheduleCard = inflater.inflate(R.layout.item_booking_card, bookingsContainer, false);
                    
                    TextView tvTerminalName = scheduleCard.findViewById(R.id.tvTerminalName);
                    TextView tvFromLocation = scheduleCard.findViewById(R.id.tvFromLocation);
                    TextView tvToLocation = scheduleCard.findViewById(R.id.tvToLocation);
                    TextView tvDepartureTime = scheduleCard.findViewById(R.id.tvDepartureTime);
                    TextView tvSeatNumbers = scheduleCard.findViewById(R.id.tvSeatNumbers);
                    TextView tvBoardingPoint = scheduleCard.findViewById(R.id.tvBoardingPoint);
                    TextView tvStatus = scheduleCard.findViewById(R.id.tvStatus);
                    android.widget.Button btnViewDetails = scheduleCard.findViewById(R.id.btnViewDetails);
                    
                    // Get schedule data from cursor
                    String fromLocation = null;
                    String toLocation = null;
                    String departureTime = null;
                    String arrivalTime = null;
                    String scheduleDate = null;
                    int routeNumber = 0;
                    long scheduleId = -1;
                    double price = 0;
                    String busType = null;
                    int availableSeats = 0;
                    
                    try {
                        int fromIndex = cursor.getColumnIndex("from_location");
                        if (fromIndex >= 0) fromLocation = cursor.getString(fromIndex);
                        
                        int toIndex = cursor.getColumnIndex("to_location");
                        if (toIndex >= 0) toLocation = cursor.getString(toIndex);
                        
                        int depTimeIndex = cursor.getColumnIndex("departure_time");
                        if (depTimeIndex >= 0) departureTime = cursor.getString(depTimeIndex);
                        
                        int arrTimeIndex = cursor.getColumnIndex("arrival_time");
                        if (arrTimeIndex >= 0) arrivalTime = cursor.getString(arrTimeIndex);
                        
                        int dateIndex = cursor.getColumnIndex("date");
                        if (dateIndex >= 0) scheduleDate = cursor.getString(dateIndex);
                        
                        int routeNumberIndex = cursor.getColumnIndex("route_number");
                        if (routeNumberIndex >= 0) routeNumber = cursor.getInt(routeNumberIndex);
                        
                        int scheduleIdIndex = cursor.getColumnIndex("schedule_id");
                        if (scheduleIdIndex >= 0) scheduleId = cursor.getLong(scheduleIdIndex);
                        
                        int priceIndex = cursor.getColumnIndex("price");
                        if (priceIndex >= 0) price = cursor.getDouble(priceIndex);
                        
                        int busTypeIndex = cursor.getColumnIndex("bus_type");
                        if (busTypeIndex >= 0) busType = cursor.getString(busTypeIndex);
                        
                        int availableSeatsIndex = cursor.getColumnIndex("available_seats");
                        if (availableSeatsIndex >= 0) availableSeats = cursor.getInt(availableSeatsIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting cursor data: " + e.getMessage(), e);
                    }
                    
                    if (scheduleId == -1) {
                        android.util.Log.w("UpcomingJourneyActivity", "Skipping schedule with invalid schedule ID");
                        continue;
                    }
                    
                    // Set terminal name (route number)
                    if (tvTerminalName != null) {
                        if (routeNumber > 0) {
                            tvTerminalName.setText("Tuyến số " + routeNumber);
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
                            String timeStr = departureTime != null ? DateTimeHelper.formatTime(departureTime) : "Chưa có";
                            String dayStr = scheduleDate != null ? DateTimeHelper.getDayOfWeek(scheduleDate) : "";
                            String dateStr = scheduleDate != null ? DateTimeHelper.formatDate(scheduleDate) : "Chưa có";
                            tvDepartureTime.setText(timeStr + ", " + dayStr + "\n" + dateStr);
                        } catch (Exception e) {
                            android.util.Log.e("UpcomingJourneyActivity", "Error formatting time/date: " + e.getMessage());
                            tvDepartureTime.setText("Chưa có");
                        }
                    }
                    
                    // Hide seat numbers and boarding point for schedules (not bookings)
                    if (tvSeatNumbers != null) {
                        tvSeatNumbers.setVisibility(View.GONE);
                    }
                    if (tvBoardingPoint != null) {
                        tvBoardingPoint.setVisibility(View.GONE);
                    }
                    
                    // Set status view - always "Sắp khởi hành" for upcoming schedules
                    if (tvStatus != null) {
                        tvStatus.setText("Sắp khởi hành");
                        tvStatus.setBackgroundResource(R.drawable.bg_status_upcoming);
                    }
                    
                    // Update button text
                    if (btnViewDetails != null) {
                        btnViewDetails.setText("Đặt vé");
                    }
                    
                    // Set click listener to open ChooseSeatActivity for booking
                    final long finalScheduleId = scheduleId;
                    final String finalFromLocation = fromLocation;
                    final String finalToLocation = toLocation;
                    final int finalRouteNumber = routeNumber;
                    final double finalPrice = price;
                    final String finalDepartureTime = departureTime;
                    final String finalArrivalTime = arrivalTime;
                    final String finalBusType = busType;
                    final String finalScheduleDate = scheduleDate;
                    final int finalAvailableSeats = availableSeats;
                    
                    scheduleCard.setOnClickListener(v -> {
                        try {
                            android.content.Intent intent = new android.content.Intent(UpcomingJourneyActivity.this, ChooseSeatActivity.class);
                            intent.putExtra("from_location", finalFromLocation);
                            intent.putExtra("to_location", finalToLocation);
                            intent.putExtra("schedule_id", finalScheduleId);
                            intent.putExtra("route_number", finalRouteNumber);
                            intent.putExtra("price", finalPrice);
                            intent.putExtra("departure_time", finalDepartureTime);
                            intent.putExtra("arrival_time", finalArrivalTime);
                            intent.putExtra("bus_type", finalBusType);
                            intent.putExtra("schedule_date", finalScheduleDate);
                            intent.putExtra("available_seats", finalAvailableSeats);
                            startActivity(intent);
                        } catch (Exception e) {
                            android.util.Log.e("UpcomingJourneyActivity", "Error opening ChooseSeatActivity: " + e.getMessage(), e);
                            android.widget.Toast.makeText(UpcomingJourneyActivity.this, "Không thể mở trang đặt vé", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                    
                    if (btnViewDetails != null) {
                        btnViewDetails.setOnClickListener(v -> {
                            try {
                                android.content.Intent intent = new android.content.Intent(UpcomingJourneyActivity.this, ChooseSeatActivity.class);
                                intent.putExtra("from_location", finalFromLocation);
                                intent.putExtra("to_location", finalToLocation);
                                intent.putExtra("schedule_id", finalScheduleId);
                                intent.putExtra("route_number", finalRouteNumber);
                                intent.putExtra("price", finalPrice);
                                intent.putExtra("departure_time", finalDepartureTime);
                                intent.putExtra("arrival_time", finalArrivalTime);
                                intent.putExtra("bus_type", finalBusType);
                                intent.putExtra("schedule_date", finalScheduleDate);
                                intent.putExtra("available_seats", finalAvailableSeats);
                                startActivity(intent);
                            } catch (Exception e) {
                                android.util.Log.e("UpcomingJourneyActivity", "Error opening ChooseSeatActivity: " + e.getMessage(), e);
                                android.widget.Toast.makeText(UpcomingJourneyActivity.this, "Không thể mở trang đặt vé", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    
                    bookingsContainer.addView(scheduleCard);
                    hasVisibleItems = true;
                    terminalCounter++;
                } catch (Exception e) {
                    android.util.Log.e("UpcomingJourneyActivity", "Error processing schedule card: " + e.getMessage(), e);
                }
            } while (cursor.moveToNext());
            
            // Show message if no items
            if (!hasVisibleItems) {
                showEmptyMessage(bookingsContainer, "Không có chuyến xe sắp tới. Vui lòng thử lại sau!");
            }
        } catch (Exception e) {
            android.util.Log.e("UpcomingJourneyActivity", "Error loading schedules: " + e.getMessage(), e);
            showEmptyMessage(bookingsContainer, "Lỗi khi tải dữ liệu");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("UpcomingJourneyActivity", "Error closing cursor: " + e.getMessage(), e);
                }
            }
        }
    }

    private void loadUserBookings() {
        LinearLayout bookingsContainer = findViewById(R.id.llBookingsContainer);
        if (bookingsContainer == null) {
            android.util.Log.e("UpcomingJourneyActivity", "llBookingsContainer is null");
            return;
        }
        
        // Clear existing views
        bookingsContainer.removeAllViews();
        
        Cursor cursor = null;
        try {
            // Get user bookings (actual tickets purchased by user)
            if (userEmail == null || userEmail.isEmpty()) {
                android.util.Log.e("UpcomingJourneyActivity", "User email is null or empty");
                showEmptyMessage(bookingsContainer, "Vui lòng đăng nhập để xem vé của bạn");
                return;
            }
            
            cursor = dbHelper.getUserBookingsWithDetails(userEmail);
            
            if (cursor == null) {
                android.util.Log.e("UpcomingJourneyActivity", "Cursor is null");
                showEmptyMessage(bookingsContainer, "Không thể tải dữ liệu");
                return;
            }
            
            if (!cursor.moveToFirst()) {
                showEmptyMessage(bookingsContainer, "Bạn chưa có vé nào. Hãy đặt vé để xem ở đây!");
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
                    
                    // Get booking ID and schedule ID for click listener
                    long bookingId = -1;
                    long scheduleId = -1;
                    try {
                        int bookingIdIndex = cursor.getColumnIndex("booking_id");
                        if (bookingIdIndex >= 0) {
                            bookingId = cursor.getLong(bookingIdIndex);
                        }
                        int scheduleIdIndex = cursor.getColumnIndex("schedule_id");
                        if (scheduleIdIndex >= 0) {
                            scheduleId = cursor.getLong(scheduleIdIndex);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting booking_id/schedule_id: " + e.getMessage());
                    }
                    
                    if (bookingId == -1) {
                        android.util.Log.w("UpcomingJourneyActivity", "Skipping booking with invalid booking ID");
                        continue;
                    }
                    
                    // Get data from cursor - safely
                    String companyName = null;
                    String fromLocation = null;
                    String toLocation = null;
                    String departureTime = null;
                    String arrivalTime = null;
                    String scheduleDate = null;
                    double price = 0;
                    String busType = null;
                    int routeNumber = 0;
                    int availableSeats = 0;
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
                        int priceIndex = cursor.getColumnIndex("price");
                        if (priceIndex >= 0) price = cursor.getDouble(priceIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting price: " + e.getMessage());
                    }
                    
                    try {
                        int busTypeIndex = cursor.getColumnIndex("bus_type");
                        if (busTypeIndex >= 0) busType = cursor.getString(busTypeIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting bus_type: " + e.getMessage());
                    }
                    
                    try {
                        int routeNumberIndex = cursor.getColumnIndex("route_number");
                        if (routeNumberIndex >= 0) routeNumber = cursor.getInt(routeNumberIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting route_number: " + e.getMessage());
                    }
                    
                    try {
                        int availableSeatsIndex = cursor.getColumnIndex("available_seats");
                        if (availableSeatsIndex >= 0) availableSeats = cursor.getInt(availableSeatsIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting available_seats: " + e.getMessage());
                    }
                    
                    // Get booking-specific information
                    try {
                        int boardingPointIndex = cursor.getColumnIndex("boarding_point");
                        if (boardingPointIndex >= 0) boardingPoint = cursor.getString(boardingPointIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting boarding_point: " + e.getMessage());
                    }
                    
                    try {
                        int seatNumbersIndex = cursor.getColumnIndex("seat_numbers");
                        if (seatNumbersIndex >= 0) seatNumbers = cursor.getString(seatNumbersIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting seat_numbers: " + e.getMessage());
                    }
                    
                    // Get route number from cursor
                    try {
                        int routeNumberIndex = cursor.getColumnIndex("route_number");
                        if (routeNumberIndex >= 0) routeNumber = cursor.getInt(routeNumberIndex);
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting route_number: " + e.getMessage());
                    }
                    
                    // Get status from database first, then calculate if needed
                    String dbStatus = null;
                    try {
                        int statusIndex = cursor.getColumnIndex("status");
                        if (statusIndex >= 0) {
                            dbStatus = cursor.getString(statusIndex);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("UpcomingJourneyActivity", "Error getting status: " + e.getMessage());
                    }
                    
                    // Determine final status: if cancelled, keep it; otherwise calculate from date/time
                    String status;
                    if (dbStatus != null && dbStatus.equalsIgnoreCase("cancelled")) {
                        // Keep cancelled status
                        status = "cancelled";
                    } else {
                        // For "confirmed" or any other status, calculate actual journey status from date/time
                        status = determineJourneyStatus(scheduleDate, departureTime, arrivalTime);
                    }
                    
                    // Set terminal name (use route number or "Bến xe X")
                    if (tvTerminalName != null) {
                        if (routeNumber > 0) {
                            tvTerminalName.setText("Tuyến số " + routeNumber);
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
                            String timeStr = departureTime != null ? DateTimeHelper.formatTime(departureTime) : "Chưa có";
                            String dayStr = scheduleDate != null ? DateTimeHelper.getDayOfWeek(scheduleDate) : "";
                            String dateStr = scheduleDate != null ? DateTimeHelper.formatDate(scheduleDate) : "Chưa có";
                            tvDepartureTime.setText(timeStr + ", " + dayStr + "\n" + dateStr);
                        } catch (Exception e) {
                            android.util.Log.e("UpcomingJourneyActivity", "Error formatting time/date: " + e.getMessage());
                            tvDepartureTime.setText("Chưa có");
                        }
                    }
                    
                    // Set seat numbers (from actual booking)
                    if (tvSeatNumbers != null) {
                        if (seatNumbers != null && !seatNumbers.isEmpty()) {
                            tvSeatNumbers.setText("Ghế: " + seatNumbers);
                        } else {
                            tvSeatNumbers.setText("Ghế: --");
                        }
                    }
                    
                    // Set boarding point (from actual booking)
                    if (tvBoardingPoint != null) {
                        if (boardingPoint != null && !boardingPoint.isEmpty()) {
                            tvBoardingPoint.setText("Điểm đón: " + boardingPoint);
                        } else {
                            tvBoardingPoint.setText("Điểm đón: --");
                        }
                    }
                    
                    // Set status view
                    setStatusView(tvStatus, status);
                    
                    // Filter logic: Show bookings based on status
                    // upcoming = chưa khởi hành (before departure time)
                    // completed = đã khởi hành (after departure time, includes ongoing and completed)
                    // cancelled = đã hủy
                    boolean shouldShow = false;
                    if (currentFilter.equals("upcoming")) {
                        // Chưa khởi hành: chỉ hiển thị status = "upcoming" (trước thời gian khởi hành)
                        if (status != null && status.equals("upcoming") && !status.equalsIgnoreCase("cancelled")) {
                            shouldShow = true;
                        }
                    } else if (currentFilter.equals("completed")) {
                        // Đã khởi hành: hiển thị cả "ongoing" (đang diễn ra) và "completed" (đã hoàn thành)
                        // Tức là đã qua thời gian khởi hành
                        if (status != null && (status.equals("ongoing") || status.equals("completed")) && !status.equalsIgnoreCase("cancelled")) {
                            shouldShow = true;
                        }
                    } else if (currentFilter.equals("cancelled")) {
                        // Đã hủy: status is cancelled
                        if (status != null && status.equalsIgnoreCase("cancelled")) {
                            shouldShow = true;
                        }
                    }
                    
                    if (shouldShow) {
                        // Create final variables for lambda
                        final long finalBookingId = bookingId;
                        
                        // For user bookings, clicking will open BookingDetailActivity to view ticket details
                        bookingCard.setOnClickListener(v -> {
                            try {
                                android.content.Intent intent = new android.content.Intent(UpcomingJourneyActivity.this, BookingDetailActivity.class);
                                intent.putExtra("booking_id", finalBookingId);
                                startActivity(intent);
                            } catch (Exception e) {
                                android.util.Log.e("UpcomingJourneyActivity", "Error opening BookingDetailActivity: " + e.getMessage(), e);
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
                                    android.util.Log.e("UpcomingJourneyActivity", "Error opening BookingDetailActivity: " + e.getMessage(), e);
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
                if (currentFilter.equals("upcoming")) {
                    showEmptyMessage(bookingsContainer, "Bạn chưa có vé nào chưa khởi hành");
                } else if (currentFilter.equals("completed")) {
                    showEmptyMessage(bookingsContainer, "Bạn chưa có vé nào đã khởi hành");
                } else if (currentFilter.equals("cancelled")) {
                    showEmptyMessage(bookingsContainer, "Bạn chưa có vé nào đã hủy");
                } else {
                    showEmptyMessage(bookingsContainer, "Bạn chưa có vé nào. Hãy đặt vé để xem ở đây!");
                }
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
    
    // Use DateTimeHelper utility methods instead of duplicate code
    
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
        
        if (status != null && status.equalsIgnoreCase("cancelled")) {
            tvStatus.setText("Đã hủy");
            tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
        } else {
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
    }
    
    // Use DateTimeHelper utility methods instead of duplicate code

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}

