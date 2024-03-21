package com.ash.photobomb.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.databinding.FragmentGroupQRBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GroupQR extends Fragment {

    FragmentGroupQRBinding binding;
    SharedPreferencesHelper helper;
    GroupInfoModel model;

    AshDialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGroupQRBinding.inflate(getLayoutInflater(), container, false);

        dialog = new AshDialog(getContext());
        dialog.show();

        helper = new SharedPreferencesHelper(getContext());
        GroupInfoModel currentGroup = helper.getCurrentGroup();

        Call<JsonObject> call1 = ApiController.getInstance(requireContext()).getapi().
                getGroupDetail(currentGroup.getGroup_id());

        call1.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Gson gson = new Gson();
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                dialog.dismiss();
                if (jsonResponse.optString("status").equals("true")) {
                    JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                    model = gson.fromJson(dataJsonObject.toString(), GroupInfoModel.class);
                    Picasso.get().load(model.getQr_code()).into(binding.qrCode);
                    helper.setCurrentGroup(model, true);

                    if (model.getIs_disable_qr_code().equals("1")) {
                        binding.disable.setVisibility(View.GONE);
                        binding.enable.setVisibility(View.VISIBLE);
                    } else if (model.getIs_disable_qr_code().equals("0")) {
                        binding.disable.setVisibility(View.VISIBLE);
                        binding.enable.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

        binding.enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.enable.setVisibility(View.GONE);
                changeStateOfQR("0");
            }
        });

        binding.disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.disable.setVisibility(View.GONE);
                changeStateOfQR("1");
            }
        });

        binding.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                Call<JsonObject> call1 = ApiController.getInstance(requireContext()).getapi().
                        refreshQRCode(currentGroup.getGroup_id());

                call1.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        JSONObject jsonResponse = ApiSet.getResponseData(response);
                        dialog.dismiss();
                        if (jsonResponse.optString("status").equals("true")) {
                            JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                            assert dataJsonObject != null;
                            Picasso.get().load(dataJsonObject.optString("qr_code")).into(binding.qrCode);
                            model.setQr_code(dataJsonObject.optString("qr_code"));
                            model.setKey_qrcode(dataJsonObject.optString("key_qrcode"));

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        });


        return binding.getRoot();
    }

    public void changeStateOfQR(String state) {
        dialog.show();
        Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi().
                disableEnableQRCode(model.getId(), state);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                dialog.dismiss();
                if (jsonResponse.optString("status").equals("true")) {
                    JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                    if (dataJsonObject.optString("is_disable_qr_code").equals("1")) {
                        binding.disable.setVisibility(View.GONE);
                        binding.enable.setVisibility(View.VISIBLE);

                    } else if (dataJsonObject.optString("is_disable_qr_code").equals("0")) {
                        binding.disable.setVisibility(View.VISIBLE);
                        binding.enable.setVisibility(View.GONE);

                    }
                }
                Toast.makeText(getContext(), jsonResponse.optString("message"), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });
    }
}