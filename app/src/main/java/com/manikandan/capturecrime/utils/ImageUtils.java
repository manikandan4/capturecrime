package com.manikandan.capturecrime.utils;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {
    public static String copyImageToAppStorage(Context context, Uri imageUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        if (inputStream == null) throw new IOException("Unable to open input stream");

        // Use internal storage instead of external files directory for better persistence
        File picturesDir = new File(context.getFilesDir(), "crime_images");
        if (!picturesDir.exists()) {
            if (!picturesDir.mkdirs()) {
                throw new IOException("Failed to create images directory");
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File imageFile = new File(picturesDir, "crime_" + timeStamp + ".jpg");

        FileOutputStream outputStream = new FileOutputStream(imageFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();

        return imageFile.getAbsolutePath();
    }

    public static void deleteImageFile(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                if (!imageFile.delete()) {
                    // Log the failure but don't throw exception as it's not critical
                    android.util.Log.w("ImageUtils", "Failed to delete image file: " + imagePath);
                }
            }
        }
    }
}
