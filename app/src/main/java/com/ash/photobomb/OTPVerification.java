package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.ResponseModel;
import com.ash.photobomb.API_Model_Classes.SignUpDataModel;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.DataSharingArray.ShareData;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.ActivityOtpverificationBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerification extends AppCompatActivity {

    ActivityOtpverificationBinding binding;
    String layout = "";
    AshDialog progress;
    SharedPreferencesHelper signUpData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpverificationBinding.inflate(getLayoutInflater());
        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));
        setContentView(binding.getRoot());

        signUpData = new SharedPreferencesHelper(OTPVerification.this);


        progress = new AshDialog(OTPVerification.this, "Verifying account", "Please wait we are verifying your account.");


        Intent data = getIntent();
        if (data != null && data.getStringExtra("layout") != null) {
            layout = data.getStringExtra("layout");
            binding.description.setText(data.getStringExtra("description"));
        }


        moveNumber(binding.OtpBox1, binding.OtpBox2);
        moveNumber(binding.OtpBox2, binding.OtpBox3);
        moveNumber(binding.OtpBox3, binding.OtpBox4);

        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkEditText(binding.OtpBox1);
                checkEditText(binding.OtpBox2);
                checkEditText(binding.OtpBox3);
                checkEditText(binding.OtpBox4);

                if (!binding.OtpBox1.getText().toString().isEmpty() && !binding.OtpBox2.getText().toString().isEmpty() &&
                        !binding.OtpBox3.getText().toString().isEmpty() && !binding.OtpBox4.getText().toString().isEmpty()) {

                    progress.show();

                    String otp = binding.OtpBox1.getText().toString() + binding.OtpBox2.getText().toString() +
                            binding.OtpBox3.getText().toString() + binding.OtpBox4.getText().toString();


                    SignUpDataModel signUpDataModel = signUpData.getSignUpUserData();

                    Call<JsonObject> call = null;

                    if (layout != null){
                        if (layout.equals("registration")){
                            call = ApiController.getInstance().getapi().
                                    otpVerificationForRegistration(otp, signUpDataModel.getMobile(), signUpDataModel.getCountry_code());
                        }
                        else if (layout.equals("resetPassword")){
                            call = ApiController.getInstance().getapi().
                                    verifyOtpToResetPassword(otp, signUpDataModel.getMobile(), signUpDataModel.getCountry_code());
                        }
                    }

                    assert call != null;
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                            Gson gson = new Gson();

                            JSONObject jsonResponse = ApiSet.getResponseData(response);

                            if (jsonResponse != null) {
                                if (layout.equals("registration")) {
                                    if (jsonResponse.optString("status").equals("true")) {
                                        Call<JsonObject> signUserRegistrationCall = ApiController.getInstance().getapi().
                                                getregister(signUpDataModel.getEmail(), signUpDataModel.getPassword(), signUpDataModel.getIs_social(),
                                                        signUpDataModel.getMobile(), signUpDataModel.getCountry_code(), signUpDataModel.getDevice_type(),
                                                        signUpDataModel.getDevice_token(), signUpDataModel.getName(), signUpDataModel.getSocial_type(),
                                                        signUpDataModel.getDevice_id());

                                        signUserRegistrationCall.enqueue(new Callback<JsonObject>() {
                                            @Override
                                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {



                                                JSONObject jsonResponse = ApiSet.getResponseData(response);

                                                if (jsonResponse.optString("status").equals("true")) {

                                                    if (layout.equals("registration")) {


                                                        JSONObject dataJsonObject = Objects.requireNonNull(jsonResponse.optJSONObject("data")).optJSONObject("user_data");
                                                        if (dataJsonObject != null) {
                                                            User userprofile = gson.fromJson(dataJsonObject.toString(), User.class);
                                                            userprofile.setJwt((Objects.requireNonNull(jsonResponse.optJSONObject("data")).optString("jwt")));
                                                            signUpData.setCurrentUserData(userprofile);
                                                            signUpData.setJWTValue(userprofile.getJwt());

                                                            SharedPreferences sharedPreferences = getSharedPreferences("is_user_exist", Context.MODE_PRIVATE);
                                                            sharedPreferences.edit().putBoolean("condition", true).apply();

                                                            Intent intent = new Intent(getApplicationContext(), SuccessfullyPage.class);
                                                            intent.putExtra("description", "you have successfully become Photo-bomb user");
                                                            intent.putExtra("layout", "registration");


                                                            progress.dismiss();
                                                            finish();
                                                            startActivity(intent);
                                                        }
                                                    }

                                                } else {
                                                    progress.dismiss();
                                                    Toast.makeText(OTPVerification.this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                                progress.dismiss();
                                            }
                                        });


                                    }
                                }

                                else if (layout.equals("resetPassword")) {
                                    Toast.makeText(OTPVerification.this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                                    if (jsonResponse.optString("status").equals("true")) {


                                        JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                                        if (dataJsonObject != null) {
                                            User userprofile = gson.fromJson(dataJsonObject.toString(), User.class);
                                            userprofile.setJwt((Objects.requireNonNull(jsonResponse.optJSONObject("data")).optString("jwt")));
                                            signUpData.setCurrentUserData(userprofile);
                                            signUpData.setJWTValue(userprofile.getJwt());

                                            Intent intent = new Intent(getApplicationContext(), UpdatePassword.class);
                                            intent.putExtra("layout", "resetPassword");

                                            progress.dismiss();
                                            finish();
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                        }
                    });

                }
            }
        });

    }

    public void moveNumber(EditText editText1, EditText editText2) {
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty() || editText1.getText().toString().length() > 0) {
                    editText2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    editText1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent back;
        if (layout.equals("registration")) {
            back = new Intent(getApplicationContext(), SignUp.class);
        } else {
            back = new Intent(getApplicationContext(), ForgetPassword.class);
        }
        finish();
        startActivity(back);
    }

    public void checkEditText(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill all box", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}