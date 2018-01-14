package com.ycxy.ymh.bean5;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-14.
 */

public class ResultBean {
    /**
     * mvCount : 2
     * mvs : [{"id":5308821,"cover":"https://p4.music.126.net/pKYXm--aKwaKdE_WPoCoRg==/1413971960701251.jpg","name":"千千阙歌","playCount":1630774,"briefDesc":null,"desc":null,"artistName":"陈慧娴","artistId":7225,"duration":307000,"mark":0,"artists":[{"id":7225,"name":"陈慧娴","alias":["Priscilla Chan","Priscilla"],"transNames":null}]},{"id":5620579,"cover":"https://p4.music.126.net/3ZN9xrrw0qKvbLP5uHBjhA==/3313928054094591.jpg","name":"千千阙歌（live 89）","playCount":288248,"briefDesc":null,"desc":null,"artistName":"张国荣","artistId":6457,"duration":303000,"mark":0,"artists":[{"id":6457,"name":"张国荣","alias":["Leslie Cheung"],"transNames":null}]}]
     */

    private int mvCount;
    private List<MvsBean> mvs;

    public int getMvCount() {
        return mvCount;
    }

    public void setMvCount(int mvCount) {
        this.mvCount = mvCount;
    }

    public List<MvsBean> getMvs() {
        return mvs;
    }

    public void setMvs(List<MvsBean> mvs) {
        this.mvs = mvs;
    }
}
