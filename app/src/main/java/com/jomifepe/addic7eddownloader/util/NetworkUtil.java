package com.jomifepe.addic7eddownloader.util;

import android.os.AsyncTask;
import android.os.Environment;

import org.jsoup.HttpStatusException;

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
    public interface NetworkTaskCompleteListener {
        void onComplete(String result);
    }

    public interface NetworkTaskFailureListener {
        void onFailure(Exception e);
    }

    public static class OkHTTPGETRequest extends AsyncTask<Void, Void, String> {
        private final OkHttpClient client;
        private NetworkTaskCompleteListener completeListener;
        private NetworkTaskFailureListener failureListener;
        private final String targetURL;

        public OkHTTPGETRequest(String targetURL) {
            this.targetURL = targetURL;
            this.client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
        }

        public OkHTTPGETRequest addOnCompleteListener(NetworkTaskCompleteListener listener) {
            this.completeListener = listener;
            return this;
        }

        public OkHTTPGETRequest addOnFailureListener(NetworkTaskFailureListener listener) {
            this.failureListener = listener;
            return this;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Request request = new Request.Builder()
                    .url(targetURL)
                    .header("User-Agent", Const.USER_AGENT)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new HttpStatusException(response.body().string(), response.code(), targetURL);
                }
                return response.body().string();
            } catch (Exception e) {
                Util.Log.logD(getClass().getName(), "Called for " + e.getClass());
                if (failureListener != null) {
                    failureListener.onFailure(e);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (completeListener != null && result != null) {
                completeListener.onComplete(result);
            } else {
                super.onPostExecute(result);
            }
        }
    }

    public static class FileDownload extends AsyncTask<Void, Void, Void> {
        private NetworkTaskCompleteListener completeListener;
        private NetworkTaskFailureListener failureListener;
        private final String targetURL;
        private final String refererURL;
        private final String savePath;

        public FileDownload(String targetURL, String refererURL, String savePath) {
            this.targetURL = targetURL;
            this.refererURL = refererURL;
            this.savePath = savePath;
        }

        public FileDownload addOnCompleteListener(NetworkTaskCompleteListener listener) {
            this.completeListener = listener;
            return this;
        }

        public FileDownload addOnFailureListener(NetworkTaskFailureListener listener) {
            this.failureListener = listener;
            return this;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(targetURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", Const.USER_AGENT);
                con.setRequestProperty("Referer", refererURL);

                int responseCode = con.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new HttpStatusException("Request failed", HttpURLConnection.HTTP_OK, url.toString());
                }

                String contentDisposition = con.getHeaderField("Content-Disposition");
                String filename = contentDisposition == null ?
                        "" : contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");

                ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());

                String filePath = savePath + File.separator + filename;
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                fos.close();
                rbc.close();

                if (completeListener != null) {
                    completeListener.onComplete(filename);
                }
            } catch (Exception e) {
                Util.Log.logD(getClass().getSimpleName(), "Called for " + e.getClass());
                if (failureListener != null) {
                    failureListener.onFailure(e);
                }
            }

            return null;
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
