package com.jomifepe.addic7eddownloader.util;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Network {
    public static class NetworkException extends Exception {}
    public static class ErrorResponseCodeException extends NetworkException {}

    public static class OkHTTPGETRequest extends AsyncTask<String, Void, String> {
        private final OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... targetURL) {
            Request request = new Request.Builder()
                    .url(targetURL[0])
                    .header("User-Agent", Const.USER_AGENT)
                    .build();

            Call call = client.newCall(request);
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public static class FileDownload extends AsyncTask<String, Void, FileDownload.STATUS> {
        public enum STATUS {
            ERROR, SUCCESS
        }

        @Override
        protected FileDownload.STATUS doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", Const.USER_AGENT);
                con.setRequestProperty("Referer", strings[1]);

                int responseCode = con.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK)
                    return STATUS.ERROR;

                String contentDisposition = con.getHeaderField("Content-Disposition");
                String filename = contentDisposition == null ?
                        "" : contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");

                ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());

                String path = Environment.getExternalStorageDirectory() + File.separator + "Download/" + filename;
                FileOutputStream fos = new FileOutputStream(path);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                fos.close();
                rbc.close();
            } catch (IOException e) {
                return STATUS.ERROR;
            }

            return STATUS.SUCCESS;
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

//        @Override
//        protected Void doInBackground(String... strings) {
//
//            if(android.os.Debug.isDebuggerConnected())
//                android.os.Debug.waitForDebugger();
//
//            HttpURLConnection con;
//
//            try {
//                URL targetURL = new URL(strings[0]);
//
//                con = (HttpURLConnection) targetURL.openConnection();
//                con.setRequestMethod("GET");
//                con.setRequestProperty("User-Agent", Const.USER_AGENT);
//                con.setRequestProperty("Referer", strings[1]);
//                con.connect();
//
//                int responseCode = con.getResponseCode();
//                if (responseCode != HttpURLConnection.HTTP_OK)
//                    return null;
//
//                String contentDisposition = con.getHeaderField("Content-Disposition");
//                String contentSplit[] = contentDisposition.split("filename=");
//                String filename = contentSplit[1].replace("filename=", "").replace("\"", "").trim();
//
//                String folder = Environment.getExternalStorageDirectory() + File.separator + "Addic7edDownloader/";
//                File directory = new File(folder);
//
//                if (!directory.exists()) {
//                    directory.mkdirs();
//                }
//
//                BufferedInputStream in = new BufferedInputStream(con.getInputStream(), 8192);
//                FileOutputStream out = new FileOutputStream(String.format("%s%s", folder, filename));
//
//                int count;
//                byte buffer[] = new byte[1024];
//                while ((count = in.read(buffer,0,1024)) != -1) {
//                    out.write(buffer, 0, count);
//                }
//
//                out.flush();
//                out.close();
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//    }

}
