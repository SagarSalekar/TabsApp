package com.example.sagar.tabsapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sagar on 10/3/18.
 */

public class StudentCustomArrayAdapter extends ArrayAdapter<StudentListItem> {

    private int displaypresentyview;
    private ArrayList<StudentListItem> studentitem;
    private View listItemView;
    private long classid;
    private int displaystatistics;

    private DBHandler db = new DBHandler(getContext(), null, null, 1);

    public StudentCustomArrayAdapter(Context context, ArrayList<StudentListItem> studentitem, long classid, int displaypresentyview, int displaystatistics) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, studentitem);
        this.displaypresentyview = displaypresentyview;
        this.studentitem = studentitem;
        this.classid = classid;
        this.displaystatistics = displaystatistics;
    }

    public StudentCustomArrayAdapter(Context context, ArrayList<StudentListItem> studentitem, int displaypresentyview) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, studentitem);
        this.displaypresentyview = displaypresentyview;
        this.studentitem = studentitem;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.studentlist_item, parent, false);
        }

        // Get the {@link StudentListItem} object located at this position in the list
        StudentListItem currentStudent = getItem(position);

        // Find the TextView in the classlist_itemm.xml layout with the ID clsnm
        final TextView studentTextView = listItemView.findViewById(R.id.studentnameview);
        // Get the class name from the current ClassListItem object and
        // set this text on the class TextView
        studentTextView.setText(currentStudent.getstudentsnm());

        // Find the TextView in the classlist_item.xmll layout with the ID subnm
        TextView rollnoTextView = listItemView.findViewById(R.id.rollnumberview);
        // Get the subject nam from the current ClassListItem object and
        // set this text on the number TextView
        rollnoTextView.setText(String.valueOf(currentStudent.getrollno()));

        if (displaystatistics == 1) {
            RelativeLayout studavglayout = listItemView.findViewById(R.id.studentattendanceview);
            studavglayout.setVisibility(View.VISIBLE);
            TextView studavgpview = listItemView.findViewById(R.id.studentattendancepercentageview);
            TextView studavgnview = listItemView.findViewById(R.id.studentattendancenumberview);
            presentyholder studaverage = calculatestudentpresenty(classid, currentStudent.getid());
            if (studaverage.totalcount > 0) {
                studavgpview.setText(String.valueOf((studaverage.presentycount * 100) / studaverage.totalcount) + "%");
                studavgnview.setText(String.valueOf(studaverage.presentycount) + "/" + String.valueOf(studaverage.totalcount));
            } else {
                studavgpview.setText("0%");
                studavgnview.setText("0/0");
            }
        }

        // Return the whole list item layout (containing 2 TextViews)
        // so that it can be shown in the ListView
        return listItemView;
    }

    public presentyholder calculatestudentpresenty(long classid, long studid) {
        DBHandler db = new DBHandler(getContext(), null, null, 1);
        presentyholder p = new presentyholder();
        String query = "SELECT * FROM dbattendancerecord WHERE dbclassid =" + classid + " AND dbstudid =" + studid;
        SQLiteDatabase sqlitedb = db.getReadableDatabase();
        Cursor cursor = sqlitedb.rawQuery(query, null);
        if (cursor.moveToFirst()) {

            do {
                if (cursor.getInt(cursor.getColumnIndex("dbattendance")) == 1) {
                    p.presentycount++;
                }
                p.totalcount++;

            } while (cursor.moveToNext());
        } else {
            return p;

        }
        return p;
    }


    private class presentyholder {
        int presentycount = 0, totalcount = 0;
    }
}
