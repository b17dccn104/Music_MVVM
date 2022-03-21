package com.example.music_mvvm.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.music_mvvm.R;
import com.example.music_mvvm.databinding.ItemSongBinding;
import com.example.music_mvvm.model.Song;
import com.example.music_mvvm.receiver.SongReceiver;
import com.example.music_mvvm.view.home_view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SongService extends Service {
    private MediaPlayer mediaPlayer;
    private NotificationManager notificationManager;
    private boolean songIsPlay;
    private Song msong;

    private static final int NOTIFICATION_MUSIC_ID = 1;
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_CANCEL = 2;
    public static final int ACTION_RESUME = 3;
    public static final int ACTION_START = 4;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(getString(R.string.info_channel_id),getString(R.string.info_channel_name));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Song song = (Song) intent.getSerializableExtra(getString(R.string.SONG));
            if (song != null) {
                msong = song;
                startMusic(song);
                startForeground(song);
            }
            int action_music = intent.getIntExtra(getString(R.string.ACTION_MUSIC_RECEIVER), 0);
            handleMusic(action_music);
        }
        return START_NOT_STICKY;
    }

    private void handleMusic(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_CANCEL:
                cancelMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && songIsPlay == true) {
            mediaPlayer.pause();
            songIsPlay = false;
            startForeground(msong);
            sentDataMusicToActivity(ACTION_PAUSE);
        }
    }

    private void cancelMusic() {
        stopSelf();
        sentDataMusicToActivity(ACTION_CANCEL);
    }

    private void resumeMusic() {
        if (mediaPlayer != null && songIsPlay == false) {
            mediaPlayer.start();
            songIsPlay = true;
            startForeground(msong);
            sentDataMusicToActivity(ACTION_RESUME);
        }
    }

    private void startMusic(Song song) {
        if (mediaPlayer == null) {
            Uri uriSong = Uri.parse(song.getSongURL());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uriSong);
            mediaPlayer.start();
            songIsPlay = true;
            sentDataMusicToActivity(ACTION_START);
        }
    }

    private void startForeground(Song song) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForeground(NOTIFICATION_MUSIC_ID,sentNotification(song));
        } else notificationManager.notify(NOTIFICATION_MUSIC_ID,sentNotification(song));
    }

    private Notification sentNotification(Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notifi_custom);
        remoteViews.setTextViewText(R.id.textview_title_song,song.getSongTitle());
        remoteViews.setTextViewText(R.id.textview_author_song, song.getSongAuthor());
        remoteViews.setImageViewResource(R.id.button_pause, R.drawable.ic_pause);
        remoteViews.setImageViewResource(R.id.button_cancel, R.drawable.ic_quit);

        if (songIsPlay){
            remoteViews.setOnClickPendingIntent(R.id.button_pause, getPendingIntent(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.button_pause, R.drawable.ic_pause);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.button_pause, getPendingIntent(this, ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.button_pause, R.drawable.ic_play);
        }
        remoteViews.setOnClickPendingIntent(R.id.button_cancel, getPendingIntent(this, ACTION_CANCEL));


        Notification notification = new NotificationCompat
                .Builder(this,getString(R.string.info_channel_id))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContent(remoteViews)
                .setSound(null)
                .setAutoCancel(false)
                .build();

        return notification;
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, SongReceiver.class);
        intent.putExtra(getString(R.string.ACTION_MUSIC), action);
        return PendingIntent.getBroadcast(
                context.getApplicationContext(),
                action,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void createNotificationChannel(String channerID, String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    channerID, channelName, NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setSound(null,null);
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private void sentDataMusicToActivity(int action) {
        Intent intent = new Intent(getString(R.string.SENT_DATA_ACTIVITY));
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.SONG_FROM_SERVICE), msong);
        bundle.putInt(getString(R.string.ACTION_MUSIC_SERVICE), action);
        bundle.putBoolean(getString(R.string.SONG_STATUS), songIsPlay);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
