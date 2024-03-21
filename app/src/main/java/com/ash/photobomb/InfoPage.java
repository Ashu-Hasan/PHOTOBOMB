package com.ash.photobomb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


import com.ash.photobomb.Adapter.InfoPageVPAdapter;
import com.ash.photobomb.databinding.ActivityInfoPageBinding;

import java.util.ArrayList;

public class InfoPage extends AppCompatActivity {

    ActivityInfoPageBinding binding;
    ArrayList<String> viewPagerItemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.dot1.setImageResource(R.drawable.long_circle_dot);


        // to removing  StatusBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        String[] desc = {"Photo Gallery"};
        String des = "Photobombing is the act of purposelyn putting oneself into the view of a photograph";

        viewPagerItemArrayList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            viewPagerItemArrayList.add(des);

        }

        InfoPageVPAdapter infoPageVpAdapter = new InfoPageVPAdapter(InfoPage.this, viewPagerItemArrayList);
        binding.viewpager.setAdapter(infoPageVpAdapter);

        binding.viewpager.setClipChildren(false);
        binding.viewpager.stopNestedScroll();

        binding.viewpager.setOffscreenPageLimit(2);

        binding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        binding.button.setText("Next");
                        setDot(binding.dot1);
                        break;

                    case 1:
                        setDot(binding.dot2);
                        binding.button.setText("Next");
                        break;

                    case 2:
                        setDot(binding.dot3);
                        binding.button.setText("Get Started");
                        break;

                }
            }
        });


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.button.getText().toString().equals("Get Started")) {
                    SharedPreferences sharedPreferences = getSharedPreferences("is_intro_done", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean("condition", true).apply();
                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                    finish();
                    startActivity(intent);
                } else {
                    if (binding.viewpager.getCurrentItem() == 1) {
                       /* binding.dot2.setImageResource(R.drawable.circle_dot);
                        binding.dot3.setImageResource(R.drawable.long_circle_dot);*/
                        binding.button.setText("Get Started");
                    }
                    binding.viewpager.setCurrentItem(binding.viewpager.getCurrentItem() + 1);
                }

            }
        });

    }

    public void setDot(ImageView imageView) {
        binding.dot1.setImageResource(R.drawable.circle_dot);
        binding.dot2.setImageResource(R.drawable.circle_dot);
        binding.dot3.setImageResource(R.drawable.circle_dot);

        imageView.setImageResource(R.drawable.long_circle_dot);
    }

}