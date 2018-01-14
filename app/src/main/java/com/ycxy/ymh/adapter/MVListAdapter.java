package com.ycxy.ymh.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ycxy.ymh.activity.MVListActivity;
import com.ycxy.ymh.bean5.MVList;
import com.ycxy.ymh.bean5.MvsBean;
import com.ycxy.ymh.learnenglish.R;
import com.ycxy.ymh.utils.NetUtils;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-13.
 */

public class MVListAdapter extends RecyclerView.Adapter<MVListAdapter.ViewHolder> {

    private List<MvsBean> mvsBeanList;
    private Context context;
    private Handler handler;

    public MVListAdapter(Context context, List<MvsBean> mvsBeanList, Handler handler) {
        this.context = context;
        this.mvsBeanList = mvsBeanList;
        this.handler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mv, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MvsBean mvsBean = mvsBeanList.get(position);
        String name = mvsBean.getName();
        String author = mvsBean.getArtistName();
        mvsBean.getCover();
        final int id = mvsBean.getId();
        holder.tv_mv_name.setText(name);
        holder.tv_mv_artist.setText(author);
        Glide.with(context).load(mvsBean.getCover()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mvsBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_mv_name;
        private TextView tv_mv_artist;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_mv_name = itemView.findViewById(R.id.tv_audio_name);
            tv_mv_artist = itemView.findViewById(R.id.tv_audio_artist);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
