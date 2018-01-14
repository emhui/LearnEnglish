package com.ycxy.ymh.bean;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-13.
 */

public class ResultBean {
    /**
     * songs : [{"name":"稻香","id":185709,"pst":0,"t":0,"ar":[{"id":6452,"name":"周杰伦","tns":[],"alias":[]}],"alia":[],"pop":100,"st":0,"rt":"600902000006889008","fee":8,"v":87,"crbt":"8a289b307a82998e10179dd2c3b34813","cf":"","al":{"id":18877,"name":"魔杰座","picUrl":"https://p1.music.126.net/uKR6EQ1dLq4i1UBhXmvXtQ==/721279627833133.jpg","tns":[],"pic":721279627833133},"dt":223452,"h":{"br":320000,"fid":0,"size":8941341,"vd":-1.66},"m":{"br":160000,"fid":0,"size":4470746,"vd":-1.26},"l":{"br":96000,"fid":0,"size":2682507,"vd":-1.31},"a":null,"cd":"1","no":11,"rtUrl":null,"ftype":0,"rtUrls":[],"rtype":0,"rurl":null,"mst":9,"cp":1007,"mv":506121,"publishTime":1224000000000,"privilege":{"id":185709,"fee":8,"payed":0,"st":0,"pl":128000,"dl":0,"sp":7,"cp":1,"subp":1,"cs":false,"maxbr":999000,"fl":128000,"toast":false,"flag":0}}]
     * songCount : 58
     */

    private int songCount;
    private List<SongsBean> songs;

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public List<SongsBean> getSongs() {
        return songs;
    }

    public void setSongs(List<SongsBean> songs) {
        this.songs = songs;
    }
}
