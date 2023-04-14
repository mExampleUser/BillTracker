package com.example.billstracker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Build notification based on Intent
        Context mContext = context.getApplicationContext();
        Bundle extras = intent.getExtras();
        String title = extras.getString("title", "Title");
        String message = extras.getString("message", "Message To Display");
        Intent intent1 = new Intent(mContext, Logon.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        int code = extras.getInt("notification id");
        String channelId = extras.getString("channel id");
        PendingIntent pi = PendingIntent.getActivity(mContext, code, intent1, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.billsapp_round)
                .setContentTitle(title)
                .setContentText(message).setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true);
        // Show notification
        NotificationManagerCompat manager = NotificationManagerCompat.from(mContext);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.notify(code, notification.build());
    }
}
