package com.example.music_mvvm.repositoty;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;

import com.example.music_mvvm.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongRepository {
    private Context context;
    private MutableLiveData<List<Song>> listSongFromRepository;
    private MutableLiveData<Song> songByPositionFromRepository;

    public SongRepository(Context context) {
        this.context = context;
        listSongFromRepository = new MutableLiveData<>();
        songByPositionFromRepository = new MutableLiveData<>();
    }

    public MutableLiveData<List<Song>> getListSongFromRepository() {
        listSongFromRepository.setValue(getSongFromDevice());
        return listSongFromRepository;
    }

    public MutableLiveData<Song> getSongByPositionFromRepository(int position) {
        songByPositionFromRepository.setValue(getSongByPosition(position));
        return songByPositionFromRepository;
    }

    private List<Song> getSongFromDevice() {
        List<Song> songList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int Author = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int URL = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        while (cursor.moveToNext()){
            String songTitle = cursor.getString(Title);
            String songAuthor = cursor.getString(Author);
            String songURL = cursor.getString(URL);
            Song song = new Song(songURL, songTitle, songAuthor);
            songList.add(song);
        }
        cursor.close();
        return songList;
    }

    private Song getSongByPosition(int position) {
        Song song = getSongFromDevice().get(position);
        return song;
    }

}
