package com.ash.photobomb.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.NotificationModel;
import com.ash.photobomb.Adapter.NotificationAdapter;
import com.ash.photobomb.Constructor.NotificationItemDetails;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.R;
import com.ash.photobomb.databinding.FragmentNotificationsBinding;
import com.ash.photobomb.databinding.NotificationItemDesignBinding;
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

public class Notifications extends Fragment {

    FragmentNotificationsBinding binding;

    int replaceLayoutId;
    ImageView navigationButton;

    Activity activity;
    ArrayList<NotificationModel> notificationList;
    AshDialog dialog;

    public Notifications(int relativeLayout, ImageView navigationButton, Activity activity){

        replaceLayoutId = relativeLayout;
        this.navigationButton = navigationButton;
        this.activity = activity;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBinding.inflate(getLayoutInflater(), container, false);

        dialog = new AshDialog(getContext());
        dialog.show();

        notificationList = new ArrayList<>();

        Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi().
                getNotificationList();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Gson gson = new Gson();
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                notificationList.clear();
                if (jsonResponse.optString("status").equals("true")) {
                    JSONArray dataJsonObject = null;
                    try {
                        dataJsonObject = jsonResponse.getJSONArray("data");

                        for (int index = 0; index <= Objects.requireNonNull(dataJsonObject).length(); index++){
                            try {
                                JSONObject jsonObject = dataJsonObject.getJSONObject(index);
                                NotificationModel notification = gson.fromJson(jsonObject.toString(), NotificationModel.class);
                                notificationList.add(notification);


                            } catch (JSONException e) {
//                                throw new RuntimeException(e);

                            }
                        }
                        dialog.dismiss();
                        adapter(notificationList);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                dialog.dismiss();
            }
        });


        binding.clearNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clearNotification.setVisibility(View.GONE);
                dialog.show();
                Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi().deleteAllNotification();
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JSONObject jsonResponse = ApiSet.getResponseData(response);
                        dialog.dismiss();
                        if (jsonResponse.optString("status").equals("true")) {
                            notificationList.clear();
                            adapter(notificationList);
                            binding.clearNotification.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        });


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new BottomFiles(replaceLayoutId, navigationButton, activity));
            }
        });

        return binding.getRoot();
    }


    public void replaceFragment(Fragment fragment){
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(replaceLayoutId, fragment);
        transaction.commit();

    }

    public void adapter(ArrayList<NotificationModel> list){
        NotificationAdapter adapter = new NotificationAdapter(getContext(), list, activity);
        binding.notificationRecycler.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.notificationRecycler.setLayoutManager(manager);
    }
}