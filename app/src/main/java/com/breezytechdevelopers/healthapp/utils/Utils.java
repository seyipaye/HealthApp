package com.breezytechdevelopers.healthapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

public class Utils {
    public static Uri getImageUri(Context inContext, Bitmap inImage, String title) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }

    public static Bitmap getScaledBitmap(int targetLength, Bitmap sourceBitmap) {

        // Get the dimensions of the original bitmap
        int originalWidth = sourceBitmap.getWidth();
        int originalHeight = sourceBitmap.getHeight();
        float aspectRatio = (float) originalWidth / originalHeight;

        // Calculate the target dimensions
        int targetWidth, targetHeight;

        if (originalWidth > originalHeight) {
            targetWidth = targetLength;
            targetHeight = Math.round(targetWidth / aspectRatio);
        } else {
            aspectRatio = 1 / aspectRatio;
            targetHeight = targetLength;
            targetWidth = Math.round(targetHeight / aspectRatio);
        }
        return Bitmap.createScaledBitmap(sourceBitmap, targetWidth, targetHeight, true);
    }
}
