package com.example.sagar.tabsapp;

/**
 * Created by sagar on 13/2/18.
 */

public class ClassListItem {

    private String mclassname;
    private String msubname;
    private long mid;

    public ClassListItem(String classname, String subname, long id) {
        mclassname = classname;
        msubname = subname;
        mid = id;
    }

    public String getclassnm() {
        return mclassname;
    }

    public String getsubnm() {
        return msubname;
    }

    public long getid() {
        return mid;
    }
}
