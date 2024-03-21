package com.ash.photobomb;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.ash.photobomb.databinding.ActivitySuccessfullyPageBinding;

import java.util.Objects;

public class SuccessfullyPage extends AppCompatActivity {

    ActivitySuccessfullyPageBinding binding;
    String layout = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuccessfullyPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#1A1A1A"));

        Intent data = getIntent();
        if (data != null && data.getStringExtra("layout") != null) {
            layout = data.getStringExtra("layout");
            if (layout != null && layout.equals("registration")) {
                binding.description.setText(data.getStringExtra("description"));
                binding.ok.setText("Done");
            }
            else {
                binding.ok.setText("Go Login");
            }
        }

        binding.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextActivity();
            }
        });

    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        nextActivity();
    }

    public void nextActivity(){
        Intent main;
        if (layout.equals("registration")){
            main = new Intent(getApplicationContext(), MainActivity.class);
        }
        else {
            main = new Intent(getApplicationContext(), SignIn.class);
        }
        finish();
        startActivity(main);
    }
}