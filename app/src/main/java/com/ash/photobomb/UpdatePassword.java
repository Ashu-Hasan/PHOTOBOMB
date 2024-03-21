package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.ActivityUpdatePasswordBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePassword extends AppCompatActivity {

    ActivityUpdatePasswordBinding binding;

    AshDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdatePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new AshDialog(UpdatePassword.this);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });


        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = Objects.requireNonNull(binding.password.getText()).toString().trim();
                String confirmPassword = Objects.requireNonNull(binding.confirmPassword.getText()).toString().trim();

                if (password.isEmpty() || confirmPassword.isEmpty()){
                    if (password.isEmpty()){
                        binding.password.setError("Please enter password");
                    }
                    else {
                        binding.confirmPassword.setError("Please enter password");
                    }
                    return;
                }

                if (password.equals(confirmPassword)){
                    dialog.show();
                    SharedPreferencesHelper getUser = new SharedPreferencesHelper(UpdatePassword.this);
                    User user = getUser.getCurrentUserData();

                    Call<JsonObject> call = ApiController.getInstance().getapi().
                            resetPassword(password, user.getId());

                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                            JSONObject jsonResponse = ApiSet.getResponseData(response);

                            if (jsonResponse != null){
                                Toast.makeText(UpdatePassword.this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                                if (jsonResponse.optString("status").equals("true")){
                                    Intent intent = new Intent(getApplicationContext(), SuccessfullyPage.class);
                                    intent.putExtra("description", "you have successfully Create Your New Password");
                                    intent.putExtra("layout", "resetPassword");
                                    dialog.dismiss();
                                    finish();
                                    startActivity(intent);
                                }
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                            dialog.dismiss();
                        }
                    });

                }
                else {
                    binding.password.setError("password dose not match");
                    binding.confirmPassword.setError("password dose not match");
                    return;
                }
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        back();
    }

    public void back() {

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePassword.this);

        //Setting message manually and performing action on button click
        builder.setMessage("Are you sure?  you want to cancel updation.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent loginPage = new Intent(getApplicationContext(), SignIn.class);
                        finish();
                        startActivity(loginPage);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert");
        Objects.requireNonNull(alert.getWindow()).getAttributes().windowAnimations = R.style.MyDialogAnimationToOpen;

        alert.show();


    }
}