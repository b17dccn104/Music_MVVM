package com.example.music_mvvm.view.home_view;

import static com.example.music_mvvm.service.SongService.ACTION_CANCEL;
import static com.example.music_mvvm.service.SongService.ACTION_PAUSE;
import static com.example.music_mvvm.service.SongService.ACTION_RESUME;
import static com.example.music_mvvm.service.SongService.ACTION_START;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.example.music_mvvm.R;
import com.example.music_mvvm.databinding.ActivityMainBinding;
import com.example.music_mvvm.model.Song;
import com.example.music_mvvm.service.SongService;
import com.example.music_mvvm.view.adapter.ListSongAdapter;
import com.example.music_mvvm.viewmodel.SongViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListSongAdapter.SentPositionSongToActivity {
    private ActivityMainBinding binding;
    private SongViewModel songViewModel;
    private View view;
    private ListSongAdapter adapter;
    private Song song,songFromViewModel;
    private boolean songIsPlay;
    private int actionMusic;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            songIsPlay = bundle.getBoolean(getString(R.string.SONG_STATUS));
            actionMusic = bundle.getInt(getString(R.string.ACTION_MUSIC_SERVICE));
            song = (Song) bundle.getSerializable(getString(R.string.SONG_FROM_SERVICE));
            handleSongPlayView(actionMusic);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        broadcastReceiverListener();
        if (checkPermission()){
            inItData();
        } else {
            requestPermission();
        }
        observeListSongLiveData();
        clickListener();
    }

    private void handleSongPlayView(int actionMusic) {
        switch (actionMusic) {
            case ACTION_START:
                disPlayPlaySongView();
                setDataToPlaySongView(song);
                setStatusPlayOrPause();
                break;
            case ACTION_CANCEL:
                hidePlaySongView();
                break;
            case ACTION_RESUME:
                setStatusPlayOrPause();
                break;
            case ACTION_PAUSE:
                setStatusPlayOrPause();
                break;
        }
    }

    private void setStatusPlayOrPause() {
        if (songIsPlay) {
            binding.buttonPauseHome.setImageResource(R.drawable.ic_pause);
        } else {
            binding.buttonPauseHome.setImageResource(R.drawable.ic_play);
        }
    }

    private void broadcastReceiverListener() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("sent_data_to_activity"));
    }

    private void clickListener() {
        clickCancelSong(view);
        clickPauseSong(view);
    }

    public void clickPauseSong(View view) {
        if (songIsPlay) {
            sentActionMusicToService(ACTION_PAUSE);
        } else {
            sentActionMusicToService(ACTION_RESUME);
        }
    }

    public void clickCancelSong(View view) {
        sentActionMusicToService(ACTION_CANCEL);
    }

    private void inItData() {
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel = new SongViewModel(this);
    }

    private boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED)
                return  false;
        }
        return true;
    }

    private void requestPermission() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
           inItData();
        }
    }

    private void observeListSongLiveData() {
        songViewModel.getListSongFromViewModel().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                setDataToRecyleView(songs);
            }
        });
    }

    private void setDataToRecyleView(List<Song> songList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerListsong.setLayoutManager(linearLayoutManager);
        adapter = new ListSongAdapter(this, songList);
        binding.recyclerListsong.setAdapter(adapter);
        binding.recyclerListsong.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
    }

    private void disPlayPlaySongView() {
        binding.playsongView.setVisibility(View.VISIBLE);
    }

    private void hidePlaySongView() {
        binding.playsongView.setVisibility(View.GONE);
    }

    @Override
    public void sentPositionSongToActivity(int position) {
        songViewModel.getSongByPositionFromViewHolder(position).observe(this, new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                songFromViewModel = song;
            }
        });
        Intent intent = new Intent(this, SongService.class);
        intent.putExtra(getString(R.string.SONG), songFromViewModel);
        startService(intent);
    }

    private void setDataToPlaySongView(Song song) {
        binding.setSong(song);
    }

    private void sentActionMusicToService(int actionMusic) {
        Intent intent = new Intent(this, SongService.class);
        intent.putExtra(getString(R.string.ACTION_MUSIC_RECEIVER), actionMusic);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}