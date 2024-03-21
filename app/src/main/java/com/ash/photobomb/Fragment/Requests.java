package com.ash.photobomb.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.PendingRequestModel;
import com.ash.photobomb.Adapter.PendingRequestItemAdapter;
import com.ash.photobomb.Constructor.NotificationItemDetails;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.R;
import com.ash.photobomb.databinding.FragmentRequestsBinding;
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


public class Requests extends Fragment {

    FragmentRequestsBinding binding;
    SharedPreferencesHelper helper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRequestsBinding.inflate(getLayoutInflater(), container, false);

        helper = new SharedPreferencesHelper(getContext());

        if (helper.getCurrentGroup().getIs_admin().equals("1")){
            binding.pendingRequestRecycler.setVisibility(View.VISIBLE);
        }
        else {
            binding.message.setVisibility(View.VISIBLE);
        }

        ArrayList<PendingRequestModel> pendingRequestList = new ArrayList<>();

        Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi().getRequestList();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                Gson gson = new Gson();
                pendingRequestList.clear();
                if (jsonResponse.optString("status").equals("true")){
                    try {
                        JSONArray dataJsonObject = jsonResponse.getJSONObject("data").getJSONArray("invitations_send_by_me");

                        for (int index = 0; index <= Objects.requireNonNull(dataJsonObject).length(); index++){
                            try {
                                JSONObject group = dataJsonObject.getJSONObject(index);
                                PendingRequestModel groupInfoModel = gson.fromJson(group.toString(), PendingRequestModel.class);
                                pendingRequestList.add(groupInfoModel);
                            } catch (JSONException e) {
//                                throw new RuntimeException(e);

                            }
                        }

                        adapter(pendingRequestList);
                    } catch (JSONException e) {
//                            throw new RuntimeException(e);
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });

        return binding.getRoot();
    }

    public void adapter(ArrayList<PendingRequestModel> pendingRequestList){
        PendingRequestItemAdapter adapter = new PendingRequestItemAdapter(getContext(), pendingRequestList);
        binding.pendingRequestRecycler.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.pendingRequestRecycler.setLayoutManager(manager);
    }
}