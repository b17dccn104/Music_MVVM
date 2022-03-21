package com.example.music_mvvm.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Song implements Serializable {
    private String songURL;
    private String songTitle;
    private String songAuthor;
    private Context context;

    public Song(String songURL, String songTitle, String songAuthor) {
        this.songURL = songURL;
        this.songTitle = songTitle;
        this.songAuthor = songAuthor;
    }

    public Song(Context context) {
        this.context = context;
    }

    public Song() {
    }

    public String getSongURL() {
        return songURL;
    }

    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongAuthor() {
        return songAuthor;
    }

    public void setSongAuthor(String songAuthor) {
        this.songAuthor = songAuthor;
    }


}
