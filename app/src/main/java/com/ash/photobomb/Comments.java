package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.CommentMessageModel;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Adapter.CommentChatAdapter;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.ActivityCommentsBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Comments extends AppCompatActivity {

    ActivityCommentsBinding binding;
    SharedPreferencesHelper helper;

    ArrayList<CommentMessageModel> messageItemList;
    AshDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));
        setContentView(binding.getRoot());

        dialog =new AshDialog(Comments.this);


        messageItemList = new ArrayList<>();
        helper = new SharedPreferencesHelper(Comments.this);

        adapter();


        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = binding.commentEdutText.getText().toString().trim();
                if (comment.isEmpty()){
                    Toast.makeText(Comments.this, "Please enter Comment", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.show();
                    Call<JsonObject> call = ApiController.getInstance(Comments.this).getapi()
                            .addMediaComment(helper.getCurrentGroup().getGroup_id(), helper.getCurrentMedia().getId(),
                                    "0", comment);

                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JSONObject jsonResponse = ApiSet.getResponseData(response);
                            Gson gson = new Gson();

                            if (jsonResponse !=null){
                                if (jsonResponse.optString("status").equals("true")){
                                    adapter();
                                }
                            }
                            else {
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });





    }

    public void adapter(){
        dialog.show();
        Call<JsonObject> call = ApiController.getInstance(Comments.this).getapi()
                .getMediaCommentList(helper.getCurrentGroup().getGroup_id(), helper.getCurrentMedia().getId());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                Gson gson = new Gson();

                if (jsonResponse !=null){
                    if (jsonResponse.optString("status").equals("true")){
                        JSONArray dataJsonObject = null;
                        try {
                            dataJsonObject = jsonResponse.getJSONArray("data");
                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
                        }

                        for (int index = 0; index <= Objects.requireNonNull(dataJsonObject).length(); index++){
                            try {
                                JSONObject mediaComment = dataJsonObject.getJSONObject(index);
                                CommentMessageModel groupInfoModel = gson.fromJson(mediaComment.toString(), CommentMessageModel.class);
                                messageItemList.add(groupInfoModel);
                            } catch (JSONException e) {
//                                throw new RuntimeException(e);

                            }
                        }
                        dialog.dismiss();
                        CommentChatAdapter chatAdapter = new CommentChatAdapter(messageItemList,Comments.this, R.id.replaceReplyLayout, getSupportFragmentManager(),false);
                        binding.groupImageRecyclerView.setAdapter(chatAdapter);


                        LinearLayoutManager layoutManager = new LinearLayoutManager(Comments.this);

                        // to start list from bottom to show latest data
//        layoutManager.setReverseLayout(true);
                        layoutManager.setStackFromEnd(true);

                        binding.groupImageRecyclerView.setLayoutManager(layoutManager);

                    }
                }
                else {dialog.dismiss();}
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        back();
    }

    public void back(){
        Intent back = new Intent(Comments.this, ViewImage.class);
        finish();
        startActivity(back);
    }
}