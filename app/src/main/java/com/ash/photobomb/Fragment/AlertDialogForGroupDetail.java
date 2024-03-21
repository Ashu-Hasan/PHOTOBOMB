package com.ash.photobomb.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Comments;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.MainActivity;
import com.ash.photobomb.R;
import com.ash.photobomb.databinding.FragmentAlertDialogForGroupDetailBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertDialogForGroupDetail extends BottomSheetDialogFragment {

    FragmentAlertDialogForGroupDetailBinding binding;

    Activity context;
    int imageId;
    String message = "";

    AlertDialogForGroupDetail dialog;
    String task;
    SharedPreferencesHelper helper;
    Button freezeButton;
    AshDialog ashDialog;

    public AlertDialogForGroupDetail(Activity context, int imageId, String message, String task) {
        this.context = context;
        this.imageId = imageId;
        this.message = message;
        this.task = task;
        helper = new SharedPreferencesHelper(context);
    }

    public AlertDialogForGroupDetail(Activity context, int imageId, String message, String task, Button freezeButton) {
        this.context = context;
        this.imageId = imageId;
        this.message = message;
        this.task = task;
        this.freezeButton = freezeButton;
        helper = new SharedPreferencesHelper(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAlertDialogForGroupDetailBinding.inflate(getLayoutInflater(), container, false);

        ashDialog = new AshDialog(context);

        binding.dialogImage.setImageResource(imageId);
        binding.message.setText(message);

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        binding.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                GroupInfoModel currentGroup = helper.getCurrentGroup();
                Call<JsonObject> call = null;

                switch (task) {
                    case "freeze":
                        String freezeValue = "1";
                        if (currentGroup.getIs_freeze().equals("1")) {
                            freezeValue = "0";
                        }
                        call = ApiController.getInstance(context).getapi()
                                .freezeGroup(helper.getCurrentGroup().getGroup_id(), freezeValue);

                        break;
                    case "delete":
                        call = ApiController.getInstance(context).getapi()
                                .groupDelete(helper.getCurrentGroup().getGroup_id());

                        break;
                    case "leave":
                        call = ApiController.getInstance(context).getapi()
                                .leaveGroup(helper.getCurrentGroup().getGroup_id());

                        break;
                }
                if (call != null){runCall(call);}
            }
        });

        return binding.getRoot();
    }

    public void setDialog(AlertDialogForGroupDetail enterDialog) {
        dialog = enterDialog;
    }

    public void runCall(Call<JsonObject> call){
        ashDialog.show();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);

                if (jsonResponse != null) {
                    if (jsonResponse.optString("status").equals("true")) {
                        if (task.equals("delete")){
                        FirebaseDatabase.getInstance().getReference().child("Group_pictures").child(helper.getCurrentUserData().getId())
                                .child(helper.getCurrentGroup().getName()).removeValue();
                        }
                        Intent main = new Intent(context, MainActivity.class);
                        ashDialog.dismiss();
                        context.finish();
                        context.startActivity(main);
                    }
                } else {
                    ashDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }
}