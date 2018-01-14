package com.ycxy.ymh.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Y&MH on 2018-1-13.
 */

public class CacheUtils {
    public static void saveToLocal(Context mContext, String key, String msg){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("music",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,msg);
        editor.apply();
    }

    public static String getFromLoacl(Context mContext, String key){
        SharedPreferences sp = mContext.getSharedPreferences("music", Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }
}

