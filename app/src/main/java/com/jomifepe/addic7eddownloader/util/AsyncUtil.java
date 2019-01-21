package com.jomifepe.addic7eddownloader.util;

import android.os.AsyncTask;

import com.jomifepe.addic7eddownloader.util.listener.OnCompleteListener;
import com.jomifepe.addic7eddownloader.util.listener.OnFailureListener;

public class AsyncUtil {
    public static class RunnableAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private OnFailureListener failureListener;
        private OnCompleteListener completionListener;
        private Runnable runnable;

        public RunnableAsyncTask(Runnable runnable) {
            this.runnable = runnable;
        }

        public RunnableAsyncTask addOnCompleteListener(OnCompleteListener listener) {
            this.completionListener = listener;
            return this;
        }

        public RunnableAsyncTask addOnFailureListener(OnFailureListener listener) {
            this.failureListener = listener;
            return this;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                runnable.run();
            } catch (Exception e) {
                if (failureListener != null) {
                    failureListener.onFailure(e);
                }
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success && completionListener != null) {
                completionListener.onComplete();
            }
        }
    }
}
