package com.ash.photobomb.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.API_Model_Classes.NotificationModel;
import com.ash.photobomb.Constructor.NotificationItemDetails;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.GroupItemDetailPage;
import com.ash.photobomb.R;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    Activity activity;

    ArrayList<NotificationModel> list;

    AshDialog dialog;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        dialog = new AshDialog(activity);
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item_design,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (list.get(position).getIs_read().equals("0")){
            holder.mainLayout.setBackgroundColor(Color.parseColor("#51AEFE"));
        }
        holder.notificationTitle.setText(list.get(position).getName());
        holder.notificationDescription.setText(list.get(position).getDescription());
        String date = ApiSet.convertTimestampIntoDate(Long.parseLong(list.get(position).getModified_time()), "date");
        holder.notificationTime.setText(date);
        Picasso.get().load(Uri.parse(list.get(position).getImage())).placeholder(R.drawable.logo).into(holder.itemImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                SharedPreferencesHelper helper = new SharedPreferencesHelper(context);
                 Call<JsonObject> call =  ApiController.getInstance(context).getapi().
                                        getGroupDetail(list.get(position).getGroup_id());

                                call.enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        Gson gson = new Gson();
                                        JSONObject jsonResponse = ApiSet.getResponseData(response);

                                        if (jsonResponse.optString("status").equals("true")) {

                                            try {
                                                JSONObject group = jsonResponse.getJSONObject("data");
                                                GroupInfoModel data = gson.fromJson(group.toString(), GroupInfoModel.class);
                                                data.setGroup_id(data.getId());
                                                helper.clearCurrentGroup();
                                                helper.setCurrentGroup(data, true);

                                                Call<JsonObject> call1 =  ApiController.getInstance(context).getapi().
                                                        deleteNotification(list.get(position).getId());

                                                call1.enqueue(new Callback<JsonObject>() {
                                                    @Override
                                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                        dialog.dismiss();
                                                        JSONObject jsonResponse = ApiSet.getResponseData(response);

                                                        if (jsonResponse.optString("status").equals("true")) {
                                                            Intent detail = new Intent(activity, GroupItemDetailPage.class);
                                                            detail.putExtra("layout", "Notification");
                                                            activity.finish();
                                                            activity.startActivity(detail);
                                                        }
                                                        else {
                                                            dialog.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }


                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                        dialog.dismiss();
                                    }
                                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        ConstraintLayout mainLayout;
        TextView notificationTitle, notificationDescription, notificationTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.itemImage);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationDescription = itemView.findViewById(R.id.notificationDescription);
            notificationTime = itemView.findViewById(R.id.notificationTime);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
