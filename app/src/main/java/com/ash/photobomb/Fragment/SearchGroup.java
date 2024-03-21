package com.ash.photobomb.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Adapter.GroupItemAdapter;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.MainActivity;
import com.ash.photobomb.databinding.FragmentSearchGroupBinding;
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


public class SearchGroup extends Fragment {

    FragmentSearchGroupBinding binding;
    ImageView navigationButton;
    Activity activity;
    ArrayList<GroupInfoModel> groupName;
    int replaceLayoutId;
    SharedPreferencesHelper conditionValue;

    AshDialog dialog;


    public SearchGroup(int relativeLayout, ImageView navigationButton, Activity context){
        activity = context;
        replaceLayoutId = relativeLayout;
        this.navigationButton = navigationButton;
        conditionValue = new SharedPreferencesHelper(activity);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentSearchGroupBinding.inflate(getLayoutInflater(), container, false);
        dialog = new AshDialog(getContext());

        groupName = new ArrayList<>();


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainLayout = new Intent(getContext(), MainActivity.class);
                activity.finish();
                activity.startActivity(mainLayout);
            }
        });

        binding.searchGroupEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchGroup = Objects.requireNonNull(binding.searchGroupEditText.getText()).toString().trim();
                if (!searchGroup.isEmpty()){
                    Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi()
                            .searchGroups(searchGroup);

                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JSONObject jsonResponse = ApiSet.getResponseData(response);
                            Gson gson = new Gson();
                            groupName.clear();
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
                                            GroupInfoModel groupInfoModel = gson.fromJson(group.toString(), GroupInfoModel.class);
                                            groupInfoModel.setGroup_id(groupInfoModel.getId() );
                                            groupName.add(groupInfoModel);

                                        } catch (JSONException e) {
//                                throw new RuntimeException(e);

                                        }
                                    }
                                    adapter();

                                }
                            }
                            else {
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                        }
                    });
                }
                else {
                    groupName.clear();
                    adapter();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return binding.getRoot();
    }

    public void adapter(){
        assert getFragmentManager() != null;
        @SuppressLint("CommitTransaction") FragmentTransaction transaction = getFragmentManager().beginTransaction();
        GroupItemAdapter adapter = new GroupItemAdapter(getContext(), groupName, replaceLayoutId, navigationButton, transaction, activity, "s");
        binding.groupRecyclerForSearch.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.groupRecyclerForSearch.setLayoutManager(manager);
    }
}