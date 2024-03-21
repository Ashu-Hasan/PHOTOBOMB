package com.ash.photobomb.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.R;
import com.ash.photobomb.databinding.FragmentContactUsBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ContactUs extends Fragment {

    FragmentContactUsBinding binding;
    AshDialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactUsBinding.inflate(getLayoutInflater(), container, false);
        dialog = new AshDialog(getContext());

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getContext());
        User user = sharedPreferencesHelper.getCurrentUserData();

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                // call request for update the data of user profile from the data base by api

                Call<JsonObject> call = ApiController.getInstance(getContext()).getapi()
                        .sendFeedback(user.getName(), user.getEmail(), "+91", user.getMobile(),
                                binding.feedbackMessage.getText().toString().trim());

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        dialog.dismiss();
                        JSONObject jsonResponse = ApiSet.getResponseData(response);

                        if (jsonResponse != null) {
                            Toast.makeText(getContext(), jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        dialog.dismiss();
                    }
                });

            }
        });

        return binding.getRoot();
    }
}