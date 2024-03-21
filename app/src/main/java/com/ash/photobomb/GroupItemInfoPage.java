package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.API_Model_Classes.MediaDataModel;
import com.ash.photobomb.Adapter.GroupItemImageAdapter;
import com.ash.photobomb.Constructor.AlbumItem;
import com.ash.photobomb.Database.DataSharingArray.ShareData;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.ActivityGroupItemInfoPageBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupItemInfoPage extends AppCompatActivity {

    ActivityGroupItemInfoPageBinding binding;

    SharedPreferencesHelper helper;
    ArrayList<MediaDataModel> imageList;
    ArrayList<AlbumItem> albumList;
    AshDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupItemInfoPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new AshDialog(GroupItemInfoPage.this);
        dialog.show();

        imageList = new ArrayList<>();

        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));

        helper = new SharedPreferencesHelper(GroupItemInfoPage.this);
        GroupInfoModel currentGroup = helper.getCurrentGroup();
        binding.groupTitle.setText(currentGroup.getName());


        Call<JsonObject> call = ApiController.getInstance(getApplicationContext()).getapi().
                getGroupMediaFile(currentGroup.getGroup_id());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                Gson gson = new Gson();

                if (jsonResponse.optString("status").equals("true")){
                    albumList = new ArrayList<>();
                    try {
                        JSONArray dataJsonObjectAlbumArray = jsonResponse.getJSONArray("data");
                        for (int albumIndex = 0; albumIndex < Objects.requireNonNull(dataJsonObjectAlbumArray).length(); albumIndex++){
                            try {
                                ArrayList<MediaDataModel> imageListSet = new ArrayList<>();
                                JSONObject albumGroup = dataJsonObjectAlbumArray.getJSONObject(albumIndex);
                                JSONArray dataJsonObjectImageArray = albumGroup.getJSONArray("media_data");
                                for (int imageIndex = 0; imageIndex < Objects.requireNonNull(dataJsonObjectImageArray).length(); imageIndex++){
                                    JSONObject imageGroup = dataJsonObjectImageArray.getJSONObject(imageIndex);
                                    MediaDataModel mediaDataModel = gson.fromJson(imageGroup.toString(), MediaDataModel.class);
                                    mediaDataModel.setImageSetPosition(albumIndex);
                                    imageList.add(mediaDataModel);
                                    imageListSet.add(mediaDataModel);
                                }
                                AlbumItem item = new AlbumItem(albumGroup.optString("create_date"), imageListSet);
                                albumList.add(item);

                            } catch (Exception e) {
                                dialog.dismiss();
//                            throw new RuntimeException(e);
                            }
                            adapter(albumList);
                        }
                        dialog.dismiss();

                    } catch (Exception e) {
//                        throw new RuntimeException(e);
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
            }
        });





        binding.picImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentGroup.getIs_freeze().equals("1")){
                    Toast.makeText(GroupItemInfoPage.this, "The group has been Freeze bu admin", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent picImageFromGallery = new Intent(getApplicationContext(), PhoneGallery.class);
                    finish();
                    startActivity(picImageFromGallery);
                }

            }
        });



        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupItemInfoPage.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });

        binding.menu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(GroupItemInfoPage.this, R.style.MyPopupMenu);
                MenuBuilder builder = new MenuBuilder(GroupItemInfoPage.this);
                MenuInflater inflater = new MenuInflater(GroupItemInfoPage.this);
                inflater.inflate(R.menu.group_item_page_menu, builder);

                MenuPopupHelper helper = new MenuPopupHelper(wrapper, builder, view);
                helper.setForceShowIcon(true);


                builder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.groupInfo){
                            Intent groupDetail = new Intent(getApplicationContext(), GroupItemDetailPage.class);
                            ShareData.imageUrl = imageList;
                            finish();
                            startActivity(groupDetail);
                        }
                        return true;
                    }

                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {

                    }
                });

                helper.show(-530,-100);

            }
        });

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GroupItemInfoPage.this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void adapter(ArrayList<AlbumItem> itemList){
        GroupItemImageAdapter adapter = new GroupItemImageAdapter(GroupItemInfoPage.this, itemList, binding.imageGroupRecycler, 1, false);
        binding.imageGroupRecycler.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(GroupItemInfoPage.this);
        binding.imageGroupRecycler.setLayoutManager(manager);

    }
}