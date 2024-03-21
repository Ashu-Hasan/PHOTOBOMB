package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.API_Model_Classes.MediaDataModel;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.Fragment.AboutUs;
import com.ash.photobomb.Fragment.AccountSettings;
import com.ash.photobomb.Fragment.BottomFiles;
import com.ash.photobomb.Fragment.ContactUs;
import com.ash.photobomb.Fragment.MyProfile;
import com.ash.photobomb.Fragment.Notifications;
import com.ash.photobomb.Fragment.PrivacyPolicy;
import com.ash.photobomb.Fragment.TermsConditions;
import com.ash.photobomb.databinding.ActivityMainBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.ash.photobomb.other.StorageFiles.Method;
import com.ash.photobomb.other.StorageFiles.StorageUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //implements NavigationView.OnNavigationItemSelectedListener
    ActivityMainBinding binding;

    AppBarConfiguration configuration;

    SharedPreferencesHelper helper;
    AshDialog dialog;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    int REQUEST_CODE_PERMISSION = 2731;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));

        setContentView(binding.getRoot());
        dialog = new AshDialog(MainActivity.this);

        // for google logout
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);

        helper = new SharedPreferencesHelper(MainActivity.this);
        helper.setConditionToSwitchLayout(0);


        Intent data = getIntent();

        // Inside your MainActivity's onCreate method or another suitable place
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Gson gson = new Gson();
                        // Handle the deep link data here
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String isLink = deepLink.getQueryParameter("link");
                            if (isLink != null && isLink.equals("yes")) {
                                dialog.show();
                                if (deepLink.getQueryParameter("request") != null && Objects.equals(deepLink.getQueryParameter("request"), "groupRequest")) {
                                    Call<JsonObject> call = ApiController.getInstance(MainActivity.this).getapi().
                                            sendGroupJoinRequest(deepLink.getQueryParameter("userTd"), "1", deepLink.getQueryParameter("groupTd"),
                                                    deepLink.getQueryParameter("qrKey"), helper.getCurrentUserData().getId());

                                    call.enqueue(new Callback<JsonObject>() {
                                        @Override
                                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                            JSONObject jsonResponse = ApiSet.getResponseData(response);
                                            if (jsonResponse.optString("status").equals("true") || jsonResponse.optString("status").equals("false") &&
                                                    jsonResponse.optString("message").toLowerCase(Locale.ROOT).
                                                            equalsIgnoreCase("Already requested".toLowerCase(Locale.ROOT))) {
                                                dialog.dismiss();
                                                // Code To replace or set the HomeFragment
                                                replaceFragment(new BottomFiles(R.id.relativeLayout, binding.navigationButton, MainActivity.this, "Pending"));
                                            } else {
                                                if (jsonResponse.optString("message").toLowerCase(Locale.ROOT).
                                                        equalsIgnoreCase("Already member of that group".toLowerCase(Locale.ROOT))) {

                                                    Call<JsonObject> groupDetail = ApiController.getInstance(MainActivity.this).getapi()
                                                            .getGroupDetail(deepLink.getQueryParameter("groupTd"));

                                                    groupDetail.enqueue(new Callback<JsonObject>() {
                                                        @Override
                                                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                            dialog.dismiss();
                                                            JSONObject jsonResponse = ApiSet.getResponseData(response);

                                                            if (jsonResponse.optString("status").equals("true")) {
                                                                JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                                                                GroupInfoModel model = gson.fromJson(Objects.requireNonNull(dataJsonObject).toString(), GroupInfoModel.class);
                                                                model.setGroup_id(model.getId());
                                                                helper.setCurrentGroup(model, true);

                                                                Intent groupInfo = new Intent(getApplicationContext(), GroupItemInfoPage.class);
                                                                finish();
                                                                startActivity(groupInfo);
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<JsonObject> call, Throwable t) {

                                                        }
                                                    });
                                                } else {
                                                    dialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "The Link has been expire", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<JsonObject> call, Throwable t) {
                                            dialog.dismiss();
                                        }
                                    });
                                } else {
                                    String groupIdToOpen = deepLink.getQueryParameter("groupTd");
                                    String mediaIdToOpen = deepLink.getQueryParameter("mediaId");
                                    String mediaUrlToOpen = deepLink.getQueryParameter("mediaUrl");

                                    // Use the parameters as needed
                                    if (groupIdToOpen != null && mediaIdToOpen != null) {
                                        Call<JsonObject> call = ApiController.getInstance(MainActivity.this).getapi().getGroups();

                                        call.enqueue(new Callback<JsonObject>() {
                                            @Override
                                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                                JSONObject jsonResponse = ApiSet.getResponseData(response);
                                                Gson gson = new Gson();
                                                if (jsonResponse != null) {
                                                    if (jsonResponse.optString("status").equals("true")) {
                                                        JSONArray dataJsonObject = null;
                                                        try {
                                                            dataJsonObject = jsonResponse.getJSONArray("data");
                                                        } catch (JSONException ignored) {
                                                            dialog.dismiss();
                                                        }
                                                        boolean groupExist = false;
                                                        for (int index = 0; index <= Objects.requireNonNull(dataJsonObject).length(); index++) {
                                                            try {
                                                                JSONObject group = dataJsonObject.getJSONObject(index);
                                                                GroupInfoModel groupInfoModel = gson.fromJson(group.toString(), GroupInfoModel.class);
                                                                if (groupIdToOpen.equals(groupInfoModel.getGroup_id())) {
                                                                    groupExist = true;
                                                                    Intent viewImage = new Intent(getApplicationContext(), ViewImage.class);
                                                                    MediaDataModel media = new MediaDataModel(mediaUrlToOpen, mediaIdToOpen);
                                                                    helper.setCurrentMedia(media);
                                                                    helper.setCurrentGroup(groupInfoModel, false);
                                                                    finish();
                                                                    startActivity(viewImage);
                                                                }
                                                            } catch (JSONException e) {
                                                                dialog.dismiss();

                                                            }
                                                        }
                                                        dialog.dismiss();
                                                        // to redirect requests
                                                        if (!groupExist) {

                                                        }

                                                    }
                                                } else {
                                                    dialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            } else {
                                dialog.dismiss();
                            }
                        } else {
                            dialog.dismiss();
                        }

                    } else {
                        if (data != null) {
                            if (data.getStringExtra("layout") != null && Objects.equals(data.getStringExtra("layout"), "Notification")) {
                                binding.navigationButton.setVisibility(View.GONE);
                                replaceFragment(new Notifications(R.id.relativeLayout, binding.navigationButton, MainActivity.this));
                            } else {
                                // Code To replace or set the HomeFragment
                                replaceFragment(new BottomFiles(R.id.relativeLayout, binding.navigationButton, MainActivity.this));
                            }
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Handle errors
                    Toast.makeText(MainActivity.this, "Error handling Dynamic Link: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


        binding.navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
                /*if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                else {// to removing  StatusBar
                    binding.drawerLayout.openDrawer(GravityCompat.START);}*/
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.myProfile) {
                    replaceFragment(new MyProfile(binding.navigationView, R.id.relativeLayout, binding.navigationButton, MainActivity.this));
                } else if (item.getItemId() == R.id.accountSettings) {
                    replaceFragment(new AccountSettings());
                } else if (item.getItemId() == R.id.manageStorage) {
                    Intent mStorage = new Intent(getApplicationContext(), ManageStorage.class);
                    finish();
                    startActivity(mStorage);
                } else if (item.getItemId() == R.id.aboutUs) {
                    replaceFragment(new AboutUs());
                } else if (item.getItemId() == R.id.TermsConditions) {
                    replaceFragment(new TermsConditions());
                } else if (item.getItemId() == R.id.privacyPolicy) {
                    replaceFragment(new PrivacyPolicy());
                } else if (item.getItemId() == R.id.contactUs) {
                    replaceFragment(new ContactUs());
                } else if (item.getItemId() == R.id.shareApp) {
                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("text/plain");
                    String body = "THIS IS PHOTO ALBUM APPLICATION....IT WILL EASILY AVAILABLE ON PLAY STORE ." +
                            "AND IF U WANT TO DOWNLOAD GO THROUGH LINK https://play.google.com/store/apps/details?id=com.ash.photobomb";
                    String sub = "Your Subject";
                    myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                    myIntent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(myIntent, "Share Using"));
                } else if (item.getItemId() == R.id.logOut) {
                    String isSocial = helper.getIsSocial();
                    if (isSocial.equals("1")){
                        googleSignInClient.signOut();
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("is_user_exist", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean("condition", false).apply();
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    finish();
                    startActivity(intent);
                }

                return true;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        helper.setConditionToSwitchLayout(0);
        checkStorageAccessPermission();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        } else {
            // Permission is already granted
            // Perform your image loading logic here
            new LoadDataInBackground().execute();
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayout, fragment);
        transaction.commit();
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        helper.setConditionToSwitchLayout(1);
    }


    @Override
    public void onBackPressed() {
        int condition = helper.getConditionToSwitchLayout();
        if (condition == 1 || binding.navigationButton.getVisibility() == View.GONE) {
            replaceFragment(new BottomFiles(R.id.relativeLayout, binding.navigationButton, MainActivity.this));
            if (binding.navigationView.getCheckedItem() != null) {
                Objects.requireNonNull(binding.navigationView.getCheckedItem()).setChecked(false);
            }
            helper.setConditionToSwitchLayout(0);
            if (binding.navigationButton.getVisibility() == View.GONE) {
                binding.navigationButton.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
    }

    private void checkStorageAccessPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
        }
        //ContextCompat use to retrieve resources. It provide uniform interface to access resources.
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This permission is needed to access media file in your phone")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                        1);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            // Do nothing. Because if permission is already granted then files will be accessed/loaded in splash_screen_activity
        }
    }


    private class LoadDataInBackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Load data in the background here
            String[] storagePaths = StorageUtil.getStorageDirectories(MainActivity.this);

            for (String path : storagePaths) {
                File storage = new File(path);
                Method.load_Directory_Files(storage);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // This method is called on the UI thread after doInBackground completes
            // You can update the UI or perform any post-processing here
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            // Check if the user granted the requested permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                new LoadDataInBackground().execute();
            } else {
                // Permission is denied
            }
        }
    }
}