package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.API_Model_Classes.SignUpDataModel;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.ActivitySignUpBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    String androidDevicesID;
    String deviceToken = "buibjbbuh9hy8n8y7";
    SharedPreferencesHelper signUpData;
    AshDialog progress;

    String termsAndConditionsText;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progress = new AshDialog(SignUp.this, "Verifying details", "Please wait we are verifying your details.");

        signUpData = new SharedPreferencesHelper(SignUp.this);
        signUpData.clearSignUpUserData();
        signUpData.clearCurrentUserData();
        SharedPreferences sharedPreferences = getSharedPreferences("is_user_exist", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();


        FirebaseApp.initializeApp(SignUp.this);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                deviceToken = task.getResult();

                // Handle the token as needed (send it to your server, etc.).
            } else {
                Log.e("FCM Token", "Token retrieval failed", task.getException());
            }
        });


        androidDevicesID = Settings.Secure.getString(SignUp.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        binding.passwordInputLayout.setEndIconVisible(false);
        binding.confirmPasswordInputLayout.setEndIconVisible(false);

        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));

        SpannableString spannableString = new SpannableString(binding.singInTextView.getText().toString() + " Sign In");

        ForegroundColorSpan wight = new ForegroundColorSpan(Color.WHITE);
        StyleSpan bold = new StyleSpan(Typeface.BOLD);

        spannableString.setSpan(wight, 25, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(bold, 25, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.singInTextView.setText(spannableString);

        binding.termsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTermsAndConditionsPopup(binding.termsConditionsCheckBox);
            }
        });


        // to set the visibility of editText icon in password and confirm password
        setIconVisibilityInEditText(binding.password, binding.passwordInputLayout);
        setIconVisibilityInEditText(binding.confirmPassword, binding.confirmPasswordInputLayout);


        binding.singInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                finish();
                startActivity(intent);
            }
        });

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = Objects.requireNonNull(binding.nameField.getText()).toString();
                String mobileNo = Objects.requireNonNull(binding.mobileNumber.getText()).toString();
                String emailId = Objects.requireNonNull(binding.emailFeild.getText()).toString();
                String password = Objects.requireNonNull(binding.password.getText()).toString();
                String confirmPassword = Objects.requireNonNull(binding.confirmPassword.getText()).toString();


                checkEditText(binding.nameField, "Please enter your Name");
                checkEditText(binding.mobileNumber, "Please enter your Phone No.");
                checkEditText(binding.emailFeild, "Please enter your Email ID.");
                checkEditText(binding.password, "Please enter your Password.");
                checkEditText(binding.confirmPassword, "Please enter your Confirm Password.");

                if (!emailId.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
                    binding.emailFeild.setError("Invalid email address");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    binding.passwordInputLayout.setEndIconVisible(false);
                    binding.confirmPasswordInputLayout.setEndIconVisible(false);
                    binding.password.setError("Password does not match");
                    binding.confirmPassword.setError("Password does not match");
                    return;
                }

                if (!name.isEmpty() && !mobileNo.isEmpty() && !emailId.isEmpty() && !password.isEmpty()) {
                    if (binding.termsConditionsCheckBox.isChecked()) {

                        SignUpDataModel signUpDataModel = new SignUpDataModel(emailId, password, "0", mobileNo,
                                "+91", "android", deviceToken, name, "android", androidDevicesID);
                        signUpData.setSignUpUserData(signUpDataModel);

                        // to verify mobile number
                        otpSendForRegistration(emailId, mobileNo, "+91");
                    }
                    else {
                        Toast.makeText(SignUp.this, "Please check Terms & Condition Box", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    // to set the error in empty text in editText
    public void checkEditText(TextInputEditText editText, String errorMessage) {
//        EditText editText = findViewById(editTextId);
        if (Objects.requireNonNull(editText.getText()).toString().isEmpty()) {
            editText.setError(errorMessage);
            return;
        }
    }

    // to set the visibility of editText icon
    public void setIconVisibilityInEditText(TextInputEditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Objects.requireNonNull(editText.getText()).toString().length() > 0) {
                    layout.setEndIconVisible(true);
                } else {
                    layout.setEndIconVisible(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void otpSendForRegistration(String email, String mobile, String country_code) {
        progress.show();
        Call<JsonObject> call = ApiController.getInstance().getapi().
                otpSendForRegistration(email, mobile, country_code);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                progress.dismiss();

                JSONObject jsonResponse = ApiSet.getResponseData(response);

                if (jsonResponse != null) {
                    if (jsonResponse.optString("status").equals("true")) {
                        Intent intent = new Intent(getApplicationContext(), OTPVerification.class);
                        intent.putExtra("description", "Please type the verification code sent to your mobile");
                        intent.putExtra("layout", "registration");
                        finish();
                        startActivity(intent);
                    } else {
                        if (jsonResponse.optString("message").toLowerCase(Locale.ROOT).equals("Email already registered.".toLowerCase(Locale.ROOT))) {
                            binding.emailFeild.setError(jsonResponse.optString("message"));
                            return;
                        }
                        else if (jsonResponse.optString("message").toLowerCase(Locale.ROOT).equals("Mobile number already registered.".toLowerCase(Locale.ROOT))) {
                            binding.mobileNumber.setError(jsonResponse.optString("message"));
                            return;
                        }
                        Toast.makeText(SignUp.this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });
    }

    //-------------------------------Method  (for Term And Condition PopUp )---------------------
    private void showTermsAndConditionsPopup(final CheckBox agreeCheckbox) {
        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");

        // Set the long paragraph text
        builder.setMessage(termsAndConditionsText);

        // Set positive button with a listener
        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // When "Agree" is clicked, check the checkbox
                agreeCheckbox.setChecked(true);
            }
        });

        // Set negative button with a listener
        builder.setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // When "Disagree" is clicked, uncheck the checkbox (optional)
                agreeCheckbox.setChecked(false);
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Call<JsonObject> call1 = ApiController.getInstance().getapi().
                getTerm();

        call1.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);

                if (jsonResponse.optString("status").equals("true")) {
                    JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                    assert dataJsonObject != null;
                    String plainTextDescription = dataJsonObject.optString("description");

                    termsAndConditionsText  = Html.fromHtml(plainTextDescription).toString();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }
}