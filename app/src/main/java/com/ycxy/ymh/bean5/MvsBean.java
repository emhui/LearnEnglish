package com.ycxy.ymh.bean5;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-14.
 */

public class MvsBean {
    /**
     * id : 5308821
     * cover : https://p4.music.126.net/pKYXm--aKwaKdE_WPoCoRg==/1413971960701251.jpg
     * name : 千千阙歌
     * playCount : 1630774
     * briefDesc : null
     * desc : null
     * artistName : 陈慧娴
     * artistId : 7225
     * duration : 307000
     * mark : 0
     * artists : [{"id":7225,"name":"陈慧娴","alias":["Priscilla Chan","Priscilla"],"transNames":null}]
     */

    private int id;
    private String cover;
    private String name;
    private int playCount;
    private Object briefDesc;
    private Object desc;
    private String artistName;
    private int artistId;
    private int duration;
    private int mark;
    private List<ArtistsBean> artists;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public Object getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(Object briefDesc) {
        this.briefDesc = briefDesc;
    }

    public Object getDesc() {
        return desc;
    }

    public void setDesc(Object desc) {
        this.desc = desc;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public List<ArtistsBean> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistsBean> artists) {
        this.artists = artists;
    }
}
