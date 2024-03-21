package com.ash.photobomb.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.photobomb.InfoPage;
import com.ash.photobomb.R;
import com.ash.photobomb.SignIn;


import java.util.ArrayList;

public class InfoPageVPAdapter extends RecyclerView.Adapter<InfoPageVPAdapter.ViewHolder> {

    ArrayList<String> viewPagerItemArrayList;
    Context context;

    public InfoPageVPAdapter(Context context,ArrayList<String> viewPagerItemArrayList) {
        this.context = context;
        this.viewPagerItemArrayList = viewPagerItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewpager_item_design,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.description.setText(viewPagerItemArrayList.get(position));
        holder.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("is_intro_done", Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("condition", true).apply();
                Intent intent = new Intent(context, SignIn.class);
                InfoPage infoPage = (InfoPage) context;
                infoPage.finish();
                infoPage.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return viewPagerItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, description;
        Button skip;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);

            skip = itemView.findViewById(R.id.skip);

        }
    }



}
