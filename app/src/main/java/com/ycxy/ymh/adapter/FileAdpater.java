package com.ycxy.ymh.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ycxy.ymh.activity.FileActivity;
import com.ycxy.ymh.learnenglish.R;
import com.ycxy.ymh.utils.CacheUtils;
import com.ycxy.ymh.view.LyricView;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
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
        } else if (file.isFile()) {
            String name = file.getName();
            if (file.getName().contains(".mp3") ||
                    file.getName().contains(".flac") ||
                    file.getName().contains(".wav") ||
                    file.getName().contains(".aac")) {
                holder.imageView.setBackgroundResource(R.mipmap.audio);
            } else if (name.contains(".mp4") ||
                    name.contains(".avi") ||
                    name.contains(".rmvb") ||
                    name.contains(".mkv")) {
                holder.imageView.setBackgroundResource(R.mipmap.video);
            } else if (name.contains(".jpg") ||
                    name.contains(".png") ||
                    name.contains(".bmp") ||
                    name.contains("gif") ||
                    name.contains("jpeg")) {
                holder.imageView.setBackgroundResource(R.mipmap.pic);
            } else {
                holder.imageView.setBackgroundResource(R.mipmap.file);
            }
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
        //生成文件列表
        if (subFiles != null) {
            for (File f : subFiles) {
                if (f.isDirectory() || f.getName().endsWith(".mp3") ||
                        f.getName().endsWith(".wav") ||
                        f.getName().endsWith(".aac") ||
                        f.getName().endsWith("flac") ||
                        f.getName().endsWith(".txt") ||
                        f.getName().endsWith(".lrc") ){
                    if (!f.getName().startsWith(".")){
                        fileList.add(f);
                    }
                }
            }

            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File af, File zf) {
                    if (af.getName().compareTo(zf.getName()) < 0) {
                        return -1;
                    } else if (af.getName().compareTo(zf.getName()) > 0) {
                        return 1;
                    } else {
                        return 0;
                    }

                }
            });
        }

        this.notifyDataSetChanged();
        curPath = path;
        CacheUtils.saveToLocal(context,
                FileActivity.key_file_path, curPath);
    }
    private String prePath ;
    public void scanAllMusicDir(String path){
        fileList.clear();
        File dir = new File(path);
        File[] subFiles = dir.listFiles();
        //生成文件列表
        if (subFiles != null) {
            for (File f : subFiles) {
                if (!f.getName().startsWith(".")){
                    if (f.isDirectory()) {
                        scanAllMusicDir(f.getPath());
                    } else if (f.getName().endsWith(".mp3") || f.getName().endsWith(".flac")
                            || f.getName().endsWith(".wav") || f.getName().endsWith(".aac")){
                        if (f != null ){
                            if (!prePath.equals(f.getPath())){
                                fileList.add(f.getParentFile());
                                prePath = f.getPath();
                            }
                        }

                    }
                }
            }
        }
        this.notifyDataSetChanged();
    }
}
