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
    private static final int DATABASE_VERSION = 2;

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
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Insert USA locations
        String[] usaCities = {
                "New York, NY", "Los Angeles, CA", "Chicago, IL", "Houston, TX",
                "Phoenix, AZ", "Philadelphia, PA", "San Antonio, TX", "San Diego, CA",
                "Dallas, TX", "San Jose, CA", "Austin, TX", "Jacksonville, FL",
                "Fort Worth, TX", "Columbus, OH", "Charlotte, NC", "San Francisco, CA",
                "Indianapolis, IN", "Seattle, WA", "Denver, CO", "Washington, DC",
                "Boston, MA", "El Paso, TX", "Nashville, TN", "Detroit, MI",
                "Oklahoma City, OK", "Portland, OR", "Las Vegas, NV", "Memphis, TN",
                "Louisville, KY", "Baltimore, MD", "Milwaukee, WI", "Albuquerque, NM",
                "Tucson, AZ", "Fresno, CA", "Sacramento, CA", "Kansas City, MO",
                "Mesa, AZ", "Atlanta, GA", "Omaha, NE", "Raleigh, NC"
        };

        ContentValues locationValues = new ContentValues();
        for (String city : usaCities) {
            String[] parts = city.split(", ");
            locationValues.clear();
            locationValues.put(COL_LOCATION_NAME, parts[0]);
            locationValues.put(COL_LOCATION_STATE, parts.length > 1 ? parts[1] : "");
            locationValues.put(COL_LOCATION_TYPE, "city");
            db.insert(TABLE_LOCATIONS, null, locationValues);
        }

        // Insert bus companies
        String[] companies = {
                "Greyhound Lines", "Megabus", "Peter Pan Bus Lines", "BoltBus",
                "FlixBus", "RedCoach", "Jefferson Lines", "Trailways"
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

    private void insertSampleRoutes(SQLiteDatabase db) {
        // Get location IDs for major cities
        long nyId = getLocationId(db, "New York");
        long laId = getLocationId(db, "Los Angeles");
        long chiId = getLocationId(db, "Chicago");
        long houId = getLocationId(db, "Houston");
        long phxId = getLocationId(db, "Phoenix");
        long phiId = getLocationId(db, "Philadelphia");
        long bosId = getLocationId(db, "Boston");
        long wasId = getLocationId(db, "Washington");
        long atlId = getLocationId(db, "Atlanta");
        long seaId = getLocationId(db, "Seattle");

        // Insert routes
        insertRoute(db, nyId, bosId, 215, 240); // New York to Boston
        insertRoute(db, nyId, phiId, 95, 120); // New York to Philadelphia
        insertRoute(db, nyId, wasId, 225, 270); // New York to Washington
        insertRoute(db, nyId, chiId, 790, 960); // New York to Chicago
        insertRoute(db, chiId, laId, 2015, 2520); // Chicago to Los Angeles
        insertRoute(db, laId, seaId, 1135, 1320); // Los Angeles to Seattle
        insertRoute(db, houId, atlId, 800, 960); // Houston to Atlanta
        insertRoute(db, phxId, laId, 375, 420); // Phoenix to Los Angeles
    }

    private void insertRoute(SQLiteDatabase db, long fromId, long toId, double distance, int duration) {
        ContentValues values = new ContentValues();
        values.put(COL_ROUTE_FROM, fromId);
        values.put(COL_ROUTE_TO, toId);
        values.put(COL_ROUTE_DISTANCE, distance);
        values.put(COL_ROUTE_DURATION, duration);
        db.insert(TABLE_BUS_ROUTES, null, values);
    }

    // Get all locations
    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            if (db == null) {
                return locations;
            }
            
            Cursor cursor = db.query(TABLE_LOCATIONS,
                    new String[]{COL_LOCATION_NAME, COL_LOCATION_STATE},
                    null, null, null, null,
                    COL_LOCATION_NAME + " ASC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(0);
                        String state = cursor.getString(1);
                        locations.add(name + (state != null && !state.isEmpty() ? ", " + state : ""));
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

    // Get locations by name (for search)
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
                    new String[]{COL_LOCATION_NAME, COL_LOCATION_STATE},
                    COL_LOCATION_NAME + " LIKE ? OR " + COL_LOCATION_STATE + " LIKE ?",
                    new String[]{"%" + query + "%", "%" + query + "%"},
                    null, null,
                    COL_LOCATION_NAME + " ASC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(0);
                        String state = cursor.getString(1);
                        locations.add(name + (state != null && !state.isEmpty() ? ", " + state : ""));
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

    // Get schedules for a route
    public Cursor getSchedulesForRoute(long routeId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s." + COL_SCHEDULE_ID + ", c." + COL_COMPANY_NAME + ", " +
                "s." + COL_SCHEDULE_DEPARTURE_TIME + ", s." + COL_SCHEDULE_ARRIVAL_TIME + ", " +
                "s." + COL_SCHEDULE_PRICE + ", s." + COL_SCHEDULE_BUS_TYPE + ", " +
                "s." + COL_SCHEDULE_AVAILABLE_SEATS + ", s." + COL_SCHEDULE_TOTAL_SEATS +
                " FROM " + TABLE_BUS_SCHEDULES + " s " +
                "INNER JOIN " + TABLE_BUS_COMPANIES + " c ON s." + COL_SCHEDULE_COMPANY_ID + " = c." + COL_COMPANY_ID +
                " WHERE s." + COL_SCHEDULE_ROUTE_ID + " = ? AND s." + COL_SCHEDULE_DATE + " = ? " +
                "ORDER BY s." + COL_SCHEDULE_DEPARTURE_TIME + " ASC";
        return db.rawQuery(query, new String[]{String.valueOf(routeId), date});
    }

    // Insert sample schedules for routes
    public void insertSampleSchedules() {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Get some route IDs and company IDs
        Cursor routeCursor = db.query(TABLE_BUS_ROUTES, new String[]{COL_ROUTE_ID}, null, null, null, null, null, "8");
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
                
                // Insert 2-3 schedules per route
                for (int i = 0; i < 3; i++) {
                    ContentValues values = new ContentValues();
                    values.put(COL_SCHEDULE_ROUTE_ID, routeId);
                    values.put(COL_SCHEDULE_COMPANY_ID, companyId);
                    
                    // Generate times
                    int hour = 6 + (i * 4);
                    String departureTime = String.format("%02d:00", hour);
                    String arrivalTime = String.format("%02d:00", (hour + 1) % 24);
                    
                    values.put(COL_SCHEDULE_DEPARTURE_TIME, departureTime);
                    values.put(COL_SCHEDULE_ARRIVAL_TIME, arrivalTime);
                    values.put(COL_SCHEDULE_PRICE, 50.0 + (i * 25));
                    values.put(COL_SCHEDULE_BUS_TYPE, i == 0 ? "A/C Sleeper (2+2)" : (i == 1 ? "A/C Sleeper (2+2)" : "Non A/C Sleeper (2+1)"));
                    values.put(COL_SCHEDULE_TOTAL_SEATS, 40);
                    values.put(COL_SCHEDULE_AVAILABLE_SEATS, 40 - (i * 5));
                    
                    // Use today's date for demo
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                    values.put(COL_SCHEDULE_DATE, sdf.format(new java.util.Date()));
                    
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

    // Get terminals/stops for a route
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
        values.put(COL_USER_IS_VERIFIED, 0);
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
            // Check if user is verified
            int isVerified = cursor.getInt(1);
            cursor.close();
            db.close();
            return isVerified == 1;
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

