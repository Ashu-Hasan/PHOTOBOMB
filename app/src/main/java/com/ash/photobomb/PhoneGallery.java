package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;
import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Adapter.PhoneGalleryImageAdapter;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.S3.S3ImageUploadManager;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.ActivityPhoneGalleryBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.ash.photobomb.other.StorageFiles.Constant;
import com.ash.photobomb.other.StorageFiles.Method;
import com.ash.photobomb.other.StorageFiles.StorageUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneGallery extends AppCompatActivity {

    ActivityPhoneGalleryBinding binding;

    AmazonS3Client client;
    FirebaseStorage storage;
    FirebaseDatabase database;
    SharedPreferencesHelper helper;
    AshDialog progressDialog;

    ArrayList<Integer> selectedData;
    boolean toCloseMaxCheckBoxValues = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneGalleryBinding.inflate(getLayoutInflater());
        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));
        setContentView(binding.getRoot());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        helper = new SharedPreferencesHelper(PhoneGallery.this);

        progressDialog = new AshDialog(PhoneGallery.this, "Saving images", "We are saving your images, Please wait");

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        binding.imageGroupRecycler.setLayoutManager(layoutManager);

        //if you face lack in scrolling then add following lines
        binding.imageGroupRecycler.setHasFixedSize(true);
        binding.imageGroupRecycler.setItemViewCacheSize(20);
        binding.imageGroupRecycler.setDrawingCacheEnabled(true);
        binding.imageGroupRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.imageGroupRecycler.setNestedScrollingEnabled(false);

        adapter(false);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        binding.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedData = PhoneGalleryImageAdapter.selectedItem;

                if (selectedData.size()>0) {
                    progressDialog.show();

                    int loop = 0;
                    for (int value : selectedData) {
                        loop += 1;
                        File imageFile = Constant.allMediaList.get(value);
                        if (!ApiSet.Connected(PhoneGallery.this)) {
                            Toast.makeText(getApplicationContext(), "Please connect your internet", Toast.LENGTH_SHORT).show();
                        } else {
                            GroupInfoModel groupInfoModel = helper.getCurrentGroup();
                            User user = helper.getCurrentUserData();

                            Uri imageUri = Uri.fromFile(imageFile);

                            StorageReference reference = storage.getReference().child("Group_pictures")
                                    .child(groupInfoModel.getGroup_id()).child(user.getId()).child(imageFile.getName());

                            int finalLoop = loop;
                            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            ArrayList<String> selectedImageLinks = new ArrayList<>();
                                            database.getReference().child("Group_pictures").child(groupInfoModel.getGroup_id()).child(imageFile.getName().replace(".", "")).child("uri").setValue(uri.toString());
                                            database.getReference().child("Group_pictures").child(groupInfoModel.getGroup_id()).child(imageFile.getName().replace(".", "")).child("ref").setValue(imageUri.toString());
                                            selectedImageLinks.add(String.valueOf(uri));
                                            String resultString = "[" + String.join(", ", selectedImageLinks.stream().map(s -> "\"" + s + "\"").toArray(String[]::new)) + "]";


                                            Call<JsonObject> call = ApiController.getInstance(getApplicationContext()).getapi()
                                                    .addGroupMediaFile(helper.getCurrentGroup().getGroup_id(), resultString);


                                            call.enqueue(new Callback<JsonObject>() {
                                                @Override
                                                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                                    JSONObject jsonResponse = ApiSet.getResponseData(response);

                                                    if (jsonResponse.optString("status").equals("true")) {
                                                        if (finalLoop == selectedData.size()) {
                                                            progressDialog.dismiss();
                                                            back();
                                                        }
                                                    } else {
                                                        progressDialog.dismiss();
                                                        back();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<JsonObject> call, Throwable t) {

                                                }
                                            });

                                        }
                                    });
                                }
                            });

                        }
                    }
                }
                else {
                    Toast.makeText(PhoneGallery.this, "Please Select Your image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!toCloseMaxCheckBoxValues){
                    toCloseMaxCheckBoxValues = true;
                    adapter(true);
                }
                else {
                    toCloseMaxCheckBoxValues = false;
                    adapter(false);
                }
            }
        });

//        binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(binding.checkBox.isChecked()){
//                    adapter(true);
//                }
//                else {adapter(false);}
//            }
//        });



    }

    private void adapter(boolean selectMax) {
        PhoneGalleryImageAdapter adapter = new PhoneGalleryImageAdapter(PhoneGallery.this, binding.checkBox, selectMax);
        binding.imageGroupRecycler.setAdapter(adapter);
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        Intent back = new Intent(getApplicationContext(), GroupItemInfoPage.class);
        finish();
        startActivity(back);
    }

}