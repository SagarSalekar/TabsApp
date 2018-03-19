package com.example.sagar.tabsapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class markattendance extends AppCompatActivity {
    private DBHandler db;
    private long classid;

    private ListView list;
    private thisstudentattendanceadapter adapter;


    private ArrayList<StudentListItem> studentitem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markattendance);
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

        list = findViewById(R.id.markattendancestudentslist);
        adapter = new thisstudentattendanceadapter(this, item, classid, list);
//        final StudentCustomArrayAdapter adapter = new StudentCustomArrayAdapter(this, item,classid,1);
        list.setAdapter(adapter);


        cursor.close();

        return item.size();


    }

    public class thisstudentattendanceadapter extends ArrayAdapter<StudentListItem> {

        private int displaypresentyview;
        private ArrayList<StudentListItem> studentitem;
        private View listItemView;
        private long classid;

        private DBHandler db = new DBHandler(getContext(), null, null, 1);

        public thisstudentattendanceadapter(Context context, ArrayList<StudentListItem> studentitem, long classid, ListView list) {
            // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
            // the second argument is used when the ArrayAdapter is populating a single TextView.
            // Because this is a custom adapter for two TextViews, the adapter is not
            // going to use this second argument, so it can be any value. Here, we used 0.
            super(context, 0, studentitem);
            this.studentitem = studentitem;
            this.classid = classid;
        }


        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // Check if the existing view is being reused, otherwise inflate the view
            listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.studentattendancelayout, parent, false);
            }

            // Get the {@link StudentListItem} object located at this position in the list
            StudentListItem currentStudent = getItem(position);

            // Find the TextView in the classlist_itemm.xml layout with the ID clsnm
            final TextView studentTextView = listItemView.findViewById(R.id.attendancelayoutstudentname);
            // Get the class name from the current ClassListItem object and
            // set this text on the class TextView
            studentTextView.setText(currentStudent.getstudentsnm());

            // Find the TextView in the classlist_item.xmll layout with the ID subnm
            TextView rollnoTextView = listItemView.findViewById(R.id.attendancelayoutrollno);
            // Get the subject nam from the current ClassListItem object and
            // set this text on the number TextView
            rollnoTextView.setText(String.valueOf(currentStudent.getrollno()));


            listItemView.findViewById(R.id.attendancepresentbutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Present " + studentitem.get(position).getstudentsnm(), Toast.LENGTH_SHORT).show();
                    db.markpresent(studentitem.get(position).getid(), classid);
//                    list.getChildAt(position).

                    list.smoothScrollToPosition(position);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.remove(studentitem.get(position));
                        }
                    }, 10000);
                }
            });

            listItemView.findViewById(R.id.attendanceabsentbutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Absent " + studentitem.get(position).getstudentsnm(), Toast.LENGTH_SHORT).show();
                    db.markabsent(studentitem.get(position).getid(), classid);
                    adapter.remove(studentitem.get(position));
                    list.smoothScrollToPosition(position);
                }
            });


            // Return the whole list item layout (containing 2 TextViews)
            // so that it can be shown in the ListView
            return listItemView;
        }

    }


}
