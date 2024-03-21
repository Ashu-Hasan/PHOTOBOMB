package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.ash.photobomb.Adapter.InfoPageVPAdapter;
import com.ash.photobomb.Adapter.ViewImageAdapter;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.ActivityViewImageBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewImage extends AppCompatActivity {

    ActivityViewImageBinding binding;
    MediaDataModel currentMedia;
    SharedPreferencesHelper helper;
    ArrayList<MediaDataModel> imageListWithAllInfo;

    AshDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewImageBinding.inflate(getLayoutInflater());
        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));
        setContentView(binding.getRoot());

        final int[] itemPosition = {0};

        imageListWithAllInfo = new ArrayList<>();

        dialog = new AshDialog(ViewImage.this);
        dialog.show();

        helper = new SharedPreferencesHelper(ViewImage.this);
        GroupInfoModel currentGroup = helper.getCurrentGroup();
        currentMedia = helper.getCurrentMedia();

        Call<JsonObject> call = ApiController.getInstance(getApplicationContext()).getapi().
                getGroupMediaListAll(currentGroup.getGroup_id());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                Gson gson = new Gson();
                imageListWithAllInfo.clear();
                if (jsonResponse.optString("status").equals("true")){
                    try {
                        JSONArray dataJsonObjectAlbumArray = jsonResponse.optJSONObject("data").getJSONArray("media_data");
                        for (int imageIndex = 0; imageIndex < Objects.requireNonNull(dataJsonObjectAlbumArray).length(); imageIndex++){
                            JSONObject imageGroup = dataJsonObjectAlbumArray.getJSONObject(imageIndex);
                            MediaDataModel mediaDataModel = gson.fromJson(imageGroup.toString(), MediaDataModel.class);
                            imageListWithAllInfo.add(mediaDataModel);
                            if (currentMedia.getId().equals(mediaDataModel.getId())){
                                itemPosition[0] = imageListWithAllInfo.size()-1;
                            }
                        }
                        adapter(imageListWithAllInfo);
                        binding.imageSet.setCurrentItem( itemPosition[0]);
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






        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    public void adapter(ArrayList<MediaDataModel> imageListWithAllInfo){
        ViewImageAdapter infoPageVpAdapter = new ViewImageAdapter(ViewImage.this, imageListWithAllInfo);
        binding.imageSet.setAdapter(infoPageVpAdapter);

        binding.imageSet.setClipChildren(false);
        binding.imageSet.stopNestedScroll();

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        back();
    }

    public void back(){
        Intent back = new Intent(ViewImage.this, GroupItemInfoPage.class);
        finish();
        startActivity(back);
    }
}