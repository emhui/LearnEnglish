package com.ycxy.ymh.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import com.ycxy.ymh.activity.OnlineAudioActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import okhttp3.Call;

/**
 * Created by Y&MH on 2018-1-13.
 */

public class NetUtils {
    private static final String TAG = "NetUtils";
    public static void getNetMusic(String url, final Handler handler, final Context context, final String key, final int msg){
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError: " + e.toString());
                        handler.sendEmptyMessage(OnlineAudioActivity.GETLISTERRO);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: " + response);
                        CacheUtils.saveToLocal(context,key,response);
                        handler.sendEmptyMessage(msg);
                    }
                });
    }

    public static void downLoadMusic(String url, String name, final Handler handler, final ProgressBar progressBar){
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Constants.STROAGEPATHMUSICDOWNLOAD, name + ".mp3")//
                {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        progressBar.setProgress((int) (progress * 100));
                        super.inProgress(progress, total, id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(File response, int id) {
                        handler.sendEmptyMessage(OnlineAudioActivity.DOWNLOADSUCCESS);
                    }
                });
    }
}
