package com.ash.photobomb.Database.S3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.amazonaws.HttpMethod;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;

import java.io.File;
import java.net.URL;
import java.util.Date;

public class S3ImageUploadManager {

    Context context;
    SharedPreferencesHelper sharedPreferencesHelper;
    private static final String TAG = "S3ImageUploadManager";
    private static final String BUCKET_NAME = "ASH";
    String OBJECT_KEY = "images/"; // Specify a path or folder in your bucket
    File FILE; // actual local path

    URL tempUrl = null;

    public S3ImageUploadManager(Context context){
        this.context = context;
        sharedPreferencesHelper = new SharedPreferencesHelper(context);
    }

    public URL getUploadImageUrl(File file){
        URL url = null;

        GroupInfoModel currentGroup = sharedPreferencesHelper.getCurrentGroup();

        OBJECT_KEY+= currentGroup.getGroup_id()+file.getName();
        FILE = file;
        url = uploadImage();
        return url;
    }

    public URL uploadImage() {
        final URL[] url = {null};
        AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                // Initialization successful
                AmazonS3Client s3Client = new AmazonS3Client(AWSMobileClient.getInstance());

                TransferUtility transferUtility = TransferUtility.builder()
                        .context(context)
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

                File fileToUpload = FILE;

                TransferObserver uploadObserver = transferUtility.upload(
                        BUCKET_NAME,
                        OBJECT_KEY + fileToUpload.getName(), // Use a unique object key based on your requirements
                        fileToUpload
                );

                uploadObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            // Upload completed, now generate and retrieve the presigned URL
                            url[0] = generatePresignedUrl(s3Client, OBJECT_KEY + fileToUpload.getName());
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        // Update progress if needed
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.e(TAG, "Error during upload: " + ex.getMessage());
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });

        return url[0];
    }

    private URL generatePresignedUrl(AmazonS3Client s3Client, String objectKey) {
        tempUrl = null;
        // Run this in a background thread or AsyncTask to avoid NetworkOnMainThreadException
        AsyncTask.execute(() -> {
            Date expiration = new Date(System.currentTimeMillis() + 3600000); // Set expiration time (1 hour in this case)

            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(BUCKET_NAME, objectKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);

            tempUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        });

        return tempUrl;
    }
}