package com.example.sagar.tabsapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class attendancestatistics extends AppCompatActivity {
    private DBHandler db;
    private long classid;


    private ArrayList<StudentListItem> studentitem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendancestatistics);
        Intent i = getIntent();
        classid = i.getLongExtra("classid", 0);
        Toast.makeText(this, String.valueOf(classid), Toast.LENGTH_SHORT).show();
        db = new DBHandler(this, null, null, 1);
        int studentcount = getStudentsData();
    }

    private int getStudentsData() {
        findViewById(R.id.nostudentstext).setVisibility(View.INVISIBLE);
        final ArrayList<StudentListItem> item = new ArrayList<StudentListItem>();
        String studname;
        int rollno;
        long id;
        String query = "SELECT * FROM dbstudents";
        SQLiteDatabase sqlitedb = db.getReadableDatabase();
        Cursor cursor = sqlitedb.rawQuery(query, null);
        if (cursor.moveToFirst()) {

            do {
                studname = cursor.getString(cursor.getColumnIndex("dbstudname"));
                rollno = cursor.getInt(cursor.getColumnIndex("dbrollno"));
                id = cursor.getLong(cursor.getColumnIndex("dbstudid"));

                //displays class list
                item.add(new StudentListItem(studname, rollno, id));

            } while (cursor.moveToNext());

        } else {
            findViewById(R.id.nostudentstext).setVisibility(View.VISIBLE);

        }

        final ListView list = findViewById(R.id.attendancestudentslist);
        final StudentCustomArrayAdapter adapter = new StudentCustomArrayAdapter(this, item, classid, 0, 1);
        list.setAdapter(adapter);


        cursor.close();

        return item.size();


    }

}
