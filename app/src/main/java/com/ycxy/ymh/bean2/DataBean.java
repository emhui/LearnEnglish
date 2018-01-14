package com.ycxy.ymh.bean2;

/**
 * Created by Y&MH on 2018-1-13.
 */

public class DataBean {
    /**
     * id : 185709
     * url : https://m8.music.126.net/20180113153456/ab17170080f5c8a0b4e90049973a8a2b/ymusic/7895/bfe8/2cf1/cbc731a78bcccab4760f3300247659ce.mp3
     * br : 128000
     * size : 3576519
     * md5 : cbc731a78bcccab4760f3300247659ce
     * code : 200
     * expi : 1200
     * type : mp3
     * gain : -1.28
     * fee : 8
     * uf : null
     * payed : 0
     * flag : 0
     * canExtend : false
     */

    public int id;
    public String url;
    public int br;
    public int size;
    public String md5;
    public int code;
    public int expi;
    public String type;
    public double gain;
    public int fee;
    public Object uf;
    public int payed;
    public int flag;
    public boolean canExtend;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getBr() {
        return br;
    }

    public void setBr(int br) {
        this.br = br;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getExpi() {
        return expi;
    }

    public void setExpi(int expi) {
        this.expi = expi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public Object getUf() {
        return uf;
    }

    public void setUf(Object uf) {
        this.uf = uf;
    }

    public int getPayed() {
        return payed;
    }

    public void setPayed(int payed) {
        this.payed = payed;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isCanExtend() {
        return canExtend;
    }

    public void setCanExtend(boolean canExtend) {
        this.canExtend = canExtend;
    }
}
