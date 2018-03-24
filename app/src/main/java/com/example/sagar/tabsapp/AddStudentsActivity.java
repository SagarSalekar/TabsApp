package com.example.sagar.tabsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddStudentsActivity extends AppCompatActivity implements AddStudentsDialog.AddStudent {

    private DBHandler db;
    private long classid;

    private ArrayList<StudentListItem> studentitem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstudents);
        Intent i = getIntent();
        classid = i.getLongExtra("classid", 0);
        Toast.makeText(this, String.valueOf(classid), Toast.LENGTH_SHORT).show();
        db = new DBHandler(this, null, null, 1);
        displayStudentsData();
        FloatingActionButton addstudbtn = findViewById(R.id.addstudentbutton);
        addstudbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    public void openDialog() {
        AddStudentsDialog d1 = new AddStudentsDialog();
        d1.show(getFragmentManager(), "New Class");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.studentsactivitymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.importstudents:
                Toast.makeText(this, "importstudents", Toast.LENGTH_SHORT).show();
                break;
            case R.id.deletestudents:
                Toast.makeText(this, "deletestudents", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    @Override
    public void createstudent(String studentname, int rollno) {
        if (!checkalreadyexist(rollno)) {
            long id = db.addNewStudentdb(studentname, rollno, classid);
            addNewStudent(studentname, rollno, id);
        } else {
            Toast.makeText(this, "Student with same roll no already exist!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewStudent(String studentname, int rollno, long id) {
        studentitem.add(new StudentListItem(studentname, rollno, id));
        Toast.makeText(this, "New Student " + studentname + " added sucessfully!", Toast.LENGTH_LONG).show();
        displayStudentsData();
    }

    public void displayStudentsData() {
        findViewById(R.id.nostudentstext).setVisibility(View.INVISIBLE);
        final ArrayList<StudentListItem> item = new ArrayList<StudentListItem>();
        String studname;
        int rollno;
        long id;
        String query = "SELECT * FROM dbstudents WHERE dbclassid=" + classid;
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

        StudentCustomArrayAdapter adapter = new StudentCustomArrayAdapter(this, item, 0);
        ListView list = findViewById(R.id.studentslist);
        list.setAdapter(adapter);


        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                StudentListItem tempitem = item.get(i);
                deletedialog(tempitem);
                return true;
            }
        });


        cursor.close();

    }

    //display delete dialog
    public void deletedialog(final StudentListItem tempitem) {
        LinearLayout l1 = new LinearLayout(this);
        TextView t1 = new TextView(this);
        l1.addView(t1);
        t1.setText("Are you sure you want to delete this student " + tempitem.getstudentsnm() + "?");
        t1.setPadding(20, 20, 20, 20);
        t1.setTextSize(16);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(l1)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteoreditstudent(tempitem);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    public void deleteoreditstudent(StudentListItem clickedItem) {
        db.deleteStudentdb(clickedItem);
        displayStudentsData();
        Toast.makeText(this, clickedItem.getstudentsnm() + " student deleted successfully! ", Toast.LENGTH_SHORT).show();
    }

    public boolean checkalreadyexist(int rollno) {
        String query = "SELECT * FROM dbstudents WHERE dbclassid=" + classid + " AND dbrollno=" + rollno;
        SQLiteDatabase sqlitedb = db.getReadableDatabase();
        Cursor cursor = sqlitedb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return true;

        }
        return false;
    }


}
