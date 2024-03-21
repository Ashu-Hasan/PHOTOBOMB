package com.ash.photobomb.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.photobomb.R;
import com.ash.photobomb.other.StorageFiles.Constant;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PhoneGalleryImageAdapter extends RecyclerView.Adapter<PhoneGalleryImageAdapter.ViewHolder> {

    Context context;
    boolean selectState = false;
    boolean selectMax = false;
    int selectedItemCount = 0;
    public static ArrayList<Integer> selectedItem;
    CheckBox checkBox;

    public PhoneGalleryImageAdapter(Context context, CheckBox checkBox, boolean selectMax) {
        this.context = context;
        this.selectMax = selectMax;
        this.checkBox = checkBox;
        selectedItem = new ArrayList<>();
        if (selectMax){
            selectState = true;
        }
    }

    @NonNull
    @Override
    public PhoneGalleryImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_gellery_image_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneGalleryImageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //we will load thumbnail using glid library
        Uri uri = Uri.fromFile(Constant.allMediaList.get(position));
        if (selectMax && position < 10 ){
            holder.checkImage.setVisibility(View.VISIBLE);
            selectedItemCount +=1;
            selectedItem.add(position);
        }

        Glide.with(context)
                .load(uri).thumbnail(0.1f).into(((ViewHolder)holder).albumImage);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!selectState) {
                    selectState = true;
                    selectedItemCount +=1;
                    selectedItem.add(position);
                    holder.checkImage.setVisibility(View.VISIBLE);
//                    Toast.makeText(context, Constant.allMediaList.get(position).getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (selectState){
                        if (holder.checkImage.getVisibility() == View.GONE){
                            selectedItemCount +=1;
                            if (selectedItemCount < 11){
                                if (selectedItemCount == 10){checkBox.setChecked(true);}
                                holder.checkImage.setVisibility(View.VISIBLE);
                                selectedItem.add(position);
                            }
                            else {
                                Toast.makeText(context, "You have selected Max items", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            checkBox.setChecked(false);
                            holder.checkImage.setVisibility(View.GONE);
                            selectedItemCount -=1;
                            selectedItem.remove(position);
                            if (selectedItemCount <1){selectState = false;}
                        }
                    }
                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return Constant.allMediaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView albumImage, checkImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.albumImage);
            checkImage = itemView.findViewById(R.id.albumCheckImage);
        }
    }
}
