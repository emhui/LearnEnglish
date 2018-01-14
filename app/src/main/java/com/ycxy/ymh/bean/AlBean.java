package com.ycxy.ymh.bean;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-13.
 */

public class AlBean {
    /**
     * id : 18877
     * name : 魔杰座
     * picUrl : https://p1.music.126.net/uKR6EQ1dLq4i1UBhXmvXtQ==/721279627833133.jpg
     * tns : []
     * pic : 721279627833133
     */

    private int id;
    private String name;
    private String picUrl;
    private long pic;
    private List<?> tns;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public long getPic() {
        return pic;
    }

    public void setPic(long pic) {
        this.pic = pic;
    }

    public List<?> getTns() {
        return tns;
    }

    public void setTns(List<?> tns) {
        this.tns = tns;
    }
}
