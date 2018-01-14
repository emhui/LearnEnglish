package com.ycxy.ymh.bean2;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-13.
 */

public class MusicInfo {

    /**
     * data : [{"id":185709,"url":"https://m8.music.126.net/20180113153456/ab17170080f5c8a0b4e90049973a8a2b/ymusic/7895/bfe8/2cf1/cbc731a78bcccab4760f3300247659ce.mp3","br":128000,"size":3576519,"md5":"cbc731a78bcccab4760f3300247659ce","code":200,"expi":1200,"type":"mp3","gain":-1.28,"fee":8,"uf":null,"payed":0,"flag":0,"canExtend":false}]
     * code : 200
     */

    public int code;
    public List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }
}
