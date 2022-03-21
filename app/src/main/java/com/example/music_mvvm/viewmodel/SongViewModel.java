package com.example.music_mvvm.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.music_mvvm.repositoty.SongRepository;
import com.example.music_mvvm.model.Song;

import java.util.List;

public class SongViewModel extends ViewModel {
    private List<Song> songList;
    private MutableLiveData<List<Song>> listSongFromViewModel;
    private SongRepository songRepository;

    public SongViewModel() {
    }

    public SongViewModel(Context context) {
        songRepository = new SongRepository(context);
        listSongFromViewModel = new MutableLiveData<>();
    }

    public MutableLiveData<List<Song>> getListSongFromViewModel() {
        return  songRepository.getListSongFromRepository();
    }

    public MutableLiveData<Song> getSongByPositionFromViewHolder(int position) {
        return  songRepository.getSongByPositionFromRepository(position);
    }

}
