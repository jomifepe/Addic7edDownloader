package com.jomifepe.addic7eddownloader.util;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtil {
    public interface NetworkTaskCallback {
        void onTaskCompleted(String result);
        void onTaskFailed(String result);
    }

    public static class OkHTTPGETRequest extends AsyncTask<String, Void, Pair<Boolean, String>> {
        private final OkHttpClient client;
        private NetworkTaskCallback callback;

        public OkHTTPGETRequest() {
            this.client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
        }

        public OkHTTPGETRequest(NetworkTaskCallback callback) {
            this();
            this.callback = callback;
        }

        @Override
        protected Pair<Boolean, String> doInBackground(String... targetURL) {
            Request request = new Request.Builder()
                    .url(targetURL[0])
                    .header("User-Agent", Const.USER_AGENT)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new Pair<>(false, "");
                }

                return new Pair<>(true, response.body().string());
            } catch (IOException e) {
                Log.d(getClass().getSimpleName(), "called for " + e.getClass());
                return new Pair<>(false, "");
            }
        }

        @Override
        protected void onPostExecute(Pair<Boolean, String> result) {
            if (callback != null) {
                if (result.first /* task successfull */) {
                    callback.onTaskCompleted(result.second);
                } else {
                    callback.onTaskFailed(result.second);
                }
            } else {
                super.onPostExecute(result);
            }
        }
    }

    public static class FileDownload extends AsyncTask<String, Void, Boolean> {
        private NetworkTaskCallback callback;
        private String result;

        public FileDownload() {}

        public FileDownload(NetworkTaskCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String filename;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", Const.USER_AGENT);
                con.setRequestProperty("Referer", strings[1]);

                int responseCode = con.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK)
                    return false;

                String contentDisposition = con.getHeaderField("Content-Disposition");
                result = filename = contentDisposition == null ?
                        "" : contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");

                ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());

                String path = Environment.getExternalStorageDirectory() + File.separator + "Download/" + filename;
                FileOutputStream fos = new FileOutputStream(path);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                fos.close();
                rbc.close();
            } catch (IOException e) {
                Log.d(getClass().getSimpleName(), "called for " + e.getClass());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                if (success) {
                    callback.onTaskCompleted(result);
                } else {
                    callback.onTaskFailed(result);
                }
            } else {
                super.onPostExecute(success);
            }
        }
    }

    public static class RequestContentDisposition extends AsyncTask<String, Void, RequestContentDisposition.Status> {

        public static class Status {
            public enum Code {
                OK, ERR_CONNECTION, ERR_RESPONSE_CODE, ERR_NO_FIELD
            }

            Code responseCode;
            String message;

            public Status(Code responseCode, String message) {
                this.responseCode = responseCode;
                this.message = message;
            }
        }

        @Override
        protected RequestContentDisposition.Status doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", Const.USER_AGENT);
                con.setRequestProperty("Referer", strings[1]);

                int responseCode = con.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return new Status(Status.Code.ERR_RESPONSE_CODE, String.format(Locale.getDefault(),
                            "The server responded with the code %d", responseCode));
                }

                String contentDisposition = con.getHeaderField("Content-Disposition");
                if (contentDisposition == null) {
                    return new Status(Status.Code.ERR_NO_FIELD, "No Content Disposition field was found on the response headers");
                }

                return new Status(Status.Code.OK, contentDisposition);
            } catch (IOException e) {
                return new Status(Status.Code.ERR_CONNECTION, "Failed to establish a connection with the server");
            }
        }
    }
}
