package com.example.sagar.tabsapp;

/**
 * Created by sagar on 9/3/18.
 */

public class StudentListItem {

    private String mstudentNM;
    private int mrollNo;
    private long mid;

    public StudentListItem(String studentNM, int rollNo, long id) {
        mstudentNM = studentNM;
        mrollNo = rollNo;
        mid = id;
    }

    public String getstudentsnm() {
        return mstudentNM;
    }

    public int getrollno() {
        return mrollNo;
    }

    public long getid() {
        return mid;
    }

}
