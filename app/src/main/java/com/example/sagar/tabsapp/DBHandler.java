package com.example.sagar.tabsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sagar on 16/2/18.
 */

public class DBHandler extends SQLiteOpenHelper {

    public static final String TABLE_CLASS = "dbclasses";
    public static final String COLUMN_CLASSID = "dbclassid";
    public static final String COLUMN_CLASSNAME = "dbclassname";
    public static final String COLUMN_SUBNAME = "dbsubname";
    public static final String TABLE_STUDENT = "dbstudents";
    public static final String COLUMN_STUDID = "dbstudid";
    public static final String COLUMN_STUDNAME = "dbstudname";
    public static final String COLUMN_ROLLNO = "dbrollno";
    public static final String TABLE_ATTENDANCE = "dbattendancerecord";
    public static final String COLUMN_DATE = "dbattendancedate";
    public static final String COLUMN_PRESENTY = "dbattendance";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "attendance.db";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createclassquery = "CREATE TABLE " + TABLE_CLASS + "( " +
                COLUMN_CLASSID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CLASSNAME + " VARCHAR(20), " +
                COLUMN_SUBNAME + " VARCHAR(20) " +
                ");";
        sqLiteDatabase.execSQL(createclassquery);

        String createstudentsquery = "CREATE TABLE " + TABLE_STUDENT + "( " +
                COLUMN_STUDID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_STUDNAME + " VARCHAR(20), " +
                COLUMN_ROLLNO + " INTEGER, " +
                COLUMN_CLASSID + " INTEGER, FOREIGN KEY (" + COLUMN_CLASSID + ") REFERENCES " + TABLE_CLASS + "(" + COLUMN_CLASSID + ") " +
                ");";
        sqLiteDatabase.execSQL(createstudentsquery);

        String createattendancerecordquery = "CREATE TABLE " + TABLE_ATTENDANCE + "( " +
                COLUMN_DATE + " DATETIME, " +
                COLUMN_PRESENTY + " FLAG INTEGER DEFAULT 0, " +
                COLUMN_STUDID + " INTEGER, " +
                COLUMN_CLASSID + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_STUDID + ") REFERENCES " + TABLE_STUDENT + "(" + COLUMN_STUDID + "), " +
                "FOREIGN KEY (" + COLUMN_CLASSID + ") REFERENCES " + TABLE_CLASS + "(" + COLUMN_CLASSID + ") " +
                ");";
        sqLiteDatabase.execSQL(createattendancerecordquery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        onCreate(sqLiteDatabase);

    }

    public long addNewClassdb(String clsnm, String subnm) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASSNAME, clsnm);
        values.put(COLUMN_SUBNAME, subnm);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_CLASS, null, values);
        db.close();
        Log.d("New Class Added", "New Class Added Successfully");
        return id;

    }

    public void deleteClassdb(ClassListItem item) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CLASS + " WHERE " + COLUMN_CLASSID + "=" + item.getid());
    }

    public long addNewStudentdb(String studentname, int rollno, long classid) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASSID, classid);
        values.put(COLUMN_STUDNAME, studentname);
        values.put(COLUMN_ROLLNO, rollno);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_STUDENT, null, values);
        db.close();
        Log.d("New Student Added", "New Student Added Successfully");
        return id;
    }

    public void deleteStudentdb(StudentListItem item) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_STUDENT + " WHERE " + COLUMN_STUDID + "=" + item.getid());
    }

    public void markpresent(long studentid, long classid) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASSID, classid);
        values.put(COLUMN_STUDID, studentid);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());
        values.put(COLUMN_DATE, strDate);
        values.put(COLUMN_PRESENTY, 1);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ATTENDANCE, null, values);
        db.close();
        Log.d("Marked Present! ", "Attendance record updated successfully");
    }

    public void markabsent(long studentid, long classid) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASSID, classid);
        values.put(COLUMN_STUDID, studentid);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());
        values.put(COLUMN_DATE, strDate);
        values.put(COLUMN_PRESENTY, 0);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ATTENDANCE, null, values);
        db.close();
        Log.d("Marked Absent! ", "Attendance record updated successfully");
    }

}
