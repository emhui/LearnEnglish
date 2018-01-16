package com.ycxy.ymh.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.ycxy.ymh.bean2.DataBean;
import com.ycxy.ymh.service.AudioPlayService;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

public class Utils {

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public Utils() {
        // 转换成字符串的时间
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    }

    /**
     * 把毫秒转换成：1:20:30这里形式
     *
     * @param timeMs
     * @return
     */
    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;

        int minutes = (totalSeconds / 60) % 60;

        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    /**
     * 判断是否是网络的资源
     *
     * @param uri
     * @return
     */
    public boolean isNetUri(String uri) {
        boolean reault = false;
        if (uri != null) {
            if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
                reault = true;
            }
        }
        return reault;
    }


    /**
     * 得到网络速度
     * 每隔两秒调用一次
     *
     * @param context
     * @return
     */
    public String getNetSpeed(Context context) {
        String netSpeed = "0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed = String.valueOf(speed) + " kb/s";
        return netSpeed;
    }


    /**
     * 建立一个存储音乐歌词的文件
     */
    public void hasFile() {
        File file0 = new File(Constants.STROAGEFILDOR);
        File file = new File(Constants.STROAGEPATH);
        File filel = new File(Constants.STROAGEPATHMUSICDOWNLOAD);

        if (!file0.exists()) {
            file0.mkdir();
        }

        if (!file.exists()) {
            file.mkdir();
        }

        if (!filel.exists()) {
            filel.mkdir();
        }
    }

    // 保存歌词
    public void saveLyric(String name, String msg) {
        try {
            File file = new File(Constants.STROAGEPATH + File.separator + name + ".lrc");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(msg);

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = "Utils";

    /**
     * 判断歌词是否存在
     *
     * @return
     */
    public boolean isLyricExit(String songName) {
        // File file = new File(STROAGEPATH + File.separator + songName + ".lrc");
        File file = new File(Constants.STROAGEPATH);
        File[] files = file.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.getName().equals(songName + ".lrc")) {
                    Log.d(TAG, "isLyricExit: " + songName);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取音频的真正名字，列如 Ruppina-Free Will.mp3
     *
     * @param str
     * @return
     */
    public String getAudioName(String str) {
        // 去除后缀
        str = str.split("\\.")[0].trim();
        // 艺术家-音频名
        if (str.contains("-") || str.contains("——")) {
            str = str.split("-")[1].trim();
        }
        // 音频_类型
        if (str.contains("_")) {
            str = str.split("_")[0].trim();
        }
        // 琵琶行(成龙)
        if (str.contains("(") || str.contains(")")) {
            int pos1 = str.indexOf("(");
            int pos2 = str.indexOf(")");
            str = str.substring(0, pos1).trim();
        }

        if (str.contains("（") || str.contains("）")) {
            int pos1 = str.indexOf("（");
            int pos2 = str.indexOf("）");
            str = str.substring(0, pos1).trim();
        }
        return str.trim();
    }

    /**
     * 获取歌词的路径
     *
     * @param audioName
     * @return
     */
    public String nameToPath(String audioName) {
        return Constants.STROAGEPATH + File.separator + audioName + ".lrc";
    }

    /**
     * 保存数据到内存中
     */
    public void savePos2Stor(Context mContext, int pos) {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREPOSITION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.PREPOSITION, pos);
        editor.apply();
    }

    /**
     * 获取上一次播放音乐的位置
     *
     * @return
     */
    public int getPosfStor(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREPOSITION, Context.MODE_PRIVATE);
        return sp.getInt(Constants.PREPOSITION, 0);
    }

    /**
     * 保持播放模式
     *
     * @param context
     * @param key
     * @param values
     */
    public static void putPlaymode(Context context, String key, int values) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MODE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, values).apply();
    }

    /*
    得到播放模式
     */
    public static int getPlaymode(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MODE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, AudioPlayService.REPEAT_ALL);
    }

    // 发送更新
    public void updataMediaData(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 判断SDK版本是不是4.4或者高于4.4
            String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
            MediaScannerConnection.scanFile(context, paths, null, null);
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED));
        }

        EventBus.getDefault().post(new DataBean());
    }
}
