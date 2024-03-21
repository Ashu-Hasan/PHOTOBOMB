package com.ash.photobomb.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.PendingRequestModel;
import com.ash.photobomb.Constructor.NotificationItemDetails;
import com.ash.photobomb.R;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingRequestItemAdapter extends RecyclerView.Adapter<PendingRequestItemAdapter.ViewHolder> {

    Context context;
    ArrayList<PendingRequestModel> list;
    AshDialog dialog;

    public PendingRequestItemAdapter(Context context, ArrayList<PendingRequestModel> list) {
        this.context = context;
        this.list = list;
        dialog = new AshDialog(context);
    }

    @NonNull
    @Override
    public PendingRequestItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_requests_item_design,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingRequestItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PendingRequestModel request = list.get(position);
        holder.title.setText(list.get(position).getName());
        holder.time.setText(ApiSet.convertTimestampIntoDate(Long.parseLong(list.get(position).getCreated()), " "));

        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrofit2.Call<JsonObject> call = ApiController.getInstance(context).getapi().performRequestAction(request.getUser_id(),
                        request.getGroup_id(), request.getId(), "1");

                runRequestCall(call, position);
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrofit2.Call<JsonObject> call = ApiController.getInstance(context).getapi().performRequestAction(request.getUser_id(),
                        request.getGroup_id(), request.getId(), "0");

                runRequestCall(call, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, time;
        ImageView ok, cancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            ok = itemView.findViewById(R.id.accept);
            cancel = itemView.findViewById(R.id.reject);
        }
    }

    public void runRequestCall(retrofit2.Call<JsonObject> call, int position){
        dialog.show();
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonResponse = ApiSet.getResponseData(response);
                dialog.dismiss();
                if (jsonResponse.optString("status").equals("1")){
                    list.remove(position);
                    notifyItemRemoved(position);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

}
