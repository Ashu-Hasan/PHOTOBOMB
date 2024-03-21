package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.LoginAuthenticationModel;
import com.ash.photobomb.API_Model_Classes.ResponseModel;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.DataSharingArray.ShareData;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.ActivitySignInBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {

    ActivitySignInBinding binding;
    String deviceToken;

    AshDialog dialog;
    CallbackManager callbackManager;
    String TAG = "message";
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    SharedPreferencesHelper helper;
    String isSocial = "0";
    InputMethodManager inputMethodManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(SignIn.this);
        helper = new SharedPreferencesHelper(SignIn.this);

        // for facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        // for google login
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(SignIn.this, googleSignInOptions);

        dialog = new AshDialog(SignIn.this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        SharedPreferences sharedPreferences = getSharedPreferences("is_user_exist", Context.MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean("condition", false);

        if(check){
            Intent next = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(next);
        }

        if (!ApiSet.Connected(SignIn.this)){
            ApiSet.showAlertDialog(SignIn.this, "Please check your InterNet Connection");
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                deviceToken = task.getResult();

                // Handle the token as needed (send it to your server, etc.).
            } else {
                Log.e("FCM Token", "Token retrieval failed", task.getException());
            }
        });


//        for fb login
        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        // for facebook login
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
//                        handleFacebookAccessToken(loginResult.getAccessToken());
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        try {
                                            isSocial = "2";
                                            Call<JsonObject> call = ApiController.getInstance().
                                                    getapi().getAuthenticationDataOfFB(object.getString("name"),"1", "android", deviceToken, "2", object.getString("id"));

                                            runCall(call);

                                        } catch (Exception e) {
//                                            throw new RuntimeException(e);
                                        }

                                        /*
                                        data which we can fetch from json object
                                        {
                                            "id": "12345678",
                                                "birthday": "1/1/1950",
                                                "first_name": "Chris",
                                                "gender": "male",
                                                "last_name": "Colm",
                                                "link": "http://www.facebook.com/12345678",
                                                "location": {
                                            "id": "110843418940484",
                                                    "name": "Seattle, Washington"
                                        },
                                            "locale": "en_US",
                                                "name": "Chris Colm",
                                                "timezone": -8,
                                                "updated_time": "2010-01-01T16:40:43+0000",
                                                "verified": true
                                        }*/
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
//                        Toast.makeText(SignIn.this, "onCancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
//                        Toast.makeText(SignIn.this, "onError", Toast.LENGTH_SHORT).show();
                    }
                });


        binding.fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LoginManager.getInstance().logInWithReadPermissions(SignIn.this, Arrays.asList("public_profile"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


        binding.googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });




        binding.passwordLayout.setEndIconVisible(false);

        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));

        SpannableString spannableString = new SpannableString(binding.signUp.getText().toString()+" Sign Up");

        ForegroundColorSpan wight = new ForegroundColorSpan(Color.WHITE);
        StyleSpan bold = new StyleSpan(Typeface.BOLD);

        spannableString.setSpan(wight,23,  spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(bold,23,  spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.signUp.setText(spannableString);



//        binding.mobile.setBoxBackgroundColorStateList(ColorStateList.valueOf(Color.parseColor("#ffffff")));



        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.password.getText().toString().length() > 0){
                    binding.passwordLayout.setEndIconVisible(true);
                }
                else {binding.passwordLayout.setEndIconVisible(false);}
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                finish();
                startActivity(intent);
            }
        });

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Objects.requireNonNull(binding.EmailId.getText()).toString();
                String password = Objects.requireNonNull(binding.password.getText()).toString();
                checkEditText(binding.EmailId, "Please enter your number");
                checkEditText(binding.password, "Please enter your password");

                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if ( !email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.EmailId.setError("Invalid email address");
                    return;
                }
                if (!email.isEmpty() && !password.isEmpty()){
                    Call<JsonObject> call = ApiController.getInstance().
                            getapi().getAuthenticationData(email, password, "0", "android", deviceToken);
                    runCall(call);
                }




            }
        });

        binding.forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgetPage = new Intent(SignIn.this, ForgetPassword.class);
                finish();
                startActivity(forgetPage);
            }
        });
    }


    // to set the error in empty text in editText
    public void checkEditText(TextInputEditText editText, String errorMessage){
        if (Objects.requireNonNull(editText.getText()).toString().isEmpty()){
            editText.setError(errorMessage);
            return;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SignIn.this);
                if (account != null) {
//                    Toast.makeText(this, String.valueOf(account.getIdToken()), Toast.LENGTH_SHORT).show();
                    isSocial = "1";
                    Call<JsonObject> call = ApiController.getInstance().
                            getapi().getAuthenticationDataOfGoogle(account.getEmail(), account.getDisplayName(),"1", "android", deviceToken, "1", account.getId());

                    runCall(call);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Server not working", Toast.LENGTH_SHORT).show();
//                throw new RuntimeException(e);
            }
        }

    }



    public void runCall(Call<JsonObject> call){
        dialog.show();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Gson gson = new Gson();
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                if (jsonResponse.optString("status").equals("true")){
                    SharedPreferencesHelper setLoggedUser = new SharedPreferencesHelper(SignIn.this);
                    setLoggedUser.clearCurrentUserData();

                    JSONObject dataJsonObject = Objects.requireNonNull(jsonResponse.optJSONObject("data")).optJSONObject("user_data");
                    if (dataJsonObject != null) {
                        User userprofile = gson.fromJson(dataJsonObject.toString(), User.class);
                        userprofile.setJwt((Objects.requireNonNull(jsonResponse.optJSONObject("data")).optString("jwt")));
                        setLoggedUser.setCurrentUserData(userprofile);
                        setLoggedUser.setJWTValue(userprofile.getJwt());
                        setLoggedUser.setIsSocial(isSocial);

                        SharedPreferences sharedPreferences = getSharedPreferences("is_user_exist", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putBoolean("condition", true).apply();

                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        finish();
                        startActivity(intent);
                    }

                }
                else {
                    dialog.dismiss();
                    Toast.makeText(SignIn.this, "User Not Exist, Or may be server problem", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                dialog.dismiss();
                throw new RuntimeException(t);
            }
        });
    }
}