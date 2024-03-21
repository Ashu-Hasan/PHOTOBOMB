package com.ash.photobomb.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Adapter.GroupItemAdapter;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.FragmentHomePageBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomePage extends Fragment {

    FragmentHomePageBinding binding;
    ArrayList<GroupInfoModel> groupName;


    int replaceLayoutId;
    ImageView navigationButton;
    Activity activity;

    AshDialog dialog;

    public HomePage(int relativeLayout, ImageView navigationButton, Activity activity){
        replaceLayoutId = relativeLayout;
        this.navigationButton = navigationButton;
        this.activity = activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomePageBinding.inflate(getLayoutInflater(), container, false);
        dialog = new AshDialog(getContext());

        binding.listLayout.setVisibility(View.VISIBLE);
        dialog.show();
        groupName = new ArrayList<>();

        Call<JsonObject> call = ApiController.getInstance(activity).getapi().getGroups();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                Gson gson = new Gson();

                if (jsonResponse !=null){
                    if (jsonResponse.optString("status").equals("true")){

                        JSONArray dataJsonObject = null;
                        try {
                            dataJsonObject = jsonResponse.getJSONArray("data");
                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
                            dialog.dismiss();
                        }
                        for (int index = 0; index <= Objects.requireNonNull(dataJsonObject).length(); index++){
                            try {
                                JSONObject group = dataJsonObject.getJSONObject(index);
                                GroupInfoModel groupInfoModel = gson.fromJson(group.toString(), GroupInfoModel.class);
                                groupName.add(groupInfoModel);
                            } catch (JSONException e) {
//                                throw new RuntimeException(e);
                                dialog.dismiss();
                            }
                        }
                        dialog.dismiss();
                        adapter();

                    }
                    else {dialog.dismiss();}
                }
                else {dialog.dismiss();}
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                dialog.dismiss();
            }
        });







        return binding.getRoot();
    }

    public void adapter(){
        assert getFragmentManager() != null;
        @SuppressLint("CommitTransaction") FragmentTransaction transaction = getFragmentManager().beginTransaction();
        GroupItemAdapter adapter = new GroupItemAdapter(getContext(), groupName, binding.listLayout, binding.addItemLayout,
                replaceLayoutId, navigationButton, transaction, activity, "H");
        binding.groupRecycler.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.groupRecycler.setLayoutManager(manager);
    }
}