package com.ycxy.ymh.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ycxy.ymh.bean.SongsBean;
import com.ycxy.ymh.learnenglish.R;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-13.
 */

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    private List<SongsBean> songsBeanList;
    private Context context;
    private Handler handler;

    public MusicListAdapter(Context context, List<SongsBean> songsBeanList, Handler handler){
        this.context = context;
        this.songsBeanList = songsBeanList;
        this.handler = handler;
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
        SongsBean songsBean = songsBeanList.get(position);
        String name = songsBean.getName();
        String author = songsBean.getAr().get(0).getName();
        final int id = songsBean.getId();
        holder.tv_audio_name.setText(name);
        holder.tv_audio_artist.setText(author);
    }

    @Override
    public int getItemCount() {
        return songsBeanList.size();
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
