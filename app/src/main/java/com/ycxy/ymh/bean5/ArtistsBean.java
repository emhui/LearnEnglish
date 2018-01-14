package com.ycxy.ymh.bean5;

import java.util.List;

/**
 * Created by Y&MH on 2018-1-14.
 */

public class ArtistsBean {
    /**
     * id : 7225
     * name : 陈慧娴
     * alias : ["Priscilla Chan","Priscilla"]
     * transNames : null
     */

    private int id;
    private String name;
    private Object transNames;
    private List<String> alias;

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

    public Object getTransNames() {
        return transNames;
    }

    public void setTransNames(Object transNames) {
        this.transNames = transNames;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }
}
