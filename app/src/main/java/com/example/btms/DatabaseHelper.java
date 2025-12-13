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
    private static final int DATABASE_VERSION = 5; // Increment to force database recreation

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        
        // Force recreate database on first run after update
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("BTMS_DB_PREFS", android.content.Context.MODE_PRIVATE);
            boolean dbRecreated = prefs.getBoolean("db_recreated_v5", false);
            if (!dbRecreated) {
                // Delete database file to force recreation
                context.deleteDatabase(DATABASE_NAME);
                android.util.Log.d("DatabaseHelper", "Database deleted to force recreation with route numbers");
                prefs.edit().putBoolean("db_recreated_v5", true).apply();
            }
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error checking database recreation: " + e.getMessage());
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
                + COL_SCHEDULE_TOTAL_SEATS + " INTEGER DEFAULT 40,"
                + COL_SCHEDULE_AVAILABLE_SEATS + " INTEGER DEFAULT 40,"
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
                + "FOREIGN KEY(" + COL_BOOKING_SCHEDULE_ID + ") REFERENCES " + TABLE_BUS_SCHEDULES + "(" + COL_SCHEDULE_ID + ")"
                + ")";
        db.execSQL(createBookingsTable);

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

        // Insert sample routes (New York to major cities)
        insertSampleRoutes(db);
        
        // Note: Sample schedules will be inserted on first query if needed
    }
    
    // Call this method to ensure sample schedules exist
    public void ensureSampleSchedules() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BUS_SCHEDULES, new String[]{COL_SCHEDULE_ID}, null, null, null, null, null, "1");
        boolean hasSchedules = cursor.moveToFirst();
        cursor.close();
        db.close();
        
        if (!hasSchedules) {
            insertSampleSchedules();
        }
    }
    
    // Ensure sample schedules exist for a specific date
    public void ensureSampleSchedulesForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BUS_SCHEDULES, 
                new String[]{COL_SCHEDULE_ID}, 
                COL_SCHEDULE_DATE + " = ?", 
                new String[]{date}, 
                null, null, null, "1");
        boolean hasSchedulesForDate = cursor.moveToFirst();
        cursor.close();
        db.close();
        
        if (!hasSchedulesForDate) {
            insertSampleSchedulesForDate(date);
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
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            if (db == null) {
                return locations;
            }
            
            Cursor cursor = db.query(TABLE_LOCATIONS,
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
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error getting locations: " + e.getMessage(), e);
        }
        return locations;
    }

    // Get locations by name (for search) - only district names
    public List<String> searchLocations(String query) {
        List<String> locations = new ArrayList<>();
        try {
            if (query == null || query.isEmpty()) {
                return getAllLocations();
            }
            
            SQLiteDatabase db = this.getReadableDatabase();
            if (db == null) {
                return locations;
            }
            
            Cursor cursor = db.query(TABLE_LOCATIONS,
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
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error searching locations: " + e.getMessage(), e);
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
        SQLiteDatabase db = this.getReadableDatabase();
        long id = getLocationId(db, cityName);
        db.close();
        return id;
    }

    // Get route ID by from and to locations
    public long getRouteId(String fromLocation, String toLocation) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Get location IDs
        String fromCity = fromLocation.split(",")[0].trim();
        String toCity = toLocation.split(",")[0].trim();
        
        long fromId = getLocationId(db, fromCity);
        long toId = getLocationId(db, toCity);
        
        if (fromId == -1 || toId == -1) {
            db.close();
            return -1;
        }

        Cursor cursor = db.query(TABLE_BUS_ROUTES,
                new String[]{COL_ROUTE_ID},
                COL_ROUTE_FROM + " = ? AND " + COL_ROUTE_TO + " = ?",
                new String[]{String.valueOf(fromId), String.valueOf(toId)},
                null, null, null);

        long routeId = -1;
        if (cursor.moveToFirst()) {
            routeId = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return routeId;
    }
    
    // Create route if it doesn't exist
    public long createRouteIfNotExists(String fromLocation, String toLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Get location IDs
        String fromCity = fromLocation.split(",")[0].trim();
        String toCity = toLocation.split(",")[0].trim();
        
        long fromId = getLocationId(db, fromCity);
        long toId = getLocationId(db, toCity);
        
        if (fromId == -1 || toId == -1) {
            db.close();
            return -1;
        }
        
        // Check if routes already exist for this pair
        Cursor cursor = db.query(TABLE_BUS_ROUTES,
                new String[]{COL_ROUTE_ID},
                COL_ROUTE_FROM + " = ? AND " + COL_ROUTE_TO + " = ?",
                new String[]{String.valueOf(fromId), String.valueOf(toId)},
                null, null, null);
        
        if (cursor.getCount() > 0) {
            // Routes already exist, return the first one
            cursor.moveToFirst();
            long routeId = cursor.getLong(0);
            cursor.close();
            db.close();
            return routeId;
        }
        cursor.close();
        
        // Estimate distance and duration (simple calculation for HCMC districts)
        double estimatedDistance = 5.0; // Default 5km for HCMC districts
        int estimatedDuration = 20; // Default 20 minutes
        
        // Get next route number (max route number + 1)
        Cursor maxRouteCursor = db.rawQuery("SELECT MAX(" + COL_ROUTE_NUMBER + ") FROM " + TABLE_BUS_ROUTES, null);
        int nextRouteNumber = 1;
        if (maxRouteCursor.moveToFirst() && !maxRouteCursor.isNull(0)) {
            nextRouteNumber = maxRouteCursor.getInt(0) + 1;
        }
        maxRouteCursor.close();
        
        // Get all existing route numbers to avoid duplicates
        java.util.Set<Integer> existingRouteNumbers = new java.util.HashSet<>();
        Cursor existingRoutesCursor = db.query(TABLE_BUS_ROUTES, 
                new String[]{COL_ROUTE_NUMBER}, 
                null, null, null, null, null);
        while (existingRoutesCursor.moveToNext()) {
            int routeNum = existingRoutesCursor.getInt(0);
            existingRouteNumbers.add(routeNum);
        }
        existingRoutesCursor.close();
        
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
        
        db.close();
        return firstRouteId;
    }
    
    // Get all route IDs for a from-to pair
    public java.util.List<Long> getAllRouteIdsForPair(String fromLocation, String toLocation) {
        java.util.List<Long> routeIds = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        try {
            String fromCity = fromLocation.split(",")[0].trim();
            String toCity = toLocation.split(",")[0].trim();
            
            long fromId = getLocationId(db, fromCity);
            long toId = getLocationId(db, toCity);
            
            if (fromId == -1 || toId == -1) {
                db.close();
                return routeIds;
            }
            
            Cursor cursor = db.query(TABLE_BUS_ROUTES,
                    new String[]{COL_ROUTE_ID},
                    COL_ROUTE_FROM + " = ? AND " + COL_ROUTE_TO + " = ?",
                    new String[]{String.valueOf(fromId), String.valueOf(toId)},
                    null, null, COL_ROUTE_NUMBER + " ASC");
            
            while (cursor.moveToNext()) {
                routeIds.add(cursor.getLong(0));
            }
            cursor.close();
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error getting route IDs: " + e.getMessage(), e);
        } finally {
            db.close();
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
        String today = sdf.format(new java.util.Date());
        insertSampleSchedulesForDate(today);
    }
    
    // Insert sample schedules for a specific date
    public void insertSampleSchedulesForDate(String date) {
        insertSampleSchedulesForDate(date, -1); // -1 means all routes
    }
    
    // Insert sample schedules for a specific date and route (or all routes if routeId is -1)
    public void insertSampleSchedulesForDate(String date, long specificRouteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Get route IDs - either specific route or all routes
        Cursor routeCursor;
        if (specificRouteId > 0) {
            routeCursor = db.query(TABLE_BUS_ROUTES, new String[]{COL_ROUTE_ID}, 
                    COL_ROUTE_ID + " = ?", new String[]{String.valueOf(specificRouteId)}, 
                    null, null, null);
        } else {
            routeCursor = db.query(TABLE_BUS_ROUTES, new String[]{COL_ROUTE_ID}, null, null, null, null, null);
        }
        Cursor companyCursor = db.query(TABLE_BUS_COMPANIES, new String[]{COL_COMPANY_ID}, null, null, null, null, null);
        
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
                
                // Generate random departure times for this route
                int baseHour = 6 + (int)(Math.random() * 14); // Random hour between 6 AM and 8 PM
                int baseMinute = (int)(Math.random() * 4) * 15; // 0, 15, 30, or 45
                
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
                    values.put(COL_SCHEDULE_TOTAL_SEATS, 40);
                    values.put(COL_SCHEDULE_AVAILABLE_SEATS, 40 - (int)(Math.random() * 30)); // Random available seats
                    values.put(COL_SCHEDULE_DATE, date);
                    
                    db.insert(TABLE_BUS_SCHEDULES, null, values);
                }
                
                companyIndex++;
            } while (routeCursor.moveToNext());
        }
        
        routeCursor.close();
        companyCursor.close();
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
        
        long id = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return id;
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
    
    // Get all bookings for user (including cancelled) - for history
    public Cursor getAllUserBookings(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "b." + COL_BOOKING_ID + " as booking_id, " +
                "b." + COL_BOOKING_STATUS + " as status, " +
                "b." + COL_BOOKING_TOTAL_FARE + " as total_fare, " +
                "s." + COL_SCHEDULE_DEPARTURE_TIME + ", " +
                "s." + COL_SCHEDULE_ARRIVAL_TIME + ", " +
                "s." + COL_SCHEDULE_DATE + ", " +
                "b." + COL_BOOKING_BOARDING_POINT + ", " +
                "b." + COL_BOOKING_DROP_POINT + ", " +
                "b." + COL_BOOKING_SEAT_NUMBERS + ", " +
                "c." + COL_COMPANY_NAME + ", " +
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID, COL_USER_IS_VERIFIED},
                COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD + " = ?",
                new String[]{email, password},
                null, null, null);
        
        boolean exists = cursor.moveToFirst();
        if (exists) {
            // User exists and password matches
            // Allow login (users are now auto-verified on registration)
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    // Check if email exists
    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
        
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL, COL_USER_PHONE},
                COL_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
        
        android.content.ContentValues userInfo = new android.content.ContentValues();
        if (cursor.moveToFirst()) {
            userInfo.put("id", cursor.getLong(0));
            userInfo.put("name", cursor.getString(1));
            userInfo.put("email", cursor.getString(2));
            userInfo.put("phone", cursor.getString(3));
        }
        cursor.close();
        db.close();
        return userInfo;
    }
}

