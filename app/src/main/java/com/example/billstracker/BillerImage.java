package com.example.billstracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BillerImage {

    public String storeImage (Context context, Drawable drawable, String billerName, boolean custom) throws IOException {

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        File dir = new File(context.getFilesDir(),"billerImages");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File outFile;
        if (custom) {
            outFile = new File(dir, billerName + "custom.png");
        }
        else {
            outFile = new File(dir, billerName + ".png");
        }
        FileOutputStream outStream = new FileOutputStream(outFile);
        outStream.write(bytes.toByteArray());

        outStream.flush();
        outStream.close();

        return outFile.getAbsolutePath();
    }
}
