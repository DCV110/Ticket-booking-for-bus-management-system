package com.example.btms;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private long bookingId;
    private String bookingStatus;
    private String scheduleDate;
    private String departureTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        dbHelper = new DatabaseHelper(this);

        bookingId = getIntent().getLongExtra("booking_id", -1);
        if (bookingId == -1) {
            finish();
            return;
        }

        loadBookingDetails(bookingId);
        setupActionButtons();

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }
    
    private void setupActionButtons() {
        android.widget.Button btnShare = findViewById(R.id.btnShare);
        android.widget.Button btnCancel = findViewById(R.id.btnCancel);
        
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> shareBookingInfo());
        }
        
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> showCancelBookingDialog());
        }
    }
    
    private void shareBookingInfo() {
        Cursor cursor = dbHelper.getBookingDetails(bookingId);
        if (cursor != null && cursor.moveToFirst()) {
            String companyName = cursor.getString(cursor.getColumnIndexOrThrow("company_name"));
            String fromLocation = cursor.getString(cursor.getColumnIndexOrThrow("from_location"));
            String toLocation = cursor.getString(cursor.getColumnIndexOrThrow("to_location"));
            String seatNumbers = cursor.getString(cursor.getColumnIndexOrThrow("seat_numbers"));
            String passengerName = cursor.getString(cursor.getColumnIndexOrThrow("passenger_name"));
            
            String shareText = "Thông tin đặt vé xe buýt\n\n" +
                    "Mã đặt vé: #" + bookingId + "\n" +
                    "Hãng xe: " + (companyName != null ? companyName : "N/A") + "\n" +
                    "Tuyến: " + fromLocation + " → " + toLocation + "\n" +
                    "Ngày: " + formatDate(scheduleDate) + "\n" +
                    "Giờ khởi hành: " + departureTime + "\n" +
                    "Ghế: " + (seatNumbers != null ? seatNumbers : "N/A") + "\n" +
                    "Hành khách: " + (passengerName != null ? passengerName : "N/A") + "\n\n" +
                    "Được tạo từ ứng dụng BTMS";
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Thông tin đặt vé xe buýt");
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ thông tin vé"));
            
            cursor.close();
        }
    }
    
    private void showCancelBookingDialog() {
        // Check if booking can be cancelled (must be before departure)
        if (scheduleDate != null && departureTime != null) {
            try {
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String dateTimeStr = scheduleDate + " " + departureTime;
                Date departureDateTime = dateTimeFormat.parse(dateTimeStr);
                
                if (departureDateTime != null) {
                    Calendar now = Calendar.getInstance();
                    Calendar departure = Calendar.getInstance();
                    departure.setTime(departureDateTime);
                    
                    if (now.after(departure)) {
                        Toast.makeText(this, "Không thể hủy vé vì chuyến xe đã khởi hành", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy vé")
                .setMessage("Bạn có chắc chắn muốn hủy vé này? Hành động này không thể hoàn tác.")
                .setPositiveButton("Hủy vé", (dialog, which) -> cancelBooking())
                .setNegativeButton("Không", null)
                .show();
    }
    
    private void cancelBooking() {
        // Update booking status to cancelled using helper method
        boolean success = dbHelper.updateBookingStatus(bookingId, "cancelled");
        
        if (success) {
            Toast.makeText(this, "Đã hủy vé thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra khi hủy vé", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBookingDetails(long bookingId) {
        Cursor cursor = null;
        try {
            android.util.Log.d("BookingDetailActivity", "Loading booking details for ID: " + bookingId);
            cursor = dbHelper.getBookingDetails(bookingId);

            if (cursor == null) {
                android.util.Log.e("BookingDetailActivity", "Cursor is null");
                Toast.makeText(this, "Không thể tải thông tin đặt vé. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (!cursor.moveToFirst()) {
                android.util.Log.e("BookingDetailActivity", "No data found for booking ID: " + bookingId);
                Toast.makeText(this, "Không tìm thấy thông tin đặt vé", Toast.LENGTH_SHORT).show();
                cursor.close();
                finish();
                return;
            }

            // Log column names for debugging
            String[] columnNames = cursor.getColumnNames();
            android.util.Log.d("BookingDetailActivity", "Available columns: " + java.util.Arrays.toString(columnNames));

            // Generate and display QR code
            try {
                generateQRCode(bookingId);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error generating QR code: " + e.getMessage(), e);
            }
                
            // Set booking ID
            TextView tvBookingId = findViewById(R.id.tvBookingId);
            if (tvBookingId != null) {
                tvBookingId.setText("Mã đặt vé: #" + bookingId);
            }
                
            // Bus Info - safely get company_name (may be null)
            TextView tvCompanyName = findViewById(R.id.tvCompanyName);
            TextView tvBusType = findViewById(R.id.tvBusType);
            
            String companyName = null;
            try {
                int companyNameIndex = cursor.getColumnIndex("company_name");
                if (companyNameIndex >= 0) {
                    companyName = cursor.getString(companyNameIndex);
                }
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting company_name: " + e.getMessage());
            }
            
            String busType = null;
            try {
                int busTypeIndex = cursor.getColumnIndex("bus_type");
                if (busTypeIndex >= 0) {
                    busType = cursor.getString(busTypeIndex);
                }
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting bus_type: " + e.getMessage());
            }
            
            if (tvCompanyName != null) {
                tvCompanyName.setText(companyName != null && !companyName.isEmpty() ? companyName : "EASYBUS");
            }
            if (tvBusType != null) {
                tvBusType.setText(busType != null && !busType.isEmpty() ? busType : "Tiêu chuẩn");
            }

            // Route Info - safely get all fields
            TextView tvFromLocation = findViewById(R.id.tvFromLocation);
            TextView tvToLocation = findViewById(R.id.tvToLocation);
            TextView tvBoardingPoint = findViewById(R.id.tvBoardingPoint);
            TextView tvDropPoint = findViewById(R.id.tvDropPoint);
            
            String fromLocation = null;
            String toLocation = null;
            String boardingPoint = null;
            String dropPoint = null;
            
            try {
                int fromIndex = cursor.getColumnIndex("from_location");
                if (fromIndex >= 0) fromLocation = cursor.getString(fromIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting from_location: " + e.getMessage());
            }
            
            try {
                int toIndex = cursor.getColumnIndex("to_location");
                if (toIndex >= 0) toLocation = cursor.getString(toIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting to_location: " + e.getMessage());
            }
            
            try {
                int boardingIndex = cursor.getColumnIndex("boarding_point");
                if (boardingIndex >= 0) boardingPoint = cursor.getString(boardingIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting boarding_point: " + e.getMessage());
            }
            
            try {
                int dropIndex = cursor.getColumnIndex("drop_point");
                if (dropIndex >= 0) dropPoint = cursor.getString(dropIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting drop_point: " + e.getMessage());
            }

            if (tvFromLocation != null) {
                tvFromLocation.setText("Từ: " + (fromLocation != null && !fromLocation.isEmpty() ? fromLocation : "Chưa có"));
            }
            if (tvToLocation != null) {
                tvToLocation.setText("Đến: " + (toLocation != null && !toLocation.isEmpty() ? toLocation : "Chưa có"));
            }
            if (tvBoardingPoint != null) {
                tvBoardingPoint.setText("Điểm đón: " + (boardingPoint != null && !boardingPoint.isEmpty() ? boardingPoint : "Chưa có"));
            }
            if (tvDropPoint != null) {
                tvDropPoint.setText("Điểm trả: " + (dropPoint != null && !dropPoint.isEmpty() ? dropPoint : "Chưa có"));
            }

            // Time Info - safely get all fields
            TextView tvJourneyDate = findViewById(R.id.tvJourneyDate);
            TextView tvDepartureTime = findViewById(R.id.tvDepartureTime);
            TextView tvArrivalTime = findViewById(R.id.tvArrivalTime);
            
            scheduleDate = null;
            departureTime = null;
            String arrivalTime = null;
            
            try {
                int dateIndex = cursor.getColumnIndex("date");
                if (dateIndex >= 0) scheduleDate = cursor.getString(dateIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting date: " + e.getMessage());
            }
            
            try {
                int depTimeIndex = cursor.getColumnIndex("departure_time");
                if (depTimeIndex >= 0) departureTime = cursor.getString(depTimeIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting departure_time: " + e.getMessage());
            }
            
            try {
                int arrTimeIndex = cursor.getColumnIndex("arrival_time");
                if (arrTimeIndex >= 0) arrivalTime = cursor.getString(arrTimeIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting arrival_time: " + e.getMessage());
            }

            if (tvJourneyDate != null) {
                try {
                    String formattedDate = scheduleDate != null ? formatDate(scheduleDate) : "Chưa có";
                    String dayOfWeek = scheduleDate != null ? getDayOfWeek(scheduleDate) : "";
                    tvJourneyDate.setText("Ngày: " + formattedDate + (dayOfWeek.isEmpty() ? "" : ", " + dayOfWeek));
                } catch (Exception e) {
                    android.util.Log.e("BookingDetailActivity", "Error formatting date: " + e.getMessage());
                    tvJourneyDate.setText("Ngày: Chưa có");
                }
            }
            if (tvDepartureTime != null) {
                try {
                    String timeText = departureTime != null ? formatTime12Hour(departureTime) : "Chưa có";
                    tvDepartureTime.setText("Giờ khởi hành: " + timeText);
                } catch (Exception e) {
                    android.util.Log.e("BookingDetailActivity", "Error formatting departure time: " + e.getMessage());
                    tvDepartureTime.setText("Giờ khởi hành: Chưa có");
                }
            }
            if (tvArrivalTime != null) {
                try {
                    String timeText = arrivalTime != null ? formatTime12Hour(arrivalTime) : "Chưa có";
                    tvArrivalTime.setText("Giờ đến: " + timeText);
                } catch (Exception e) {
                    android.util.Log.e("BookingDetailActivity", "Error formatting arrival time: " + e.getMessage());
                    tvArrivalTime.setText("Giờ đến: Chưa có");
                }
            }

            // Passenger Info - safely get all fields
            TextView tvPassengerName = findViewById(R.id.tvPassengerName);
            TextView tvPassengerAge = findViewById(R.id.tvPassengerAge);
            TextView tvPassengerGender = findViewById(R.id.tvPassengerGender);
            
            String passengerName = null;
            String passengerAge = null;
            String passengerGender = null;
            
            try {
                int nameIndex = cursor.getColumnIndex("passenger_name");
                if (nameIndex >= 0) passengerName = cursor.getString(nameIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting passenger_name: " + e.getMessage());
            }
            
            try {
                int ageIndex = cursor.getColumnIndex("passenger_age");
                if (ageIndex >= 0) passengerAge = cursor.getString(ageIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting passenger_age: " + e.getMessage());
            }
            
            try {
                int genderIndex = cursor.getColumnIndex("passenger_gender");
                if (genderIndex >= 0) passengerGender = cursor.getString(genderIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting passenger_gender: " + e.getMessage());
            }

            if (tvPassengerName != null) {
                tvPassengerName.setText("Tên: " + (passengerName != null && !passengerName.isEmpty() ? passengerName : "Chưa có"));
            }
            if (tvPassengerAge != null) {
                tvPassengerAge.setText("Tuổi: " + (passengerAge != null && !passengerAge.isEmpty() ? passengerAge : "Chưa có"));
            }
            if (tvPassengerGender != null) {
                String genderText = passengerGender != null && !passengerGender.isEmpty() ? 
                    (passengerGender.equalsIgnoreCase("Male") ? "Nam" : 
                     passengerGender.equalsIgnoreCase("Female") ? "Nữ" : passengerGender) : "Chưa có";
                tvPassengerGender.setText("Giới tính: " + genderText);
            }

            // Seat Info - safely get field
            TextView tvSeatNumbers = findViewById(R.id.tvSeatNumbers);
            String seatNumbers = null;
            try {
                int seatIndex = cursor.getColumnIndex("seat_numbers");
                if (seatIndex >= 0) seatNumbers = cursor.getString(seatIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting seat_numbers: " + e.getMessage());
            }
            
            if (tvSeatNumbers != null) {
                tvSeatNumbers.setText("Số ghế: " + (seatNumbers != null && !seatNumbers.isEmpty() ? seatNumbers : "Chưa có"));
            }

            // Payment Info - safely get all fields
            TextView tvTotalFare = findViewById(R.id.tvTotalFare);
            TextView tvBookingDate = findViewById(R.id.tvBookingDate);
            TextView tvBookingStatus = findViewById(R.id.tvBookingStatus);
            
            double totalFare = 0.0;
            String bookingDate = null;
            bookingStatus = null;
            
            try {
                int fareIndex = cursor.getColumnIndex("total_fare");
                if (fareIndex >= 0) totalFare = cursor.getDouble(fareIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting total_fare: " + e.getMessage());
            }
            
            try {
                int bookingDateIndex = cursor.getColumnIndex("booking_date");
                if (bookingDateIndex >= 0) bookingDate = cursor.getString(bookingDateIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting booking_date: " + e.getMessage());
            }
            
            try {
                int statusIndex = cursor.getColumnIndex("status");
                if (statusIndex >= 0) bookingStatus = cursor.getString(statusIndex);
            } catch (Exception e) {
                android.util.Log.e("BookingDetailActivity", "Error getting status: " + e.getMessage());
            }

            if (tvTotalFare != null) {
                tvTotalFare.setText("Tổng tiền: " + String.format("%.0f", totalFare) + " VNĐ");
            }
            if (tvBookingDate != null) {
                try {
                    String dateText = bookingDate != null ? formatDate(bookingDate) : "Chưa có";
                    tvBookingDate.setText("Ngày đặt vé: " + dateText);
                } catch (Exception e) {
                    android.util.Log.e("BookingDetailActivity", "Error formatting booking date: " + e.getMessage());
                    tvBookingDate.setText("Ngày đặt vé: Chưa có");
                }
            }
            if (tvBookingStatus != null) {
                String statusText = getStatusText(bookingStatus);
                tvBookingStatus.setText("Trạng thái: " + statusText);
                if (bookingStatus != null && bookingStatus.equalsIgnoreCase("confirmed")) {
                    tvBookingStatus.setTextColor(getColor(R.color.green));
                } else if (bookingStatus != null && bookingStatus.equalsIgnoreCase("cancelled")) {
                    tvBookingStatus.setTextColor(getColor(R.color.red));
                } else {
                    tvBookingStatus.setTextColor(getColor(R.color.text_secondary));
                }
            }
            
            // Hide cancel button if already cancelled or completed
            android.widget.Button btnCancel = findViewById(R.id.btnCancel);
            if (btnCancel != null) {
                if (bookingStatus != null && 
                    (bookingStatus.equalsIgnoreCase("cancelled") || 
                     bookingStatus.equalsIgnoreCase("completed"))) {
                    btnCancel.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("BookingDetailActivity", "Error loading booking details: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi tải thông tin đặt vé: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private String formatTime12Hour(String time24h) {
        if (time24h == null || time24h.isEmpty()) {
            return "Chưa có";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            Date date = inputFormat.parse(time24h);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            android.util.Log.e("BookingDetailActivity", "Error parsing time: " + time24h, e);
        }
        return time24h;
    }

    private String getDayOfWeek(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "";
        }
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
            android.util.Log.e("BookingDetailActivity", "Error parsing date: " + dateStr, e);
        }
        return "";
    }

    private void generateQRCode(long bookingId) {
        try {
            ImageView ivQRCode = findViewById(R.id.ivQRCode);
            if (ivQRCode != null) {
                String qrData = "BTMS_BOOKING_" + bookingId;
                Bitmap qrBitmap = QRCodeHelper.generateQRCode(qrData, 400, 400);
                if (qrBitmap != null) {
                    ivQRCode.setImageBitmap(qrBitmap);
                } else {
                    // Fallback: hide QR code if generation fails
                    android.util.Log.w("BookingDetailActivity", "QR code generation returned null");
                    ivQRCode.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("BookingDetailActivity", "Error generating QR code: " + e.getMessage(), e);
            ImageView ivQRCode = findViewById(R.id.ivQRCode);
            if (ivQRCode != null) {
                ivQRCode.setVisibility(View.GONE);
            }
        }
    }
    
    private String getStatusText(String status) {
        if (status == null || status.isEmpty()) {
            return "Đã xác nhận";
        }
        switch (status.toLowerCase()) {
            case "confirmed":
                return "Đã xác nhận";
            case "cancelled":
                return "Đã hủy";
            case "completed":
                return "Đã hoàn thành";
            default:
                return status;
        }
    }
    
    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "Chưa có";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            android.util.Log.e("BookingDetailActivity", "Error parsing date: " + dateStr, e);
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


