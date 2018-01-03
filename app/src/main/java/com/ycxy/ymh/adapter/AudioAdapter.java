package com.ycxy.ymh.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ycxy.ymh.bean.Audio;
import com.ycxy.ymh.learnenglish.MainActivity;
import com.ycxy.ymh.learnenglish.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Y&MH on 2018-1-3.
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder>{

    private ArrayList<Audio> audioArrayList;
    private Context mContext;

    public AudioAdapter(Context context, ArrayList<Audio> audioArrayList) {
        this.mContext = context;
        this.audioArrayList = audioArrayList;
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audio,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Audio audio = audioArrayList.get(position);

        String name = audio.getName();
        name = name.split("\\.")[0];
        holder.tv_audio_name.setText(name);
        holder.tv_audio_artist.setText(audio.getArtist());
    }

    @Override
    public int getItemCount() {
        return audioArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_audio_name;
        private TextView tv_audio_artist;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_audio_name = itemView.findViewById(R.id.tv_audio_name);
            tv_audio_artist = itemView.findViewById(R.id.tv_audio_artist);
        }
    }
}
