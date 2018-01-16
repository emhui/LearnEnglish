package com.ycxy.ymh.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Y&MH on 2018-1-5.
 */

public class Constants {
    /**
     * 先前播放位置
     */
    public static final String PREPOSITION = "preposition";
    public static final String MODE = "mode";
    /**
     * 本地文件存储目录
     */
    public static String STROAGEFILDOR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"LeMusic";
    public static String STROAGEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"LeMusic" + File.separator + "lyric";
    public static String STROAGEPATHMUSICDOWNLOAD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"LeMusic" + File.separator + "music";
    public static String LYRICAPI = "http://geci.me/api/lyric/";
    public static String URL = "";
}
