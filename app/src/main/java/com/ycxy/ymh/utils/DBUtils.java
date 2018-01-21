package com.ycxy.ymh.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.ycxy.ymh.bean.Audio;
import com.ycxy.ymh.learnenglish.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Y&MH on 01-21 021.
 */

public class DBUtils {

    public static void getAudioList(final Context mContext, final Handler handler, final int key_msg){
        new Thread() {
            @Override
            public void run() {
                super.run();
                ArrayList<Audio> audioArrayList = new ArrayList<>();
                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.TITLE,//视频文件在sdcard的名称
                        MediaStore.Audio.Media.DURATION,//视频总时长
                        MediaStore.Audio.Media.SIZE,//视频的文件大小
                        MediaStore.Audio.Media.DATA,//视频的绝对地址
                        MediaStore.Audio.Media.ARTIST,//歌曲的演唱者
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        Audio mediaItem = new Audio();

                        audioArrayList.add(mediaItem);//写在上面

                        String name = cursor.getString(0);//视频的名称
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);//视频的时长
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);//视频的文件大小
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);//视频的播放地址
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
                //Handler发消息
                Message msg = Message.obtain();
                msg.obj = audioArrayList;
                msg.what = key_msg;
                handler.sendMessage(msg);
            }
        }.start();
    }
}
