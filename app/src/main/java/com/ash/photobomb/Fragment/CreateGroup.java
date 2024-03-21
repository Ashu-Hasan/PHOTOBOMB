package com.ash.photobomb.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.Constructor.User;
import com.ash.photobomb.Database.SharedPreferences.SharedPreferencesHelper;
import com.ash.photobomb.MainActivity;
import com.ash.photobomb.R;
import com.ash.photobomb.databinding.FragmentCreateGroupBinding;
import com.ash.photobomb.other.CustomDialog.AshDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateGroup extends Fragment {

    FragmentCreateGroupBinding binding;

    FirebaseStorage storage;
    FirebaseDatabase database;

    int replaceLayoutId;
    ImageView navigationButton;

    Activity activity;
    GroupInfoModel groupItem;
    AshDialog progress;
    Uri imageUri = null;
    int PICK_IMAGE_REQUEST = 33;

    public CreateGroup(int relativeLayout, ImageView navigationButton, Activity activity) {

        replaceLayoutId = relativeLayout;
        this.navigationButton = navigationButton;
        this.activity = activity;
    }

    public CreateGroup(int relativeLayout, ImageView navigationButton, Activity activity, GroupInfoModel groupItem) {

        replaceLayoutId = relativeLayout;
        this.navigationButton = navigationButton;
        this.activity = activity;
        this.groupItem = groupItem;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateGroupBinding.inflate(getLayoutInflater(), container, false);

        progress = new AshDialog(activity);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        SharedPreferencesHelper userData = new SharedPreferencesHelper(getContext());
        User user = userData.getCurrentUserData();


        if (groupItem != null){
            binding.groupName.setText(groupItem.getName());
            binding.next.setText("Save");
        }

        binding.groupExpiryMenu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                MenuBuilder menuBuilder = OnCreatePopUpMenu(getContext(), view, R.menu.group_expiry_menu);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.hours){
                            binding.groupExpiryEditText.setText("48 Hours");
                        }
                        else if (item.getItemId() == R.id.noExpiration){
                            binding.groupExpiryEditText.setText("No Expiration");
                        }
                        return true;
                    }

                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {

                    }
                });

            }
        });
        binding.userAccessMenu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                MenuBuilder menuBuilder = OnCreatePopUpMenu(getContext(), view, R.menu.user_access_menu);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.immediateAccess) {
                            binding.userAccessEditText.setText("Immediate Access");
                        } else if (item.getItemId() == R.id.requestApproval) {
                            binding.userAccessEditText.setText("Request Approval");
                        }

                        return true;
                    }

                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {

                    }
                });
            }
        });
        binding.groupStartMenu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                MenuBuilder menuBuilder = OnCreatePopUpMenu(getContext(), view, R.menu.group_start_menu);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        if (item.getItemId() == R.id.startGroupNow) {
                            binding.groupStartEditText.setText("Start Group Now");
                        } else if (item.getItemId() == R.id.schedule) {
                            binding.groupStartEditText.setText("Schedule");
                        }

                        return true;
                    }

                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {

                    }
                });
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new BottomFiles(replaceLayoutId, navigationButton, activity));
            }
        });


        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = Objects.requireNonNull(binding.groupName.getText()).toString().trim();
                String expiringDate = Objects.requireNonNull(binding.groupExpiryEditText.getText()).toString();
                String userAccess = "1";
                String groupStart = Objects.requireNonNull(binding.userAccessEditText.getText()).toString();
                groupStart = "1";

                if (Objects.requireNonNull(binding.userAccessEditText.getText()).toString().equals("Immediate Access")){
                    userAccess = "0";
                }

                if (Objects.requireNonNull(binding.groupName.getText()).toString().isEmpty()){
                    binding.groupName.setError("Please enter group name");
                    return;
                }
                progress.show();

                Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi().
                        createGroup(groupName, "", expiringDate, userAccess, groupStart, ApiSet.getCurrentDate(), "");;

                String finalUserAccess = userAccess;
                String finalGroupStart = groupStart;
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        JSONObject jsonResponse = ApiSet.getResponseData(response);

                        if (jsonResponse !=null){
                            if (jsonResponse.optString("status").equals("true")){
                                JSONObject dataJsonObject = jsonResponse.optJSONObject("data");
                                assert dataJsonObject != null;
                                if (imageUri != null) {
                                    StorageReference reference = storage.getReference().child("Groups_picture")
                                            .child(user.getId()).child(dataJsonObject.optString("id")).child(groupName);

                                    reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    database.getReference().child("Group_pictures").child(user.getId()).child(groupName).setValue(uri.toString());

                                                    Call<JsonObject> call = ApiController.getInstance(requireContext()).getapi().
                                                            editGroup(dataJsonObject.optString("id"), groupName, String.valueOf(uri), expiringDate, finalUserAccess, finalGroupStart, ApiSet.getCurrentDate());
                                                    call.enqueue(new Callback<JsonObject>() {
                                                        @Override
                                                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                                                            JSONObject jsonResponse = ApiSet.getResponseData(response);

                                                            progress.dismiss();
                                                            assert jsonResponse != null;
                                                            if (jsonResponse.optString("status").equals("true")) {
                                                                Intent main = new Intent(getContext(), MainActivity.class);
                                                                activity.finish();
                                                                activity.startActivity(main);
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                                            progress.dismiss();  // Dismiss the progress dialog in case of failure
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                                else {Intent main = new Intent(getContext(), MainActivity.class);
                                    activity.finish();
                                    activity.startActivity(main);}
                            }
                        }
                        progress.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        progress.dismiss();
                    }
                });
            }
        });

        binding.picImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });


        return binding.getRoot();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                Picasso.get().load(imageUri).into(binding.groupImage);
            }
            else {
                Toast.makeText(activity, "image not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void replaceFragment(Fragment fragment) {
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(replaceLayoutId, fragment);
        transaction.commit();

    }

    @SuppressLint("RestrictedApi")
    public MenuBuilder OnCreatePopUpMenu(Context context, View view, int FileId) {
        Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);
        @SuppressLint("RestrictedApi") MenuBuilder builder = new MenuBuilder(context);
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(FileId, builder);

        @SuppressLint("RestrictedApi") MenuPopupHelper helper = new MenuPopupHelper(wrapper, builder, view);
        helper.setForceShowIcon(true);


        helper.show(-400,-168);

        return builder;
    }
}