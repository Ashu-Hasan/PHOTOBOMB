package com.ash.photobomb.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.fragment.app.FragmentTransaction;

import androidx.recyclerview.widget.RecyclerView;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.Fragment.CreateGroup;
import com.ash.photobomb.GroupItemInfoPage;
import com.ash.photobomb.GroupSettings;
import com.ash.photobomb.MainActivity;
import com.ash.photobomb.R;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupItemAdapter extends RecyclerView.Adapter<GroupItemAdapter.ViewHolder> {

    Context context;
    ArrayList<GroupInfoModel> list;
    ConstraintLayout listLayout, addItemLayout;

    int replaceLayoutId;
    ImageView navigationButton;
    FragmentTransaction transaction;
    Activity activity;
    SharedPreferencesHelper conditionValue;
    String layout;

    AshDialog dialog;

    public GroupItemAdapter(Context context, ArrayList<GroupInfoModel> list, ConstraintLayout listLayout,
                            ConstraintLayout addItemLayout, int replaceLayoutId, ImageView navigationButton, FragmentTransaction transaction
            , Activity activity, String layout) {
        this.context = context;
        this.list = list;
        this.addItemLayout = addItemLayout;
        this.listLayout = listLayout;
        this.replaceLayoutId = replaceLayoutId;
        this.navigationButton = navigationButton;
        this.transaction = transaction;
        this.activity = activity;
        this.layout = layout;
        conditionValue = new SharedPreferencesHelper(activity);
        dialog = new AshDialog(context);
    }

    public GroupItemAdapter(Context context, ArrayList<GroupInfoModel> list, int replaceLayoutId, ImageView navigationButton, FragmentTransaction transaction
            , Activity activity, String layout) {
        this.context = context;
        this.list = list;
        this.replaceLayoutId = replaceLayoutId;
        this.navigationButton = navigationButton;
        this.transaction = transaction;
        this.activity = activity;
        this.layout = layout;
        conditionValue = new SharedPreferencesHelper(activity);
        dialog = new AshDialog(context);
    }


    @NonNull
    @Override
    public GroupItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item_design, parent, false);

        return new GroupItemAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull GroupItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(list.get(position).getName());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (layout.equals("H") && conditionValue.getCurrentUserData().getId().equals(list.get(position).getCreated_by())) {
                    PopupMenu p = new PopupMenu(context, view);
                    p.getMenuInflater().inflate(R.menu.group_item_menu, p.getMenu());

                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            if (menuItem.getItemId() == R.id.edit) {
                                navigationButton.setVisibility(View.GONE);
                                transaction.replace(replaceLayoutId, new CreateGroup(replaceLayoutId, navigationButton, activity, list.get(position)));
                                transaction.commit();
                                conditionValue.setConditionToSwitchLayout(1);
                            } else if (menuItem.getItemId() == R.id.delete) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Deleting Group")
                                        .setMessage("Are you sure you want to delete this group")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog1, int which) {
                                                // Positive button click handling, if needed
                                                dialog1.dismiss();
                                                dialog.show();
                                                Call<JsonObject> call = ApiController.getInstance(context).getapi()
                                                        .groupDelete(list.get(position).getGroup_id());

                                                call.enqueue(new Callback<JsonObject>() {
                                                    @SuppressLint("NotifyDataSetChanged")
                                                    @Override
                                                    public void onResponse(@NonNull retrofit2.Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                                        JSONObject jsonResponse = ApiSet.getResponseData(response);
                                                        dialog.dismiss();
                                                        if (jsonResponse != null) {
                                                            if (jsonResponse.optString("status").equals("true")) {
                                                                FirebaseDatabase.getInstance().getReference().child("Group_pictures").child(conditionValue.getCurrentUserData().getId())
                                                                        .child(list.get(position).getName()).removeValue();
                                                                list.remove(position);
                                                                notifyDataSetChanged();
                                                                if (list.size()<1) {
                                                                    listLayout.setVisibility(View.GONE);
                                                                    addItemLayout.setVisibility(View.VISIBLE);
                                                                }
                                                            }
                                                        } else {
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Negative button click handling, if needed
                                                dialog.dismiss();
                                            }
                                        });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                            }

                            return true;
                        }
                    });
                    p.show();
                }
                else {
                    PopupMenu p = new PopupMenu(context, view);
                    p.getMenuInflater().inflate(R.menu.open, p.getMenu());

                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.open) {
                               openItem(position);
                            }
                            return true;
                        }
                    });
                    p.show();
                }


                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openItem(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView groupImage, arrow;
        TextView title, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupImage = itemView.findViewById(R.id.groupImage);
            arrow = itemView.findViewById(R.id.arrow);
            title = itemView.findViewById(R.id.groupTitle);
            time = itemView.findViewById(R.id.time);

        }
    }

    public void openItem(int position){
        Intent intent = new Intent(context, GroupItemInfoPage.class);
        SharedPreferencesHelper currentGroup = new SharedPreferencesHelper(context);
//                Toast.makeText(context, list.get(position).getGroup_id(), Toast.LENGTH_SHORT).show();
        currentGroup.setCurrentGroup(list.get(position), false);
        activity.finish();
        context.startActivity(intent);
    }
}
