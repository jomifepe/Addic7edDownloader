package com.jomifepe.addic7eddownloader.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;

import com.jomifepe.addic7eddownloader.R;

import java.util.Random;

public class NotificationUtil {
    public static final Random RANDOM = new Random();

    public static class SimpleNotification {
        private NotificationCompat.Builder builder;
        private NotificationManager manager;
        private static int id;

        public SimpleNotification(Context context, @StringRes int title, String message) {
            this(context, context.getString(title), message);
        }

        public SimpleNotification(Context context, String title, @StringRes int message) {
            this(context, title, context.getString(message));
        }

        public SimpleNotification(Context context, @StringRes int title, @StringRes int message) {
            this(context, context.getString(title), context.getString(message));
        }

        public SimpleNotification(Context context, String title, String message) {
            this.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            this.builder = new NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true);

            Intent intent = new Intent(context, message.getClass());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(Const.NOTIFICATION_CHANNEL,
                        "Addic7edDownloader notifications",
                        NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
        }

        public int show() {
            manager.notify(id++, builder.build());
            return id;
        }
    }
}
