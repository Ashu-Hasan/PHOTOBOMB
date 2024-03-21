package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.databinding.ActivityForgetPasswordBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPassword extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;
    AshDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new AshDialog(ForgetPassword.this);

        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        binding.resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Objects.requireNonNull(binding.mobileNumber.getText()).toString().trim().length() > 9) {
                    dialog.show();
                    Call<JsonObject> sendOtp = ApiController.getInstance().getapi().
                            sendOtpToResetPassword("+91", binding.mobileNumber.getText().toString().trim());

                    sendOtp.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JSONObject jsonResponse = ApiSet.getResponseData(response);

                            if (jsonResponse != null) {
                                Toast.makeText(ForgetPassword.this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                                if (jsonResponse.optString("status").equals("true")) {
                                    Intent OTPVerificationPage = new Intent(getApplicationContext(), OTPVerification.class);
                                    OTPVerificationPage.putExtra("description", "Please type the verification code sent to your registered mobile");
                                    OTPVerificationPage.putExtra("layout", "resetPassword");

                                    dialog.dismiss();
                                    finish();
                                    startActivity(OTPVerificationPage);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(ForgetPassword.this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
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
        Intent loginPage = new Intent(getApplicationContext(), SignIn.class);
        finish();
        startActivity(loginPage);
    }
}