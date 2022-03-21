package com.example.music_mvvm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.music_mvvm.R;
import com.example.music_mvvm.service.SongService;

public class SongReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int intentFromNoti = intent.getIntExtra(context.getString(R.string.ACTION_MUSIC), 0);
        Intent intentFromReceiver = new Intent(context, SongService.class);
        intentFromReceiver.putExtra(context.getString(R.string.ACTION_MUSIC_RECEIVER), intentFromNoti);
        context.startService(intentFromReceiver);
    }
}
