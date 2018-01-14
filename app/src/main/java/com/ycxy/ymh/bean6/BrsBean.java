package com.ycxy.ymh.bean6;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Y&MH on 2018-1-14.
 */

public class BrsBean {
    /**
     * 480 : http://v4.music.126.net/20180115093101/4f844adeef22838f65cfff71ef721752/web/cloudmusic/NzI4Mjg2ODE=/b1687caeb3f0b9f6d189f8459e708085/18f40f718b11fab82ec1b44673b43ed1.mp4
     * 240 : http://v4.music.126.net/20180115093101/27f8d6c7c45b230795555b03127d15d4/web/cloudmusic/NzI4Mjg2ODE=/b1687caeb3f0b9f6d189f8459e708085/573902c555ae9454c2824dc69f26bc00.mp4
     * 720 : http://v4.music.126.net/20180115093101/548eb2728b07008b8979c0e753a7a239/web/cloudmusic/NzI4Mjg2ODE=/b1687caeb3f0b9f6d189f8459e708085/3bf050ab2cf25404edd235a18247526f.mp4
     */

    @JSONField(name = "480")
    private String _$480;
    @JSONField(name = "240")
    private String _$240;
    @JSONField(name = "720")
    private String _$720;

    public String get_$480() {
        return _$480;
    }

    public void set_$480(String _$480) {
        this._$480 = _$480;
    }

    public String get_$240() {
        return _$240;
    }

    public void set_$240(String _$240) {
        this._$240 = _$240;
    }

    public String get_$720() {
        return _$720;
    }

    public void set_$720(String _$720) {
        this._$720 = _$720;
    }
}
