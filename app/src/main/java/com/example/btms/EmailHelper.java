package com.example.btms;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EmailHelper {
    
    /**
     * Send booking confirmation email with QR code attachment
     * 
     * @param context Application context
     * @param recipientEmail Recipient email address
     * @param bookingCode Booking code (e.g., ITE1203A)
     * @param qrBitmap QR code bitmap to attach
     * @param bookingDetails Booking details text
     */
    public static void sendBookingEmail(Context context, String recipientEmail, 
                                       String bookingCode, Bitmap qrBitmap, 
                                       String bookingDetails) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Xác nhận đặt vé xe buýt - Mã: " + bookingCode);
            
            String emailBody = "Xin chào,\n\n" +
                    "Cảm ơn bạn đã đặt vé xe buýt qua hệ thống BTMS.\n\n" +
                    "Mã đặt vé của bạn: " + bookingCode + "\n\n" +
                    bookingDetails + "\n\n" +
                    "Mã QR code đã được đính kèm trong email này.\n\n" +
                    "Trân trọng,\n" +
                    "Đội ngũ BTMS";
            
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
            
            // Save QR code to temporary file and attach
            if (qrBitmap != null) {
                File qrFile = saveBitmapToFile(context, qrBitmap, "qr_code_" + bookingCode + ".png");
                if (qrFile != null && qrFile.exists()) {
                    try {
                        // Use FileProvider to get safe URI (content:// instead of file://)
                        // This prevents FileUriExposedException on Android 7.0+
                        Uri qrUri = FileProvider.getUriForFile(
                                context,
                                context.getPackageName() + ".provider", // Must match authorities in AndroidManifest.xml
                                qrFile
                        );
                        
                        emailIntent.putExtra(Intent.EXTRA_STREAM, qrUri);
                        // IMPORTANT: Grant read permission to the receiving app (Gmail, Outlook, etc.)
                        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception e) {
                        Log.e("EmailHelper", "Error attaching QR code: " + e.getMessage(), e);
                    }
                }
            }
            
            try {
                context.startActivity(Intent.createChooser(emailIntent, "Gửi email xác nhận"));
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e("EmailHelper", "No email app found: " + ex.getMessage(), ex);
            }
        } catch (Exception e) {
            Log.e("EmailHelper", "Error sending email: " + e.getMessage(), e);
        }
    }
    
    /**
     * Save bitmap to temporary file
     */
    private static File saveBitmapToFile(Context context, Bitmap bitmap, String filename) {
        try {
            File cacheDir = context.getCacheDir();
            File imageFile = new File(cacheDir, filename);
            
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            
            return imageFile;
        } catch (IOException e) {
            Log.e("EmailHelper", "Error saving bitmap: " + e.getMessage(), e);
            return null;
        }
    }
}

