package com.example.btms;

import java.util.Random;

public class BookingCodeHelper {
    
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final Random random = new Random();
    
    /**
     * Generate a random booking code with format: 3 letters + 4 numbers + 1 letter
     * Example: ITE1203A, ABC1234Z
     * 
     * @return Random booking code string
     */
    public static String generateBookingCode() {
        StringBuilder code = new StringBuilder();
        
        // First 3 letters
        for (int i = 0; i < 3; i++) {
            code.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }
        
        // 4 numbers
        for (int i = 0; i < 4; i++) {
            code.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        
        // Last letter
        code.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        
        return code.toString();
    }
}

