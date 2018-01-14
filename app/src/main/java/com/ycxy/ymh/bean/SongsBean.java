package com.ycxy.ymh.bean;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-13.
 */

public class SongsBean {
    /**
     * name : 稻香
     * id : 185709
     * pst : 0
     * t : 0
     * ar : [{"id":6452,"name":"周杰伦","tns":[],"alias":[]}]
     * alia : []
     * pop : 100
     * st : 0
     * rt : 600902000006889008
     * fee : 8
     * v : 87
     * crbt : 8a289b307a82998e10179dd2c3b34813
     * cf :
     * al : {"id":18877,"name":"魔杰座","picUrl":"https://p1.music.126.net/uKR6EQ1dLq4i1UBhXmvXtQ==/721279627833133.jpg","tns":[],"pic":721279627833133}
     * dt : 223452
     * h : {"br":320000,"fid":0,"size":8941341,"vd":-1.66}
     * m : {"br":160000,"fid":0,"size":4470746,"vd":-1.26}
     * l : {"br":96000,"fid":0,"size":2682507,"vd":-1.31}
     * a : null
     * cd : 1
     * no : 11
     * rtUrl : null
     * ftype : 0
     * rtUrls : []
     * rtype : 0
     * rurl : null
     * mst : 9
     * cp : 1007
     * mv : 506121
     * publishTime : 1224000000000
     * privilege : {"id":185709,"fee":8,"payed":0,"st":0,"pl":128000,"dl":0,"sp":7,"cp":1,"subp":1,"cs":false,"maxbr":999000,"fl":128000,"toast":false,"flag":0}
     */

    private String name;
    private int id;
    private int pst;
    private int t;
    private int pop;
    private int st;
    private String rt;
    private int fee;
    private int v;
    private String crbt;
    private String cf;
    private AlBean al;
    private int dt;
    private HBean h;
    private MBean m;
    private LBean l;
    private Object a;
    private String cd;
    private int no;
    private Object rtUrl;
    private int ftype;
    private int rtype;
    private Object rurl;
    private int mst;
    private int cp;
    private int mv;
    private long publishTime;
    private PrivilegeBean privilege;
    private List<ArBean> ar;
    private List<?> alia;
    private List<?> rtUrls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPst() {
        return pst;
    }

    public void setPst(int pst) {
        this.pst = pst;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getPop() {
        return pop;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    public int getSt() {
        return st;
    }

    public void setSt(int st) {
        this.st = st;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public String getCrbt() {
        return crbt;
    }

    public void setCrbt(String crbt) {
        this.crbt = crbt;
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public AlBean getAl() {
        return al;
    }

    public void setAl(AlBean al) {
        this.al = al;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public HBean getH() {
        return h;
    }

    public void setH(HBean h) {
        this.h = h;
    }

    public MBean getM() {
        return m;
    }

    public void setM(MBean m) {
        this.m = m;
    }

    public LBean getL() {
        return l;
    }

    public void setL(LBean l) {
        this.l = l;
    }

    public Object getA() {
        return a;
    }

    public void setA(Object a) {
        this.a = a;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Object getRtUrl() {
        return rtUrl;
    }

    public void setRtUrl(Object rtUrl) {
        this.rtUrl = rtUrl;
    }

    public int getFtype() {
        return ftype;
    }

    public void setFtype(int ftype) {
        this.ftype = ftype;
    }

    public int getRtype() {
        return rtype;
    }

    public void setRtype(int rtype) {
        this.rtype = rtype;
    }

    public Object getRurl() {
        return rurl;
    }

    public void setRurl(Object rurl) {
        this.rurl = rurl;
    }

    public int getMst() {
        return mst;
    }

    public void setMst(int mst) {
        this.mst = mst;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public int getMv() {
        return mv;
    }

    public void setMv(int mv) {
        this.mv = mv;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public PrivilegeBean getPrivilege() {
        return privilege;
    }

    public void setPrivilege(PrivilegeBean privilege) {
        this.privilege = privilege;
    }

    public List<ArBean> getAr() {
        return ar;
    }

    public void setAr(List<ArBean> ar) {
        this.ar = ar;
    }

    public List<?> getAlia() {
        return alia;
    }

    public void setAlia(List<?> alia) {
        this.alia = alia;
    }

    public List<?> getRtUrls() {
        return rtUrls;
    }

    public void setRtUrls(List<?> rtUrls) {
        this.rtUrls = rtUrls;
    }
}
