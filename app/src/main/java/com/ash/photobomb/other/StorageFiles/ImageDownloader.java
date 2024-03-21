package com.ash.photobomb.other.StorageFiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ash.photobomb.other.CustomDialog.AshDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Void, String> {



    @SuppressLint("StaticFieldLeak")
    private Context context;

    public ImageDownloader(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String imageUrl = params[0];
        try {
            // Open a connection to the URL
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            // Read the data from the URL
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            // Save the image to the "Photobomb" folder in internal storage
            saveImageToGallery(bitmap);

            return "Image downloaded and saved to internal storage";

        } catch (IOException e) {
            return "Error downloading image: " + e.getMessage();
        }
    }

    public void saveImageToGallery(Bitmap bitmapImage) {
        try {
            // Check if the external storage is available
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // Get the public gallery directory
                File galleryDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                // Create a "Photobomb" folder if it doesn't exist
                File photobombDir = new File(galleryDir, "Photobomb");
                if (!photobombDir.exists()) {
                    photobombDir.mkdirs();
                }

                // Create a file to save the image
                File imageFile = new File(photobombDir, "downloaded_image.png");

                // Create a file output stream
                FileOutputStream fos = new FileOutputStream(imageFile);

                // Compress and save the image to the file
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

                // Close the file output stream
                fos.close();

                // Notify the media scanner about the new file
                scanMedia(imageFile);

                Log.d("ImageDownloader", "Image saved to: " + imageFile.getAbsolutePath());
            } else {
                Log.e("ImageDownloader", "External storage not available");
            }

        } catch (IOException e) {
            Log.e("ImageDownloader", "Error saving image: " + e.getMessage());
        }

    }
    private void scanMedia(File file) {
        Uri contentUri = Uri.fromFile(file);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

/*
    private void saveImageToInternalStorage(Bitmap bitmapImage) {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // Get the public gallery directory
                File galleryDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                // Create a "Photobomb" folder if it doesn't exist
                File photobombDir = new File(galleryDir, "Photobomb");
                if (!photobombDir.exists()) {
                    photobombDir.mkdirs();
                }

                // Create a file to save the image
                File imageFile = new File(photobombDir, "downloaded_image.png");

                // Create a file output stream
                FileOutputStream fos = new FileOutputStream(imageFile);

                // Compress and save the image to the file
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

                // Close the file output stream
                fos.close();

                Log.d("ImageDownloader", "Image saved to: " + imageFile.getAbsolutePath());
            } else {
                Log.e("ImageDownloader", "External storage not available");
            }
        } catch (IOException e) {
            Log.e("ImageDownloader", "Error saving image: " + e.getMessage());
        }
    }
*/
}

