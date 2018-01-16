package com.cn.filelist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Y&MH on 2018-1-15.
 */

public class FileAdpater extends RecyclerView.Adapter<FileAdpater.ViewHolder> {

    private Context context;
    public List<File> fileList = new LinkedList<>();

    public String curPath;

    public FileAdpater(Context context) {
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File file = fileList.get(position);
        holder.textView.setText(file.getName());

        if (file.isDirectory()) {
            holder.imageView.setBackgroundResource(R.mipmap.dict);
        }

        if (file.isFile()) {
            holder.imageView.setBackgroundResource(R.mipmap.file);
        }

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_file);
            textView = itemView.findViewById(R.id.tv_file);
        }
    }

    private static final String TAG = "FileAdpater";
    //扫描文件夹
    public void scanFiles(String path) {

        fileList.clear();
        File dir = new File(path);
        File[] subFiles = dir.listFiles();
        Log.d(TAG, "scanFiles: " + subFiles.length);
        //生成文件列表
        if (subFiles != null) {
            for (File f : subFiles) {
                fileList.add(f);
                Log.d(TAG, "scanFiles: " + f.getName());
            }
        }
        this.notifyDataSetChanged();
        curPath = path;
    }
}
