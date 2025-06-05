package com.example.sosapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class VoiceService extends Service {

    public static final String CHANNEL_ID = "VoiceServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Add logic to simulate listening here

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SOS Listening")
                .setContentText("Listening for your SOS phrase...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        // You can simulate background listening logic here (e.g., periodic checks)

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop any background tasks here
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
