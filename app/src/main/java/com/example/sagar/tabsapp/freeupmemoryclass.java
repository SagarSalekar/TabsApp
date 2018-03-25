package com.example.sagar.tabsapp;

/**
 * Created by sagar on 25/3/18.
 */

public class freeupmemoryclass {
    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
}
