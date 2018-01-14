package com.ycxy.ymh.bean6;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-14.
 */

public class DataBean {
    /**
     * id : 5330199
     * name : Dear friends
     * artistId : 21848
     * artistName : TRIPLANE
     * briefDesc :
     * desc : null
     * cover : http://p4.music.126.net/3O4vGLbxMrmIxi236ZKnrg==/3251255887408520.jpg
     * coverId : 3251255887408520
     * playCount : 279268
     * subCount : 9698
     * shareCount : 1584
     * likeCount : 894
     * commentCount : 1669
     * duration : 536000
     * nType : 0
     * publishTime : 2010-03-17
     * brs : {"480":"http://v4.music.126.net/20180115093101/4f844adeef22838f65cfff71ef721752/web/cloudmusic/NzI4Mjg2ODE=/b1687caeb3f0b9f6d189f8459e708085/18f40f718b11fab82ec1b44673b43ed1.mp4","240":"http://v4.music.126.net/20180115093101/27f8d6c7c45b230795555b03127d15d4/web/cloudmusic/NzI4Mjg2ODE=/b1687caeb3f0b9f6d189f8459e708085/573902c555ae9454c2824dc69f26bc00.mp4","720":"http://v4.music.126.net/20180115093101/548eb2728b07008b8979c0e753a7a239/web/cloudmusic/NzI4Mjg2ODE=/b1687caeb3f0b9f6d189f8459e708085/3bf050ab2cf25404edd235a18247526f.mp4"}
     * artists : [{"id":21848,"name":"TRIPLANE"}]
     * isReward : false
     * commentThreadId : R_MV_5_5330199
     */

    private int id;
    private String name;
    private int artistId;
    private String artistName;
    private String briefDesc;
    private Object desc;
    private String cover;
    private long coverId;
    private int playCount;
    private int subCount;
    private int shareCount;
    private int likeCount;
    private int commentCount;
    private int duration;
    private int nType;
    private String publishTime;
    private BrsBean brs;
    private boolean isReward;
    private String commentThreadId;
    private List<ArtistsBean> artists;

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

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    public Object getDesc() {
        return desc;
    }

    public void setDesc(Object desc) {
        this.desc = desc;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getCoverId() {
        return coverId;
    }

    public void setCoverId(long coverId) {
        this.coverId = coverId;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getSubCount() {
        return subCount;
    }

    public void setSubCount(int subCount) {
        this.subCount = subCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getNType() {
        return nType;
    }

    public void setNType(int nType) {
        this.nType = nType;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public BrsBean getBrs() {
        return brs;
    }

    public void setBrs(BrsBean brs) {
        this.brs = brs;
    }

    public boolean isIsReward() {
        return isReward;
    }

    public void setIsReward(boolean isReward) {
        this.isReward = isReward;
    }

    public String getCommentThreadId() {
        return commentThreadId;
    }

    public void setCommentThreadId(String commentThreadId) {
        this.commentThreadId = commentThreadId;
    }

    public List<ArtistsBean> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistsBean> artists) {
        this.artists = artists;
    }
}
