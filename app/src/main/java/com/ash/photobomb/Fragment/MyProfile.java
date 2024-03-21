package com.ash.photobomb.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.MainActivity;
import com.ash.photobomb.R;
import com.ash.photobomb.databinding.FragmentMyProfileBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfile extends Fragment {

    FragmentMyProfileBinding binding;
    AshDialog progress;
    int replaceLayoutId;
    ImageView navigationButton;
    FirebaseStorage storage;
    FirebaseDatabase database;
    Activity activity;
    NavigationView navigationView;
    SharedPreferencesHelper helper;
    int PICK_IMAGE_REQUEST = 33;
    Uri imageUri = null;

    public MyProfile(NavigationView navigationView, int relativeLayout, ImageView navigationButton, Activity context){
        activity = context;
        replaceLayoutId = relativeLayout;
        this.navigationButton = navigationButton;
        this.navigationView = navigationView;
        helper = new SharedPreferencesHelper(context);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyProfileBinding.inflate(getLayoutInflater(), container, false);


        progress = new AshDialog(activity);
        progress.show();  // Show the progress dialog immediately


        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getContext());
        User user = sharedPreferencesHelper.getCurrentUserData();

        // call request for fetch the data of user profile from the data base by api
        Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi()
                .getUserProfileData();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Gson gson = new Gson();

                JSONObject jsonResponse = ApiSet.getResponseData(response);

                assert jsonResponse != null;
                if (jsonResponse.optString("status").equals("true")) {
                    JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                    if (dataJsonObject != null) {
                        User userprofile = gson.fromJson(dataJsonObject.toString(), User.class);
                        userprofile.setJwt((Objects.requireNonNull(jsonResponse.optJSONObject("data")).optString("jwt")));
                        binding.nameEditText.setText(userprofile.getName());
                        binding.emailIdEditText.setText(userprofile.getEmail());
                        binding.mobileEditText.setText(userprofile.getMobile());
                        if (userprofile.getProfile_picture() != null) {
                            Picasso.get().load(Uri.parse(userprofile.getProfile_picture())).placeholder(R.drawable.user_logo).into(binding.profileImage);
                        }
                        progress.dismiss();  // Dismiss the progress dialog after loading data
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progress.dismiss();  // Dismiss the progress dialog in case of failure
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageUri != null) {
                    progress.show();
                    StorageReference reference = storage.getReference().child("Groups_picture")
                            .child(user.getId()).child(binding.nameEditText.getText().toString().trim());

                    reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child("Group_pictures").child(user.getId()).child(binding.nameEditText.getText().toString().trim()).setValue(uri.toString());

                                    Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi()
                                            .updateUserProfileData(Objects.requireNonNull(binding.nameEditText.getText()).toString().trim(), Objects.requireNonNull(binding.emailIdEditText.getText()).toString().trim(),
                                                    "+91", Objects.requireNonNull(binding.mobileEditText.getText()).toString().trim(), String.valueOf(uri), user.getJwt());

                                    call.enqueue(new Callback<JsonObject>() {
                                        @Override
                                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                                            JSONObject jsonResponse = ApiSet.getResponseData(response);

                                            progress.dismiss();
                                            assert jsonResponse != null;
                                            if (jsonResponse.optString("status").equals("true")) {
                                                Toast.makeText(getContext(), jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                                                assert getFragmentManager() != null;
                                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                transaction.replace(replaceLayoutId ,new BottomFiles(replaceLayoutId, navigationButton, activity));
                                                Objects.requireNonNull(navigationView.getCheckedItem()).setChecked(false);
                                                transaction.commit();

                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                            progress.dismiss();  // Dismiss the progress dialog in case of failure
                                        }
                                    });                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(activity, "Your image is not set perfectly", Toast.LENGTH_SHORT).show();
                }




            }
        });

        binding.picImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                Picasso.get().load(imageUri).into(binding.profileImage);
            }
            else {
                Toast.makeText(activity, "image not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

}