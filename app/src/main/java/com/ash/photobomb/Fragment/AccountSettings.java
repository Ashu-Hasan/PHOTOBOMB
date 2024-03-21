package com.ash.photobomb.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ash.photobomb.databinding.FragmentAccountSettingsBinding;

public class AccountSettings extends Fragment {

    FragmentAccountSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentAccountSettingsBinding.inflate(getLayoutInflater(), container, false);

        return binding.getRoot();
    }
}