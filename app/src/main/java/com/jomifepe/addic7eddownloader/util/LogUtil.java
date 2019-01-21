package com.jomifepe.addic7eddownloader.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;
import android.util.Log;

import com.jomifepe.addic7eddownloader.R;

public class LogUtil {
    public static void logD(Activity activity, String message) {
        Log.d(activity.getClass().getName(), String.format("%s%s",
                activity.getString(R.string.debug_logger_tag), message));
    }

    public static void logD(Activity activity, @StringRes int stringRes) {
        Log.d(activity.getClass().getName(), String.format("%s%s",
                activity.getString(R.string.debug_logger_tag),
                activity.getString(stringRes)));
    }

    public static void logD(Context context, String message) {
        Log.d(((Activity) context).getClass().getName(), String.format("%s%s",
                context.getString(R.string.debug_logger_tag), message));
    }

    public static void logD(Context context, @StringRes int stringRes) {
        Log.d(((Activity) context).getClass().getName(), String.format("%s%s",
                context.getString(R.string.debug_logger_tag),
                context.getString(stringRes)));
    }

    public static void logD(Context context, String tag, String message) {
        Log.d(tag, String.format("%s%s", context.getString(R.string.debug_logger_tag), message));
    }

    public static void logD(Context context, String tag, @StringRes int stringRes) {
        Log.d(tag, String.format("%s%s", context.getString(R.string.debug_logger_tag),
                context.getString(stringRes)));
    }

    public static void logD(String tag, String prefix, String message) {
        Log.d(tag, String.format("%s%s", prefix, message));
    }
}
