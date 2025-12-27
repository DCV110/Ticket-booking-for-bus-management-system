package com.example.btms;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyHelper {
    
    /**
     * Format price from base value to Vietnamese currency format with thousands separator
     * Example: 50 -> "50.000 VNĐ", 1500 -> "1.500.000 VNĐ"
     * 
     * @param price The base price value
     * @return Formatted price string with thousands separator and "VNĐ" suffix
     */
    public static String formatPrice(double price) {
        // Multiply by 1000 to convert to actual VND amount
        double vndAmount = price * 1000;
        
        // Create DecimalFormat with Vietnamese locale (uses dot as thousands separator)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        formatter.setGroupingSize(3);
        
        return formatter.format(vndAmount) + " VNĐ";
    }
    
    /**
     * Format price without "VNĐ" suffix (just the number with thousands separator)
     * Example: 50 -> "50.000", 1500 -> "1.500.000"
     * 
     * @param price The base price value
     * @return Formatted price string with thousands separator only
     */
    public static String formatPriceNumber(double price) {
        // Multiply by 1000 to convert to actual VND amount
        double vndAmount = price * 1000;
        
        // Create DecimalFormat with Vietnamese locale (uses dot as thousands separator)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        formatter.setGroupingSize(3);
        
        return formatter.format(vndAmount);
    }
    
    /**
     * Format VND amount directly (without multiplying by 1000)
     * Used for wallet balance and transactions where amount is already in VND
     * Example: 500 -> "500 VNĐ", 500000 -> "500.000 VNĐ"
     * 
     * @param amount The VND amount (already in VND, not multiplied)
     * @return Formatted VND string with thousands separator and "VNĐ" suffix
     */
    public static String formatVND(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        formatter.setGroupingSize(3);
        
        return formatter.format(amount) + " VNĐ";
    }
    
    /**
     * Format VND amount without "VNĐ" suffix
     * Example: 500 -> "500", 500000 -> "500.000"
     * 
     * @param amount The VND amount (already in VND, not multiplied)
     * @return Formatted VND string with thousands separator only
     */
    public static String formatVNDNumber(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        formatter.setGroupingSize(3);
        
        return formatter.format(amount);
    }
}

