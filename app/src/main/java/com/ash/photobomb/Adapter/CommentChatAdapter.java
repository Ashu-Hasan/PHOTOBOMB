package com.ash.photobomb.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.CommentMessageModel;
import com.ash.photobomb.Comments;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.Fragment.ReplyOfCommentsItem;
import com.ash.photobomb.R;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentChatAdapter extends RecyclerView.Adapter{

    ArrayList<CommentMessageModel> messageModels;
    Activity context;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;
    SharedPreferencesHelper helper;
    boolean reply = false;
    int replaceId;
    FragmentManager managerSupport;

    AshDialog dialog;

    public CommentChatAdapter(ArrayList<CommentMessageModel> messageModels, Activity context,int replaceId, FragmentManager managerSupport, boolean reply) {
        this.messageModels = messageModels;
        this.context = context;
        this.reply = reply;
        this.replaceId = replaceId;
        this.managerSupport = managerSupport;
        helper = new SharedPreferencesHelper(context);
        dialog = new AshDialog(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        User currentUser = helper.getCurrentUserData();
        int result = 0;

        if (!reply){
            if (currentUser.getId().equals(messageModels.get(position).getUser_id()))
            {
                result = SENDER_VIEW_TYPE;
            }
            else {
                result =  RECEIVER_VIEW_TYPE;
            }
        }
        else {result = SENDER_VIEW_TYPE;}
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        CommentMessageModel model = messageModels.get(position);

        if (reply){
            ((SenderViewHolder) holder).replyComment.setVisibility(View.GONE);
        }
        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).receiverUserName.setText(model.getUser_name());
            ((SenderViewHolder) holder).senderMsg.setText(model.getComments());
            ((SenderViewHolder) holder).senderTime.setText(ApiSet.convertTimestampIntoDate(Long.parseLong(model.getModified()), "time"));

        } else {
            ((ReceiverViewHolder) holder).receiverUserName.setText(model.getUser_name());
            ((ReceiverViewHolder) holder).receiverMsg.setText(model.getComments());
            ((ReceiverViewHolder) holder).receiverTime.setText(ApiSet.convertTimestampIntoDate(Long.parseLong(model.getModified()), "time"));

        }

        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).editComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                        Toast.makeText(context, model.getParent_id(), Toast.LENGTH_SHORT).show();
                    ((SenderViewHolder) holder).editLayout.setVisibility(View.GONE);
                    ((SenderViewHolder) holder).saveLayout.setVisibility(View.VISIBLE);

                    ((SenderViewHolder) holder).editCommentEditText.setText(((SenderViewHolder) holder).senderMsg.getText().toString());

                }
            });

            ((SenderViewHolder) holder).cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SenderViewHolder) holder).editLayout.setVisibility(View.VISIBLE);
                    ((SenderViewHolder) holder).saveLayout.setVisibility(View.GONE);

                }
            });

            ((SenderViewHolder) holder).save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!((SenderViewHolder) holder).editCommentEditText.getText().toString().trim().isEmpty()) {
                        dialog.show();
                        retrofit2.Call<JsonObject> call = ApiController.getInstance(context).getapi()
                                .editMediaComment(helper.getCurrentGroup().getGroup_id(), helper.getCurrentMedia().getId(),
                                        messageModels.get(position).getId(), ((SenderViewHolder) holder).editCommentEditText.getText().toString().trim());

                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull retrofit2.Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                JSONObject jsonResponse = ApiSet.getResponseData(response);
                                dialog.dismiss();
                                if (jsonResponse != null) {
                                    if (jsonResponse.optString("status").equals("true")) {
                                        ((SenderViewHolder) holder).editLayout.setVisibility(View.VISIBLE);
                                        ((SenderViewHolder) holder).saveLayout.setVisibility(View.GONE);

                                        ((SenderViewHolder) holder).senderMsg.setText(((SenderViewHolder) holder).editCommentEditText.getText().toString());
                                    }
                                } else {

                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
                    } else {
                        Toast.makeText(context, "Please enter something", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ((SenderViewHolder) holder).deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    //Uncomment the below code to Set the message and title from the strings.xml file
                    builder.setMessage("Are you sour you").setTitle("DeletingData");

                    //Setting message manually and performing action on button click
                    builder.setMessage("Do you want to close this application ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog1, int id) {
                                    dialog1.dismiss();
                                    dialog.show();
                                    retrofit2.Call<JsonObject> call = ApiController.getInstance(context).getapi()
                                            .deleteMediaGroupComment(helper.getCurrentGroup().getGroup_id(), helper.getCurrentMedia().getId(),
                                                    messageModels.get(position).getId());

                                    call.enqueue(new Callback<JsonObject>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onResponse(@NonNull retrofit2.Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                            JSONObject jsonResponse = ApiSet.getResponseData(response);
                                            dialog.dismiss();
                                            if (jsonResponse != null) {
                                                if (jsonResponse.optString("status").equals("true")) {
                                                    messageModels.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            } else {

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<JsonObject> call, Throwable t) {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("AlertDialogExample");
//                    alert.getWindow().getAttributes().windowAnimations = R.style.MyDialogAnimation;

                    alert.show();

                }
            });

            ((SenderViewHolder) holder).replyComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = managerSupport;

                    ReplyOfCommentsItem popupFragment = new ReplyOfCommentsItem(context, String.valueOf(model.getId()), fragmentManager);
                    popupFragment.setCancelable(false);
                    popupFragment.show(fragmentManager, "popupFragment");

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView receiverMsg, receiverTime, receiverUserName;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMsg = itemView.findViewById(R.id.receiver);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            receiverUserName = itemView.findViewById(R.id.userName);
        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMsg, senderTime, senderDate, editComment, deleteComment, replyComment, save, cancel, receiverUserName;
        LinearLayout saveLayout, editLayout;
        EditText editCommentEditText;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsg = itemView.findViewById(R.id.sender);
            senderTime = itemView.findViewById(R.id.time);
            senderDate = itemView.findViewById(R.id.date);
            editComment = itemView.findViewById(R.id.editButton);
            deleteComment = itemView.findViewById(R.id.delete);
            replyComment = itemView.findViewById(R.id.reply);
            saveLayout = itemView.findViewById(R.id.saveLayout);
            save = itemView.findViewById(R.id.save);
            cancel = itemView.findViewById(R.id.cancel);
            editLayout = itemView.findViewById(R.id.editLayout);
            editCommentEditText = itemView.findViewById(R.id.editEditText);
            receiverUserName = itemView.findViewById(R.id.userName);
        }
    }

}
