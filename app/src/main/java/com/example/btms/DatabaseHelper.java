package com.example.btms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "btms.db";
    private static final int DATABASE_VERSION = 12; // Added notifications table

    // Table: users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_PHONE = "phone";
    private static final String COL_USER_IS_VERIFIED = "is_verified";
    private static final String COL_USER_VERIFICATION_CODE = "verification_code";
    private static final String COL_USER_CREATED_AT = "created_at";
    private static final String COL_USER_LOGIN_TYPE = "login_type"; // email, google

    // Table: locations
    private static final String TABLE_LOCATIONS = "locations";
    private static final String COL_LOCATION_ID = "id";
    private static final String COL_LOCATION_NAME = "name";

    private static final String COL_LOCATION_STATE = "state";
    private static final String COL_LOCATION_TYPE = "type"; // city, terminal, station

    // Table: bus_routes
    private static final String TABLE_BUS_ROUTES = "bus_routes";
    private static final String COL_ROUTE_ID = "id";
    private static final String COL_ROUTE_FROM = "from_location_id";
    private static final String COL_ROUTE_TO = "to_location_id";
    private static final String COL_ROUTE_DISTANCE = "distance";
    private static final String COL_ROUTE_DURATION = "duration";
    private static final String COL_ROUTE_NUMBER = "route_number";

    // Table: bus_companies
    private static final String TABLE_BUS_COMPANIES = "bus_companies";
    private static final String COL_COMPANY_ID = "id";
    private static final String COL_COMPANY_NAME = "name";
    private static final String COL_COMPANY_RATING = "rating";

    // Table: bus_schedules
    private static final String TABLE_BUS_SCHEDULES = "bus_schedules";
    private static final String COL_SCHEDULE_ID = "id";
    private static final String COL_SCHEDULE_ROUTE_ID = "route_id";
    private static final String COL_SCHEDULE_COMPANY_ID = "company_id";
    private static final String COL_SCHEDULE_DEPARTURE_TIME = "departure_time";
    private static final String COL_SCHEDULE_ARRIVAL_TIME = "arrival_time";
    private static final String COL_SCHEDULE_PRICE = "price";
    private static final String COL_SCHEDULE_BUS_TYPE = "bus_type";
    private static final String COL_SCHEDULE_TOTAL_SEATS = "total_seats";
    private static final String COL_SCHEDULE_AVAILABLE_SEATS = "available_seats";
    private static final String COL_SCHEDULE_DATE = "date";

    // Table: bookings
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String COL_BOOKING_ID = "id";
    private static final String COL_BOOKING_SCHEDULE_ID = "schedule_id";
    private static final String COL_BOOKING_USER_EMAIL = "user_email";
    private static final String COL_BOOKING_PASSENGER_NAME = "passenger_name";
    private static final String COL_BOOKING_PASSENGER_AGE = "passenger_age";
    private static final String COL_BOOKING_PASSENGER_GENDER = "passenger_gender";
    private static final String COL_BOOKING_SEAT_NUMBERS = "seat_numbers";
    private static final String COL_BOOKING_BOARDING_POINT = "boarding_point";
    private static final String COL_BOOKING_DROP_POINT = "drop_point";
    private static final String COL_BOOKING_TOTAL_FARE = "total_fare";
    private static final String COL_BOOKING_STATUS = "status";
    private static final String COL_BOOKING_BOOKING_DATE = "booking_date";
    private static final String COL_BOOKING_CODE = "booking_code";

    // Table: wallet
    private static final String TABLE_WALLET = "wallet";
    private static final String COL_WALLET_ID = "id";
    private static final String COL_WALLET_USER_EMAIL = "user_email";
    private static final String COL_WALLET_BALANCE = "balance";
    private static final String COL_WALLET_UPDATED_AT = "updated_at";

    // Table: wallet_transactions
    private static final String TABLE_WALLET_TRANSACTIONS = "wallet_transactions";
    private static final String COL_TRANSACTION_ID = "id";
    private static final String COL_TRANSACTION_USER_EMAIL = "user_email";
    private static final String COL_TRANSACTION_TYPE = "type"; // deposit, withdraw, payment
    private static final String COL_TRANSACTION_AMOUNT = "amount";
    private static final String COL_TRANSACTION_DESCRIPTION = "description";
    private static final String COL_TRANSACTION_DATE = "transaction_date";

    // Table: notifications
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String COL_NOTIFICATION_ID = "id";
    private static final String COL_NOTIFICATION_USER_EMAIL = "user_email";
    private static final String COL_NOTIFICATION_TITLE = "title";
    private static final String COL_NOTIFICATION_MESSAGE = "message";
    private static final String COL_NOTIFICATION_TYPE = "type"; // booking, reminder, promotion, system
    private static final String COL_NOTIFICATION_IS_READ = "is_read";
    private static final String COL_NOTIFICATION_CREATED_AT = "created_at";
    private static final String COL_NOTIFICATION_BOOKING_ID = "booking_id"; // Optional: link to booking

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        try {
            db.execSQL("PRAGMA journal_mode=WAL;");
            android.util.Log.d("DatabaseHelper", "WAL mode enabled for better concurrency");
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error enabling WAL mode: " + e.getMessage(), e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USER_NAME + " TEXT NOT NULL,"
                + COL_USER_EMAIL + " TEXT UNIQUE NOT NULL,"
                + COL_USER_PASSWORD + " TEXT,"
                + COL_USER_PHONE + " TEXT,"
                + COL_USER_IS_VERIFIED + " INTEGER DEFAULT 0,"
                + COL_USER_VERIFICATION_CODE + " TEXT,"
                + COL_USER_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP,"
                + COL_USER_LOGIN_TYPE + " TEXT DEFAULT 'email'"
                + ")";
        db.execSQL(createUsersTable);

        // Create locations table
        String createLocationsTable = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + COL_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_LOCATION_NAME + " TEXT NOT NULL,"
                + COL_LOCATION_STATE + " TEXT,"
                + COL_LOCATION_TYPE + " TEXT DEFAULT 'city'"
                + ")";
        db.execSQL(createLocationsTable);

        // Create bus_companies table
        String createCompaniesTable = "CREATE TABLE " + TABLE_BUS_COMPANIES + "("
                + COL_COMPANY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_COMPANY_NAME + " TEXT NOT NULL,"
                + COL_COMPANY_RATING + " REAL DEFAULT 0.0"
                + ")";
        db.execSQL(createCompaniesTable);

        // Create bus_routes table
        String createRoutesTable = "CREATE TABLE " + TABLE_BUS_ROUTES + "("
                + COL_ROUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_ROUTE_FROM + " INTEGER NOT NULL,"
                + COL_ROUTE_TO + " INTEGER NOT NULL,"
                + COL_ROUTE_DISTANCE + " REAL,"
                + COL_ROUTE_DURATION + " INTEGER,"
                + COL_ROUTE_NUMBER + " INTEGER,"
                + "FOREIGN KEY(" + COL_ROUTE_FROM + ") REFERENCES " + TABLE_LOCATIONS + "(" + COL_LOCATION_ID + "),"
                + "FOREIGN KEY(" + COL_ROUTE_TO + ") REFERENCES " + TABLE_LOCATIONS + "(" + COL_LOCATION_ID + ")"
                + ")";
        db.execSQL(createRoutesTable);

        // Create bus_schedules table
        String createSchedulesTable = "CREATE TABLE " + TABLE_BUS_SCHEDULES + "("
                + COL_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_SCHEDULE_ROUTE_ID + " INTEGER NOT NULL,"
                + COL_SCHEDULE_COMPANY_ID + " INTEGER NOT NULL,"
                + COL_SCHEDULE_DEPARTURE_TIME + " TEXT NOT NULL,"
                + COL_SCHEDULE_ARRIVAL_TIME + " TEXT NOT NULL,"
                + COL_SCHEDULE_PRICE + " REAL NOT NULL,"
                + COL_SCHEDULE_BUS_TYPE + " TEXT,"
                + COL_SCHEDULE_TOTAL_SEATS + " INTEGER DEFAULT 28,"
                + COL_SCHEDULE_AVAILABLE_SEATS + " INTEGER DEFAULT 28,"
                + COL_SCHEDULE_DATE + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + COL_SCHEDULE_ROUTE_ID + ") REFERENCES " + TABLE_BUS_ROUTES + "(" + COL_ROUTE_ID + "),"
                + "FOREIGN KEY(" + COL_SCHEDULE_COMPANY_ID + ") REFERENCES " + TABLE_BUS_COMPANIES + "(" + COL_COMPANY_ID + ")"
                + ")";
        db.execSQL(createSchedulesTable);

        // Create bookings table
        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + "("
                + COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_BOOKING_SCHEDULE_ID + " INTEGER NOT NULL,"
                + COL_BOOKING_USER_EMAIL + " TEXT NOT NULL,"
                + COL_BOOKING_PASSENGER_NAME + " TEXT NOT NULL,"
                + COL_BOOKING_PASSENGER_AGE + " INTEGER,"
                + COL_BOOKING_PASSENGER_GENDER + " TEXT,"
                + COL_BOOKING_SEAT_NUMBERS + " TEXT,"
                + COL_BOOKING_BOARDING_POINT + " TEXT,"
                + COL_BOOKING_DROP_POINT + " TEXT,"
                + COL_BOOKING_TOTAL_FARE + " REAL NOT NULL,"
                + COL_BOOKING_STATUS + " TEXT DEFAULT 'confirmed',"
                + COL_BOOKING_BOOKING_DATE + " TEXT,"
                + COL_BOOKING_CODE + " TEXT UNIQUE,"
                + "FOREIGN KEY(" + COL_BOOKING_SCHEDULE_ID + ") REFERENCES " + TABLE_BUS_SCHEDULES + "(" + COL_SCHEDULE_ID + ")"
                + ")";
        db.execSQL(createBookingsTable);

        // Create wallet table
        String createWalletTable = "CREATE TABLE " + TABLE_WALLET + "("
                + COL_WALLET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_WALLET_USER_EMAIL + " TEXT UNIQUE NOT NULL,"
                + COL_WALLET_BALANCE + " REAL DEFAULT 0.0,"
                + COL_WALLET_UPDATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(createWalletTable);

        // Create wallet_transactions table
        String createWalletTransactionsTable = "CREATE TABLE " + TABLE_WALLET_TRANSACTIONS + "("
                + COL_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TRANSACTION_USER_EMAIL + " TEXT NOT NULL,"
                + COL_TRANSACTION_TYPE + " TEXT NOT NULL,"
                + COL_TRANSACTION_AMOUNT + " REAL NOT NULL,"
                + COL_TRANSACTION_DESCRIPTION + " TEXT,"
                + COL_TRANSACTION_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(createWalletTransactionsTable);

        // Create notifications table
        String createNotificationsTable = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + COL_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NOTIFICATION_USER_EMAIL + " TEXT NOT NULL,"
                + COL_NOTIFICATION_TITLE + " TEXT NOT NULL,"
                + COL_NOTIFICATION_MESSAGE + " TEXT NOT NULL,"
                + COL_NOTIFICATION_TYPE + " TEXT DEFAULT 'system',"
                + COL_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0,"
                + COL_NOTIFICATION_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP,"
                + COL_NOTIFICATION_BOOKING_ID + " INTEGER"
                + ")";
        db.execSQL(createNotificationsTable);

        // Insert initial data
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add users table if upgrading from version 1
            String createUsersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "("
                    + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_USER_NAME + " TEXT NOT NULL,"
                    + COL_USER_EMAIL + " TEXT UNIQUE NOT NULL,"
                    + COL_USER_PASSWORD + " TEXT,"
                    + COL_USER_PHONE + " TEXT,"
                    + COL_USER_IS_VERIFIED + " INTEGER DEFAULT 0,"
                    + COL_USER_VERIFICATION_CODE + " TEXT,"
                    + COL_USER_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP,"
                    + COL_USER_LOGIN_TYPE + " TEXT DEFAULT 'email'"
                    + ")";
            db.execSQL(createUsersTable);
        }
        
        if (oldVersion < 3) {
            // Clear old locations and insert HCMC districts
            db.execSQL("DELETE FROM " + TABLE_LOCATIONS);
            db.execSQL("DELETE FROM " + TABLE_BUS_ROUTES);
            db.execSQL("DELETE FROM " + TABLE_BUS_SCHEDULES);
            
            // Add route_number column if it doesn't exist
            try {
                db.execSQL("ALTER TABLE " + TABLE_BUS_ROUTES + " ADD COLUMN " + COL_ROUTE_NUMBER + " INTEGER");
            } catch (Exception e) {
                // Column might already exist, ignore
            }
            
            // Insert Ho Chi Minh City districts
            String[] hcmcDistricts = {
                    "Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5", "Quận 6",
                    "Quận 7", "Quận 8", "Quận 9", "Quận 10", "Quận 11", "Quận 12",
                    "Bình Thạnh", "Tân Bình", "Tân Phú", "Phú Nhuận", "Gò Vấp", "Bình Tân", "Thủ Đức"
            };

            ContentValues locationValues = new ContentValues();
            for (String district : hcmcDistricts) {
                locationValues.clear();
                locationValues.put(COL_LOCATION_NAME, district);
                locationValues.put(COL_LOCATION_STATE, "TP. Hồ Chí Minh");
                locationValues.put(COL_LOCATION_TYPE, "district");
                db.insert(TABLE_LOCATIONS, null, locationValues);
            }
            
            // Recreate routes between HCMC districts
            insertSampleRoutes(db);
        }
        
        if (oldVersion < 4) {
            // Force recreate all routes and schedules to ensure route_number is set correctly
            android.util.Log.d("DatabaseHelper", "Upgrading to version 4: Recreating routes with route numbers");
            db.execSQL("DELETE FROM " + TABLE_BUS_ROUTES);
            db.execSQL("DELETE FROM " + TABLE_BUS_SCHEDULES);
            insertSampleRoutes(db);
        }
        
        if (oldVersion < 5) {
            // Force recreate all routes and schedules again to ensure route_number is set correctly
            android.util.Log.d("DatabaseHelper", "Upgrading to version 5: Recreating routes with route numbers");
            db.execSQL("DELETE FROM " + TABLE_BUS_ROUTES);
            db.execSQL("DELETE FROM " + TABLE_BUS_SCHEDULES);
            insertSampleRoutes(db);
        }
        
        if (oldVersion < 6) {
            // Remove all old schedules (past schedules) and keep only future schedules
            android.util.Log.d("DatabaseHelper", "Upgrading to version 6: Removing old schedules");
            
            // Get current date and time
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            java.util.Calendar now = java.util.Calendar.getInstance();
            String currentDate = dateFormat.format(now.getTime());
            String currentTime = timeFormat.format(now.getTime());
            
            // Delete all schedules that are in the past
            // Using: date < currentDate OR (date = currentDate AND departure_time < currentTime)
            String deleteQuery = "DELETE FROM " + TABLE_BUS_SCHEDULES + 
                    " WHERE (" + COL_SCHEDULE_DATE + " < ? OR " +
                    "(" + COL_SCHEDULE_DATE + " = ? AND " + COL_SCHEDULE_DEPARTURE_TIME + " < ?))";
            
            db.execSQL(deleteQuery, new String[]{currentDate, currentDate, currentTime});
            android.util.Log.d("DatabaseHelper", "Deleted old schedules before " + currentDate + " " + currentTime);
        }

        if (oldVersion < 7) {
            // Force recreation of schedules and bookings for new sync logic
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUS_SCHEDULES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
            
            // Re-create them using onCreate logic
            String createSchedulesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BUS_SCHEDULES + "("
                    + COL_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_SCHEDULE_ROUTE_ID + " INTEGER NOT NULL,"
                    + COL_SCHEDULE_COMPANY_ID + " INTEGER NOT NULL,"
                    + COL_SCHEDULE_DEPARTURE_TIME + " TEXT NOT NULL,"
                    + COL_SCHEDULE_ARRIVAL_TIME + " TEXT NOT NULL,"
                    + COL_SCHEDULE_PRICE + " REAL NOT NULL,"
                    + COL_SCHEDULE_BUS_TYPE + " TEXT,"
                    + COL_SCHEDULE_TOTAL_SEATS + " INTEGER DEFAULT 28,"
                    + COL_SCHEDULE_AVAILABLE_SEATS + " INTEGER DEFAULT 28,"
                    + COL_SCHEDULE_DATE + " TEXT NOT NULL,"
                    + "FOREIGN KEY(" + COL_SCHEDULE_ROUTE_ID + ") REFERENCES " + TABLE_BUS_ROUTES + "(" + COL_ROUTE_ID + "),"
                    + "FOREIGN KEY(" + COL_SCHEDULE_COMPANY_ID + ") REFERENCES " + TABLE_BUS_COMPANIES + "(" + COL_COMPANY_ID + ")"
                    + ")";
            db.execSQL(createSchedulesTable);

            String createBookingsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKINGS + "("
                    + COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_BOOKING_SCHEDULE_ID + " INTEGER NOT NULL,"
                    + COL_BOOKING_USER_EMAIL + " TEXT NOT NULL,"
                    + COL_BOOKING_PASSENGER_NAME + " TEXT NOT NULL,"
                    + COL_BOOKING_PASSENGER_AGE + " INTEGER,"
                    + COL_BOOKING_PASSENGER_GENDER + " TEXT,"
                    + COL_BOOKING_SEAT_NUMBERS + " TEXT,"
                    + COL_BOOKING_BOARDING_POINT + " TEXT,"
                    + COL_BOOKING_DROP_POINT + " TEXT,"
                    + COL_BOOKING_TOTAL_FARE + " REAL NOT NULL,"
                    + COL_BOOKING_STATUS + " TEXT DEFAULT 'confirmed',"
                    + COL_BOOKING_BOOKING_DATE + " TEXT,"
                    + COL_BOOKING_CODE + " TEXT UNIQUE,"
                    + "FOREIGN KEY(" + COL_BOOKING_SCHEDULE_ID + ") REFERENCES " + TABLE_BUS_SCHEDULES + "(" + COL_SCHEDULE_ID + ")"
                    + ")";
            db.execSQL(createBookingsTable);
        }
        
        if (oldVersion < 9) {
            // Add booking_code column to bookings table
            android.util.Log.d("DatabaseHelper", "Upgrading to version 9: Adding booking_code column");
            try {
                db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COL_BOOKING_CODE + " TEXT UNIQUE");
            } catch (Exception e) {
                android.util.Log.e("DatabaseHelper", "Error adding booking_code column: " + e.getMessage());
            }
        }
        
        if (oldVersion < 10) {
            // Ensure booking_code column exists (for databases that might have been created incorrectly)
            android.util.Log.d("DatabaseHelper", "Upgrading to version 10: Ensuring booking_code column exists");
            try {
                // Check if column exists by trying to query it
                Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_BOOKINGS + ")", null);
                boolean columnExists = false;
                if (cursor != null) {
                    int nameIndex = cursor.getColumnIndex("name");
                    while (cursor.moveToNext()) {
                        String columnName = cursor.getString(nameIndex);
                        if (COL_BOOKING_CODE.equals(columnName)) {
                            columnExists = true;
                            break;
                        }
                    }
                    cursor.close();
                }
                
                if (!columnExists) {
                    android.util.Log.d("DatabaseHelper", "booking_code column does not exist, adding it");
                    db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COL_BOOKING_CODE + " TEXT UNIQUE");
                } else {
                    android.util.Log.d("DatabaseHelper", "booking_code column already exists");
                }
            } catch (Exception e) {
                android.util.Log.e("DatabaseHelper", "Error ensuring booking_code column: " + e.getMessage());
                // If ALTER fails, try to recreate the table
                try {
                    android.util.Log.d("DatabaseHelper", "Attempting to recreate bookings table with booking_code");
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
                    String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + "("
                            + COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COL_BOOKING_SCHEDULE_ID + " INTEGER NOT NULL,"
                            + COL_BOOKING_USER_EMAIL + " TEXT NOT NULL,"
                            + COL_BOOKING_PASSENGER_NAME + " TEXT NOT NULL,"
                            + COL_BOOKING_PASSENGER_AGE + " INTEGER,"
                            + COL_BOOKING_PASSENGER_GENDER + " TEXT,"
                            + COL_BOOKING_SEAT_NUMBERS + " TEXT,"
                            + COL_BOOKING_BOARDING_POINT + " TEXT,"
                            + COL_BOOKING_DROP_POINT + " TEXT,"
                            + COL_BOOKING_TOTAL_FARE + " REAL NOT NULL,"
                            + COL_BOOKING_STATUS + " TEXT DEFAULT 'confirmed',"
                            + COL_BOOKING_BOOKING_DATE + " TEXT,"
                            + COL_BOOKING_CODE + " TEXT UNIQUE,"
                            + "FOREIGN KEY(" + COL_BOOKING_SCHEDULE_ID + ") REFERENCES " + TABLE_BUS_SCHEDULES + "(" + COL_SCHEDULE_ID + ")"
                            + ")";
                    db.execSQL(createBookingsTable);
                } catch (Exception e2) {
                    android.util.Log.e("DatabaseHelper", "Error recreating bookings table: " + e2.getMessage());
                }
            }
        }
        
        if (oldVersion < 11) {
            // Create wallet tables
            android.util.Log.d("DatabaseHelper", "Upgrading to version 11: Creating wallet tables");
            try {
                String createWalletTable = "CREATE TABLE IF NOT EXISTS " + TABLE_WALLET + "("
                        + COL_WALLET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COL_WALLET_USER_EMAIL + " TEXT UNIQUE NOT NULL,"
                        + COL_WALLET_BALANCE + " REAL DEFAULT 0.0,"
                        + COL_WALLET_UPDATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP"
                        + ")";
                db.execSQL(createWalletTable);

                String createWalletTransactionsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_WALLET_TRANSACTIONS + "("
                        + COL_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COL_TRANSACTION_USER_EMAIL + " TEXT NOT NULL,"
                        + COL_TRANSACTION_TYPE + " TEXT NOT NULL,"
                        + COL_TRANSACTION_AMOUNT + " REAL NOT NULL,"
                        + COL_TRANSACTION_DESCRIPTION + " TEXT,"
                        + COL_TRANSACTION_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP"
                        + ")";
                db.execSQL(createWalletTransactionsTable);
            } catch (Exception e) {
                android.util.Log.e("DatabaseHelper", "Error creating wallet tables: " + e.getMessage());
            }
        }
        
        if (oldVersion < 12) {
            // Create notifications table
            android.util.Log.d("DatabaseHelper", "Upgrading to version 12: Creating notifications table");
            try {
                String createNotificationsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + "("
                        + COL_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COL_NOTIFICATION_USER_EMAIL + " TEXT NOT NULL,"
                        + COL_NOTIFICATION_TITLE + " TEXT NOT NULL,"
                        + COL_NOTIFICATION_MESSAGE + " TEXT NOT NULL,"
                        + COL_NOTIFICATION_TYPE + " TEXT DEFAULT 'system',"
                        + COL_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0,"
                        + COL_NOTIFICATION_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP,"
                        + COL_NOTIFICATION_BOOKING_ID + " INTEGER"
                        + ")";
                db.execSQL(createNotificationsTable);
            } catch (Exception e) {
                android.util.Log.e("DatabaseHelper", "Error creating notifications table: " + e.getMessage());
            }
        }
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Insert Ho Chi Minh City districts
        String[] hcmcDistricts = {
                "Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5", "Quận 6",
                "Quận 7", "Quận 8", "Quận 9", "Quận 10", "Quận 11", "Quận 12",
                "Bình Thạnh", "Tân Bình", "Tân Phú", "Phú Nhuận", "Gò Vấp", "Bình Tân", "Thủ Đức"
        };

        ContentValues locationValues = new ContentValues();
        for (String district : hcmcDistricts) {
            locationValues.clear();
            locationValues.put(COL_LOCATION_NAME, district);
            locationValues.put(COL_LOCATION_STATE, "TP. Hồ Chí Minh");
            locationValues.put(COL_LOCATION_TYPE, "district");
            db.insert(TABLE_LOCATIONS, null, locationValues);
        }

        // Insert bus companies
        String[] companies = {
                "EASYBUS", "Hoàng Long", "Mai Linh", "Thành Bưởi",
                "Xe Khách Miền Tây", "Xe Khách Miền Đông", "Xe Khách Sài Gòn", "Phương Trang"
        };

        ContentValues companyValues = new ContentValues();
        for (String company : companies) {
            companyValues.clear();
            companyValues.put(COL_COMPANY_NAME, company);
            companyValues.put(COL_COMPANY_RATING, 4.0 + Math.random() * 1.0);
            db.insert(TABLE_BUS_COMPANIES, null, companyValues);
        }

        // Insert sample routes
        insertSampleRoutes(db);
        
        // Note: Sample schedules will be created on-demand when user searches for trips
        // This avoids blocking database creation and potential crashes
        // Schedules are also auto-created in getSuggestedJourneys() if none exist
    }
    
    // Initialize sample data after database is created (call this from app startup)
    public void initializeSampleData() {
        SQLiteDatabase db = null;
        Cursor routeCheck = null;
        try {
            android.util.Log.d("DatabaseHelper", "Initializing sample data...");
            
            // Ensure we have routes
            db = this.getReadableDatabase();
            if (db == null || !db.isOpen()) {
                android.util.Log.e("DatabaseHelper", "Cannot get readable database in initializeSampleData");
                return;
            }
            
            routeCheck = db.query(TABLE_BUS_ROUTES, new String[]{COL_ROUTE_ID}, null, null, null, null, null, "1");
            boolean hasRoutes = routeCheck != null && routeCheck.moveToFirst();
            if (routeCheck != null) {
                routeCheck.close();
                routeCheck = null;
            }
            
            // Don't close db here - we'll use it or let insertSampleSchedulesForDate manage its own connections
            
            if (!hasRoutes) {
                android.util.Log.w("DatabaseHelper", "No routes found, creating sample routes...");
                // Close read database before opening write database
                if (db != null && db.isOpen()) {
                    // Don't close - let SQLiteOpenHelper manage it
                }
                SQLiteDatabase writeDb = this.getWritableDatabase();
                if (writeDb != null && writeDb.isOpen()) {
                    insertSampleRoutes(writeDb);
                    // Don't close writeDb - let SQLiteOpenHelper manage it
                }
            }
            
            // Create sample schedules for next 7 days
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Calendar cal = java.util.Calendar.getInstance();
            
            // Create schedules for today and next 7 days
            for (int i = 0; i < 7; i++) {
                cal.setTime(new java.util.Date());
                cal.add(java.util.Calendar.DAY_OF_MONTH, i);
                String date = sdf.format(cal.getTime());
                try {
                    insertSampleSchedulesForDate(date);
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error creating schedules for date " + date + ": " + e.getMessage(), e);
                    // Continue with next date
                }
            }
            
            android.util.Log.d("DatabaseHelper", "Sample data initialization complete");
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error initializing sample data: " + e.getMessage(), e);
        } finally {
            // Clean up cursors
            if (routeCheck != null && !routeCheck.isClosed()) {
                try {
                    routeCheck.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing routeCheck cursor: " + e.getMessage(), e);
                }
            }
            // Don't close db here - let SQLiteOpenHelper manage database connections
            // Closing it manually can cause connection pool issues
        }
    }
    
    // Call this method to ensure sample schedules exist
    public void ensureSampleSchedules() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                return;
            }
            cursor = db.query(TABLE_BUS_SCHEDULES, new String[]{COL_SCHEDULE_ID}, null, null, null, null, null, "1");
            boolean hasSchedules = cursor != null && cursor.moveToFirst();
            
            if (!hasSchedules) {
                insertSampleSchedules();
            }
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in ensureSampleSchedules: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                }
            }
            // Don't close db here - let SQLiteOpenHelper manage connection pool
        }
    }
    
    // Ensure sample schedules exist for a specific date
    public void ensureSampleSchedulesForDate(String date) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                return;
            }
            cursor = db.query(TABLE_BUS_SCHEDULES, 
                    new String[]{COL_SCHEDULE_ID}, 
                    COL_SCHEDULE_DATE + " = ?", 
                    new String[]{date}, 
                    null, null, null, "1");
            boolean hasSchedulesForDate = cursor != null && cursor.moveToFirst();
            
            if (!hasSchedulesForDate) {
                insertSampleSchedulesForDate(date);
            }
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in ensureSampleSchedulesForDate: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                }
            }
            // Don't close db here - let SQLiteOpenHelper manage connection pool
        }
    }

    private void insertSampleRoutes(SQLiteDatabase db) {
        // Get location IDs for major districts in Ho Chi Minh City
        long q1Id = getLocationId(db, "Quận 1");
        long q2Id = getLocationId(db, "Quận 2");
        long q3Id = getLocationId(db, "Quận 3");
        long q4Id = getLocationId(db, "Quận 4");
        long q5Id = getLocationId(db, "Quận 5");
        long q6Id = getLocationId(db, "Quận 6");
        long q7Id = getLocationId(db, "Quận 7");
        long q8Id = getLocationId(db, "Quận 8");
        long q9Id = getLocationId(db, "Quận 9");
        long q10Id = getLocationId(db, "Quận 10");
        long q11Id = getLocationId(db, "Quận 11");
        long q12Id = getLocationId(db, "Quận 12");
        long binhThanhId = getLocationId(db, "Bình Thạnh");
        long tanBinhId = getLocationId(db, "Tân Bình");
        long tanPhuId = getLocationId(db, "Tân Phú");
        long goVapId = getLocationId(db, "Gò Vấp");
        long thuDucId = getLocationId(db, "Thủ Đức");

        // Insert routes between districts with route numbers
        // Each route from A to B can have multiple route numbers (e.g., route 5, 12, 16)
        // For simplicity, we'll assign sequential route numbers
        int routeNumber = 1;
        insertRouteWithNumber(db, q1Id, q3Id, 3.5, 15, routeNumber++); // Tuyến 1: Quận 1 to Quận 3
        insertRouteWithNumber(db, q1Id, q3Id, 3.5, 15, routeNumber++); // Tuyến 2: Quận 1 to Quận 3 (alternative)
        insertRouteWithNumber(db, q1Id, q4Id, 2.0, 10, routeNumber++); // Tuyến 3: Quận 1 to Quận 4
        insertRouteWithNumber(db, q1Id, q5Id, 4.0, 20, routeNumber++); // Tuyến 4: Quận 1 to Quận 5
        insertRouteWithNumber(db, q1Id, q5Id, 4.0, 20, routeNumber++); // Tuyến 5: Quận 1 to Quận 5 (alternative)
        insertRouteWithNumber(db, q1Id, binhThanhId, 5.0, 25, routeNumber++); // Tuyến 6: Quận 1 to Bình Thạnh
        insertRouteWithNumber(db, q1Id, q2Id, 8.0, 35, routeNumber++); // Tuyến 7: Quận 1 to Quận 2
        insertRouteWithNumber(db, q1Id, q2Id, 8.0, 35, routeNumber++); // Tuyến 8: Quận 1 to Quận 2 (alternative)
        insertRouteWithNumber(db, q1Id, q7Id, 12.0, 45, routeNumber++); // Tuyến 9: Quận 1 to Quận 7
        insertRouteWithNumber(db, q3Id, tanBinhId, 6.0, 25, routeNumber++); // Tuyến 10: Quận 3 to Tân Bình
        insertRouteWithNumber(db, q3Id, q10Id, 4.5, 20, routeNumber++); // Tuyến 11: Quận 3 to Quận 10
        insertRouteWithNumber(db, q3Id, q10Id, 4.5, 20, routeNumber++); // Tuyến 12: Quận 3 to Quận 10 (alternative)
        insertRouteWithNumber(db, q5Id, q6Id, 3.0, 15, routeNumber++); // Tuyến 13: Quận 5 to Quận 6
        insertRouteWithNumber(db, q5Id, q8Id, 5.0, 20, routeNumber++); // Tuyến 14: Quận 5 to Quận 8
        insertRouteWithNumber(db, q5Id, q8Id, 5.0, 20, routeNumber++); // Tuyến 15: Quận 5 to Quận 8 (alternative)
        insertRouteWithNumber(db, q5Id, q8Id, 5.0, 20, routeNumber++); // Tuyến 16: Quận 5 to Quận 8 (alternative)
        insertRouteWithNumber(db, tanBinhId, tanPhuId, 7.0, 30, routeNumber++); // Tuyến 17: Tân Bình to Tân Phú
        insertRouteWithNumber(db, tanBinhId, goVapId, 5.5, 25, routeNumber++); // Tuyến 18: Tân Bình to Gò Vấp
        insertRouteWithNumber(db, binhThanhId, thuDucId, 10.0, 40, routeNumber++); // Tuyến 19: Bình Thạnh to Thủ Đức
        insertRouteWithNumber(db, q2Id, q9Id, 8.0, 35, routeNumber++); // Tuyến 20: Quận 2 to Quận 9
        insertRouteWithNumber(db, q7Id, q8Id, 6.0, 25, routeNumber++); // Tuyến 21: Quận 7 to Quận 8
        insertRouteWithNumber(db, q12Id, goVapId, 8.0, 35, routeNumber++); // Tuyến 22: Quận 12 to Gò Vấp
        insertRouteWithNumber(db, q10Id, q11Id, 3.5, 15, routeNumber++); // Tuyến 23: Quận 10 to Quận 11
    }

    private void insertRoute(SQLiteDatabase db, long fromId, long toId, double distance, int duration) {
        ContentValues values = new ContentValues();
        values.put(COL_ROUTE_FROM, fromId);
        values.put(COL_ROUTE_TO, toId);
        values.put(COL_ROUTE_DISTANCE, distance);
        values.put(COL_ROUTE_DURATION, duration);
        // Route number will be set to route ID after insertion
        long routeId = db.insert(TABLE_BUS_ROUTES, null, values);
        // Update route number to be the same as route ID
        ContentValues updateValues = new ContentValues();
        updateValues.put(COL_ROUTE_NUMBER, (int)routeId);
        db.update(TABLE_BUS_ROUTES, updateValues, COL_ROUTE_ID + " = ?", new String[]{String.valueOf(routeId)});
    }
    
    private void insertRouteWithNumber(SQLiteDatabase db, long fromId, long toId, double distance, int duration, int routeNumber) {
        ContentValues values = new ContentValues();
        values.put(COL_ROUTE_FROM, fromId);
        values.put(COL_ROUTE_TO, toId);
        values.put(COL_ROUTE_DISTANCE, distance);
        values.put(COL_ROUTE_DURATION, duration);
        values.put(COL_ROUTE_NUMBER, routeNumber);
        db.insert(TABLE_BUS_ROUTES, null, values);
    }

    // Get all locations (only district names, without state for cleaner UI)
    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                return locations;
            }
            
            cursor = db.query(TABLE_LOCATIONS,
                    new String[]{COL_LOCATION_NAME},
                    null, null, null, null,
                    COL_LOCATION_NAME + " ASC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(0);
                        if (name != null && !name.isEmpty()) {
                            locations.add(name);
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error getting locations: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                }
            }
            // Don't close db here - let SQLiteOpenHelper manage connection pool
            // Closing it manually can cause connection pool issues when other threads need database
        }
        return locations;
    }

    // Get locations by name (for search) - only district names
    public List<String> searchLocations(String query) {
        List<String> locations = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            if (query == null || query.isEmpty()) {
                return getAllLocations();
            }
            
            db = this.getReadableDatabase();
            if (db == null) {
                return locations;
            }
            
            cursor = db.query(TABLE_LOCATIONS,
                    new String[]{COL_LOCATION_NAME},
                    COL_LOCATION_NAME + " LIKE ?",
                    new String[]{"%" + query + "%"},
                    null, null,
                    COL_LOCATION_NAME + " ASC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(0);
                        if (name != null && !name.isEmpty()) {
                            locations.add(name);
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error searching locations: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                }
            }
            // Don't close db here - let SQLiteOpenHelper manage connection pool
        }
        return locations;
    }

    // Get location ID by name (private helper method)
    private long getLocationId(SQLiteDatabase db, String cityName) {
        Cursor cursor = db.query(TABLE_LOCATIONS,
                new String[]{COL_LOCATION_ID},
                COL_LOCATION_NAME + " LIKE ?",
                new String[]{cityName + "%"},
                null, null, null);
        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }
    
    // Public method to get location ID
    public long getLocationIdByName(String cityName) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                return -1;
            }
            return getLocationId(db, cityName);
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in getLocationIdByName: " + e.getMessage(), e);
            return -1;
        } finally {
            // Don't close db here - let SQLiteOpenHelper manage connection pool
        }
    }

    // Get route ID by from and to locations
    public long getRouteId(String fromLocation, String toLocation) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                return -1;
            }
            
            // Get location IDs
            String fromCity = fromLocation.split(",")[0].trim();
            String toCity = toLocation.split(",")[0].trim();
            
            long fromId = getLocationId(db, fromCity);
            long toId = getLocationId(db, toCity);
            
            if (fromId == -1 || toId == -1) {
                return -1;
            }

            cursor = db.query(TABLE_BUS_ROUTES,
                    new String[]{COL_ROUTE_ID},
                    COL_ROUTE_FROM + " = ? AND " + COL_ROUTE_TO + " = ?",
                    new String[]{String.valueOf(fromId), String.valueOf(toId)},
                    null, null, null);

            long routeId = -1;
            if (cursor != null && cursor.moveToFirst()) {
                routeId = cursor.getLong(0);
            }
            return routeId;
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in getRouteId: " + e.getMessage(), e);
            return -1;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                }
            }
            // Don't close db here - let SQLiteOpenHelper manage connection pool
        }
    }
    
    // Create route if it doesn't exist
    public long createRouteIfNotExists(String fromLocation, String toLocation) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Cursor maxRouteCursor = null;
        Cursor existingRoutesCursor = null;
        try {
            db = this.getWritableDatabase();
            if (db == null) {
                return -1;
            }
            
            // Get location IDs
            String fromCity = fromLocation.split(",")[0].trim();
            String toCity = toLocation.split(",")[0].trim();
            
            long fromId = getLocationId(db, fromCity);
            long toId = getLocationId(db, toCity);
            
            if (fromId == -1 || toId == -1) {
                return -1;
            }
            
            // Check if routes already exist for this pair
            cursor = db.query(TABLE_BUS_ROUTES,
                    new String[]{COL_ROUTE_ID},
                    COL_ROUTE_FROM + " = ? AND " + COL_ROUTE_TO + " = ?",
                    new String[]{String.valueOf(fromId), String.valueOf(toId)},
                    null, null, null);
            
            if (cursor != null && cursor.getCount() > 0) {
                // Routes already exist, return the first one
                cursor.moveToFirst();
                long routeId = cursor.getLong(0);
                return routeId;
            }
        
            
            // Estimate distance and duration (simple calculation for HCMC districts)
            double estimatedDistance = 5.0; // Default 5km for HCMC districts
            int estimatedDuration = 20; // Default 20 minutes
            
            // Get next route number (max route number + 1)
            maxRouteCursor = db.rawQuery("SELECT MAX(" + COL_ROUTE_NUMBER + ") FROM " + TABLE_BUS_ROUTES, null);
            int nextRouteNumber = 1;
            if (maxRouteCursor != null && maxRouteCursor.moveToFirst() && !maxRouteCursor.isNull(0)) {
                nextRouteNumber = maxRouteCursor.getInt(0) + 1;
            }
            
            // Get all existing route numbers to avoid duplicates
            java.util.Set<Integer> existingRouteNumbers = new java.util.HashSet<>();
            existingRoutesCursor = db.query(TABLE_BUS_ROUTES, 
                    new String[]{COL_ROUTE_NUMBER}, 
                    null, null, null, null, null);
            if (existingRoutesCursor != null) {
                while (existingRoutesCursor.moveToNext()) {
                    int routeNum = existingRoutesCursor.getInt(0);
                    existingRouteNumbers.add(routeNum);
                }
            }
            
            // Create 3-5 random routes for the same A-B pair with different route numbers
            // This simulates having multiple bus lines (tuyến) for the same route
            int numRoutes = 3 + (int)(Math.random() * 3); // 3 to 5 routes
            long firstRouteId = -1;
            int currentRouteNumber = nextRouteNumber;
            
            for (int i = 0; i < numRoutes; i++) {
                // Find next available route number (skip if already exists)
                while (existingRouteNumbers.contains(currentRouteNumber)) {
                    currentRouteNumber++;
                }
                
                ContentValues values = new ContentValues();
                values.put(COL_ROUTE_FROM, fromId);
                values.put(COL_ROUTE_TO, toId);
                values.put(COL_ROUTE_DISTANCE, estimatedDistance + (Math.random() * 2)); // Slight variation
                values.put(COL_ROUTE_DURATION, estimatedDuration + (int)(Math.random() * 10)); // Slight variation
                values.put(COL_ROUTE_NUMBER, currentRouteNumber);
                
                long routeId = db.insert(TABLE_BUS_ROUTES, null, values);
                existingRouteNumbers.add(currentRouteNumber); // Add to set to avoid duplicates in same batch
                currentRouteNumber++; // Move to next number
                
                if (i == 0) {
                    firstRouteId = routeId;
                }
                android.util.Log.d("DatabaseHelper", "Created route " + (i + 1) + "/" + numRoutes + " from " + fromCity + " to " + toCity + " with ID: " + routeId + ", Route Number: " + (currentRouteNumber - 1));
            }
            
            return firstRouteId;
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in createRouteIfNotExists: " + e.getMessage(), e);
            return -1;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                }
            }
            if (maxRouteCursor != null && !maxRouteCursor.isClosed()) {
                try {
                    maxRouteCursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing maxRouteCursor: " + e.getMessage(), e);
                }
            }
            if (existingRoutesCursor != null && !existingRoutesCursor.isClosed()) {
                try {
                    existingRoutesCursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing existingRoutesCursor: " + e.getMessage(), e);
                }
            }
            // Don't close db here - let SQLiteOpenHelper manage connection pool
        }
    }
    
    // Get all route IDs for a from-to pair
    public java.util.List<Long> getAllRouteIdsForPair(String fromLocation, String toLocation) {
        java.util.List<Long> routeIds = new java.util.ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                return routeIds;
            }
            
            String fromCity = fromLocation.split(",")[0].trim();
            String toCity = toLocation.split(",")[0].trim();
            
            long fromId = getLocationId(db, fromCity);
            long toId = getLocationId(db, toCity);
            
            if (fromId == -1 || toId == -1) {
                return routeIds;
            }
            
            cursor = db.query(TABLE_BUS_ROUTES,
                    new String[]{COL_ROUTE_ID},
                    COL_ROUTE_FROM + " = ? AND " + COL_ROUTE_TO + " = ?",
                    new String[]{String.valueOf(fromId), String.valueOf(toId)},
                    null, null, COL_ROUTE_NUMBER + " ASC");
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    routeIds.add(cursor.getLong(0));
                }
            }
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error getting route IDs: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                }
            }
            // Don't close db here - let SQLiteOpenHelper manage connection pool
        }
        
        return routeIds;
    }

    // Get schedules for a route - returns route_number instead of company_name
    public Cursor getSchedulesForRoute(long routeId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s." + COL_SCHEDULE_ID + ", r." + COL_ROUTE_NUMBER + ", " +
                "s." + COL_SCHEDULE_DEPARTURE_TIME + ", s." + COL_SCHEDULE_ARRIVAL_TIME + ", " +
                "s." + COL_SCHEDULE_PRICE + ", s." + COL_SCHEDULE_BUS_TYPE + ", " +
                "s." + COL_SCHEDULE_AVAILABLE_SEATS + ", s." + COL_SCHEDULE_TOTAL_SEATS +
                " FROM " + TABLE_BUS_SCHEDULES + " s " +
                "INNER JOIN " + TABLE_BUS_ROUTES + " r ON s." + COL_SCHEDULE_ROUTE_ID + " = r." + COL_ROUTE_ID +
                " WHERE s." + COL_SCHEDULE_ROUTE_ID + " = ? AND s." + COL_SCHEDULE_DATE + " = ? " +
                "ORDER BY r." + COL_ROUTE_NUMBER + " ASC, s." + COL_SCHEDULE_DEPARTURE_TIME + " ASC";
        return db.rawQuery(query, new String[]{String.valueOf(routeId), date});
    }
    
    // Get all schedules for routes from A to B (returns all routes with different route numbers)
    public Cursor getSchedulesForRouteByLocations(String fromLocation, String toLocation, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        try {
            // Get location IDs
            String fromCity = fromLocation.split(",")[0].trim();
            String toCity = toLocation.split(",")[0].trim();
            
            long fromId = getLocationId(db, fromCity);
            long toId = getLocationId(db, toCity);
            
            android.util.Log.d("DatabaseHelper", "getSchedulesForRouteByLocations: fromCity=" + fromCity + " (id=" + fromId + "), toCity=" + toCity + " (id=" + toId + "), date=" + date);
            
            if (fromId == -1 || toId == -1) {
                android.util.Log.e("DatabaseHelper", "Location not found: fromId=" + fromId + ", toId=" + toId);
                // Don't close db here, return null cursor
                return null;
            }
            
            String query = "SELECT s." + COL_SCHEDULE_ID + ", r." + COL_ROUTE_NUMBER + ", " +
                    "s." + COL_SCHEDULE_DEPARTURE_TIME + ", s." + COL_SCHEDULE_ARRIVAL_TIME + ", " +
                    "s." + COL_SCHEDULE_PRICE + ", s." + COL_SCHEDULE_BUS_TYPE + ", " +
                    "s." + COL_SCHEDULE_AVAILABLE_SEATS + ", s." + COL_SCHEDULE_TOTAL_SEATS + ", " +
                    "s." + COL_SCHEDULE_ROUTE_ID +
                    " FROM " + TABLE_BUS_SCHEDULES + " s " +
                    "INNER JOIN " + TABLE_BUS_ROUTES + " r ON s." + COL_SCHEDULE_ROUTE_ID + " = r." + COL_ROUTE_ID +
                    " WHERE r." + COL_ROUTE_FROM + " = ? AND r." + COL_ROUTE_TO + " = ? AND s." + COL_SCHEDULE_DATE + " = ? " +
                    "ORDER BY r." + COL_ROUTE_NUMBER + " ASC, s." + COL_SCHEDULE_DEPARTURE_TIME + " ASC";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(fromId), String.valueOf(toId), date});
            // Note: Don't close db here, cursor needs the database to be open
            return cursor;
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in getSchedulesForRouteByLocations: " + e.getMessage(), e);
            return null;
        }
    }

    // Insert sample schedules for routes
    public void insertSampleSchedules() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        
        // Create schedules for today and next 7 days (only future schedules)
        for (int i = 0; i < 7; i++) {
            cal.setTime(new java.util.Date());
            cal.add(java.util.Calendar.DAY_OF_MONTH, i);
            String date = sdf.format(cal.getTime());
            insertSampleSchedulesForDate(date);
        }
    }
    
    // Insert sample schedules for a specific date
    public void insertSampleSchedulesForDate(String date) {
        insertSampleSchedulesForDate(date, -1); // -1 means all routes
    }
    
    // Insert sample schedules for a specific date and route (or all routes if routeId is -1)
    public void insertSampleSchedulesForDate(String date, long specificRouteId) {
        SQLiteDatabase db = null;
        Cursor routeCursor = null;
        Cursor companyCursor = null;
        try {
            // Try to get writable database, but check if it's still available
            try {
                db = this.getWritableDatabase();
            } catch (Exception dbException) {
                android.util.Log.e("DatabaseHelper", "Error getting writable database: " + dbException.getMessage(), dbException);
                // If database helper was closed, try to create a new instance
                if (dbException.getMessage() != null && dbException.getMessage().contains("closed")) {
                    android.util.Log.w("DatabaseHelper", "Database helper was closed, skipping insertSampleSchedulesForDate");
                    return;
                }
                throw dbException;
            }
            
            if (db == null || !db.isOpen()) {
                android.util.Log.e("DatabaseHelper", "Cannot get writable database in insertSampleSchedulesForDate");
                return;
            }
            
            // Get route IDs - either specific route or all routes
            if (specificRouteId > 0) {
                routeCursor = db.query(TABLE_BUS_ROUTES, new String[]{COL_ROUTE_ID}, 
                        COL_ROUTE_ID + " = ?", new String[]{String.valueOf(specificRouteId)}, 
                        null, null, null);
            } else {
                routeCursor = db.query(TABLE_BUS_ROUTES, new String[]{COL_ROUTE_ID}, null, null, null, null, null);
            }
            companyCursor = db.query(TABLE_BUS_COMPANIES, new String[]{COL_COMPANY_ID}, null, null, null, null, null);
            
            // Check if we have routes and companies
            if (routeCursor == null || companyCursor == null) {
                android.util.Log.e("DatabaseHelper", "Cannot get routes or companies cursor");
                return;
            }
            
            if (routeCursor.getCount() == 0) {
                android.util.Log.w("DatabaseHelper", "No routes found in database. Cannot create schedules.");
                return;
            }
            
            if (companyCursor.getCount() == 0) {
                android.util.Log.w("DatabaseHelper", "No companies found in database. Cannot create schedules.");
                return;
            }
            
            if (routeCursor.moveToFirst() && companyCursor.moveToFirst()) {
            int companyIndex = 0;
            do {
                long routeId = routeCursor.getLong(0);
                
                // Get company ID (cycle through companies)
                if (!companyCursor.moveToPosition(companyIndex % companyCursor.getCount())) {
                    companyCursor.moveToFirst();
                }
                long companyId = companyCursor.getLong(0);
                
                // Insert multiple schedules per route with different times and companies
                // Create 8-10 schedules with different departure times throughout the day
                String[] busTypes = {
                    "A/C Sleeper (2+2)", 
                    "A/C Sleeper (2+2)", 
                    "Non A/C Sleeper (2+1)", 
                    "A/C Semi-Sleeper (2+2)",
                    "Luxury A/C Sleeper (2+1)",
                    "Standard A/C (2+2)",
                    "Premium Sleeper (2+1)",
                    "Economy Non A/C (2+2)"
                };
                double[] basePrices = {50.0, 65.0, 45.0, 55.0, 80.0, 40.0, 90.0, 35.0};
                
                // Create 2-3 schedules per route to have variety across multiple routes
                // Different routes will have different times
                int numSchedules = 2 + (int)(Math.random() * 2); // 2 to 3 schedules per route
                
                // Get current date and time to ensure schedules are in the future
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                java.util.Calendar now = java.util.Calendar.getInstance();
                String currentDate = dateFormat.format(now.getTime());
                String currentTime = timeFormat.format(now.getTime());
                String[] currentTimeParts = currentTime.split(":");
                int currentHour = Integer.parseInt(currentTimeParts[0]);
                int currentMinute = Integer.parseInt(currentTimeParts[1]);
                
                // Generate random departure times for this route
                // If date is today, ensure times are within next 1 hour (from current time to 1 hour later)
                int baseHour;
                int baseMinute;
                if (date.equals(currentDate)) {
                    // For today, create schedules from current time onwards (at least 30 minutes later)
                    // Add random minutes (30-180 minutes) to current time for variety
                    int additionalMinutes = 30 + (int)(Math.random() * 150); // 30 minutes to 3 hours later
                    baseHour = currentHour;
                    baseMinute = currentMinute + additionalMinutes;
                    while (baseMinute >= 60) {
                        baseMinute -= 60;
                        baseHour++;
                        if (baseHour >= 24) {
                            // If too late today, skip creating schedules for today
                            continue; // Skip this route for today
                        }
                    }
                } else {
                    // For future dates, use random times between 6 AM and 10 PM for more variety
                    baseHour = 6 + (int)(Math.random() * 16); // Random hour between 6 AM and 10 PM
                    baseMinute = (int)(Math.random() * 4) * 15; // 0, 15, 30, or 45
                }
                
                for (int i = 0; i < numSchedules; i++) {
                    ContentValues values = new ContentValues();
                    values.put(COL_SCHEDULE_ROUTE_ID, routeId);
                    
                    // Cycle through companies for variety - ensure each schedule has different company
                    int currentCompanyIndex = (companyIndex + i) % companyCursor.getCount();
                    if (!companyCursor.moveToPosition(currentCompanyIndex)) {
                        companyCursor.moveToFirst();
                    }
                    long currentCompanyId = companyCursor.getLong(0);
                    values.put(COL_SCHEDULE_COMPANY_ID, currentCompanyId);
                    
                    // Generate different departure times for this schedule
                    // Spread schedules throughout the day for this route
                    int hour = baseHour + (i * 2); // Add 2 hours between each schedule
                    if (hour >= 24) {
                        hour = hour % 24;
                    }
                    int minute = baseMinute + (i * 15); // Add 15 minutes variation
                    if (minute >= 60) {
                        minute = minute % 60;
                        hour = (hour + 1) % 24;
                    }
                    
                    // If date is today, ensure time is after current time (at least 30 minutes later)
                    if (date.equals(currentDate)) {
                        // Check if time is before current time
                        if (hour < currentHour || (hour == currentHour && minute < currentMinute)) {
                            // Skip if before current time
                            continue;
                        }
                        // Ensure at least 30 minutes difference from current time
                        int minHour = currentHour;
                        int minMinute = currentMinute + 30;
                        if (minMinute >= 60) {
                            minMinute -= 60;
                            minHour++;
                        }
                        if (hour < minHour || (hour == minHour && minute < minMinute)) {
                            // Adjust to at least 30 minutes later
                            hour = minHour;
                            minute = minMinute;
                            if (hour >= 24) {
                                continue; // Skip if too late
                            }
                        }
                    }
                    
                    // Calculate arrival time (journey duration varies by route)
                    int journeyDuration = 20 + (int)(Math.random() * 40); // 20-60 minutes
                    int arrivalHour = hour;
                    int arrivalMinute = minute + journeyDuration;
                    if (arrivalMinute >= 60) {
                        arrivalMinute = arrivalMinute % 60;
                        arrivalHour = (arrivalHour + 1) % 24;
                    }
                    
                    String departureTime = String.format("%02d:%02d", hour, minute);
                    String arrivalTime = String.format("%02d:%02d", arrivalHour, arrivalMinute);
                    
                    values.put(COL_SCHEDULE_DEPARTURE_TIME, departureTime);
                    values.put(COL_SCHEDULE_ARRIVAL_TIME, arrivalTime);
                    
                    // Vary prices based on time and bus type
                    double price = basePrices[i % basePrices.length] + (Math.random() * 25);
                    values.put(COL_SCHEDULE_PRICE, Math.round(price));
                    
                    values.put(COL_SCHEDULE_BUS_TYPE, busTypes[i % busTypes.length]);
                    values.put(COL_SCHEDULE_TOTAL_SEATS, 28);
                    
                    // Make it more likely to have few seats left for testing
                    int bookedCount;
                    double rand = Math.random();
                    if (rand < 0.3) {
                        bookedCount = 23 + (int)(Math.random() * 5); // 23-27 booked (1-5 left)
                    } else {
                        bookedCount = (int)(Math.random() * 20); // 0-19 booked (9-28 left)
                    }
                    
                    int availSeats = 28 - bookedCount;
                    values.put(COL_SCHEDULE_AVAILABLE_SEATS, availSeats);
                    values.put(COL_SCHEDULE_DATE, date);
                    
                    long sId = db.insert(TABLE_BUS_SCHEDULES, null, values);
                    
                    // Insert sample bookings to match available seats
                    if (sId != -1 && bookedCount > 0) {
                        double schedulePrice = Math.round(price); // Use the price we just calculated
                        insertSampleBookingsForSchedule(db, sId, bookedCount, schedulePrice);
                    }
                }
                
                companyIndex++;
            } while (routeCursor.moveToNext());
            }
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in insertSampleSchedulesForDate: " + e.getMessage(), e);
        } finally {
            if (routeCursor != null && !routeCursor.isClosed()) {
                routeCursor.close();
            }
            if (companyCursor != null && !companyCursor.isClosed()) {
                companyCursor.close();
            }
            // Don't close db here as it might be used by caller
        }
    }

    // Helper to insert sample bookings when generating schedules
    private void insertSampleBookingsForSchedule(SQLiteDatabase db, long scheduleId, int count, double pricePerSeat) {
        // Check if database is still open and valid
        if (db == null || !db.isOpen()) {
            android.util.Log.e("DatabaseHelper", "Database is null or closed in insertSampleBookingsForSchedule");
            return;
        }
        
        // Use the provided price per seat (no need to query database)
        if (pricePerSeat <= 0) {
            android.util.Log.w("DatabaseHelper", "Schedule price is 0 for scheduleId: " + scheduleId + ". Using default price 100000.");
            pricePerSeat = 100000; // Default price if invalid
        }
        
        String[] seatPrefixes = {"A", "B", "C"};
        String[] passengerNames = {"Khách hàng mẫu", "Người dùng A", "Người dùng B"};
        String[] genders = {"Male", "Female"};
        java.util.Random random = new java.util.Random();
        
        // Generate random unique seat numbers
        java.util.Set<String> chosenSeats = new java.util.HashSet<>();
        while (chosenSeats.size() < count) {
            String prefix = seatPrefixes[random.nextInt(seatPrefixes.length)];
            int num = 1 + random.nextInt(14);
            chosenSeats.add(prefix + num);
        }
        
        // Group seats into a few bookings
        java.util.List<String> seatList = new java.util.ArrayList<>(chosenSeats);
        int index = 0;
        while (index < seatList.size()) {
            // Check database again before each insert
            if (db == null || !db.isOpen()) {
                android.util.Log.w("DatabaseHelper", "Database was closed during booking insertion, stopping at index: " + index);
                return;
            }
            
            int groupSize = 1 + random.nextInt(3); // 1-3 seats per booking
            if (index + groupSize > seatList.size()) groupSize = seatList.size() - index;
            
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < groupSize; i++) {
                if (i > 0) sb.append(", ");
                sb.append(seatList.get(index + i));
            }
            
            ContentValues v = new ContentValues();
            v.put(COL_BOOKING_SCHEDULE_ID, scheduleId);
            v.put(COL_BOOKING_USER_EMAIL, "sample@example.com");
            v.put(COL_BOOKING_PASSENGER_NAME, passengerNames[random.nextInt(passengerNames.length)]);
            v.put(COL_BOOKING_PASSENGER_AGE, 18 + random.nextInt(40)); // Age between 18 and 57
            v.put(COL_BOOKING_PASSENGER_GENDER, genders[random.nextInt(genders.length)]);
            v.put(COL_BOOKING_SEAT_NUMBERS, sb.toString());
            v.put(COL_BOOKING_BOARDING_POINT, "Điểm đón mẫu");
            v.put(COL_BOOKING_DROP_POINT, "Điểm trả mẫu");
            v.put(COL_BOOKING_TOTAL_FARE, pricePerSeat * groupSize); // Calculate total fare: price per seat * number of seats
            v.put(COL_BOOKING_STATUS, "confirmed");
            v.put(COL_BOOKING_BOOKING_DATE, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
            v.put(COL_BOOKING_CODE, "BOOK" + System.currentTimeMillis() + "-" + random.nextInt(1000));
            
            try {
                long newRowId = db.insert(TABLE_BOOKINGS, null, v);
                if (newRowId == -1) {
                    android.util.Log.e("DatabaseHelper", "Error inserting sample booking for scheduleId: " + scheduleId);
                }
            } catch (Exception insertException) {
                android.util.Log.e("DatabaseHelper", "Error inserting booking: " + insertException.getMessage(), insertException);
                // If database was closed, stop trying to insert more bookings
                if (insertException.getMessage() != null && 
                    (insertException.getMessage().contains("closed") || 
                     insertException.getMessage().contains("re-open"))) {
                    android.util.Log.w("DatabaseHelper", "Database was closed, stopping booking insertion");
                    return;
                }
            }
            
            index += groupSize;
        }
    }

    // Insert booking
    public long insertBooking(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return id;
    }

    // Insert booking with all parameters
    public long insertBooking(long scheduleId, String userEmail, String passengerName, 
                              String passengerAge, String passengerGender, String seatNumbers,
                              String boardingPoint, String dropPoint, double totalFare, 
                              String status, String bookingDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Generate unique booking code
        String bookingCode = BookingCodeHelper.generateBookingCode();
        // Ensure uniqueness (retry if duplicate)
        int retries = 0;
        while (retries < 10) {
            Cursor checkCursor = db.query(TABLE_BOOKINGS, new String[]{COL_BOOKING_ID}, 
                    COL_BOOKING_CODE + " = ?", new String[]{bookingCode}, null, null, null);
            if (checkCursor != null && checkCursor.getCount() == 0) {
                checkCursor.close();
                break;
            }
            if (checkCursor != null) checkCursor.close();
            bookingCode = BookingCodeHelper.generateBookingCode();
            retries++;
        }
        
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_SCHEDULE_ID, scheduleId);
        values.put(COL_BOOKING_USER_EMAIL, userEmail);
        values.put(COL_BOOKING_PASSENGER_NAME, passengerName);
        values.put(COL_BOOKING_PASSENGER_AGE, passengerAge);
        values.put(COL_BOOKING_PASSENGER_GENDER, passengerGender);
        values.put(COL_BOOKING_SEAT_NUMBERS, seatNumbers);
        values.put(COL_BOOKING_BOARDING_POINT, boardingPoint);
        values.put(COL_BOOKING_DROP_POINT, dropPoint);
        values.put(COL_BOOKING_TOTAL_FARE, totalFare);
        values.put(COL_BOOKING_STATUS, status);
        values.put(COL_BOOKING_BOOKING_DATE, bookingDate);
        values.put(COL_BOOKING_CODE, bookingCode);
        
        long id = db.insert(TABLE_BOOKINGS, null, values);
        
        if (id != -1 && status.equals("confirmed")) {
            // Decrement available seats based on number of seats booked
            int numSeats = 1;
            if (seatNumbers != null && !seatNumbers.isEmpty()) {
                numSeats = seatNumbers.split(",").length;
            }
            
            String updateQuery = "UPDATE " + TABLE_BUS_SCHEDULES + 
                    " SET " + COL_SCHEDULE_AVAILABLE_SEATS + " = " + COL_SCHEDULE_AVAILABLE_SEATS + " - ?" +
                    " WHERE " + COL_SCHEDULE_ID + " = ?";
            db.execSQL(updateQuery, new Object[]{numSeats, scheduleId});
            android.util.Log.d("DatabaseHelper", "Updated available seats for schedule " + scheduleId + ", decreased by " + numSeats);
        }
        
        db.close();
        return id;
    }
    
    // Get schedule available seats
    public int getScheduleAvailableSeats(long scheduleId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int availableSeats = 0;
        int retries = 0;
        int maxRetries = 3;
        
        while (retries < maxRetries) {
            try {
                db = this.getReadableDatabase();
                if (db == null) {
                    return 0;
                }
                
                // Check if database is open
                if (!db.isOpen()) {
                    android.util.Log.w("DatabaseHelper", "Database is not open, retrying...");
                    retries++;
                    if (retries < maxRetries) {
                        try {
                            Thread.sleep(100); // Wait 100ms before retry
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return 0;
                        }
                        continue;
                    }
                    return 0;
                }
                
                cursor = db.query(TABLE_BUS_SCHEDULES, new String[]{COL_SCHEDULE_AVAILABLE_SEATS},
                        COL_SCHEDULE_ID + " = ?", new String[]{String.valueOf(scheduleId)},
                        null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    availableSeats = cursor.getInt(0);
                }
                break; // Success, exit retry loop
            } catch (android.database.sqlite.SQLiteDatabaseLockedException e) {
                android.util.Log.w("DatabaseHelper", "Database locked, retrying... (attempt " + (retries + 1) + "/" + maxRetries + ")");
                retries++;
                if (retries < maxRetries) {
                    try {
                        Thread.sleep(200); // Wait 200ms before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return 0;
                    }
                    // Close cursor if it was opened
                    if (cursor != null && !cursor.isClosed()) {
                        try {
                            cursor.close();
                        } catch (Exception ce) {
                            // Ignore
                        }
                    }
                    continue;
                } else {
                    android.util.Log.e("DatabaseHelper", "Database locked after " + maxRetries + " retries: " + e.getMessage(), e);
                    return 0;
                }
            } catch (Exception e) {
                android.util.Log.e("DatabaseHelper", "Error getting available seats: " + e.getMessage(), e);
                break; // Don't retry for other exceptions
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    try {
                        cursor.close();
                    } catch (Exception e) {
                        android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                    }
                }
                // Don't close db here - let SQLiteOpenHelper manage connection pool
            }
        }
        return availableSeats;
    }

    // Get bookings for user
    public Cursor getUserBookings(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b." + COL_BOOKING_ID + ", b." + COL_BOOKING_PASSENGER_NAME + ", " +
                "s." + COL_SCHEDULE_DEPARTURE_TIME + ", b." + COL_BOOKING_TOTAL_FARE + ", " +
                "b." + COL_BOOKING_STATUS +
                " FROM " + TABLE_BOOKINGS + " b " +
                "INNER JOIN " + TABLE_BUS_SCHEDULES + " s ON b." + COL_BOOKING_SCHEDULE_ID + " = s." + COL_SCHEDULE_ID +
                " WHERE b." + COL_BOOKING_USER_EMAIL + " = ? " +
                "ORDER BY b." + COL_BOOKING_BOOKING_DATE + " DESC";
        return db.rawQuery(query, new String[]{userEmail});
    }
    
    // Get detailed bookings for user with all journey information
    public Cursor getUserBookingsWithDetails(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT " +
                    "b." + COL_BOOKING_ID + " as booking_id, " +
                    "b." + COL_BOOKING_SCHEDULE_ID + " as schedule_id, " +
                    "b." + COL_BOOKING_STATUS + " as status, " +
                    "b." + COL_BOOKING_TOTAL_FARE + " as total_fare, " +
                    "s." + COL_SCHEDULE_DEPARTURE_TIME + " as departure_time, " +
                    "s." + COL_SCHEDULE_ARRIVAL_TIME + " as arrival_time, " +
                    "s." + COL_SCHEDULE_DATE + " as date, " +
                    "b." + COL_BOOKING_BOARDING_POINT + " as boarding_point, " +
                    "b." + COL_BOOKING_DROP_POINT + " as drop_point, " +
                    "b." + COL_BOOKING_SEAT_NUMBERS + " as seat_numbers, " +
                    "r." + COL_ROUTE_NUMBER + " as route_number, " +
                    "COALESCE(c." + COL_COMPANY_NAME + ", 'EASYBUS') as company_name, " +
                    "l1." + COL_LOCATION_NAME + " as from_location, " +
                    "l2." + COL_LOCATION_NAME + " as to_location " +
                    "FROM " + TABLE_BOOKINGS + " b " +
                    "INNER JOIN " + TABLE_BUS_SCHEDULES + " s ON b." + COL_BOOKING_SCHEDULE_ID + " = s." + COL_SCHEDULE_ID + " " +
                    "INNER JOIN " + TABLE_BUS_ROUTES + " r ON s." + COL_SCHEDULE_ROUTE_ID + " = r." + COL_ROUTE_ID + " " +
                    "INNER JOIN " + TABLE_LOCATIONS + " l1 ON r." + COL_ROUTE_FROM + " = l1." + COL_LOCATION_ID + " " +
                    "INNER JOIN " + TABLE_LOCATIONS + " l2 ON r." + COL_ROUTE_TO + " = l2." + COL_LOCATION_ID + " " +
                    "LEFT JOIN " + TABLE_BUS_COMPANIES + " c ON s." + COL_SCHEDULE_COMPANY_ID + " = c." + COL_COMPANY_ID + " " +
                    "WHERE b." + COL_BOOKING_USER_EMAIL + " = ? " +
                    "ORDER BY s." + COL_SCHEDULE_DATE + " ASC, s." + COL_SCHEDULE_DEPARTURE_TIME + " ASC";
            android.util.Log.d("DatabaseHelper", "Executing getUserBookingsWithDetails for: " + userEmail);
            Cursor cursor = db.rawQuery(query, new String[]{userEmail});
            android.util.Log.d("DatabaseHelper", "Query returned " + (cursor != null ? cursor.getCount() : 0) + " rows");
            return cursor;
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in getUserBookingsWithDetails: " + e.getMessage(), e);
            return null;
        }
    }
    
    // Get list of booked seats for a specific schedule
    public List<String> getBookedSeatsForSchedule(long scheduleId) {
        List<String> bookedSeats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String query = "SELECT " + COL_BOOKING_SEAT_NUMBERS + 
                    " FROM " + TABLE_BOOKINGS + 
                    " WHERE " + COL_BOOKING_SCHEDULE_ID + " = ?" +
                    " AND " + COL_BOOKING_STATUS + " != 'cancelled'";
            
            cursor = db.rawQuery(query, new String[]{String.valueOf(scheduleId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String seatNumbers = cursor.getString(0);
                    if (seatNumbers != null && !seatNumbers.isEmpty()) {
                        // Parse seat numbers (format: "1A, 1B, 2A" or "A1, B1, C1")
                        String[] seats = seatNumbers.split(",");
                        for (String seat : seats) {
                            String trimmedSeat = seat.trim();
                            if (!trimmedSeat.isEmpty() && !bookedSeats.contains(trimmedSeat)) {
                                bookedSeats.add(trimmedSeat);
                            }
                        }
                    }
                } while (cursor.moveToNext());
            }
            
            android.util.Log.d("DatabaseHelper", "Found " + bookedSeats.size() + " booked seats for schedule " + scheduleId);
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error getting booked seats: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        
        return bookedSeats;
    }
    
    // Get suggested journeys (upcoming journeys after current time)
    public Cursor getSuggestedJourneys(int limit) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            // Use writable database for both delete and query operations to avoid connection pool issues
            db = this.getWritableDatabase();
            if (db == null) {
                android.util.Log.e("DatabaseHelper", "Cannot get writable database in getSuggestedJourneys");
                return null;
            }
            
            // Check if database is open
            if (!db.isOpen()) {
                android.util.Log.e("DatabaseHelper", "Database is not open in getSuggestedJourneys");
                return null;
            }
            // Get current date and time
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            java.util.Calendar now = java.util.Calendar.getInstance();
            String currentDate = dateFormat.format(now.getTime());
            String currentTime = timeFormat.format(now.getTime());
            
            // First, delete old schedules (past schedules) to keep database clean
            // Delete schedules that are before current time
            // Use the same database object for delete operation
            try {
                if (db != null && db.isOpen()) {
                    String deleteQuery = "DELETE FROM " + TABLE_BUS_SCHEDULES + 
                            " WHERE (" + COL_SCHEDULE_DATE + " < ? OR " +
                            "(" + COL_SCHEDULE_DATE + " = ? AND " + COL_SCHEDULE_DEPARTURE_TIME + " < ?))";
                    db.execSQL(deleteQuery, new String[]{currentDate, currentDate, currentTime});
                    android.util.Log.d("DatabaseHelper", "Cleaned up old schedules (before " + currentDate + " " + currentTime + ")");
                }
            } catch (Exception e) {
                android.util.Log.e("DatabaseHelper", "Error deleting old schedules: " + e.getMessage(), e);
            }
            
            // Check if there are any upcoming schedules in the future (after current time)
            // This includes schedules from now to several days in the future for random suggestions
            String checkQuery = "SELECT COUNT(*) FROM " + TABLE_BUS_SCHEDULES + " s " +
                    "WHERE (s." + COL_SCHEDULE_DATE + " > ? OR " +
                    "(s." + COL_SCHEDULE_DATE + " = ? AND s." + COL_SCHEDULE_DEPARTURE_TIME + " >= ?))";
            android.database.Cursor checkCursor = db.rawQuery(checkQuery, new String[]{
                    currentDate, currentDate, currentTime
            });
            int count = 0;
            if (checkCursor.moveToFirst()) {
                count = checkCursor.getInt(0);
            }
            checkCursor.close();
            
            // If no upcoming schedules exist, create them (but don't block)
            if (count == 0) {
                android.util.Log.d("DatabaseHelper", "No upcoming schedules found, will create new ones");
                // Don't close db here - we'll need it later. Just create schedules in background.
                
                try {
                    // First, ensure we have routes in database
                    // Use the existing db object instead of opening a new one
                    Cursor routeCheck = db.query(TABLE_BUS_ROUTES, new String[]{COL_ROUTE_ID}, null, null, null, null, null, "1");
                    boolean hasRoutes = routeCheck != null && routeCheck.moveToFirst();
                    if (routeCheck != null) {
                        routeCheck.close();
                    }
                    
                    if (hasRoutes) {
                        // Check current time - if it's too late (after 22:00), start from tomorrow
                        // Also ensure schedules are at least 1 hour from now
                        try {
                            int currentHour = Integer.parseInt(currentTime.split(":")[0]);
                            int currentMinute = Integer.parseInt(currentTime.split(":")[1]);
                            int startDay = 0;
                            
                            // If current time + 1 hour is after 23:00, start from tomorrow
                            int futureHour = currentHour + 1;
                            if (futureHour >= 24) {
                                startDay = 1; // Start from tomorrow if too late
                            }
                            
                            // Create schedules for today (from now onwards) and next 7 days
                            // This ensures we have plenty of random suggestions with various routes and locations
                            java.util.Calendar cal = java.util.Calendar.getInstance();
                            for (int i = 0; i < 7; i++) {
                                cal.setTime(new java.util.Date());
                                cal.add(java.util.Calendar.DAY_OF_MONTH, i);
                                String date = dateFormat.format(cal.getTime());
                                try {
                                    insertSampleSchedulesForDate(date);
                                } catch (Exception e) {
                                    android.util.Log.e("DatabaseHelper", "Error creating schedules for date " + date + ": " + e.getMessage(), e);
                                    // Continue with next date
                                }
                            }
                        } catch (Exception scheduleException) {
                            android.util.Log.e("DatabaseHelper", "Error in schedule creation logic: " + scheduleException.getMessage(), scheduleException);
                        }
                    } else {
                        android.util.Log.w("DatabaseHelper", "No routes found. Cannot create schedules.");
                    }
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error creating sample schedules: " + e.getMessage(), e);
                }
                
                // Ensure db is still open and valid before querying
                // Reopen if needed, but try to avoid closing the original db
                try {
                    if (db == null || !db.isOpen()) {
                        db = this.getReadableDatabase();
                    }
                } catch (Exception ex) {
                    android.util.Log.e("DatabaseHelper", "Error checking/reopening database: " + ex.getMessage(), ex);
                    // Try to get a new database connection
                    try {
                        db = this.getReadableDatabase();
                    } catch (Exception ex2) {
                        android.util.Log.e("DatabaseHelper", "Error getting new database connection: " + ex2.getMessage(), ex2);
                        return null;
                    }
                }
            }
            
            // Query to get random upcoming schedules in the future (after current time)
            // This will return random suggestions from various routes and locations
            // Schedules should be: (date > currentDate) OR (date = currentDate AND time >= currentTime)
            // Only get schedules with valid route_number and available seats > 0
            // Order randomly to get variety, then by date and time ascending
            String query = "SELECT DISTINCT " +
                    "s." + COL_SCHEDULE_ID + " as schedule_id, " +
                    "s." + COL_SCHEDULE_ROUTE_ID + " as route_id, " +
                    "s." + COL_SCHEDULE_DEPARTURE_TIME + " as departure_time, " +
                    "s." + COL_SCHEDULE_ARRIVAL_TIME + " as arrival_time, " +
                    "s." + COL_SCHEDULE_DATE + " as date, " +
                    "s." + COL_SCHEDULE_PRICE + " as price, " +
                    "s." + COL_SCHEDULE_BUS_TYPE + " as bus_type, " +
                    "s." + COL_SCHEDULE_AVAILABLE_SEATS + " as available_seats, " +
                    "r." + COL_ROUTE_NUMBER + " as route_number, " +
                    "COALESCE(c." + COL_COMPANY_NAME + ", 'EASYBUS') as company_name, " +
                    "l1." + COL_LOCATION_NAME + " as from_location, " +
                    "l2." + COL_LOCATION_NAME + " as to_location " +
                    "FROM " + TABLE_BUS_SCHEDULES + " s " +
                    "INNER JOIN " + TABLE_BUS_ROUTES + " r ON s." + COL_SCHEDULE_ROUTE_ID + " = r." + COL_ROUTE_ID + " " +
                    "INNER JOIN " + TABLE_LOCATIONS + " l1 ON r." + COL_ROUTE_FROM + " = l1." + COL_LOCATION_ID + " " +
                    "INNER JOIN " + TABLE_LOCATIONS + " l2 ON r." + COL_ROUTE_TO + " = l2." + COL_LOCATION_ID + " " +
                    "LEFT JOIN " + TABLE_BUS_COMPANIES + " c ON s." + COL_SCHEDULE_COMPANY_ID + " = c." + COL_COMPANY_ID + " " +
                    "WHERE (s." + COL_SCHEDULE_DATE + " > ? OR " +
                    "(s." + COL_SCHEDULE_DATE + " = ? AND s." + COL_SCHEDULE_DEPARTURE_TIME + " >= ?)) " +
                    "AND r." + COL_ROUTE_NUMBER + " IS NOT NULL AND r." + COL_ROUTE_NUMBER + " > 0 " +
                    "AND s." + COL_SCHEDULE_AVAILABLE_SEATS + " > 0 " +
                    "ORDER BY RANDOM() " + // Random order for variety
                    "LIMIT ?";
            
            android.util.Log.d("DatabaseHelper", "Getting random suggested journeys in the future - From: " + currentDate + " " + currentTime);
            try {
                cursor = db.rawQuery(query, new String[]{
                        currentDate, currentDate, currentTime,
                        String.valueOf(limit)
                });
                android.util.Log.d("DatabaseHelper", "Query returned " + (cursor != null ? cursor.getCount() : 0) + " suggested journeys");
                // Note: Don't close db here, cursor needs the database to be open
                return cursor;
            } catch (Exception queryException) {
                android.util.Log.e("DatabaseHelper", "Error executing query in getSuggestedJourneys: " + queryException.getMessage(), queryException);
                if (cursor != null && !cursor.isClosed()) {
                    try {
                        cursor.close();
                    } catch (Exception ex) {
                        android.util.Log.e("DatabaseHelper", "Error closing cursor: " + ex.getMessage(), ex);
                    }
                }
                return null;
            }
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in getSuggestedJourneys: " + e.getMessage(), e);
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception ex) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor in catch: " + ex.getMessage(), ex);
                }
            }
            // Don't close db here as it might be used by other operations
            return null;
        }
    }
    
    // Get all bookings for user (including cancelled) - for history
    public Cursor getAllUserBookings(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "b." + COL_BOOKING_ID + " as booking_id, " +
                "b." + COL_BOOKING_STATUS + " as status, " +
                "b." + COL_BOOKING_TOTAL_FARE + " as total_fare, " +
                "s." + COL_SCHEDULE_DEPARTURE_TIME + " as departure_time, " +
                "s." + COL_SCHEDULE_ARRIVAL_TIME + " as arrival_time, " +
                "s." + COL_SCHEDULE_DATE + " as date, " +
                "b." + COL_BOOKING_BOARDING_POINT + " as boarding_point, " +
                "b." + COL_BOOKING_DROP_POINT + " as drop_point, " +
                "b." + COL_BOOKING_SEAT_NUMBERS + " as seat_numbers, " +
                "COALESCE(c." + COL_COMPANY_NAME + ", 'EASYBUS') as company_name, " +
                "l1." + COL_LOCATION_NAME + " as from_location, " +
                "l2." + COL_LOCATION_NAME + " as to_location " +
                "FROM " + TABLE_BOOKINGS + " b " +
                "INNER JOIN " + TABLE_BUS_SCHEDULES + " s ON b." + COL_BOOKING_SCHEDULE_ID + " = s." + COL_SCHEDULE_ID + " " +
                "INNER JOIN " + TABLE_BUS_ROUTES + " r ON s." + COL_SCHEDULE_ROUTE_ID + " = r." + COL_ROUTE_ID + " " +
                "INNER JOIN " + TABLE_LOCATIONS + " l1 ON r." + COL_ROUTE_FROM + " = l1." + COL_LOCATION_ID + " " +
                "INNER JOIN " + TABLE_LOCATIONS + " l2 ON r." + COL_ROUTE_TO + " = l2." + COL_LOCATION_ID + " " +
                "LEFT JOIN " + TABLE_BUS_COMPANIES + " c ON s." + COL_SCHEDULE_COMPANY_ID + " = c." + COL_COMPANY_ID + " " +
                "WHERE b." + COL_BOOKING_USER_EMAIL + " = ? " +
                "ORDER BY s." + COL_SCHEDULE_DATE + " DESC, s." + COL_SCHEDULE_DEPARTURE_TIME + " DESC";
        return db.rawQuery(query, new String[]{userEmail});
    }

    // Get full booking details by booking ID
    public Cursor getBookingDetails(long bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT " +
                    "b." + COL_BOOKING_ID + " as booking_id, " +
                    "b." + COL_BOOKING_SCHEDULE_ID + " as schedule_id, " +
                    "b." + COL_BOOKING_USER_EMAIL + " as user_email, " +
                    "b." + COL_BOOKING_PASSENGER_NAME + " as passenger_name, " +
                    "b." + COL_BOOKING_PASSENGER_AGE + " as passenger_age, " +
                    "b." + COL_BOOKING_PASSENGER_GENDER + " as passenger_gender, " +
                    "b." + COL_BOOKING_SEAT_NUMBERS + " as seat_numbers, " +
                    "b." + COL_BOOKING_BOARDING_POINT + " as boarding_point, " +
                    "b." + COL_BOOKING_DROP_POINT + " as drop_point, " +
                    "b." + COL_BOOKING_TOTAL_FARE + " as total_fare, " +
                    "b." + COL_BOOKING_STATUS + " as status, " +
                    "b." + COL_BOOKING_BOOKING_DATE + " as booking_date, " +
                    "b." + COL_BOOKING_CODE + " as booking_code, " +
                    "r." + COL_ROUTE_NUMBER + " as route_number, " +
                    "s." + COL_SCHEDULE_DEPARTURE_TIME + " as departure_time, " +
                    "s." + COL_SCHEDULE_ARRIVAL_TIME + " as arrival_time, " +
                    "s." + COL_SCHEDULE_DATE + " as date, " +
                    "s." + COL_SCHEDULE_PRICE + " as price, " +
                    "s." + COL_SCHEDULE_BUS_TYPE + " as bus_type, " +
                    "COALESCE(c." + COL_COMPANY_NAME + ", 'EASYBUS') as company_name, " +
                    "l1." + COL_LOCATION_NAME + " as from_location, " +
                    "l2." + COL_LOCATION_NAME + " as to_location " +
                    "FROM " + TABLE_BOOKINGS + " b " +
                    "INNER JOIN " + TABLE_BUS_SCHEDULES + " s ON b." + COL_BOOKING_SCHEDULE_ID + " = s." + COL_SCHEDULE_ID + " " +
                    "INNER JOIN " + TABLE_BUS_ROUTES + " r ON s." + COL_SCHEDULE_ROUTE_ID + " = r." + COL_ROUTE_ID + " " +
                    "INNER JOIN " + TABLE_LOCATIONS + " l1 ON r." + COL_ROUTE_FROM + " = l1." + COL_LOCATION_ID + " " +
                    "INNER JOIN " + TABLE_LOCATIONS + " l2 ON r." + COL_ROUTE_TO + " = l2." + COL_LOCATION_ID + " " +
                    "LEFT JOIN " + TABLE_BUS_COMPANIES + " c ON s." + COL_SCHEDULE_COMPANY_ID + " = c." + COL_COMPANY_ID + " " +
                    "WHERE b." + COL_BOOKING_ID + " = ?";
            android.util.Log.d("DatabaseHelper", "Executing query for booking ID: " + bookingId);
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(bookingId)});
            android.util.Log.d("DatabaseHelper", "Query returned " + (cursor != null ? cursor.getCount() : 0) + " rows");
            return cursor;
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in getBookingDetails: " + e.getMessage(), e);
            return null;
        }
    }

    // Get schedule details by schedule ID
    public Cursor getScheduleDetails(long scheduleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                COL_SCHEDULE_ID + ", " +
                COL_SCHEDULE_DEPARTURE_TIME + ", " +
                COL_SCHEDULE_ARRIVAL_TIME + ", " +
                COL_SCHEDULE_DATE + ", " +
                COL_SCHEDULE_PRICE + ", " +
                COL_SCHEDULE_BUS_TYPE + " " +
                "FROM " + TABLE_BUS_SCHEDULES + " " +
                "WHERE " + COL_SCHEDULE_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(scheduleId)});
    }
    
    // Update booking status
    public boolean updateBookingStatus(long bookingId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_STATUS, status);
        
        int rowsAffected = db.update(TABLE_BOOKINGS, values, 
                COL_BOOKING_ID + " = ?", 
                new String[]{String.valueOf(bookingId)});
        db.close();
        return rowsAffected > 0;
    }
    
    // Update user information
    public boolean updateUserInfo(String email, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (name != null) {
            values.put(COL_USER_NAME, name);
        }
        if (phone != null) {
            values.put(COL_USER_PHONE, phone);
        }
        
        int rowsAffected = db.update(TABLE_USERS, values, 
                COL_USER_EMAIL + " = ?", 
                new String[]{email});
        db.close();
        return rowsAffected > 0;
    }
    
    // Update user password
    public boolean updateUserPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, newPassword);
        
        int rowsAffected = db.update(TABLE_USERS, values, 
                COL_USER_EMAIL + " = ?", 
                new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    // Get terminals/stops for a route
    // Get street names for a district (for boarding/drop points)
    public List<String> getStreetsForDistrict(String districtName) {
        List<String> streets = new ArrayList<>();
        
        // Extract district name (remove state if present)
        String district = districtName.split(",")[0].trim();
        
        // Map of districts to their common street names in Ho Chi Minh City
        java.util.Map<String, String[]> districtStreets = new java.util.HashMap<>();
        
        // Quận 1 - Central district with famous streets
        districtStreets.put("Quận 1", new String[]{
            "Đường Nguyễn Huệ", "Đường Lê Lợi", "Đường Đồng Khởi", 
            "Đường Pasteur", "Đường Nam Kỳ Khởi Nghĩa", "Đường Điện Biên Phủ",
            "Đường Hai Bà Trưng", "Đường Lý Tự Trọng", "Đường Nguyễn Du"
        });
        
        // Quận 2
        districtStreets.put("Quận 2", new String[]{
            "Đường Nguyễn Duy Trinh", "Đường Nguyễn Thị Định", "Đường Võ Văn Tần",
            "Đường Mai Chí Thọ", "Đường Nguyễn Văn Hưởng", "Đường Song Hành"
        });
        
        // Quận 3
        districtStreets.put("Quận 3", new String[]{
            "Đường Võ Thị Sáu", "Đường Nguyễn Đình Chiểu", "Đường Lý Chính Thắng",
            "Đường Cách Mạng Tháng Tám", "Đường Nguyễn Văn Trỗi", "Đường Lê Văn Sỹ"
        });
        
        // Quận 4
        districtStreets.put("Quận 4", new String[]{
            "Đường Khánh Hội", "Đường Tôn Thất Thuyết", "Đường Nguyễn Tất Thành",
            "Đường Hoàng Diệu", "Đường Nguyễn Khoái"
        });
        
        // Quận 5
        districtStreets.put("Quận 5", new String[]{
            "Đường Nguyễn Trãi", "Đường Hải Thượng Lãn Ông", "Đường Trần Hưng Đạo",
            "Đường Châu Văn Liêm", "Đường An Dương Vương"
        });
        
        // Quận 6
        districtStreets.put("Quận 6", new String[]{
            "Đường Hậu Giang", "Đường Lê Quang Sung", "Đường Minh Phụng",
            "Đường Tân Hương", "Đường Hồng Bàng"
        });
        
        // Quận 7
        districtStreets.put("Quận 7", new String[]{
            "Đường Nguyễn Thị Thập", "Đường Huỳnh Tấn Phát", "Đường Nguyễn Lương Bằng",
            "Đường Lê Văn Lương", "Đường Nguyễn Văn Linh"
        });
        
        // Quận 8
        districtStreets.put("Quận 8", new String[]{
            "Đường Dương Bá Trạc", "Đường Tạ Quang Bửu", "Đường Phạm Thế Hiển",
            "Đường Bùi Minh Trực", "Đường Hưng Phú"
        });
        
        // Quận 9
        districtStreets.put("Quận 9", new String[]{
            "Đường Đỗ Xuân Hợp", "Đường Lê Văn Việt", "Đường Nguyễn Xiển",
            "Đường Đỗ Văn Dậy", "Đường Tân Chánh Hiệp"
        });
        
        // Quận 10
        districtStreets.put("Quận 10", new String[]{
            "Đường 3 Tháng 2", "Đường Lý Thái Tổ", "Đường Ngô Gia Tự",
            "Đường Lạc Long Quân", "Đường Hùng Vương"
        });
        
        // Quận 11
        districtStreets.put("Quận 11", new String[]{
            "Đường Lạc Long Quân", "Đường Tân Hương", "Đường Lê Đại Hành",
            "Đường Nguyễn Oanh", "Đường Tân Chánh Hiệp"
        });
        
        // Quận 12
        districtStreets.put("Quận 12", new String[]{
            "Đường Tân Thới Hiệp", "Đường Nguyễn Ảnh Thủ", "Đường Tô Ký",
            "Đường Trường Chinh", "Đường Nguyễn Văn Quá"
        });
        
        // Bình Thạnh
        districtStreets.put("Bình Thạnh", new String[]{
            "Đường Xô Viết Nghệ Tĩnh", "Đường Bạch Đằng", "Đường Điện Biên Phủ",
            "Đường Nguyễn Gia Trí", "Đường Phan Đăng Lưu"
        });
        
        // Tân Bình
        districtStreets.put("Tân Bình", new String[]{
            "Đường Cộng Hòa", "Đường Hoàng Văn Thụ", "Đường Trường Chinh",
            "Đường Tân Sơn Nhì", "Đường Bạch Đằng"
        });
        
        // Tân Phú
        districtStreets.put("Tân Phú", new String[]{
            "Đường Tân Hương", "Đường Tân Sơn Nhì", "Đường Lê Trọng Tấn",
            "Đường Tây Thạnh", "Đường Phú Thọ Hòa"
        });
        
        // Phú Nhuận
        districtStreets.put("Phú Nhuận", new String[]{
            "Đường Phan Xích Long", "Đường Hoàng Văn Thụ", "Đường Nguyễn Văn Trỗi",
            "Đường Phan Đình Phùng", "Đường Nguyễn Kiệm"
        });
        
        // Gò Vấp
        districtStreets.put("Gò Vấp", new String[]{
            "Đường Quang Trung", "Đường Nguyễn Oanh", "Đường Phan Văn Trị",
            "Đường Nguyễn Văn Nghi", "Đường Lê Đức Thọ"
        });
        
        // Bình Tân
        districtStreets.put("Bình Tân", new String[]{
            "Đường Tân Hương", "Đường Tân Kỳ Tân Quý", "Đường Bình Long",
            "Đường Tân Sơn Nhì", "Đường Hương Lộ 2"
        });
        
        // Thủ Đức
        districtStreets.put("Thủ Đức", new String[]{
            "Đường Võ Văn Ngân", "Đường Kha Vạn Cân", "Đường Vạn Hạnh",
            "Đường Lê Văn Việt", "Đường Nguyễn Xiển"
        });
        
        // Get streets for the district
        String[] districtStreetList = districtStreets.get(district);
        if (districtStreetList != null) {
            for (String street : districtStreetList) {
                streets.add(street);
            }
        } else {
            // Default streets if district not found
            streets.add("Đường " + district);
            streets.add("Đường " + district + " 1");
            streets.add("Đường " + district + " 2");
        }
        
        return streets;
    }
    
    public List<String> getTerminalsForRoute(String fromLocation, String toLocation) {
        List<String> terminals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Get location IDs
        String fromCity = fromLocation.split(",")[0].trim();
        String toCity = toLocation.split(",")[0].trim();
        
        long fromId = getLocationId(db, fromCity);
        long toId = getLocationId(db, toCity);
        
        // Query for terminals
        Cursor cursor = db.query(TABLE_LOCATIONS,
                new String[]{COL_LOCATION_NAME},
                COL_LOCATION_TYPE + " = ? AND (" + COL_LOCATION_NAME + " LIKE ? OR " + COL_LOCATION_NAME + " LIKE ?)",
                new String[]{"terminal", fromCity + "%", toCity + "%"},
                null, null, null);
        
        if (cursor.moveToFirst()) {
            do {
                terminals.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        // If no terminals found, return default terminals based on cities
        if (terminals.isEmpty()) {
            terminals.add(fromCity + " Main Terminal");
            terminals.add(fromCity + " Downtown Terminal");
            terminals.add(fromCity + " Airport Terminal");
            terminals.add(toCity + " Main Terminal");
            terminals.add(toCity + " Downtown Terminal");
            terminals.add(toCity + " Airport Terminal");
        }
        
        db.close();
        return terminals;
    }

    // User management methods
    // Register new user
    public long registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Check if email already exists
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
        
        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return -1; // Email already exists
        }
        cursor.close();
        
        // Generate verification code
        String verificationCode = String.valueOf((int)(Math.random() * 900000) + 100000);
        
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password); // In production, hash this password
        // Auto-verify users for easier testing (set to 1 instead of 0)
        values.put(COL_USER_IS_VERIFIED, 1);
        values.put(COL_USER_VERIFICATION_CODE, verificationCode);
        values.put(COL_USER_LOGIN_TYPE, "email");
        
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Login user
    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                android.util.Log.e("DatabaseHelper", "Cannot get readable database");
                return false;
            }
            
            cursor = db.query(TABLE_USERS,
                    new String[]{COL_USER_ID, COL_USER_IS_VERIFIED},
                    COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD + " = ?",
                    new String[]{email, password},
                    null, null, null);
            
            if (cursor == null) {
                android.util.Log.e("DatabaseHelper", "Cursor is null");
                return false;
            }
            
            boolean exists = cursor.moveToFirst();
            if (exists) {
                // User exists and password matches
                // Allow login (users are now auto-verified on registration)
                return true;
            }
            return false;
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in loginUser: " + e.getMessage(), e);
            return false;
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                }
            }
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing database: " + e.getMessage(), e);
                }
            }
        }
    }

    // Check if email exists
    public boolean emailExists(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                android.util.Log.e("DatabaseHelper", "Cannot get readable database");
                return false;
            }
            
            cursor = db.query(TABLE_USERS,
                    new String[]{COL_USER_ID},
                    COL_USER_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null);
            
            if (cursor == null) {
                return false;
            }
            
            boolean exists = cursor.moveToFirst();
            return exists;
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in emailExists: " + e.getMessage(), e);
            return false;
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                }
            }
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing database: " + e.getMessage(), e);
                }
            }
        }
    }

    // Verify user email
    public boolean verifyUser(String email, String verificationCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID, COL_USER_VERIFICATION_CODE},
                COL_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
        
        if (cursor.moveToFirst()) {
            String code = cursor.getString(1);
            if (code != null && code.equals(verificationCode)) {
                ContentValues values = new ContentValues();
                values.put(COL_USER_IS_VERIFIED, 1);
                db.update(TABLE_USERS, values, COL_USER_EMAIL + " = ?", new String[]{email});
                cursor.close();
                db.close();
                return true;
            }
        }
        cursor.close();
        db.close();
        return false;
    }

    // Get verification code for user
    public String getVerificationCode(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_VERIFICATION_CODE},
                COL_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
        
        String code = null;
        if (cursor.moveToFirst()) {
            code = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return code;
    }

    // Reset password
    public boolean resetPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, newPassword); // In production, hash this password
        
        int rowsAffected = db.update(TABLE_USERS, values, COL_USER_EMAIL + " = ?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    // Register/Login with Google
    public long registerOrLoginGoogle(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Check if user exists
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
        
        if (cursor.moveToFirst()) {
            // User exists, update login type if needed
            long userId = cursor.getLong(0);
            ContentValues values = new ContentValues();
            values.put(COL_USER_LOGIN_TYPE, "google");
            values.put(COL_USER_IS_VERIFIED, 1); // Google accounts are pre-verified
            db.update(TABLE_USERS, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            cursor.close();
            db.close();
            return userId;
        }
        cursor.close();
        
        // Create new user
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_IS_VERIFIED, 1);
        values.put(COL_USER_LOGIN_TYPE, "google");
        
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Get user info
    public android.content.ContentValues getUserInfo(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        android.content.ContentValues userInfo = new android.content.ContentValues();
        
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                android.util.Log.e("DatabaseHelper", "Cannot get readable database");
                return userInfo;
            }
            
            cursor = db.query(TABLE_USERS,
                    new String[]{COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL, COL_USER_PHONE},
                    COL_USER_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                try {
                    userInfo.put("id", cursor.getLong(0));
                    String name = cursor.getString(1);
                    if (name != null) {
                        userInfo.put("name", name);
                    }
                    String emailValue = cursor.getString(2);
                    if (emailValue != null) {
                        userInfo.put("email", emailValue);
                    }
                    String phone = cursor.getString(3);
                    if (phone != null) {
                        userInfo.put("phone", phone);
                    }
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error reading user info: " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error in getUserInfo: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing cursor: " + e.getMessage(), e);
                }
            }
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    android.util.Log.e("DatabaseHelper", "Error closing database: " + e.getMessage(), e);
                }
            }
        }
        return userInfo;
    }
    
    // ========== WALLET METHODS ==========
    
    /**
     * Get wallet balance for a user
     */
    public double getWalletBalance(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        double balance = 0.0;
        
        Cursor cursor = db.query(TABLE_WALLET,
                new String[]{COL_WALLET_BALANCE},
                COL_WALLET_USER_EMAIL + " = ?",
                new String[]{userEmail},
                null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            int balanceIndex = cursor.getColumnIndex(COL_WALLET_BALANCE);
            if (balanceIndex >= 0) {
                balance = cursor.getDouble(balanceIndex);
            }
            cursor.close();
        } else {
            // Create wallet if doesn't exist
            createWallet(userEmail);
        }
        
        return balance;
    }
    
    /**
     * Create wallet for a user
     */
    public void createWallet(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_WALLET_USER_EMAIL, userEmail);
        values.put(COL_WALLET_BALANCE, 0.0);
        db.insert(TABLE_WALLET, null, values);
    }
    
    /**
     * Deposit money to wallet
     */
    public boolean depositToWallet(String userEmail, double amount, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            db.beginTransaction();
            
            // Get current balance
            double currentBalance = getWalletBalance(userEmail);
            double newBalance = currentBalance + amount;
            
            // Update wallet balance
            ContentValues walletValues = new ContentValues();
            walletValues.put(COL_WALLET_BALANCE, newBalance);
            walletValues.put(COL_WALLET_UPDATED_AT, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
            
            int updated = db.update(TABLE_WALLET, walletValues,
                    COL_WALLET_USER_EMAIL + " = ?",
                    new String[]{userEmail});
            
            if (updated == 0) {
                // Wallet doesn't exist, create it
                createWallet(userEmail);
                walletValues.put(COL_WALLET_BALANCE, amount);
                db.insert(TABLE_WALLET, null, walletValues);
            }
            
            // Add transaction record
            ContentValues transactionValues = new ContentValues();
            transactionValues.put(COL_TRANSACTION_USER_EMAIL, userEmail);
            transactionValues.put(COL_TRANSACTION_TYPE, "deposit");
            transactionValues.put(COL_TRANSACTION_AMOUNT, amount);
            transactionValues.put(COL_TRANSACTION_DESCRIPTION, description != null ? description : "Nạp tiền vào ví");
            transactionValues.put(COL_TRANSACTION_DATE, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
            db.insert(TABLE_WALLET_TRANSACTIONS, null, transactionValues);
            
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error depositing to wallet: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }
    
    /**
     * Withdraw money from wallet
     */
    public boolean withdrawFromWallet(String userEmail, double amount, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            db.beginTransaction();
            
            // Get current balance
            double currentBalance = getWalletBalance(userEmail);
            
            if (currentBalance < amount) {
                return false; // Insufficient balance
            }
            
            double newBalance = currentBalance - amount;
            
            // Update wallet balance
            ContentValues walletValues = new ContentValues();
            walletValues.put(COL_WALLET_BALANCE, newBalance);
            walletValues.put(COL_WALLET_UPDATED_AT, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
            
            int updated = db.update(TABLE_WALLET, walletValues,
                    COL_WALLET_USER_EMAIL + " = ?",
                    new String[]{userEmail});
            
            if (updated == 0) {
                return false;
            }
            
            // Add transaction record
            ContentValues transactionValues = new ContentValues();
            transactionValues.put(COL_TRANSACTION_USER_EMAIL, userEmail);
            transactionValues.put(COL_TRANSACTION_TYPE, "withdraw");
            transactionValues.put(COL_TRANSACTION_AMOUNT, amount);
            transactionValues.put(COL_TRANSACTION_DESCRIPTION, description != null ? description : "Rút tiền từ ví");
            transactionValues.put(COL_TRANSACTION_DATE, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
            db.insert(TABLE_WALLET_TRANSACTIONS, null, transactionValues);
            
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error withdrawing from wallet: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }
    
    /**
     * Pay using wallet
     */
    public boolean payWithWallet(String userEmail, double amount, String description) {
        return withdrawFromWallet(userEmail, amount, description != null ? description : "Thanh toán đặt vé");
    }
    
    /**
     * Get wallet transactions
     */
    public List<ContentValues> getWalletTransactions(String userEmail, int limit) {
        List<ContentValues> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_WALLET_TRANSACTIONS,
                null,
                COL_TRANSACTION_USER_EMAIL + " = ?",
                new String[]{userEmail},
                null, null,
                COL_TRANSACTION_DATE + " DESC",
                limit > 0 ? String.valueOf(limit) : null);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContentValues values = new ContentValues();
                for (String column : cursor.getColumnNames()) {
                    int index = cursor.getColumnIndex(column);
                    if (index >= 0) {
                        int type = cursor.getType(index);
                        switch (type) {
                            case Cursor.FIELD_TYPE_INTEGER:
                                values.put(column, cursor.getLong(index));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                values.put(column, cursor.getDouble(index));
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                values.put(column, cursor.getString(index));
                                break;
                        }
                    }
                }
                transactions.add(values);
            }
            cursor.close();
        }
        
        return transactions;
    }
    
    // ========== NOTIFICATION METHODS ==========
    
    /**
     * Insert a new notification
     */
    public long insertNotification(String userEmail, String title, String message, String type, Long bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTIFICATION_USER_EMAIL, userEmail);
        values.put(COL_NOTIFICATION_TITLE, title);
        values.put(COL_NOTIFICATION_MESSAGE, message);
        values.put(COL_NOTIFICATION_TYPE, type != null ? type : "system");
        values.put(COL_NOTIFICATION_IS_READ, 0);
        if (bookingId != null) {
            values.put(COL_NOTIFICATION_BOOKING_ID, bookingId);
        }
        values.put(COL_NOTIFICATION_CREATED_AT, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
        
        long id = db.insert(TABLE_NOTIFICATIONS, null, values);
        return id;
    }
    
    /**
     * Get all notifications for a user
     */
    public Cursor getUserNotifications(String userEmail, int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NOTIFICATIONS,
                null,
                COL_NOTIFICATION_USER_EMAIL + " = ?",
                new String[]{userEmail},
                null, null,
                COL_NOTIFICATION_CREATED_AT + " DESC",
                limit > 0 ? String.valueOf(limit) : null);
    }
    
    /**
     * Get unread notification count
     */
    public int getUnreadNotificationCount(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTIFICATIONS,
                new String[]{"COUNT(*) as count"},
                COL_NOTIFICATION_USER_EMAIL + " = ? AND " + COL_NOTIFICATION_IS_READ + " = 0",
                new String[]{userEmail},
                null, null, null);
        
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }
    
    /**
     * Mark notification as read
     */
    public boolean markNotificationAsRead(long notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTIFICATION_IS_READ, 1);
        
        int updated = db.update(TABLE_NOTIFICATIONS, values,
                COL_NOTIFICATION_ID + " = ?",
                new String[]{String.valueOf(notificationId)});
        return updated > 0;
    }
    
    /**
     * Mark all notifications as read for a user
     */
    public boolean markAllNotificationsAsRead(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTIFICATION_IS_READ, 1);
        
        int updated = db.update(TABLE_NOTIFICATIONS, values,
                COL_NOTIFICATION_USER_EMAIL + " = ? AND " + COL_NOTIFICATION_IS_READ + " = 0",
                new String[]{userEmail});
        return updated > 0;
    }
    
    /**
     * Delete a notification
     */
    public boolean deleteNotification(long notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete(TABLE_NOTIFICATIONS,
                COL_NOTIFICATION_ID + " = ?",
                new String[]{String.valueOf(notificationId)});
        return deleted > 0;
    }
    
    /**
     * Delete all notifications for a user
     */
    public boolean deleteAllNotifications(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete(TABLE_NOTIFICATIONS,
                COL_NOTIFICATION_USER_EMAIL + " = ?",
                new String[]{userEmail});
        return deleted > 0;
    }
}

