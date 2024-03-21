package com.ash.photobomb.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.MediaDataModel;
import com.ash.photobomb.Constructor.AlbumItem;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;

import com.ash.photobomb.GroupItemInfoPage;
import com.ash.photobomb.R;
import com.ash.photobomb.ViewImage;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupItemImageAdapter extends RecyclerView.Adapter<GroupItemImageAdapter.ViewHolder> {

    Activity context;
    ArrayList<AlbumItem> itemList, albumSetToRefresh;

    ArrayList<MediaDataModel> imageList;

    ArrayList<String> dateList;
    int designValue;
    RecyclerView imageGroupRecycler;
    SharedPreferencesHelper helper;

    public GroupItemImageAdapter(){}


    public GroupItemImageAdapter(Activity context,  ArrayList<AlbumItem> ItemList, RecyclerView imageGroupRecycler, int designValue, boolean container){
        this.context = context;
        this.itemList = ItemList;
        this.designValue = designValue;
        this.imageGroupRecycler = imageGroupRecycler;
        helper = new SharedPreferencesHelper(context);
    }
    public GroupItemImageAdapter(Activity context,  ArrayList<AlbumItem> albumSetToRefresh, ArrayList<MediaDataModel> imageList, RecyclerView imageGroupRecycler, int designValue){
        this.context = context;
        this.albumSetToRefresh = albumSetToRefresh;
        this.designValue = designValue;
        this.imageList = imageList;
        this.imageGroupRecycler = imageGroupRecycler;
        helper = new SharedPreferencesHelper(context);
    }

    public GroupItemImageAdapter(Activity context, ArrayList<MediaDataModel> imageList, int designValue){
        this.context = context;
        this.imageList = imageList;
        this.designValue = designValue;
        helper = new SharedPreferencesHelper(context);
    }


    @NonNull
    @Override
    public GroupItemImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (designValue == 1){
            // 1 for full album design
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_item_image_item_design,parent,false);
        }
        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_item_image_design,parent,false);
        }

        return new GroupItemImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupItemImageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (designValue == 1){
            ArrayList<MediaDataModel> tempImageList = itemList.get(position).getImageList();
            holder.date.setText(itemList.get(position).getDate());

            Collections.reverse(tempImageList);

            GroupItemImageAdapter adapter = new GroupItemImageAdapter(context, itemList, itemList.get(position).getImageList(), imageGroupRecycler,2);
            holder.imageRecyclerView.setAdapter(adapter);

            GridLayoutManager manager = new GridLayoutManager(context, 3);
            holder.imageRecyclerView.setLayoutManager(manager);

        }

        else {
            Picasso.get().load(Uri.parse(imageList.get(position).getMedia_url())).into(holder.image);
//            holder.image.setImageURI(Uri.parse(imageList.get(position)));
        }

        if (designValue != 1){
            holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public boolean onLongClick(View view) {

                    AshDialog dialog = new AshDialog(context, "Deleting", "Please wait");

                    Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu1);
                    MenuBuilder builder = new MenuBuilder(context);
                    MenuInflater inflater = new MenuInflater(context);
                    inflater.inflate(R.menu.delete_item, builder);

                    MenuPopupHelper helper1 = new MenuPopupHelper(wrapper, builder, view);
                    helper1.setForceShowIcon(true);


                    builder.setCallback(new MenuBuilder.Callback() {
                        @Override
                        public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                            if (item.getItemId() == R.id.delete){
                                dialog.show();

                                Call<JsonObject> call = ApiController.getInstance(context)
                                        .getapi().deleteGroupMediaFile(helper.getCurrentGroup().getGroup_id(), imageList.get(position).getId());

                                call.enqueue(new Callback<JsonObject>() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                        JSONObject jsonResponse = ApiSet.getResponseData(response);
                                        if (jsonResponse.optString("status").equals("true")){
                                            int itemSet = imageList.get(position).getImageSetPosition();
                                            imageList.remove(position);
                                            if (imageList.size() < 1){
                                                albumSetToRefresh.remove(itemSet);
                                                GroupItemImageAdapter adapter = new GroupItemImageAdapter(context, albumSetToRefresh, imageGroupRecycler, 1, false);
                                                imageGroupRecycler.setAdapter(adapter);

                                                LinearLayoutManager manager = new LinearLayoutManager(context);
                                                imageGroupRecycler.setLayoutManager(manager);
                                            }
                                            notifyDataSetChanged();
                                            dialog.dismiss();
                                        }
                                        else {
                                            dialog.dismiss();
                                            Toast.makeText(context, "There is a problem with server", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                            return true;
                        }

                        @Override
                        public void onMenuModeChange(@NonNull MenuBuilder menu) {

                        }
                    });

                    helper1.show(-300,-280);

                    return true;
                }
            });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent viewImage = new Intent(context, ViewImage.class);
                    helper.setCurrentMedia(imageList.get(position));
                    context.finish();
                    context.startActivity(viewImage);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (designValue == 1){size = itemList.size();}
        else {size = imageList.size();}
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        RecyclerView imageRecyclerView;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (designValue == 1){
                date = itemView.findViewById(R.id.date);
                imageRecyclerView = itemView.findViewById(R.id.albumRecyclerView);
            }
            else {
                image = itemView.findViewById(R.id.albumImage);
            }
        }
    }
}
