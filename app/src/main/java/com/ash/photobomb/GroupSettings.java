package com.ash.photobomb;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.ActivityGroupSettingsBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupSettings extends AppCompatActivity {

    ActivityGroupSettingsBinding binding;
    AshDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new AshDialog(GroupSettings.this);

        binding.groupExpiryMenu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                MenuBuilder menuBuilder = OnCreatePopUpMenu(GroupSettings.this, view, R.menu.group_expiry_menu);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.hours){
                            binding.groupExpiryEditText.setText("48 Hours");
                        }
                        else if (item.getItemId() == R.id.noExpiration){
                            binding.groupExpiryEditText.setText("No Expiration");
                        }
                        return true;
                    }

                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {

                    }
                });

            }
        });
        binding.userAccessMenu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                MenuBuilder menuBuilder = OnCreatePopUpMenu(GroupSettings.this, view, R.menu.user_access_menu);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.immediateAccess) {
                            binding.userAccessEditText.setText("Immediate Access");
                        } else if (item.getItemId() == R.id.requestApproval) {
                            binding.userAccessEditText.setText("Request Approval");
                        }

                        return true;
                    }

                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {

                    }
                });
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                SharedPreferencesHelper userData = new SharedPreferencesHelper(GroupSettings.this);
                String userAccess = "1";
                GroupInfoModel groupInfoModel = userData.getCurrentGroup();
                if (Objects.requireNonNull(binding.userAccessEditText.getText()).toString().equals("Immediate Access")){
                    userAccess = "0";
                }
                Call<JsonObject> call = ApiController.getInstance(GroupSettings.this).getapi().
                        changeGroupSettings(groupInfoModel.getGroup_id(),
                                Objects.requireNonNull(binding.groupExpiryEditText.getText()).toString(),
                                Objects.requireNonNull(userAccess));

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        JSONObject jsonResponse = ApiSet.getResponseData(response);
                        Toast.makeText(GroupSettings.this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                        if (jsonResponse.optString("status").equals("true")) {
                            Call<JsonObject> call1 = ApiController.getInstance(GroupSettings.this).getapi().
                                    getGroupDetail(groupInfoModel.getGroup_id());

                            call1.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                    Gson gson = new Gson();
                                    JSONObject jsonResponse = ApiSet.getResponseData(response);
                                    dialog.dismiss();
                                    if (jsonResponse.optString("status").equals("true")){
                                        JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                                        GroupInfoModel groupInfoModel = gson.fromJson(dataJsonObject.toString(), GroupInfoModel.class);
                                        userData.clearCurrentGroup();
                                        userData.setCurrentGroup(groupInfoModel, true);

                                        back();

                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    dialog.dismiss();
                                }
                            });

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dialog.dismiss();
                    }
                });

            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               back();
            }
        });

    }

    @SuppressLint("RestrictedApi")
    public MenuBuilder OnCreatePopUpMenu(Context context, View view, int FileId) {
        Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);
        @SuppressLint("RestrictedApi") MenuBuilder builder = new MenuBuilder(context);
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(FileId, builder);

        @SuppressLint("RestrictedApi") MenuPopupHelper helper = new MenuPopupHelper(wrapper, builder, view);
        helper.setForceShowIcon(true);


        helper.show(-400,-168);

        return builder;
    }

    public void back(){
        Intent intent = new Intent(getApplicationContext(), GroupItemDetailPage.class);
        finish();
        startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        back();
    }

}