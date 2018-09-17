package com.jomifepe.addic7eddownloader.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.persistence.room.Database;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.Random;

public class Util {
    public static final Random RANDOM = new Random();

    public static class Notification {
        private NotificationCompat.Builder builder;
        private NotificationManager manager;
        private int id;

        public Notification(Context context, int id, @StringRes int titleRes, @StringRes int messageRes) {
            this(context, id, context.getString(titleRes), context.getString(messageRes));
        }

        public Notification(Context context, int id, String title, String message) {
            this.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            this.id = id;
            this.builder = new NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
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

        public void show() {
            manager.notify(id, builder.build());
        }
    }

    public static class RunnableAsyncTask extends AsyncTask<Void, Void, Void> {
        public interface TaskCallback {
            void onError(Exception e);
        }

        private TaskCallback callback;
        private Runnable runnable;
        private Exception ex;

        public RunnableAsyncTask(Runnable runnable) {
            this.runnable = runnable;
        }

        public RunnableAsyncTask(Runnable runnable, TaskCallback callback) {
            this.runnable = runnable;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                runnable.run();
            } catch (Exception ex) {
                this.ex = ex;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (callback != null && ex != null) {
                callback.onError(ex);
            }
        }
    }
}
