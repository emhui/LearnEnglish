package com.ycxy.ymh.bean6;

/**
 * Created by Y&MH on 2018-1-14.
 */

public class MVInfo {

    /**
     * loadingPic :
     * bufferPic :
     * loadingPicFS :
     * bufferPicFS :
     * subed : false
     * data : {"id":5330199,"name":"Dear friends","artistId":21848,"artistName":"TRIPLANE","briefDesc":"","desc":null,"cover":"http://p4.music.126.net/3O4vGLbxMrmIxi236ZKnrg==/3251255887408520.jpg","coverId":3251255887408520,"playCount":279268,"subCount":9698,"shareCount":1584,"likeCount":894,"commentCount":1669,"duration":536000,"nType":0,"publishTime":"2010-03-17","brs":{"480":"http://v4.music.126.net/20180115093101/4f844adeef22838f65cfff71ef721752/web/cloudmusic/NzI4Mjg2ODE=/b1687caeb3f0b9f6d189f8459e708085/18f40f718b11fab82ec1b44673b43ed1.mp4","240":"http://v4.music.126.net/20180115093101/27f8d6c7c45b230795555b03127d15d4/web/cloudmusic/NzI4Mjg2ODE=/b1687caeb3f0b9f6d189f8459e708085/573902c555ae9454c2824dc69f26bc00.mp4","720":"http://v4.music.126.net/20180115093101/548eb2728b07008b8979c0e753a7a239/web/cloudmusic/NzI4Mjg2ODE=/b1687caeb3f0b9f6d189f8459e708085/3bf050ab2cf25404edd235a18247526f.mp4"},"artists":[{"id":21848,"name":"TRIPLANE"}],"isReward":false,"commentThreadId":"R_MV_5_5330199"}
     * code : 200
     */

    private String loadingPic;
    private String bufferPic;
    private String loadingPicFS;
    private String bufferPicFS;
    private boolean subed;
    private DataBean data;
    private int code;

    public String getLoadingPic() {
        return loadingPic;
    }

    public void setLoadingPic(String loadingPic) {
        this.loadingPic = loadingPic;
    }

    public String getBufferPic() {
        return bufferPic;
    }

    public void setBufferPic(String bufferPic) {
        this.bufferPic = bufferPic;
    }

    public String getLoadingPicFS() {
        return loadingPicFS;
    }

    public void setLoadingPicFS(String loadingPicFS) {
        this.loadingPicFS = loadingPicFS;
    }

    public String getBufferPicFS() {
        return bufferPicFS;
    }

    public void setBufferPicFS(String bufferPicFS) {
        this.bufferPicFS = bufferPicFS;
    }

    public boolean isSubed() {
        return subed;
    }

    public void setSubed(boolean subed) {
        this.subed = subed;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
