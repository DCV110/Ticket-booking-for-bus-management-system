package com.example.btms;

import org.json.JSONObject;

public class QRCodeDataHelper {
    
    /**
     * Generate QR code data with full booking information in JSON format
     * 
     * @param bookingCode Booking code (e.g., ITE1203A)
     * @param routeNumber Route number
     * @param fromLocation From location
     * @param toLocation To location
     * @param scheduleDate Schedule date (yyyy-MM-dd)
     * @param departureTime Departure time (HH:mm)
     * @param arrivalTime Arrival time (HH:mm)
     * @param seatNumbers Seat numbers (comma-separated)
     * @param passengerName Passenger name
     * @param totalFare Total fare
     * @return JSON string with booking information
     */
    public static String generateQRData(String bookingCode, int routeNumber, 
                                        String fromLocation, String toLocation,
                                        String scheduleDate, String departureTime, 
                                        String arrivalTime, String seatNumbers,
                                        String passengerName, double totalFare) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "BTMS_BOOKING");
            json.put("booking_code", bookingCode != null ? bookingCode : "");
            json.put("route_number", routeNumber);
            json.put("from", fromLocation != null ? fromLocation : "");
            json.put("to", toLocation != null ? toLocation : "");
            json.put("date", scheduleDate != null ? scheduleDate : "");
            json.put("departure_time", departureTime != null ? departureTime : "");
            json.put("arrival_time", arrivalTime != null ? arrivalTime : "");
            json.put("seats", seatNumbers != null ? seatNumbers : "");
            json.put("passenger", passengerName != null ? passengerName : "");
            json.put("total_fare", totalFare);
            
            return json.toString();
        } catch (Exception e) {
            android.util.Log.e("QRCodeDataHelper", "Error creating JSON: " + e.getMessage(), e);
            // Fallback to simple format
            return "BTMS_BOOKING_" + bookingCode;
        }
    }
    
    /**
     * Generate QR code data in human-readable text format
     * 
     * @param bookingCode Booking code
     * @param routeNumber Route number
     * @param fromLocation From location
     * @param toLocation To location
     * @param scheduleDate Schedule date
     * @param departureTime Departure time
     * @param arrivalTime Arrival time
     * @param seatNumbers Seat numbers
     * @param passengerName Passenger name
     * @param totalFare Total fare
     * @return Formatted text string
     */
    public static String generateQRDataText(String bookingCode, int routeNumber,
                                           String fromLocation, String toLocation,
                                           String scheduleDate, String departureTime,
                                           String arrivalTime, String seatNumbers,
                                           String passengerName, double totalFare) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== VÉ XE BUÝT BTMS ===\n");
        sb.append("Mã đặt vé: ").append(bookingCode != null ? bookingCode : "").append("\n");
        sb.append("Tuyến số: ").append(routeNumber).append("\n");
        sb.append("Từ: ").append(fromLocation != null ? fromLocation : "").append("\n");
        sb.append("Đến: ").append(toLocation != null ? toLocation : "").append("\n");
        sb.append("Ngày: ").append(scheduleDate != null ? scheduleDate : "").append("\n");
        sb.append("Giờ khởi hành: ").append(departureTime != null ? departureTime : "").append("\n");
        sb.append("Giờ đến: ").append(arrivalTime != null ? arrivalTime : "").append("\n");
        sb.append("Ghế: ").append(seatNumbers != null ? seatNumbers : "").append("\n");
        sb.append("Hành khách: ").append(passengerName != null ? passengerName : "").append("\n");
        sb.append("Tổng tiền: ").append(CurrencyHelper.formatPrice(totalFare)).append("\n");
        sb.append("====================");
        
        return sb.toString();
    }
}

