package me.android.awm.musicianapp.net;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import me.android.awm.musicianapp.MainApplication;

import static android.content.Context.MODE_PRIVATE;

public class DownloadTask extends AsyncTask<Void,Void, Bitmap> {
    private String uri;
    private Context ctx;
    private DownloadTaskCallback callback;
    private String name_file;
    private Uri imageInternalUri;


    public DownloadTask(Context ctx, String uri, DownloadTaskCallback callback,
                        String name_file){
        this.ctx = ctx;
        this.uri = uri;
        this.callback = callback;
        this.name_file = name_file;
    }


    // Before the tasks execution
    protected void onPreExecute(){

    }

    // Do the task in background/non UI thread
    protected Bitmap doInBackground(Void... params){
        HttpURLConnection connection = null;

        try{
            URL url = new URL(uri);
            // Initialize a new http url connection
            connection = (HttpURLConnection) url.openConnection();

            // Connect the http url connection
            connection.connect();

            // Get the input stream from http url connection
            InputStream inputStream = connection.getInputStream();

            /*
                BufferedInputStream
                    A BufferedInputStream adds functionality to another input stream-namely,
                    the ability to buffer the input and to support the mark and reset methods.
            */
            /*
                BufferedInputStream(InputStream in)
                    Creates a BufferedInputStream and saves its argument,
                    the input stream in, for later use.
            */
            // Initialize a new BufferedInputStream from InputStream
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            /*
                decodeStream
                    Bitmap decodeStream (InputStream is)
                        Decode an input stream into a bitmap. If the input stream is null, or
                        cannot be used to decode a bitmap, the function returns null. The stream's
                        position will be where ever it was after the encoded data was read.

                    Parameters
                        is InputStream : The input stream that holds the raw data
                                          to be decoded into a bitmap.
                    Returns
                        Bitmap : The decoded bitmap, or null if the image data could not be decoded.
            */
            // Convert BufferedInputStream to Bitmap object
            Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

            // Return the downloaded bitmap
            return bmp;
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            // Disconnect the http url connection
            connection.disconnect();
        }
        return null;
    }

    // When all async task done
    protected void onPostExecute(Bitmap result){
        // Hide the progress dialog

        if(result!=null){
            // Display the downloaded image into ImageView
            //mImageView.setImageBitmap(result);

            // Save bitmap to internal storage
            imageInternalUri = saveImageToInternalStorage(result);
            if (callback != null) {
                try {
                    callback.downloadTaskCallbackResult(imageInternalUri );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Set the ImageView image from internal storage
            //mImageViewInternal.setImageURI(imageInternalUri);
        }
    }


    // Custom method to convert string to url
    protected URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    // Custom method to save a bitmap into internal storage
    protected Uri saveImageToInternalStorage(Bitmap bitmap){
        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(ctx);

        // Initializing a new file
        // The bellow line return a directory in internal storage
        File file = wrapper.getDir("Images",MODE_PRIVATE);

        // Create a file to save the image
        file = new File(file, name_file+".jpg");

        try{
            // Initialize a new OutputStream
            OutputStream stream = null;

            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(file);

            // Compress the bitmap
            bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, stream);

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();



        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());

        // Return the saved image Uri
        return savedImageURI;
    }

    public interface DownloadTaskCallback {
        void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException;

    }
}
