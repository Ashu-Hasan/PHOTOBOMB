package com.ash.photobomb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.Fragment.BottomFiles;
import com.ash.photobomb.databinding.ActivityScanQrcodeBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.ash.photobomb.other.barcodereader.BarcodeCapture;
import com.ash.photobomb.other.barcodereader.BarcodeGraphic;
import com.ash.photobomb.other.barcodereader.mobilevisionbarcodescanner.BarcodeRetriever;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQRCode extends AppCompatActivity implements BarcodeRetriever {

    ActivityScanQrcodeBinding binding;
    BarcodeCapture barcodeCapture;
    int checkBarcode = 0;

    AshDialog dialog;
    SharedPreferencesHelper helper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanQrcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // to change StatusBar Color
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));

        helper = new SharedPreferencesHelper(getApplicationContext());
        dialog = new AshDialog(ScanQRCode.this);

        barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(R.id.barcode);

        assert barcodeCapture != null;
        barcodeCapture.setRetrieval(this);


        binding.flashOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.flashOFF.setVisibility(View.GONE);
                binding.flashON.setVisibility(View.VISIBLE);
                barcodeCapture.setShowFlash(true);
                barcodeCapture.refresh(true);
            }
        });

        binding.flashON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.flashOFF.setVisibility(View.VISIBLE);
                binding.flashON.setVisibility(View.GONE);
                barcodeCapture.setShowFlash(false);
                barcodeCapture.refresh(true);
            }
        });

        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(main);
            }
        });
    }

    @Override
    public void onRetrieved(Barcode barcode) {
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
              /*  String message = barcode.displayValue;
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRCode.this)
                        .setTitle("code retrieved")
                        .setMessage(message);
                builder.show();*/

                // keys to to data.. id, name, key_qrcode, expiring_date, grp_join_appr_wall, created_by, is_start_now, strt_date, is_freeze
                if (!barcode.displayValue.isEmpty()){
                    if (checkBarcode == 0){
                        checkBarcode = 1;
                        barcodeCapture.stopScanning();
                        dialog.show();
                        JSONObject qrResult;
                        try {
                            qrResult = new JSONObject(barcode.displayValue);

                            Call<JsonObject> call = ApiController.getInstance(ScanQRCode.this).getapi().getGroupDetail(qrResult.optString("id"));

                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                    Gson gson = new Gson();
                                    JSONObject jsonResponse = ApiSet.getResponseData(response);
                                    if (jsonResponse.optString("status").equals("true")){
                                        JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                                        GroupInfoModel groupInfoModel = gson.fromJson(Objects.requireNonNull(dataJsonObject).toString(), GroupInfoModel.class);
                                        if (!qrResult.optString("key_qrcode").equals(groupInfoModel.getKey_qrcode())){
                                            showDialog("This QR has been expired");
                                        }else if (qrResult.optString("is_freeze").equals("1")){
                                            showDialog("This group has been freeze, you can't join it right now.");
                                        }
                                        else if (qrResult.optString("grp_join_appr_wall").equals("1")){
                                            sendGroupJoinRequest(qrResult, groupInfoModel);
                                        }
                                        else if (qrResult.optString("grp_join_appr_wall").equals("0")){

                                            Call<JsonObject> call1 = ApiController.getInstance(ScanQRCode.this).getapi()
                                                    .addGroupMember(qrResult.optString("id"), helper.getCurrentUserData().getId());
                                            call1.enqueue(new Callback<JsonObject>() {
                                                @Override
                                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                    dialog.dismiss();
                                                    helper.setCurrentGroup(groupInfoModel, true);
                                                    Intent groupInfo = new Intent(getApplicationContext(), GroupItemInfoPage.class);
                                                    finish();
                                                    startActivity(groupInfo);
                                                }

                                                @Override
                                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                    else {
                                        dialog.dismiss();
                                        showDialog("This group is no longer exist");
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    dialog.dismiss();
                                }
                            });

                        } catch (JSONException e) {
                            //throw new RuntimeException(e);
                            dialog.dismiss();
                        }

                    }
                }
            }
        });
        barcodeCapture.stopScanning();
    }

    @Override
    public void onRetrievedMultiple(Barcode closetToClick, List<BarcodeGraphic> barcodeGraphics) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = "Code selected : " + closetToClick.displayValue + "\n\nother " +
                        "codes in frame include : \n";
                for (int index = 0; index < barcodeGraphics.size(); index++) {
                    Barcode barcode = barcodeGraphics.get(index).getBarcode();
                    message += (index + 1) + ". " + barcode.displayValue + "\n";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRCode.this)
                        .setTitle("code retrieved")
                        .setMessage(message);
                builder.show();
            }
        });
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            Barcode barcode = sparseArray.valueAt(i);
            Log.e("value", barcode.displayValue);
        }
    }

    @Override
    public void onRetrievedFailed(String reason) {

    }

    @Override
    public void onPermissionRequestDenied() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(main);
    }

    public void showDialog(String message){
        dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRCode.this)
                        .setTitle("code retrieved")
                        .setCancelable(false)
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkBarcode = 0;
                                dialogInterface.dismiss();
                                barcodeCapture.refresh(true);
                            }
                        });
                builder.show();

    }

    public void sendGroupJoinRequest(JSONObject jsonObject, GroupInfoModel groupInfoModel){
        Call<JsonObject> call = ApiController.getInstance(ScanQRCode.this).getapi().
                sendGroupJoinRequest(jsonObject.optString("created_by"), "2", jsonObject.optString("id"),
                        jsonObject.optString("key_qrcode"), helper.getCurrentUserData().getId());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                dialog.dismiss();
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                if (jsonResponse.optString("status").equals("true") ){
                    showDialog("Request has been send to admin");
                }
                else if (jsonResponse.optString("status").equals("false") &&
                        jsonResponse.optString("message").toLowerCase(Locale.ROOT).
                                equalsIgnoreCase("Already requested".toLowerCase(Locale.ROOT))) {
                    showDialog("Request already has been send to admin");
                } else {
                    if (jsonResponse.optString("message").toLowerCase(Locale.ROOT).
                            equalsIgnoreCase("Already member of that group".toLowerCase(Locale.ROOT))){
                        helper.setCurrentGroup(groupInfoModel, true);
                        Intent groupInfo = new Intent(getApplicationContext(), GroupItemInfoPage.class);
                        finish();
                        startActivity(groupInfo);
                    }
                    else {
                        showDialog("Unable to read QR");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                dialog.dismiss();
            }
        });
    }
}