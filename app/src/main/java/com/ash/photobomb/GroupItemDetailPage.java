package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.API_Model_Classes.MediaDataModel;
import com.ash.photobomb.Adapter.GroupItemImageAdapter;
import com.ash.photobomb.Database.DataSharingArray.ShareData;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.Fragment.AlertDialogForGroupDetail;
import com.ash.photobomb.Fragment.GroupQR;
import com.ash.photobomb.Fragment.Participants;
import com.ash.photobomb.Fragment.Requests;
import com.ash.photobomb.databinding.ActivityGroupItemDetailPageBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GroupItemDetailPage extends AppCompatActivity {

    ActivityGroupItemDetailPageBinding binding;
    ArrayList<MediaDataModel> images;
    SharedPreferencesHelper currentGroupInfo;
    GroupInfoModel currentGroup;
    String layout = "";

    AshDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupItemDetailPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));

        dialog = new AshDialog(GroupItemDetailPage.this);

        Intent data = getIntent();

        if (data != null && data.getStringExtra("layout") != null){
            layout = data.getStringExtra("layout");
        }


        currentGroupInfo = new SharedPreferencesHelper(GroupItemDetailPage.this);
        currentGroup = currentGroupInfo.getCurrentGroup();
        if (currentGroupInfo.getCurrentUserData().getId().equals(currentGroup.getCreated_by())){
            binding.forAdmin.setVisibility(View.VISIBLE);
            binding.settingLayout.setVisibility(View.VISIBLE);
        }else {binding.forUser.setVisibility(View.VISIBLE);}
        if (currentGroup.getIs_admin().equals("1")){
            binding.shareLink.setVisibility(View.VISIBLE);
        }

        // 0 for Unfreeze Group and 1 for Freeze Group
        if (currentGroup.getIs_freeze().equals("0")){
            binding.freezeGroup.setText("Freeze Group");
        }
        else {
            binding.freezeGroup.setText("Unfreeze Group");
        }

        binding.groupName.setText(currentGroup.getName());

        images = ShareData.imageUrl;

        if (images != null && images.size() > 0){
            imageAdapter();
        }
        else {
            dialog.show();
            Call<JsonObject> call = ApiController.getInstance(getApplicationContext()).getapi().
                    getGroupMediaFile(currentGroup.getGroup_id());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JSONObject jsonResponse = ApiSet.getResponseData(response);
                    Gson gson = new Gson();

                    if (jsonResponse.optString("status").equals("true")){
                        try {
                            JSONArray dataJsonObjectAlbumArray = jsonResponse.getJSONArray("data");
                            for (int albumIndex = 0; albumIndex < Objects.requireNonNull(dataJsonObjectAlbumArray).length(); albumIndex++){
                                try {
                                    JSONObject albumGroup = dataJsonObjectAlbumArray.getJSONObject(albumIndex);
                                    JSONArray dataJsonObjectImageArray = albumGroup.getJSONArray("media_data");
                                    for (int imageIndex = 0; imageIndex < Objects.requireNonNull(dataJsonObjectImageArray).length(); imageIndex++){
                                        JSONObject imageGroup = dataJsonObjectImageArray.getJSONObject(imageIndex);
                                        MediaDataModel mediaDataModel = gson.fromJson(imageGroup.toString(), MediaDataModel.class);
                                        images.add(mediaDataModel);
                                    }


                                } catch (Exception e) {
//                            throw new RuntimeException(e);
                                }
                            }
                            dialog.dismiss();
                            imageAdapter();
                        } catch (Exception e) {
//                        throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                }
            });
        }


        replaceFragment(new Participants());

        binding.tabBar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){replaceFragment(new Participants());}
                else if (tab.getPosition() == 1){replaceFragment(new GroupQR());}
                else if (tab.getPosition() == 2){replaceFragment(new Requests());}
                else {Toast.makeText(GroupItemDetailPage.this, "Content not available", Toast.LENGTH_SHORT).show();}

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });


        binding.openSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GroupSettings.class);
                finish();
                startActivity(intent);
            }
        });

        binding.showImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (images == null || images.size()<1){
                    Toast.makeText(GroupItemDetailPage.this, "Please add dataBase", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (binding.groupImages.getVisibility() == View.VISIBLE){
                        binding.groupImages.setVisibility(View.GONE);
                        binding.showImages.setImageResource(R.drawable.down_arrow_icon);
                    }
                    else {
                        binding.groupImages.setVisibility(View.VISIBLE);
                        binding.showImages.setImageResource(R.drawable.up_arrow_icon);
                    }
                }
            }
        });

        binding.deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Are you sure you want to delete this group?";
                AlertDialogForGroupDetail dialog = new AlertDialogForGroupDetail(GroupItemDetailPage.this ,R.drawable.empty_group_icon, message, "delete");
                dialog.setDialog(dialog);
                dialog.show(getSupportFragmentManager(), dialog.getTag());

            }
        });

        binding.shareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri baseUri = Uri.parse("https://play.google.com/store/apps/details?id=com.ash.help_me_in_study");

                // Add additional parameters to the baseUri
                Uri updatedUri = baseUri.buildUpon()
                        .appendQueryParameter("groupTd", currentGroup.getGroup_id())
                        .appendQueryParameter("qrKey", currentGroup.getKey_qrcode())
                        .appendQueryParameter("userTd", currentGroupInfo.getCurrentUserData().getId())
                        .appendQueryParameter("request", "groupRequest")
                        .appendQueryParameter("link", "yes")
                        .build();

                // Generate a dynamic link
                FirebaseDynamicLinks.getInstance()
                        .createDynamicLink()
                        .setLink(updatedUri)
                        .setDomainUriPrefix("https://ashphotobomb.page.link") // Set your domain URI prefix
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                        .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                        .addOnSuccessListener(GroupItemDetailPage.this, shortDynamicLink -> {
                            Uri shortLink = shortDynamicLink.getShortLink();
                            Toast.makeText(GroupItemDetailPage.this,"share Group Link with other",Toast.LENGTH_SHORT).show();

                            Intent myIntent = new Intent(Intent.ACTION_SEND);
                            myIntent.setType("text/plain");
                            String body = "Hey,  \n\nJoin my group on PhotoBomb \n\nIt is a link :- "+ shortLink;
                            myIntent.putExtra(Intent.EXTRA_TEXT, body);

                            // Grant read permissions for the content URI
                            myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                            startActivity(Intent.createChooser(myIntent, "Share Using"));

                        })
                        .addOnFailureListener(GroupItemDetailPage.this, e -> {
                            Toast.makeText(GroupItemDetailPage.this, "Sorry we are unable to generate link", Toast.LENGTH_SHORT).show();

                            // Handle errors
                        });
            }
        });

        binding.leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Are you sure you want to delete this group?";
                AlertDialogForGroupDetail dialog = new AlertDialogForGroupDetail(GroupItemDetailPage.this ,R.drawable.empty_group_icon, message, "leave");
                dialog.setDialog(dialog);
                dialog.show(getSupportFragmentManager(), dialog.getTag());

            }
        });

        binding.freezeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Are you sure you want to"+binding.freezeGroup.getText().toString()+" your Group?";
                AlertDialogForGroupDetail dialog = new AlertDialogForGroupDetail(GroupItemDetailPage.this, R.drawable.scanner_icon_for_group_detail_dialog, message, "freeze", binding.freezeGroup);
                dialog.setDialog(dialog);
                dialog.show(getSupportFragmentManager(), dialog.getTag());
            }
        });



    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayoutToReplace, fragment);
        transaction.commit();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        back();
    }

    public void back(){
        Intent home = null;
        if (layout.equals("Notification")){
            home = new Intent(getApplicationContext(), MainActivity.class);
            home.putExtra("layout", "Notification");
        }
        else {home = new Intent(getApplicationContext(), GroupItemInfoPage.class);}

        finish();
        startActivity(home);
    }

    public void imageAdapter(){
        GroupItemImageAdapter adapter = new GroupItemImageAdapter(GroupItemDetailPage.this, images,  2);
        binding.groupImages.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(GroupItemDetailPage.this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        binding.groupImages.setLayoutManager(manager);
    }
}