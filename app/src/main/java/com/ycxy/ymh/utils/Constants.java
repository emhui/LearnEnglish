package com.ycxy.ymh.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Y&MH on 2018-1-5.
 */

public class Constants {
    /**
     * 本地文件存储目录
     */
    public static String STROAGEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "lyric";
    public static String LYRICAPI = "http://geci.me/api/lyric/";
    public static String URL = "";
}
