package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.PlanModel;
import com.ash.photobomb.API_Model_Classes.ProfileSubscriptionResponseModel;
import com.ash.photobomb.databinding.ActivityManageStorageBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageStorage extends AppCompatActivity implements PaymentResultListener {

    ActivityManageStorageBinding binding;
    AshDialog dialog;

    String totalSpace = "", usedString = "Used :- ",  totalString = "Total :- ",KB = "KB", MB = " MB ", GB = " GB ";

    boolean activate = false;
    int choosedPlan = 0;
    ArrayList<PlanModel> plansList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageStorageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new AshDialog(ManageStorage.this);
        plansList = new ArrayList<>();

        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));


        binding.renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activate) {
                    Checkout checkout = new Checkout();
                    checkout.setKeyID("rzp_test_UQGxMyQPJUBYqL");
                    checkout.setImage(R.drawable.user_logo);

                    // to get real amount convert cents into rupee
//                double finalAmount = Float.parseFloat(String.valueOf(1))*100;
                    double finalAmount = 100;

                    try {

                        JSONObject options = new JSONObject();
                        options.put("name", "ASH");
                        options.put("description", "ASH");
                        options.put("currency", "INR");
                        options.put("amount", finalAmount + " ");
                        options.put("prefill.email", "ashu.hasan155221@gmail.com");
                        options.put("prefill.contact", "9897161476");

                        checkout.open(ManageStorage.this, options);

                    } catch (Exception e) {
//                    throw new RuntimeException(e);
                        Toast.makeText(ManageStorage.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.gb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosedPlan = 0;
                updateSpaceValues(binding.gb1);
            }
        });
        binding.gb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosedPlan = 1;
                updateSpaceValues(binding.gb2);
            }
        });
        binding.gb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosedPlan = 2;
                updateSpaceValues(binding.gb3);
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.show();
        Call<JsonObject> call1 = ApiController.getInstance(ManageStorage.this).getapi().
                getProfileSubscription();

        call1.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Gson gson = new Gson();
                int totalStorageSize = 0;
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                dialog.dismiss();
                if (jsonResponse.optString("status").equals("true")) {
                    JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                    assert dataJsonObject != null;
                    ProfileSubscriptionResponseModel model = gson.fromJson(dataJsonObject.toString(), ProfileSubscriptionResponseModel.class);
                    binding.totalSpace.setText(totalString + model.getGivin_space() + GB);
                    binding.usedSpace.setText(usedString + model.getUsed_data_gb() + GB);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.storageLine.getLayoutParams();
                    params.weight = (float)((Double.parseDouble(model.getUsed_data_gb()))/Double.parseDouble(model.getGivin_space())) * 100;
                    binding.storageLine.setLayoutParams(params);

                    binding.storage.setText(model.getUsed_data_gb() + GB +"/" + model.getGivin_space() + GB);

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

        ApiController.getInstance(ManageStorage.this).getapi().getUserPlan().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                Gson gson = new Gson();
                plansList.clear();
                if (jsonResponse.optString("status").equals("true")){
                    try {
                        JSONArray dataJsonObject = jsonResponse.getJSONArray("data");

                        for (int index = 0; index <= Objects.requireNonNull(dataJsonObject).length(); index++){
                            try {
                                JSONObject group = dataJsonObject.getJSONObject(index);
                                PlanModel groupInfoModel = gson.fromJson(group.toString(), PlanModel.class);
                                plansList.add(groupInfoModel);
                            } catch (JSONException e) {
//                                throw new RuntimeException(e);
                            }
                        }
                    } catch (JSONException e) {
//                            throw new RuntimeException(e);
                    }


                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        back();
    }

    public void back(){
        Intent home= new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(home);
    }

    @Override
    public void onPaymentSuccess(String s) {
        dialog.show();
        ApiController.getInstance(ManageStorage.this).getapi().purchaseSubcriptionPlan(plansList.get(choosedPlan).getId(), plansList.get(choosedPlan).getValidity(),
                plansList.get(choosedPlan).getPaid_data(), "Razorpay", plansList.get(choosedPlan).getPaid_price(),
                String.valueOf(new Date().getDate()), "djbdui8eu3h83b").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                Toast.makeText(ManageStorage.this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();;
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(ManageStorage.this, "Payment Failure", Toast.LENGTH_SHORT).show();
    }

    public void updateSpaceValues(FrameLayout layout){
        activate = true;
        binding.gb1.setBackgroundResource(R.drawable.stork_storage_button_bg);
        binding.gb2.setBackgroundResource(R.drawable.stork_storage_button_bg);
        binding.gb3.setBackgroundResource(R.drawable.stork_storage_button_bg);
        layout.setBackgroundResource(R.drawable.no_stork_storage_button_bg);
        binding.renew.setBackgroundResource(R.drawable.button);
        binding.renew.setTextColor(Color.parseColor("#ffffff"));
    }
}