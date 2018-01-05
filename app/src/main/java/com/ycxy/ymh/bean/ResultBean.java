package com.ycxy.ymh.bean;

/**
 * Created by Y&MH on 2018-1-5.
 */

public class ResultBean {
    /**
     * aid : 2272745
     * artist_id : 17249
     * lrc : http://s.gecimi.com/lrc/263/26395/2639587.lrc
     * sid : 2639587
     * song : 花好月圆
     */

    private int aid;
    private int artist_id;
    private String lrc;
    private int sid;
    private String song;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }
}
