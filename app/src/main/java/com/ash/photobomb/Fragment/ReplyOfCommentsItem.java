package com.ash.photobomb.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.CommentMessageModel;
import com.ash.photobomb.Adapter.CommentChatAdapter;
import com.ash.photobomb.Comments;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.R;
import com.ash.photobomb.databinding.FragmentReplyOfComentsItemBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
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


public class ReplyOfCommentsItem extends DialogFragment {

    FragmentReplyOfComentsItemBinding binding;

    Activity context;
    String commentItemId;
    FragmentManager fragmentManager;
    SharedPreferencesHelper helper;
    ArrayList<CommentMessageModel> messageItemList;

    AshDialog dialog;
    public ReplyOfCommentsItem(Activity context, String commentItemId, FragmentManager fragmentManager){
        this.context = context;
        this.commentItemId = commentItemId;
        helper = new SharedPreferencesHelper(context);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentReplyOfComentsItemBinding.inflate(getLayoutInflater(), container, false);
        dialog = new AshDialog(getContext());

        messageItemList = new ArrayList<>();

        adapter();


        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Customize the size and position of the dialog
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });

        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = binding.commentEdutText.getText().toString().trim();
                if (comment.isEmpty()){
                    Toast.makeText(context, "Please enter Comment", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.show();
                    Call<JsonObject> call = ApiController.getInstance(context).getapi()
                            .addMediaComment(helper.getCurrentGroup().getGroup_id(), helper.getCurrentMedia().getId(),
                                    commentItemId, comment);

                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JSONObject jsonResponse = ApiSet.getResponseData(response);

                            if (jsonResponse !=null){
                                if (jsonResponse.optString("status").equals("true")){
                                    binding.commentEdutText.setText("");
                                    adapter();
                                }
                            }
                            else {
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                        }
                    });
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Customize the size and position of the dialog
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT ;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            Objects.requireNonNull(getDialog().getWindow()).setLayout(width, height);
            getDialog().getWindow().setGravity(android.view.Gravity.CENTER);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


    public void adapter(){
        if (!dialog.isVisible()){
            dialog.show();
        }
        Call<JsonObject> call = ApiController.getInstance(context).getapi()
                .getMediaCommentReply(helper.getCurrentGroup().getGroup_id(), helper.getCurrentMedia().getId(), commentItemId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                messageItemList.clear();
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

                        if (dataJsonObject != null && Objects.requireNonNull(dataJsonObject).length() >0) {
                            for (int index = 0; index <= Objects.requireNonNull(dataJsonObject).length(); index++) {
                                try {
                                    JSONObject mediaComment = dataJsonObject.getJSONObject(index);
                                    CommentMessageModel groupInfoModel = gson.fromJson(mediaComment.toString(), CommentMessageModel.class);
                                    messageItemList.add(groupInfoModel);
                                } catch (JSONException e) {
//                                throw new RuntimeException(e);

                                }
                            }
                            dialog.dismiss();
                        }

                        CommentChatAdapter chatAdapter = new CommentChatAdapter(messageItemList,context, R.id.replaceReplyLayout, fragmentManager,true);
                        binding.groupImageRecyclerView.setAdapter(chatAdapter);


                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

                        // to start list from bottom to show latest data
//        layoutManager.setReverseLayout(true);
                        layoutManager.setStackFromEnd(true);

                        binding.groupImageRecyclerView.setLayoutManager(layoutManager);

                    }
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

}