package com.example.music_mvvm.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_mvvm.R;
import com.example.music_mvvm.databinding.ItemSongBinding;
import com.example.music_mvvm.model.Song;

import java.util.List;
import java.util.zip.Inflater;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.MyViewHolder> {
    private List<Song> songList;
    private Context context;
    private SentPositionSongToActivity sentPositionSongToActivity;

    public ListSongAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSongBinding itemSongBinding = ItemSongBinding.inflate(layoutInflater, parent, false);
        sentPositionSongToActivity = (SentPositionSongToActivity) context;
        return new MyViewHolder(itemSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (songList == null) {
            return;
        }
        holder.bindData(position);
        holder.clickListener(position);
    }

    @Override
    public int getItemCount() {
        if (songList != null){
            return songList.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemSongBinding itemSongBinding;

        public MyViewHolder(@NonNull ItemSongBinding itemSongBinding) {
            super(itemSongBinding.getRoot());
            this.itemSongBinding = itemSongBinding;
        }

        private void bindData(int position) {
            Song song = songList.get(position);
            itemSongBinding.setSong(song);
            itemSongBinding.executePendingBindings();
        }

        private void clickListener(int postion) {
            itemSongBinding.itemSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sentPositionSongToActivity.sentPositionSongToActivity(postion);
                }
            });
        }
    }

    public interface SentPositionSongToActivity {
        void sentPositionSongToActivity(int position);
    }
}
