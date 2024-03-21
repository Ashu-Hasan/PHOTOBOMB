package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.Database.DataSharingArray.ShareData;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.ActivitySplashScreenBinding;
import com.ash.photobomb.other.StorageFiles.Method;
import com.ash.photobomb.other.StorageFiles.StorageUtil;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash_Screen extends AppCompatActivity {

    float v = 0;
    TextToSpeech myTTS;

    ActivitySplashScreenBinding binding;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startBackgroundAnimation();
        startAnim();

        // removing top bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        new LoadDataInBackground().execute();


        int SPLASH_SCREEN_TIME_OUT = 2500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("is_user_exist", Context.MODE_PRIVATE);
                boolean checkUser = sharedPreferences.getBoolean("condition", false);
                SharedPreferences sharedPreferencesForIntroPage = getSharedPreferences("is_intro_done", Context.MODE_PRIVATE);
                boolean checkIntro = sharedPreferencesForIntroPage.getBoolean("condition", false);

                Intent next;
                if(!checkIntro){
                    next = new Intent(Splash_Screen.this, InfoPage.class);

                }
                else if(checkUser) {
                    next = new Intent(getApplicationContext(), MainActivity.class);
                }
                else {
                    next = new Intent(getApplicationContext(), SignIn.class);

                }
                finish();
                startActivity(next);


                //invoke the SecondActivity.
                finish();

            }
        }, SPLASH_SCREEN_TIME_OUT);

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferencesHelper setAppVersion = new SharedPreferencesHelper(Splash_Screen.this);

        /*myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    Locale localeToUse = new Locale("en", "US");
                    myTTS.setLanguage(localeToUse);
                    myTTS.speak("Welcome", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });*/

        Call<JsonObject> call = ApiController.getInstance().getapi().getVersionInfo();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);

                if (jsonResponse != null){
                    if (jsonResponse.optString("status").equals("true")){
                        JSONObject jsonObject = jsonResponse.optJSONObject("data");
                        assert jsonObject != null;
                        setAppVersion.setAppVersion(jsonObject.optString("android"));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });
    }

    //-------------------------------Method  for background animation---------------------
    private void startBackgroundAnimation() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(Animation.INFINITE);

        binding.mainwall.startAnimation(scaleAnimation);
    }
    //-------------------------------Star Animation Method---------------------
    private void startAnim()
    {
        anim(binding.line1,1000);
        anim(binding.line2,1200);
        anim(binding.line3,500);
        anim(binding.line4,1300);
        anim(binding.line5,1250);
        anim(binding.line6,980);
    }


    //-------------------------------method  for applying star animation and setting delay---------------------
    private void anim(View view, long delay){
        view.animate()
                .translationX(-1500)
                .translationY(-1500)
                .setDuration(1000)
                .setStartDelay(delay)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        // Not needed for this implementation
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        // Reset the view properties after the animation ends
                        view.setTranslationX(0);
                        view.setTranslationY(0);

                        // Restart the animation
                        anim(view, 0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        // Not needed for this implementation
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                        // Not needed for this implementation
                    }
                });
    }


    private class LoadDataInBackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Load data in the background here
            String[] storagePaths = StorageUtil.getStorageDirectories(Splash_Screen.this);

            for (String path : storagePaths) {
                File storage = new File(path);
                Method.load_Directory_Files(storage);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // This method is called on the UI thread after doInBackground completes
            // You can update the UI or perform any post-processing here
        }
    }

}