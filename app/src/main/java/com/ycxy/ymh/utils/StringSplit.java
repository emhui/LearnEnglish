package com.ycxy.ymh.utils;

/**
 * Created by Y&MH on 2018-1-5.
 */

public class StringSplit {
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
        return str;
    }
}
