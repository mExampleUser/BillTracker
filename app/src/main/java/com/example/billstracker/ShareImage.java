package com.example.billstracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;

import java.io.IOException;
import java.io.OutputStream;

public class ShareImage {

    public void shareImage (Context context, View view, String message) {

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        Intent share1 = new Intent(Intent.ACTION_SEND);
        share1.setType("image/png");
        ContentValues contentValues = new ContentValues();
        String path = System.currentTimeMillis() + ".png";
        contentValues.put(MediaStore.Images.Media.TITLE, path);
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, path);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/images/album");
        }
        Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            OutputStream fos = context.getContentResolver().openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        share1.putExtra(Intent.EXTRA_STREAM, imageUri);
        share1.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(share1, "Share Badge"));
    }
}
