package com.ash.photobomb.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.MediaDataModel;
import com.ash.photobomb.Comments;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.GroupItemDetailPage;
import com.ash.photobomb.R;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.ash.photobomb.other.StorageFiles.ImageDownloader;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewImageAdapter extends RecyclerView.Adapter<ViewImageAdapter.ViewHolder> {

    ArrayList<MediaDataModel> imageSet;
    Activity context;
    SharedPreferencesHelper helper;
    AshDialog dialog;

    public ViewImageAdapter(Activity context, ArrayList<MediaDataModel> imageSet) {
        this.imageSet = imageSet;
        this.context = context;
        dialog = new AshDialog(context);
        helper = new SharedPreferencesHelper(context);
    }

    @NonNull
    @Override
    public ViewImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_image_design, parent, false);

        return new ViewImageAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewImageAdapter.ViewHolder holder, int position) {
        MediaDataModel listItem = imageSet.get(position);


        Picasso.get().load(Uri.parse(listItem.getMedia_url())).into(holder.image);
        if (listItem.getIs_like().equals("true")) {
            holder.likeIcon.setImageResource(R.drawable.like_heart_icon);
            helper.setConditionToCheckLike(listItem.getId());
        }
        holder.likeCount.setText(listItem.getLikes_count() + " Likes");
        holder.commentCount.setText(listItem.getComment_count() + " Comments");


        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                Call<JsonObject> call;

                if (helper.getConditionToCheckLike(listItem.getId()).equals("Y")) {
                    call = ApiController.getInstance(context).getapi().
                            unlikeMediaGroup(helper.getCurrentGroup().getGroup_id(), listItem.getId());
                } else {
                    call = ApiController.getInstance(context).getapi().
                            likeMediaGroup(helper.getCurrentGroup().getGroup_id(), listItem.getId());

                }

                call.enqueue(new Callback<JsonObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JSONObject jsonResponse = ApiSet.getResponseData(response);
                        dialog.dismiss();
                        if (jsonResponse.optString("status").equals("true")) {
                            JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                            if (dataJsonObject != null) {
                                holder.likeCount.setText(dataJsonObject.optString("likes_count") + " Likes");
                                if (helper.getConditionToCheckLike(listItem.getId()).equals("Y")) {
                                    helper.clearConditionToCheckLike(listItem.getId());
                                    holder.likeIcon.setImageResource(R.drawable.unlike_heart_icon);

                                } else {
                                    helper.setConditionToCheckLike(listItem.getId());
                                    holder.likeIcon.setImageResource(R.drawable.like_heart_icon);

                                }
                            }
                        } else {
                            Toast.makeText(context, "Unable to perform your task", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(context, "e", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent comments = new Intent(context, Comments.class);
                context.finish();
                context.startActivity(comments);
            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);
                MenuBuilder builder = new MenuBuilder(context);
                MenuInflater inflater = new MenuInflater(context);
                if (listItem.getAdded_by().equals(helper.getCurrentUserData().getId())){
                    inflater.inflate(R.menu.view_image_page_menu_for_admin, builder);
                }
                else {inflater.inflate(R.menu.view_image_page_menu_for_user, builder);}

                MenuPopupHelper menuPopupHelper = new MenuPopupHelper(wrapper, builder, view);
                menuPopupHelper.setForceShowIcon(true);


                builder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.groupInfo) {
                            Intent groupDetail = new Intent(context, GroupItemDetailPage.class);
                            context.finish();
                            context.startActivity(groupDetail);
                        } else if (item.getItemId() == R.id.share) {
                            Uri baseUri = Uri.parse("https://play.google.com/store/apps/details?id=com.ash.help_me_in_study");

                            // Add additional parameters to the baseUri
                            Uri updatedUri = baseUri.buildUpon()
                                    .appendQueryParameter("groupTd", helper.getCurrentGroup().getGroup_id())
                                    .appendQueryParameter("mediaId", listItem.getId())
                                    .appendQueryParameter("mediaUrl", listItem.getMedia_url())
                                    .appendQueryParameter("link", "yes")
                                    .build();

                            // Generate a dynamic link
                            FirebaseDynamicLinks.getInstance()
                                    .createDynamicLink()
                                    .setLink(updatedUri)
                                    .setDomainUriPrefix("https://ashphotobomb.page.link") // Set your domain URI prefix
                                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                                    .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                                    .addOnSuccessListener(context, shortDynamicLink -> {
                                        Uri shortLink = shortDynamicLink.getShortLink();
                                        Intent myIntent = new Intent(Intent.ACTION_SEND);
                                        myIntent.setType("text/plain");
                                        String body = "Hey, See this pic \n\nJoin my group on PhotoBomb \n\nIt is a link :- "+ shortLink;
                                        String sub = "See This Image";
                                        myIntent.putExtra(Intent.EXTRA_STREAM, ApiSet.downloadImage(context,Uri.parse(listItem.getMedia_url())));
                                        myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                                        myIntent.putExtra(Intent.EXTRA_TEXT, body);

                                        // Grant read permissions for the content URI
                                        myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                                        context.startActivity(Intent.createChooser(myIntent, "Share Using"));

                                    })
                                    .addOnFailureListener(context, e -> {
                                        Toast.makeText(context, "Sorry we are unable to generate link", Toast.LENGTH_SHORT).show();

                                        // Handle errors
                                    });
                        }else if (item.getItemId() == R.id.save) {
                            ImageDownloader imageDownloader = new ImageDownloader(context); // 'this' should be the context
                            imageDownloader.execute(String.valueOf(listItem.getMedia_url()));
                        }
                        return true;
                    }

                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {

                    }
                });

                menuPopupHelper.show(-530, -100);

            }
        });

    }

    @Override
    public int getItemCount() {
        return imageSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView back, menu, likeIcon, image;
        TextView likeCount, commentCount;
        LinearLayout likeLayout, commentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            back = itemView.findViewById(R.id.back);
            menu = itemView.findViewById(R.id.menu);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            image = itemView.findViewById(R.id.image);
            likeCount = itemView.findViewById(R.id.likeCount);
            commentCount = itemView.findViewById(R.id.commentCount);
            likeLayout = itemView.findViewById(R.id.likeLayout);
            commentLayout = itemView.findViewById(R.id.commentLayout);
        }
    }
}
