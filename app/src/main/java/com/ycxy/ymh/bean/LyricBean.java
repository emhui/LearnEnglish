package com.ycxy.ymh.bean;

import com.ycxy.ymh.bean4.ResultInter;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-5.
 */

public class LyricBean {

    /**
     * code : 0
     * count : 10
     * result : [{"aid":2272745,"artist_id":17249,"lrc":"http://s.gecimi.com/lrc/263/26395/2639587.lrc","sid":2639587,"song":"花好月圆"},{"aid":1643258,"artist_id":18009,"lrc":"http://s.gecimi.com/lrc/177/17737/1773766.lrc","sid":1773766,"song":"花好月圆"},{"aid":1643258,"artist_id":18009,"lrc":"http://s.gecimi.com/lrc/177/17737/1773773.lrc","sid":1773773,"song":"花好月圆"},{"aid":1869410,"artist_id":18009,"lrc":"http://s.gecimi.com/lrc/207/20795/2079528.lrc","sid":2079528,"song":"花好月圆"},{"aid":2514458,"artist_id":36234,"lrc":"http://s.gecimi.com/lrc/298/29812/2981228.lrc","sid":2981228,"song":"花好月圆"},{"aid":2815094,"artist_id":39068,"lrc":"http://s.gecimi.com/lrc/339/33982/3398224.lrc","sid":3398224,"song":"花好月圆"},{"aid":2653535,"artist_id":40796,"lrc":"http://s.gecimi.com/lrc/317/31751/3175113.lrc","sid":3175113,"song":"花好月圆"},{"aid":2776259,"artist_id":41965,"lrc":"http://s.gecimi.com/lrc/334/33456/3345696.lrc","sid":3345696,"song":"花好月圆"},{"aid":3104669,"artist_id":43234,"lrc":"http://s.gecimi.com/lrc/378/37879/3787933.lrc","sid":3787933,"song":"花好月圆"},{"aid":3090665,"artist_id":44636,"lrc":"http://s.gecimi.com/lrc/376/37699/3769939.lrc","sid":3769939,"song":"花好月圆"}]
     */

    private int code;
    private int count;
    public List<ResultInter> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ResultInter> getResult() {
        return result;
    }

    public void setResult(List<ResultInter> result) {
        this.result = result;
    }
}
