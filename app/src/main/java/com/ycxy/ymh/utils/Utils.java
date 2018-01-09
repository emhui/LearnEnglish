package com.ycxy.ymh.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.TrafficStats;

import java.io.File;
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
     * @param context
     * @return
     */
    public String getNetSpeed(Context context) {
        String netSpeed = "0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed  = String.valueOf(speed) + " kb/s";
        return  netSpeed;
    }

    /**
     * 建立一个存储音乐歌词的文件
     */
    public void hasFile() {
        File file = new File(Constants.STROAGEPATH);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 判断歌词是否存在
     * @return
     */
    public boolean isLyricExit(String songName) {
        // File file = new File(STROAGEPATH + File.separator + songName + ".lrc");
        File file = new File(Constants.STROAGEPATH);
        File[] files = file.listFiles();

        for (File f : files) {
            if (f.getName().equals(songName + ".lrc")) {
                return true;
            }
        }
        return false;
    }
    /**
     * 获取音频的真正名字，列如 Ruppina-Free Will.mp3
     * @param str
     * @return
     */
    public String getAudioName(String str){
        // 去除后缀
        str = str.split("\\.")[0];
        // 艺术家-音频名
        if (str.contains("-")||str.contains("——")){
            str = str.split("-")[1];
        }
        // 音频_类型
        if (str.contains("_")) {
            str = str.split("_")[0];
        }
        // 琵琶行(成龙)
        if (str.contains("(") || str.contains(")")) {
            int pos1 = str.indexOf("(");
            int pos2 = str.indexOf(")");
            str = str.substring(0, pos1);
        }
        return str.trim();
    }

    /**
     * 获取歌词的路径
     * @param audioName
     * @return
     */
    public String nameToPath(String audioName){
        return Constants.STROAGEPATH + File.separator + audioName + ".lrc";
    }

    /**
     * 保存数据到内存中
     */
    public void savePos2Stor(Context mContext, int pos){
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREPOSITION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.PREPOSITION, pos);
        editor.apply();
    }

    /**
     * 获取上一次播放音乐的位置
     * @return
     */
    public int getPosfStor(Context mContext){
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREPOSITION, Context.MODE_PRIVATE);
        return sp.getInt(Constants.PREPOSITION,0);
    }
}
