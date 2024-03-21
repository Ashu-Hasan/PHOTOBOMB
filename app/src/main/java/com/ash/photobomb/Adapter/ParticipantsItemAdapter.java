package com.ash.photobomb.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.API_Model_Classes.GroupMemberModel;
import com.ash.photobomb.Constructor.NotificationItemDetails;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.R;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipantsItemAdapter extends RecyclerView.Adapter<ParticipantsItemAdapter.ViewHolder> {

    Context context;
    ArrayList<GroupMemberModel> list;
    SharedPreferencesHelper helper;

    public ParticipantsItemAdapter(Context context, ArrayList<GroupMemberModel> list) {
        this.context = context;
        this.list = list;
        helper = new SharedPreferencesHelper(context);
    }

    @NonNull
    @Override
    public ParticipantsItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant_list_item_design,parent,false);

        return new ParticipantsItemAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ParticipantsItemAdapter.ViewHolder holder, int position) {
        holder.Name.setText(list.get(position).getName());
        holder.Number.setText(list.get(position).getCountry_code()+" "+list.get(position).getMobile());
        if (list.get(position).getIs_admin().equals("1") || !list.get(position).getCreated().equals(helper.getCurrentUserData().getId())){
            holder.makeAdmin.setVisibility(View.GONE);
        }


        holder.makeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesHelper helper = new SharedPreferencesHelper(context);
                GroupInfoModel currentGroup = helper.getCurrentGroup();
                User user = helper.getCurrentUserData();
                AshDialog dialog = new AshDialog(context);
                dialog.show();

                Call<JsonObject> call = ApiController.getInstance(context).getapi().
                        makeGroupAdmin(currentGroup.getGroup_id(), currentGroup.getId(), user.getId());

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JSONObject jsonResponse = ApiSet.getResponseData(response);

                        if (jsonResponse.optString("status").equals("true")){
                            holder.makeAdmin.setVisibility(View.GONE);
                            dialog.dismiss();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name, Number;
        ImageView makeAdmin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.name);
            Number = itemView.findViewById(R.id.number);
            makeAdmin = itemView.findViewById(R.id.makeAdmin);

        }
    }
}
