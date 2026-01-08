package com.example.btms;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private String[] paymentMethods = {"Tiền mặt", "MoMo", "My wallet", "VISA", "Credit/Debit Card", "PayPal"};
    private String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private String[] years = {"2024", "2025", "2026", "2027", "2028", "2029", "2030"};
    
    private AutoCompleteTextView actvPaymentMethod;
    private com.google.android.material.textfield.TextInputLayout tilMoMoPhone;
    private com.google.android.material.textfield.TextInputLayout tilNameOnCard;
    private com.google.android.material.textfield.TextInputLayout tilCardNumber;
    private com.google.android.material.textfield.TextInputLayout tilMonth;
    private com.google.android.material.textfield.TextInputLayout tilYear;
    private com.google.android.material.textfield.TextInputLayout tilCardSecurityCode;
    private DatabaseHelper dbHelper;
    private android.content.SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Get user name from SharedPreferences or database
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", null);
        String userName = sharedPreferences.getString("user_name", null);
        
        // If name not in SharedPreferences, get from database
        if (userName == null && userEmail != null) {
            android.content.ContentValues userInfo = dbHelper.getUserInfo(userEmail);
            if (userInfo != null && userInfo.containsKey("name")) {
                userName = userInfo.getAsString("name");
                // Save to SharedPreferences for future use
                if (userName != null) {
                    sharedPreferences.edit().putString("user_name", userName).apply();
                }
            }
        }
        
        // Update greeting with user name
        android.widget.TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null) {
            if (userName != null && !userName.isEmpty()) {
                tvGreeting.setText("Xin chào " + userName + "!");
            } else {
                tvGreeting.setText("Xin chào!");
            }
        }
        
        // Update bus info card with route number
        int routeNumber = getIntent().getIntExtra("route_number", 0);
        String busType = getIntent().getStringExtra("bus_type");
        String departureTime = getIntent().getStringExtra("departure_time");
        String arrivalTime = getIntent().getStringExtra("arrival_time");
        
        android.widget.TextView tvCompanyName = findViewById(R.id.tvCompanyName);
        if (tvCompanyName != null && routeNumber > 0) {
            tvCompanyName.setText("Tuyến số " + routeNumber);
        }
        
        android.widget.TextView tvBusType = findViewById(R.id.tvBusType);
        if (tvBusType != null && busType != null) {
            tvBusType.setText(busType);
        }
        
        if (departureTime != null && arrivalTime != null) {
            android.widget.TextView tvDurationTime = findViewById(R.id.tvDurationTime);
            if (tvDurationTime != null) {
                String timeDisplay = DateTimeHelper.formatTime12Hour(departureTime) + " - " + DateTimeHelper.formatTime12Hour(arrivalTime);
                tvDurationTime.setText(timeDisplay);
            }
        }

        actvPaymentMethod = findViewById(R.id.actvPaymentMethod);
        AutoCompleteTextView actvMonth = findViewById(R.id.actvMonth);
        AutoCompleteTextView actvYear = findViewById(R.id.actvYear);
        Button btnPayNow = findViewById(R.id.btnPayNow);
        
        tilMoMoPhone = findViewById(R.id.tilMoMoPhone);
        tilNameOnCard = findViewById(R.id.tilNameOnCard);
        tilCardNumber = findViewById(R.id.tilCardNumber);
        tilMonth = findViewById(R.id.tilMonth);
        tilYear = findViewById(R.id.tilYear);
        tilCardSecurityCode = findViewById(R.id.tilCardSecurityCode);

        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, paymentMethods);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, months);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, years);

        actvPaymentMethod.setAdapter(paymentAdapter);
        actvMonth.setAdapter(monthAdapter);
        actvYear.setAdapter(yearAdapter);
        
        // Enable dropdown on focus/click for payment method
        actvPaymentMethod.setThreshold(0); // Show dropdown immediately when clicked
        actvPaymentMethod.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actvPaymentMethod.showDropDown();
            }
        });
        actvPaymentMethod.setOnClickListener(v -> {
            actvPaymentMethod.showDropDown();
        });
        
        // Enable dropdown for Month and Year
        actvMonth.setThreshold(0);
        actvMonth.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actvMonth.showDropDown();
            }
        });
        actvMonth.setOnClickListener(v -> {
            actvMonth.showDropDown();
        });
        
        actvYear.setThreshold(0);
        actvYear.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actvYear.showDropDown();
            }
        });
        actvYear.setOnClickListener(v -> {
            actvYear.showDropDown();
        });
        
        // Set default payment method
        actvPaymentMethod.setText(paymentMethods[0], false);
        
        // Handle payment method selection
        actvPaymentMethod.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMethod = paymentMethods[position];
            toggleCardFields(selectedMethod);
        });
        
        // Initialize card fields visibility
        toggleCardFields(paymentMethods[0]);

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (validatePaymentForm()) {
                        processPayment(dbHelper, sharedPreferences, routeNumber);
                    }
                } catch (Exception e) {
                    android.util.Log.e("PaymentActivity", "Error processing payment: " + e.getMessage(), e);
                    android.widget.Toast.makeText(PaymentActivity.this, 
                            "Có lỗi xảy ra khi thanh toán. Vui lòng thử lại.", 
                            android.widget.Toast.LENGTH_LONG).show();
                }
            }
        });

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNav(rootView, R.id.navWallet);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }
    
    private void toggleCardFields(String paymentMethod) {
        boolean isCash = paymentMethod.equals("Tiền mặt");
        boolean isMoMo = paymentMethod.equals("MoMo");
        boolean isWallet = paymentMethod.equals("My wallet");
        boolean showCardFields = !isCash && !isMoMo && !isWallet;
        
        // MoMo: chỉ hiển thị số điện thoại
        if (tilMoMoPhone != null) {
            tilMoMoPhone.setVisibility(isMoMo ? View.VISIBLE : View.GONE);
        }
        
        // Wallet: hiển thị số dư
        android.view.View cardWalletBalance = findViewById(R.id.cardWalletBalance);
        android.widget.TextView tvWalletBalance = findViewById(R.id.tvWalletBalance);
        if (cardWalletBalance != null && tvWalletBalance != null) {
            if (isWallet) {
                cardWalletBalance.setVisibility(View.VISIBLE);
                // Load wallet balance
                String userEmail = sharedPreferences.getString("user_email", null);
                if (userEmail != null && dbHelper != null) {
                    double balance = dbHelper.getWalletBalance(userEmail);
                    tvWalletBalance.setText("Số dư ví: " + CurrencyHelper.formatVND(balance));
                }
            } else {
                cardWalletBalance.setVisibility(View.GONE);
            }
        }
        
        // Điều chỉnh constraint của cardWalletBalance và tilNameOnCard dựa trên view nào đang hiển thị
        if (cardWalletBalance != null) {
            android.view.ViewGroup.LayoutParams walletParams = cardWalletBalance.getLayoutParams();
            if (walletParams instanceof androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) {
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams walletConstraintParams = 
                    (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) walletParams;
                
                if (isMoMo && tilMoMoPhone != null && tilMoMoPhone.getVisibility() == View.VISIBLE) {
                    // Khi MoMo hiển thị, cardWalletBalance constraint vào tilMoMoPhone
                    walletConstraintParams.topToBottom = R.id.tilMoMoPhone;
                } else {
                    // Mặc định constraint vào tilPaymentMethod
                    walletConstraintParams.topToBottom = R.id.tilPaymentMethod;
                }
                cardWalletBalance.setLayoutParams(walletConstraintParams);
            }
        }
        
        // Điều chỉnh constraint của tilNameOnCard
        if (tilNameOnCard != null) {
            android.view.ViewGroup.LayoutParams params = tilNameOnCard.getLayoutParams();
            if (params instanceof androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) {
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams constraintParams = 
                    (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) params;
                
                if (isMoMo && tilMoMoPhone != null && tilMoMoPhone.getVisibility() == View.VISIBLE) {
                    // Khi MoMo hiển thị, constraint vào tilMoMoPhone
                    constraintParams.topToBottom = R.id.tilMoMoPhone;
                } else if (isWallet && cardWalletBalance != null && cardWalletBalance.getVisibility() == View.VISIBLE) {
                    // Khi Wallet hiển thị, constraint vào cardWalletBalance
                    constraintParams.topToBottom = R.id.cardWalletBalance;
                } else {
                    // Mặc định constraint vào tilPaymentMethod
                    constraintParams.topToBottom = R.id.tilPaymentMethod;
                }
                tilNameOnCard.setLayoutParams(constraintParams);
            }
            tilNameOnCard.setVisibility(showCardFields ? View.VISIBLE : View.GONE);
        }
        if (tilCardNumber != null) {
            tilCardNumber.setVisibility(showCardFields ? View.VISIBLE : View.GONE);
        }
        if (tilMonth != null) {
            tilMonth.setVisibility(showCardFields ? View.VISIBLE : View.GONE);
        }
        if (tilYear != null) {
            tilYear.setVisibility(showCardFields ? View.VISIBLE : View.GONE);
        }
        if (tilCardSecurityCode != null) {
            tilCardSecurityCode.setVisibility(showCardFields ? View.VISIBLE : View.GONE);
        }
    }
    
    private boolean validatePaymentForm() {
        String paymentMethod = actvPaymentMethod.getText().toString().trim();
        
        if (paymentMethod.isEmpty()) {
            android.widget.Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", 
                    android.widget.Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Validate MoMo phone number
        if (paymentMethod.equals("MoMo")) {
            com.google.android.material.textfield.TextInputEditText etMoMoPhone = findViewById(R.id.etMoMoPhone);
            if (etMoMoPhone != null) {
                String phoneNumber = etMoMoPhone.getText().toString().trim();
                if (phoneNumber.isEmpty()) {
                    android.widget.Toast.makeText(this, "Vui lòng nhập số điện thoại MoMo", 
                            android.widget.Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (phoneNumber.length() < 10) {
                    android.widget.Toast.makeText(this, "Số điện thoại MoMo không hợp lệ", 
                            android.widget.Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true; // MoMo only needs phone number
        }
        
        // Validate Wallet payment
        if (paymentMethod.equals("My wallet")) {
            String userEmail = sharedPreferences.getString("user_email", null);
            if (userEmail == null) {
                android.widget.Toast.makeText(this, "Vui lòng đăng nhập", 
                        android.widget.Toast.LENGTH_SHORT).show();
                return false;
            }
            // Wallet balance will be checked in processPayment
            return true;
        }
        
        // If not cash payment, not MoMo, and not Wallet, validate card fields
        if (!paymentMethod.equals("Tiền mặt")) {
            com.google.android.material.textfield.TextInputEditText etNameOnCard = findViewById(R.id.etNameOnCard);
            com.google.android.material.textfield.TextInputEditText etCardNumber = findViewById(R.id.etCardNumber);
            AutoCompleteTextView actvMonth = findViewById(R.id.actvMonth);
            AutoCompleteTextView actvYear = findViewById(R.id.actvYear);
            com.google.android.material.textfield.TextInputEditText etCardSecurityCode = findViewById(R.id.etCardSecurityCode);
            
            if (etNameOnCard != null && etNameOnCard.getText().toString().trim().isEmpty()) {
                android.widget.Toast.makeText(this, "Vui lòng nhập tên trên thẻ", 
                        android.widget.Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (etCardNumber != null && etCardNumber.getText().toString().trim().isEmpty()) {
                android.widget.Toast.makeText(this, "Vui lòng nhập số thẻ", 
                        android.widget.Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (actvMonth != null && actvMonth.getText().toString().trim().isEmpty()) {
                android.widget.Toast.makeText(this, "Vui lòng chọn tháng hết hạn", 
                        android.widget.Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (actvYear != null && actvYear.getText().toString().trim().isEmpty()) {
                android.widget.Toast.makeText(this, "Vui lòng chọn năm hết hạn", 
                        android.widget.Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (etCardSecurityCode != null && etCardSecurityCode.getText().toString().trim().isEmpty()) {
                android.widget.Toast.makeText(this, "Vui lòng nhập mã bảo mật", 
                        android.widget.Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        
        return true;
    }

    private void processPayment(DatabaseHelper dbHelper, android.content.SharedPreferences sharedPreferences, int routeNumber) {
        try {
            BookingData data = getBookingDataFromIntent();
            if (data == null) {
                return;
            }
            
            // Check payment method and process wallet payment if needed
            String paymentMethod = actvPaymentMethod.getText().toString().trim();
            if (paymentMethod.equals("My wallet")) {
                String userEmail = sharedPreferences.getString("user_email", null);
                if (userEmail == null) {
                    android.widget.Toast.makeText(this, "Vui lòng đăng nhập", 
                            android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Convert totalFare from thousands to actual VND (multiply by 1000)
                double totalFareVND = data.totalFare * 1000;
                double walletBalance = dbHelper.getWalletBalance(userEmail);
                if (walletBalance < totalFareVND) {
                    android.widget.Toast.makeText(this, 
                            "Số dư ví không đủ. Số dư hiện tại: " + CurrencyHelper.formatVND(walletBalance) + 
                            ". Cần: " + CurrencyHelper.formatPrice(data.totalFare), 
                            android.widget.Toast.LENGTH_LONG).show();
                    return;
                }
            }
            
            long bookingId = saveBookingToDatabase(dbHelper, sharedPreferences, data);
            if (bookingId <= 0) {
                android.widget.Toast.makeText(this, "Lỗi khi lưu thông tin đặt vé. Vui lòng thử lại.", 
                        android.widget.Toast.LENGTH_LONG).show();
                return;
            }
            
            String bookingCode = getBookingCode(dbHelper, bookingId);
            
            // Process wallet payment after booking is saved
            if (paymentMethod.equals("My wallet")) {
                String userEmail = sharedPreferences.getString("user_email", null);
                if (userEmail != null) {
                    // Convert totalFare from thousands to actual VND (multiply by 1000)
                    double totalFareVND = data.totalFare * 1000;
                    String description = "Thanh toán đặt vé - Mã: " + (bookingCode != null ? bookingCode : String.valueOf(bookingId));
                    if (!dbHelper.payWithWallet(userEmail, totalFareVND, description)) {
                        android.widget.Toast.makeText(this, "Lỗi khi thanh toán bằng ví. Vui lòng thử lại.", 
                                android.widget.Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            updateScheduleTimes(dbHelper, data);
            
            // Send email in background to avoid blocking UI
            try {
                sendBookingConfirmationEmail(data, bookingCode, bookingId, routeNumber);
            } catch (Exception e) {
                android.util.Log.e("PaymentActivity", "Error sending email: " + e.getMessage(), e);
                // Continue even if email fails
            }
            
            showBookingNotifications(data, bookingId);
            navigateToConfirmation(data, bookingId);
        } catch (Exception e) {
            android.util.Log.e("PaymentActivity", "Error in processPayment: " + e.getMessage(), e);
            android.widget.Toast.makeText(this, "Có lỗi xảy ra: " + e.getMessage(), 
                    android.widget.Toast.LENGTH_LONG).show();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }
    
    private static class BookingData {
        String fromLocation;
        String toLocation;
        long scheduleId;
        double price;
        String boardingPoint;
        String dropPoint;
        String scheduleDate;
        String departureTime;
        String arrivalTime;
        String contactEmail;
        String passengerName;
        String passengerAge;
        String gender;
        String seatNumbers;
        double totalFare;
    }
    
    private BookingData getBookingDataFromIntent() {
        BookingData data = new BookingData();
        Intent intent = getIntent();
        
        data.fromLocation = intent.getStringExtra("from_location");
        data.toLocation = intent.getStringExtra("to_location");
        data.scheduleId = intent.getLongExtra("schedule_id", -1);
        data.price = intent.getDoubleExtra("price", 0);
        data.boardingPoint = intent.getStringExtra("boarding_point");
        data.dropPoint = intent.getStringExtra("drop_point");
        data.scheduleDate = intent.getStringExtra("schedule_date");
        data.departureTime = intent.getStringExtra("departure_time");
        data.arrivalTime = intent.getStringExtra("arrival_time");
        data.contactEmail = intent.getStringExtra("contact_email");
        
        if (data.contactEmail == null || data.contactEmail.isEmpty()) {
            android.widget.Toast.makeText(this, "Vui lòng nhập email để nhận vé", android.widget.Toast.LENGTH_SHORT).show();
            return null;
        }
        
        java.util.ArrayList<String> passengerNames = intent.getStringArrayListExtra("passenger_names");
        java.util.ArrayList<String> passengerAges = intent.getStringArrayListExtra("passenger_ages");
        java.util.ArrayList<String> passengerGenders = intent.getStringArrayListExtra("passenger_genders");
        
        data.passengerName = (passengerNames != null && !passengerNames.isEmpty()) ? passengerNames.get(0) : "";
        data.passengerAge = (passengerAges != null && !passengerAges.isEmpty()) ? passengerAges.get(0) : "";
        data.gender = (passengerGenders != null && !passengerGenders.isEmpty()) ? passengerGenders.get(0) : "";
        
        if (passengerNames != null && passengerNames.size() > 1) {
            StringBuilder namesBuilder = new StringBuilder();
            for (int i = 0; i < passengerNames.size(); i++) {
                if (i > 0) namesBuilder.append(", ");
                namesBuilder.append(passengerNames.get(i));
            }
            data.passengerName = namesBuilder.toString();
        }
        
        data.seatNumbers = intent.getStringExtra("selected_seats");
        if (data.seatNumbers == null || data.seatNumbers.isEmpty()) {
            data.seatNumbers = "";
        }
        
        data.totalFare = intent.getDoubleExtra("total_fare", data.price);
        
        return data;
    }
    
    private long saveBookingToDatabase(DatabaseHelper dbHelper, android.content.SharedPreferences sharedPreferences, BookingData data) {
        String bookingUserEmail = sharedPreferences.getString("user_email", "hello@example.com");
        // Save booking date with time for accurate sorting
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        String bookingDate = sdf.format(new java.util.Date());
        
        android.util.Log.d("PaymentActivity", "Saving booking to database - User: " + bookingUserEmail + ", Schedule ID: " + data.scheduleId);
        
        long bookingId = dbHelper.insertBooking(
                data.scheduleId,
                bookingUserEmail,
                data.passengerName,
                data.passengerAge,
                data.gender,
                data.seatNumbers,
                data.boardingPoint,
                data.dropPoint,
                data.totalFare,
                "confirmed",
                bookingDate
        );
        
        android.util.Log.d("PaymentActivity", "Booking saved with ID: " + bookingId);
        return bookingId;
    }
    
    private String getBookingCode(DatabaseHelper dbHelper, long bookingId) {
        if (bookingId <= 0) {
            return null;
        }
        
        android.database.Cursor bookingCursor = dbHelper.getBookingDetails(bookingId);
        if (bookingCursor != null && bookingCursor.moveToFirst()) {
            int codeIndex = bookingCursor.getColumnIndex("booking_code");
            if (codeIndex >= 0) {
                String code = bookingCursor.getString(codeIndex);
                bookingCursor.close();
                return code;
            }
            bookingCursor.close();
        }
        return null;
    }
    
    private void updateScheduleTimes(DatabaseHelper dbHelper, BookingData data) {
        if (data.scheduleDate == null || data.scheduleDate.isEmpty() || 
            data.departureTime == null || data.departureTime.isEmpty()) {
            android.database.Cursor scheduleCursor = dbHelper.getScheduleDetails(data.scheduleId);
            if (scheduleCursor != null && scheduleCursor.moveToFirst()) {
                if (data.scheduleDate == null || data.scheduleDate.isEmpty()) {
                    data.scheduleDate = scheduleCursor.getString(scheduleCursor.getColumnIndexOrThrow("date"));
                }
                if (data.departureTime == null || data.departureTime.isEmpty()) {
                    data.departureTime = scheduleCursor.getString(scheduleCursor.getColumnIndexOrThrow("departure_time"));
                }
                if (data.arrivalTime == null || data.arrivalTime.isEmpty()) {
                    int arrIndex = scheduleCursor.getColumnIndex("arrival_time");
                    if (arrIndex >= 0) {
                        data.arrivalTime = scheduleCursor.getString(arrIndex);
                    }
                }
                scheduleCursor.close();
            }
        }
    }
    
    private void sendBookingConfirmationEmail(BookingData data, String bookingCode, long bookingId, int routeNumber) {
        try {
            Bitmap qrBitmap = null;
            if (bookingCode != null) {
                String qrData = QRCodeDataHelper.generateQRDataText(
                        bookingCode,
                        routeNumber,
                        data.fromLocation,
                        data.toLocation,
                        data.scheduleDate,
                        data.departureTime,
                        data.arrivalTime,
                        data.seatNumbers,
                        data.passengerName,
                        data.totalFare
                );
                if (qrData != null) {
                    qrBitmap = QRCodeHelper.generateQRCode(qrData, 400, 400);
                }
            }
            
            String bookingDetails = "Thông tin đặt vé:\n" +
                    "Mã đặt vé: " + (bookingCode != null ? bookingCode : String.valueOf(bookingId)) + "\n" +
                    "Tuyến số: " + routeNumber + "\n" +
                    "Từ: " + (data.fromLocation != null ? data.fromLocation : "N/A") + "\n" +
                    "Đến: " + (data.toLocation != null ? data.toLocation : "N/A") + "\n" +
                    "Ngày: " + (data.scheduleDate != null ? DateTimeHelper.formatDate(data.scheduleDate) : "N/A") + "\n" +
                    "Giờ khởi hành: " + (data.departureTime != null ? DateTimeHelper.formatTime12Hour(data.departureTime) : "N/A") + "\n" +
                    "Giờ đến: " + (data.arrivalTime != null ? DateTimeHelper.formatTime12Hour(data.arrivalTime) : "N/A") + "\n" +
                    "Ghế: " + (data.seatNumbers != null && !data.seatNumbers.isEmpty() ? data.seatNumbers : "N/A") + "\n" +
                    "Hành khách: " + (data.passengerName != null && !data.passengerName.isEmpty() ? data.passengerName : "N/A") + "\n" +
                    "Tổng tiền: " + CurrencyHelper.formatPrice(data.totalFare);
            
            android.util.Log.d("PaymentActivity", "Sending email to contact email: " + data.contactEmail);
            if (data.contactEmail != null && !data.contactEmail.isEmpty()) {
                EmailHelper.sendBookingEmail(this, data.contactEmail, 
                        bookingCode != null ? bookingCode : String.valueOf(bookingId),
                        qrBitmap, bookingDetails);
            }
        } catch (Exception e) {
            android.util.Log.e("PaymentActivity", "Error sending email: " + e.getMessage(), e);
            throw e;
        }
    }
    
    private void showBookingNotifications(BookingData data, long bookingId) {
        if (data.departureTime != null && data.scheduleDate != null) {
            String userEmail = sharedPreferences.getString("user_email", null);
            NotificationHelper.showBookingConfirmationNotification(
                    this, userEmail, data.fromLocation, data.toLocation, data.departureTime, data.scheduleDate, bookingId);
            
            NotificationHelper.scheduleDepartureReminder(
                    this, bookingId, data.fromLocation, data.toLocation, data.departureTime, data.scheduleDate);
        }
    }
    
    private void navigateToConfirmation(BookingData data, long bookingId) {
        Intent intent = new Intent(PaymentActivity.this, OrderConfirmationActivity.class);
        intent.putExtra("booking_id", bookingId);
        intent.putExtra("from_location", data.fromLocation);
        intent.putExtra("to_location", data.toLocation);
        startActivity(intent);
        finish();
    }
}

