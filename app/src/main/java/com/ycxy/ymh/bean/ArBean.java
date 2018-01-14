package com.ycxy.ymh.bean;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-13.
 */

public class ArBean {
    /**
     * id : 6452
     * name : 周杰伦
     * tns : []
     * alias : []
     */

    private int id;
    private String name;
    private List<?> tns;
    private List<?> alias;

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

    public List<?> getTns() {
        return tns;
    }

    public void setTns(List<?> tns) {
        this.tns = tns;
    }

    public List<?> getAlias() {
        return alias;
    }

    public void setAlias(List<?> alias) {
        this.alias = alias;
    }
}
