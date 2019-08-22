package me.android.awm.musicianapp.net;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


public class HttpManager extends AsyncTask<Void, Void, Void> {
    private final String boundary;
    private String uri;
    private Context ctx;
    private static final String LINE_FEED = "\r\n";
    private int respCode;
    private boolean allOk;
    private HttpManagerCallback callback;
    private String json;
    private JSONObject jsonObject;
    private String image;
    private String result;
    private String payloadAsString;
    private String charset = "UTF-8";
    private OutputStream outputStream;
    private PrintWriter writer;



    public enum OPERATION_TYPE {
        OPERATION_TYPE_GET(0),
        OPERATION_TYPE_POST(1),
        OPERATION_TYPE_MULTIPART(2);
        private final int value;

        OPERATION_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private OPERATION_TYPE operationType;

    public HttpManager(Context ctx, String uri, JSONObject payload,
                       OPERATION_TYPE operationType, HttpManagerCallback callback) throws JSONException {
        this.ctx = ctx;
        this.uri = uri;
        this.boundary = "===" + System.currentTimeMillis() + "===";
        this.jsonObject = payload;

        if (payload != null) {
            /*
            if(!payload.has("app")) {
                payload.put("app_id", ctx.getResources().getInteger(R.integer.app_id));
            }*/
            this.payloadAsString = payload.toString();
            //System.out.println(payloadAsString);
        }
        this.callback = callback;
        this.operationType = operationType;
    }

    public HttpManager(Context ctx, String uri, Object dummyParam, String payload,
                       OPERATION_TYPE operationType,HttpManagerCallback callback) throws JSONException {
        this.ctx = ctx;
        this.uri = uri;
        this.payloadAsString = payload;
        this.callback = callback;
        this.operationType = operationType;
        boundary = "===" + System.currentTimeMillis() + "===";

    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection httpUrlConnection = null;
        try {
            URL url = new URL(uri);
            httpUrlConnection = (HttpURLConnection) url.openConnection();

            switch (operationType) {
                case OPERATION_TYPE_GET: {
                    httpUrlConnection.setRequestProperty("Content-Type", "application/json");
                    //System.out.println("Inviata Richiesta" );
                    result = manageGet(httpUrlConnection);
                    break;
                }
                case OPERATION_TYPE_POST: {
                    httpUrlConnection.setRequestProperty("Content-Type", "application/json");
                    //System.out.println("Inviato: " + payloadAsString);
                    result = managePost(httpUrlConnection, payloadAsString);
                    break;
                }
                case OPERATION_TYPE_MULTIPART:{
                    httpUrlConnection.setRequestProperty("Content-Type",
                            "multipart/form-data; boundary=" + boundary);
                    result = manageMultipart(httpUrlConnection, payloadAsString);
                    break;
                }

            }
            allOk = true;
        } catch (Exception exc) {
            exc.printStackTrace();
            allOk = false;
        } finally {
            //System.out.println("stato ritorno " + result);
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Void v) {
        if (callback != null) {
            try {
                callback.httpManagerCallbackResult(result, allOk);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public interface HttpManagerCallback {
        void httpManagerCallbackResult(String response, boolean esito) throws JSONException;

    }

    private String managePost(HttpURLConnection httpUrlConnection, String payload) throws IOException {
        //httpUrlConnection.setRequestProperty("X-CSRFToken", csrf);

        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);

        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.connect();
        OutputStream out = new BufferedOutputStream(httpUrlConnection.getOutputStream());
        if (payload != null) {
            out.write(payload.getBytes());
        }
        out.close();

        int status = httpUrlConnection.getResponseCode();

        if(status > 399 && status < 600) {
            InputStream error = httpUrlConnection.getErrorStream();
            String result = readFully(error);
            error.close();
            System.out.println("Questo il errore: " + result);
            return result;
        }

        /*
        //COOKIES
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.getCookies();
        int cookieIdx = 0;

        for (HttpCookie ck: cookies) {
            System.out.println("------------------ Cookie." + ++cookieIdx + " ------------------");
            System.out.println("Cookie name: " + ck.getName());
            System.out.println("Domain: " + ck.getDomain());
            System.out.println("Max age: " + ck.getMaxAge());
            System.out.println("Server path: " + ck.getPath());
            System.out.println("Is secured: " + ck.getSecure());
            System.out.println("Cookie value: " + ck.getValue());
            System.out.println("Cookie protocol version: " + ck.getVersion());
        }
        //COOKIES
        */

        InputStream in = new BufferedInputStream(httpUrlConnection.getInputStream());
        String result = readFully(in);
        in.close();
        //System.out.println("Questo il risultato: " + result);

        return result;
    }


    private String manageMultipart(HttpURLConnection httpUrlConnection, String payload)throws IOException{
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setUseCaches(false);
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.connect();
        outputStream = httpUrlConnection.getOutputStream();
        try {
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                    true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Iterator iterator = jsonObject.keys();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            try {
                String obj = jsonObject.getString(key);
                if(key.equals("img") && !obj.equals("")) {
                    File imgUri = new File(obj);
                    this.addFilePart(key, imgUri);
                }
                else{
                    this.addFormField(key,obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        // checks server's status code first
        int status = httpUrlConnection.getResponseCode();

        if(status > 399 && status < 600) {
            InputStream error = httpUrlConnection.getErrorStream();
            String result = readFully(error);
            error.close();
            System.out.println("Questo il errore: " + result);
            return result;
        }

        InputStream in = new BufferedInputStream(httpUrlConnection.getInputStream());
        String result = readFully(in);
        in.close();
        //System.out.println("Questo il risultato: " + result);
        return result;
    }


    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append(LINE_FEED);
        writer.flush();
    }

    //http://developer.android.com/training/basics/network-ops/connecting.html
    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.

    private String manageGet(HttpURLConnection conn) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //System.out.println("connesso!");
            // Starts the query
            conn.connect();

            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readFully(is);
            //System.out.println(contentAsString);

            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    public static final int IO_BUFFER_SIZE = 8 * 1024;

    public static final String readFully(final InputStream pInputStream) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final Scanner sc = new Scanner(pInputStream);
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
        }
        return sb.toString();
    }
}
