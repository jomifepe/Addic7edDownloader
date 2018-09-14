package com.jomifepe.addic7eddownloader.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.jomifepe.addic7eddownloader.R;

import java.util.Random;

public class Util {
    public static final Random RANDOM = new Random();

    public static class Notification {
        private NotificationCompat.Builder builder;
        private NotificationManager manager;
        private int id;

        public Notification(Context context, int id, String title, String content) {
            this.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            this.id = id;
            this.builder = new NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true);

            Intent intent = new Intent(context, content.getClass());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(Const.NOTIFICATION_CHANNEL,
                        "Addic7edDownloader notifications",
                        NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
        }

        public void show() {
            manager.notify(id, builder.build());
        }
    }
}
