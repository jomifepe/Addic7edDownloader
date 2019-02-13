package com.jomifepe.addic7eddownloader.util;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.ui.BaseFragment;
import com.jomifepe.addic7eddownloader.ui.adapter.MultiOptionListRecyclerAdapter;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;
import com.jomifepe.addic7eddownloader.util.listener.OnCompleteListener;
import com.jomifepe.addic7eddownloader.util.listener.OnFailureListener;
import com.jomifepe.addic7eddownloader.util.listener.OnTaskEndedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Util {
    public static class Log {
        public static final String DEBUG_TAG = "[ADD7DOWNLOADER_DEBUG]: ";

        public static void logD(Activity activity, String message) {
            android.util.Log.d(activity.getClass().getName(), DEBUG_TAG + message);
        }

        public static void logD(Activity activity, @StringRes int stringRes) {
            android.util.Log.d(activity.getClass().getName(), DEBUG_TAG + activity.getString(stringRes));
        }

        public static <F extends BaseFragment> void logD(F fragment, String message) {
            android.util.Log.d(fragment.getClass().getName(), DEBUG_TAG + message);
        }

        public static <F extends BaseFragment> void logD(F fragment, @StringRes int stringRes) {
            android.util.Log.d(fragment.getClass().getName(),
                    DEBUG_TAG + fragment.getViewContext().getString(stringRes));
        }

        public static void logD(Context context, String message) {
            android.util.Log.d(context.getClass().getName(), DEBUG_TAG + message);
        }

        public static void logD(Context context, @StringRes int stringRes) {
            android.util.Log.d(context.getClass().getName(),
                    DEBUG_TAG + context.getString(stringRes));
        }

        public static void logD(String tag, String message) {
            android.util.Log.d(tag,  DEBUG_TAG + message);
        }

        public static void logD(Context context, String tag, @StringRes int stringRes) {
            android.util.Log.d(tag, context.getString(stringRes));
        }
    }

    public static class Message {
        public static void lToast(Context context, @StringRes int message) {
            Toast.makeText(context, context.getString(message), Toast.LENGTH_LONG).show();
        }

        public static void lToast(Context context, String message) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }

        public static void sToast(Context context, @StringRes int message) {
            Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show();
        }

        public static void sToast(Context context, String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static class Date {
        public static java.util.Date getCurrentDate() {
            return Calendar.getInstance().getTime();
        }

        public static String getISO8601FormattedDate() {
            java.util.Date currentDate = getCurrentDate();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return df.format(currentDate);
        }

        public static String getFormattedDate(String format) {
            java.util.Date currentDate = getCurrentDate();
            SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
            return df.format(currentDate);
        }

        public static Long getCurrentTimestamp() {
            return System.currentTimeMillis();
        }
    }

    public static class Dialog {
        private static @StyleRes int getBaseDialogTheme() {
            return android.R.style.Theme_Material_Light_Dialog_NoActionBar;
        }

        public static AlertDialog createOkMessageDialog(Context context, String message) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    new ContextThemeWrapper(context, getBaseDialogTheme()))
                    .setMessage(message)
                    .setPositiveButton(R.string.dialog_action_ok, null);
            return alertDialog.create();
        }

        public static AlertDialog createOkMessageDialog(Context context, @StringRes int message) {
            return createOkMessageDialog(context,
                    context.getString(message));
        }

        public static AlertDialog createOkMessageDialog(Context context, String title, String message) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    new ContextThemeWrapper(context, getBaseDialogTheme()))
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.dialog_action_ok, null);
            return alertDialog.create();
        }

        public static AlertDialog createOkMessageDialog(Context context,
                                                        @StringRes int titleResource,
                                                        @StringRes int messageResource) {
            return createOkMessageDialog(context,
                    context.getString(titleResource),
                    context.getString(messageResource));
        }

        public static AlertDialog createOkMessageDialog(Context context, String title, String message,
                                                        final DialogInterface.OnClickListener callback) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    new ContextThemeWrapper(context, getBaseDialogTheme()))
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.dialog_action_ok, callback);
            return alertDialog.create();
        }

        public static AlertDialog createOkMessageDialog(Context context,
                                                        @StringRes int titleResource,
                                                        @StringRes int messageResource,
                                                        final DialogInterface.OnClickListener callback) {
            return createOkMessageDialog(context, context.getString(titleResource),
                    context.getString(messageResource), callback);
        }

        public static AlertDialog createPositiveNegativeDialog(Context context, String positive,
                                                               String negative, String message,
                                                               final DialogInterface.OnClickListener callback) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    new ContextThemeWrapper(context, getBaseDialogTheme()))
                    .setMessage(message)
                    .setPositiveButton(positive, callback)
                    .setNegativeButton(negative, callback);
            return alertDialog.create();
        }

        public static AlertDialog createPositiveNegativeDialog(Context context, @StringRes int positive,
                                                          @StringRes int negative, @StringRes int message,
                                                          final DialogInterface.OnClickListener callback) {
            return createPositiveNegativeDialog(context, context.getString(positive),
                    context.getString(negative), context.getString(message), callback);
        }

        public static AlertDialog createYesNoDialog(Context context, String message,
                                                    final DialogInterface.OnClickListener callback) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    new ContextThemeWrapper(context, getBaseDialogTheme()))
                    .setMessage(message)
                    .setPositiveButton(context.getText(R.string.dialog_positive_button), callback)
                    .setNegativeButton(context.getText(R.string.dialog_negative_button), callback);
            return alertDialog.create();
        }

        public static AlertDialog createYesNoDialog(Context context,
                                               @StringRes int messageResource,
                                               final DialogInterface.OnClickListener callback) {
            return createYesNoDialog(context, context.getString(messageResource), callback);
        }

        public static AlertDialog createMultiOptionMenu(Context context, String title,
                                                        List<Pair<Integer, String>> options,
                                                        RecyclerViewItemShortClick clickListener) {
            LayoutInflater inflater = LayoutInflater.from(context);
            final View dialogView = inflater.inflate(R.layout.dialog_multi_option_menu, null);

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                    new ContextThemeWrapper(context, getBaseDialogTheme()))
                    .setView(dialogView)
                    .setTitle(title)
                    .setNegativeButton(R.string.dialog_close_button, null);

            final AlertDialog dialog = dialogBuilder.create();
            final RecyclerViewItemShortClick listItemClickListener = (view, position) -> {
                dialog.dismiss();
                clickListener.onItemShortClick(view, position);
            };

            final MultiOptionListRecyclerAdapter listAdapter =
                    new MultiOptionListRecyclerAdapter(listItemClickListener);
            final RecyclerView rvParkingSpots = dialogView.findViewById(R.id.rv_dialog_multi_option);
            rvParkingSpots.setAdapter(listAdapter);
            rvParkingSpots.setLayoutManager(new LinearLayoutManager(context));
            listAdapter.setList(options);

            return dialog;
        }
    }

    public static class Notification {
        private NotificationCompat.Builder builder;
        private NotificationManager manager;
        private static int id;

        public Notification(Context context, @StringRes int title, String message) {
            this(context, context.getString(title), message);
        }

        public Notification(Context context, String title, @StringRes int message) {
            this(context, title, context.getString(message));
        }

        public Notification(Context context, @StringRes int title, @StringRes int message) {
            this(context, context.getString(title), context.getString(message));
        }

        public Notification(Context context, String title, String message) {
            this.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            this.builder = new NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true);

            Intent intent = new Intent(context, message.getClass());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
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

    public static class Async {
        public static class RunnableTask extends AsyncTask<Void, Void, Boolean> {
            private OnFailureListener failureListener;
            private OnCompleteListener completionListener;
            private OnTaskEndedListener taskEndedListener;
            private Runnable runnable;
            private Exception runnableException;

            public RunnableTask(Runnable runnable) {
                this.runnable = runnable;
            }

            /**
             * Sets a listener that's called if the runnable executed successfully
             * @param listener
             * @return RunnableTask
             */
            public RunnableTask addOnCompleteListener(OnCompleteListener listener) {
                this.completionListener = listener;
                return this;
            }

            /**
             * Sets a listener that's called if the runnable throws an exception
             * @param listener
             * @return RunnableTask
             */
            public RunnableTask addOnFailureListener(OnFailureListener listener) {
                this.failureListener = listener;
                return this;
            }

            /**
             * Sets a listener that's always called after the execution or failure
             * @param listener
             * @return RunnableTask
             */
            public RunnableTask addOnTaskEndedListener(OnTaskEndedListener listener) {
                this.taskEndedListener = listener;
                return this;
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    runnableException = e;
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    if (completionListener != null) {
                        completionListener.onComplete();
                    }
                } else {
                    if (failureListener != null && runnableException != null) {
                        failureListener.onFailure(runnableException);
                    }
                }

                if (taskEndedListener != null) {
                    taskEndedListener.onTaskEnded();
                }
            }
        }
    }
}
