package com.ash.photobomb.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.API_Model_Classes.GroupMemberModel;
import com.ash.photobomb.Adapter.ParticipantsItemAdapter;
import com.ash.photobomb.Adapter.PendingRequestItemAdapter;
import com.ash.photobomb.Constructor.NotificationItemDetails;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.R;
import com.ash.photobomb.databinding.FragmentParticipantsBinding;
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

public class Participants extends Fragment {

    FragmentParticipantsBinding binding;

    SharedPreferencesHelper data;
    AshDialog dialog;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentParticipantsBinding.inflate(getLayoutInflater(), container, false);

        dialog = new AshDialog(getContext());

        data = new SharedPreferencesHelper(getContext());
        GroupInfoModel currentGroup = data.getCurrentGroup();
        ArrayList<GroupMemberModel> groupMembersList = new ArrayList<>();

        Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi().
                getGroupMembers(currentGroup.getGroup_id());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                Gson gson = new Gson();

                if (jsonResponse !=null){
                    if (jsonResponse.optString("status").equals("true")){
                        JSONArray dataJsonObject = null;
                        try {
                            dataJsonObject = jsonResponse.getJSONArray("data");
                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
                        }

                        for (int index = 0; index <= Objects.requireNonNull(dataJsonObject).length(); index++){
                            try {
                                JSONObject group = dataJsonObject.getJSONObject(index);
                                GroupMemberModel groupInfoModel = gson.fromJson(group.toString(), GroupMemberModel.class);
                                groupMembersList.add(groupInfoModel);

                                binding.participantsCount.setText(String.valueOf(groupMembersList.size())+" Participants");

                                ParticipantsItemAdapter adapter = new ParticipantsItemAdapter(getContext(), groupMembersList);
                                binding.notificationRecycler.setAdapter(adapter);

                                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                                binding.notificationRecycler.setLayoutManager(manager);

                            } catch (JSONException e) {
//                                throw new RuntimeException(e);

                            }
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });


        binding.participantsCount.setText(String.valueOf(groupMembersList.size())+" Participants");

        ParticipantsItemAdapter adapter = new ParticipantsItemAdapter(getContext(), groupMembersList);
        binding.notificationRecycler.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.notificationRecycler.setLayoutManager(manager);


        return binding.getRoot();
    }
}