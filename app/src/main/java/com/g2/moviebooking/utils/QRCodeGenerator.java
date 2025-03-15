package com.g2.moviebooking.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator {
    
    private static final String TAG = "QRCodeGenerator";
    
    /**
     * Generate a QR code bitmap from the given content
     * 
     * @param content The content to encode in the QR code
     * @param width The width of the QR code
     * @param height The height of the QR code
     * @return A bitmap containing the QR code, or null if generation failed
     */
    public static Bitmap generateQRCode(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            Log.e(TAG, "Error generating QR code: " + e.getMessage());
            return null;
        }
    }
}