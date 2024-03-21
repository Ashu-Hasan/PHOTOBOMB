package com.ash.photobomb.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.GroupItemDetailPage;
import com.ash.photobomb.R;
import com.ash.photobomb.ScanQRCode;
import com.ash.photobomb.databinding.FragmentBottomFilesBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomFiles extends Fragment {
    FragmentBottomFilesBinding binding;

    int replaceLayoutId;
    ImageView navigationButton;
    Activity activity;
    SharedPreferencesHelper helper;

    View notificationIndicator;

    String layout = "";

    AshDialog dialog;
    public BottomFiles(int relativeLayout, ImageView navigationButton, Activity context, String layout){
        activity = context;
        replaceLayoutId = relativeLayout;
        this.navigationButton = navigationButton;
        helper = new SharedPreferencesHelper(activity);
        this.layout = layout;
        dialog = new AshDialog(context);

    }

    public BottomFiles(int relativeLayout, ImageView navigationButton, Activity context){
        activity = context;
        replaceLayoutId = relativeLayout;
        this.navigationButton = navigationButton;
        helper = new SharedPreferencesHelper(activity);
        dialog = new AshDialog(context);
    }

    public BottomFiles(int relativeLayout, ImageView navigationButton){
        replaceLayoutId = relativeLayout;
        this.navigationButton = navigationButton;
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentBottomFilesBinding.inflate(getLayoutInflater(), container, false);
        /* It is the mandatory configuration which is needed to be
         * declared after calling setContentView --*/



       /* LoadingPopup.getInstance(getActivity())
                .defaultLovelyLoading()
                .setBackgroundColor(android.R.color.holo_red_dark)
//                .setBackgroundOpacity(20)
                .build();

        LoadingPopup.showLoadingPopUp();*/

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
                        helper.setCurrentUserData(userprofile);
                        NavigationView navigationView = activity.findViewById(R.id.navigationView);

                        TextView navUserName = navigationView.getHeaderView(0).findViewById(R.id.userName);
                        TextView navUserEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
                        ImageView navUserImage = navigationView.getHeaderView(0).findViewById(R.id.userImage);
                        if (userprofile.getName() != null){
                            navUserName.setText(userprofile.getName());
                        }
                        if (userprofile.getEmail() != null){
                            navUserEmail.setText(userprofile.getEmail());
                        }
                        if (userprofile.getProfile_picture() != null){
                            Picasso.get().load(Uri.parse(userprofile.getProfile_picture())).placeholder(R.drawable.user_logo).into(navUserImage);
                        }
                        // Dismiss the progress dialog after loading data
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                // Dismiss the progress dialog in case of failure
            }
        });


        showIndicator();

        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();



        if (layout.equals("Notification")){
            replaceFragment(new Notifications(replaceLayoutId, navigationButton, activity));
        }else if (layout.equals("Pending")){
            binding.bottomNavigationView.setSelectedItemId(R.id.pending);
            transaction.replace(R.id.fragment_layout, new PendingRequests(replaceLayoutId, navigationButton, activity));
            transaction.commit();
            helper.setConditionToSwitchLayout(1);
        }
        else {
            binding.bottomNavigationView.setSelectedItemId(R.id.homePage);
            navigationButton.setVisibility(View.VISIBLE);
            transaction.replace(R.id.fragment_layout, new HomePage(replaceLayoutId, navigationButton, activity));
            transaction.commit();
        }


        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new SearchGroup(replaceLayoutId, navigationButton, activity));
                navigationButton.setVisibility(View.GONE);
            }
        });

        binding.scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ScanQRCode.class);
                activity.finish();
                startActivity(intent);
            }
        });

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.addGroup){
                    replaceFragment(new CreateGroup(replaceLayoutId, navigationButton, activity));
                    navigationButton.setVisibility(View.GONE);
                }
                else if (item.getItemId() == R.id.notification){
                    replaceFragment(new Notifications(replaceLayoutId, navigationButton, activity));
                    navigationButton.setVisibility(View.GONE);
                }
                else if (item.getItemId() == R.id.homePage){
                    replaceFragment(new BottomFiles(replaceLayoutId, navigationButton, activity));
                    navigationButton.setVisibility(View.GONE);
                }
                else if (item.getItemId() == R.id.pending){
                    assert getFragmentManager() != null;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_layout, new PendingRequests(replaceLayoutId, navigationButton, activity));
                    transaction.commit();
                    helper.setConditionToSwitchLayout(1);
                }


                return true;
            }
        });

        return binding.getRoot();
    }

    public void replaceFragment(Fragment fragment){
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(replaceLayoutId, fragment);
        transaction.commit();
        helper.setConditionToSwitchLayout(1);


    }

    private void showIndicator(){
        dialog.show();

        Call<JsonObject> call1 = ApiController.getInstance(requireContext()).getapi().getRequestList();
        call1.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                Gson gson = new Gson();
                if (jsonResponse.optString("status").equals("true")){
                    try {
                        JSONArray dataJsonObject = jsonResponse.getJSONObject("data").getJSONArray("invitations_send_to_me");
                        if (Objects.requireNonNull(dataJsonObject).length() > 0){
                            setIndicator(R.id.pending, R.layout.pending_indicator);
                        }

                    } catch (JSONException e) {
//                            throw new RuntimeException(e);
                    }


                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

        Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi().
                getNotificationList();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);

                if (jsonResponse.optString("status").equals("true")) {
                    JSONArray dataJsonObject = null;
                    try {
                        dataJsonObject = jsonResponse.getJSONArray("data");

                        for (int index = 0; index <= Objects.requireNonNull(dataJsonObject).length(); index++){
                            try {
                                JSONObject group = dataJsonObject.getJSONObject(index);

                                if (group.optString("is_read").equals("0")){
                                    setIndicator(R.id.notification, R.layout.notification_indicator);
                                    break;
                                }

                            } catch (JSONException e) {
//                                throw new RuntimeException(e);

                            }
                        }
                        dialog.dismiss();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                }
                else {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                dialog.dismiss();
            }
        });

    }

    public void setIndicator(int itemId, int designId){
        BottomNavigationItemView itemView = binding.bottomNavigationView.findViewById(itemId);

        notificationIndicator = LayoutInflater.from(getContext()).inflate(designId,binding.bottomNavigationView,false);
        notificationIndicator.setVisibility(View.VISIBLE);

        itemView.addView(notificationIndicator);
    }



}