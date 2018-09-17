package com.jomifepe.addic7eddownloader.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Locale;

public class ErrorUtil {
    public static class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
        private final String TAG = DefaultExceptionHandler.class.getSimpleName();

        private final Context context;
        private final Thread.UncaughtExceptionHandler rootHandler;

        public DefaultExceptionHandler(Context context) {
            this.context = context;
            this.rootHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            try {
                Log.d(TAG, "called for " + throwable.getClass());
                File filesDir = context.getFilesDir();
                File file = new File(filesDir, Const.File.DEFAULT_ERROR_LOG_FILENAME);
                String timestamp = Long.toString(System.currentTimeMillis() / 1000);
                FileUtils.writeStringToFile(file, String.format(Locale.getDefault(), "\n%s - %s %s",
                        timestamp, throwable.getClass().getSimpleName(), throwable.getLocalizedMessage()));
            } catch (Exception e) {
                Log.e(TAG, "Exception Logger failed!", e);
            }
        }
    }
}

