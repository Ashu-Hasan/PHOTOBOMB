package com.ash.photobomb.Constructor;

import android.graphics.drawable.Drawable;

import com.ash.photobomb.API_Model_Classes.MediaDataModel;

import java.util.ArrayList;

public class AlbumItem {
    String date;
    ArrayList<MediaDataModel> imageList;

    public AlbumItem(String date, ArrayList<MediaDataModel> imageList) {
        this.date = date;
        this.imageList = imageList;
    }

    public ArrayList<MediaDataModel> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<MediaDataModel> imageList) {
        this.imageList = imageList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    }
