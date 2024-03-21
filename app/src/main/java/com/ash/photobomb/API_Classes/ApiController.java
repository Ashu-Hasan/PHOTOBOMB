package com.ash.photobomb.API_Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.DataSharingArray.ShareData;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.Fragment.BottomFiles;
import com.ash.photobomb.SignUp;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiController
{
   static String url="https://testing.myphotobomb.com/";
   private static ApiController clientobject;
   private static Retrofit retrofit;
   static Context context;

   static User user;

    static String androidDevicesID;

    SharedPreferencesHelper helper = new SharedPreferencesHelper(context);

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("jwt", helper.getJWTValue())
                        .header("device_type", "android")
                        .header("version", helper.getAppVersion())
                        .header("device_id", androidDevicesID)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            })
            .build();

    ApiController(String data){
        retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

     ApiController()
     {
        retrofit=new Retrofit.Builder()
                     .baseUrl(url)
                     .addConverterFactory(GsonConverterFactory.create())
                     .build();
     }

     @SuppressLint("HardwareIds")
     public static synchronized ApiController getInstance(Context context1)
     {


         context =context1;
         androidDevicesID = Settings.Secure.getString(context1.getContentResolver(), Settings.Secure.ANDROID_ID);
         clientobject=new ApiController("with header");
          return clientobject;
     }
     public static synchronized ApiController getInstance()
     {
         clientobject=new ApiController();
         /* if(clientobject==null)
              clientobject=new ApiController();*/
          return clientobject;
     }

     public ApiSet getapi()
     {
         return retrofit.create(ApiSet.class);
     }

}
