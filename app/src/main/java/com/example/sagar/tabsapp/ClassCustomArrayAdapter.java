package com.example.sagar.tabsapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sagar on 15/2/18.
 */

public class ClassCustomArrayAdapter extends ArrayAdapter<ClassListItem> {

    private int displaypercentage;
    private View listItemView;
    private int fragmentno;

    public ClassCustomArrayAdapter(Context context, ArrayList<ClassListItem> classitem, int displayStatistics, int fragment) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, classitem);
        this.displaypercentage = displayStatistics;
        fragmentno = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.classlist_item, parent, false);
        }

        // Get the {@link ClassListItem} object located at this position in the list
        ClassListItem currentClass = getItem(position);

        // Find the TextView in the classlist_itemm.xml layout with the ID clsnm
        TextView classTextView = listItemView.findViewById(R.id.clsnm);
        // Get the class name from the current ClassListItem object and
        // set this text on the class TextView
        classTextView.setText(currentClass.getclassnm());

        // Find the TextView in the classlist_item.xmll layout with the ID subnm
        TextView subTextView = listItemView.findViewById(R.id.subnm);
        // Get the subject nam from the current ClassListItem object and
        // set this text on the number TextView
        subTextView.setText(currentClass.getsubnm());

        if (displaypercentage == 1) {
            TextView classavgview = listItemView.findViewById(R.id.classpercentage);
            classavgview.setVisibility(View.VISIBLE);
            int classaverage = (int) calculateclasspresenty(currentClass.getid());
            if (classaverage >= 0) {
                classavgview.setText(String.valueOf(classaverage) + "%");
            }
        }

        // Return the whole list item layout (containing 2 TextViews)
        // so that it can be shown in the ListView
        return listItemView;
    }

    public float calculateclasspresenty(long classid) {
        DBHandler db = new DBHandler(getContext(), null, null, 1);
        int totalcount = 0;
        int presentycount = 0;
        String query = "SELECT * FROM dbattendancerecord WHERE dbclassid =" + classid;
        SQLiteDatabase sqlitedb = db.getReadableDatabase();
        Cursor cursor = sqlitedb.rawQuery(query, null);
        if (cursor.moveToFirst()) {

            do {
                if (cursor.getInt(cursor.getColumnIndex("dbattendance")) == 1) {
                    presentycount++;
                }
                totalcount++;

            } while (cursor.moveToNext());
        } else {
            return 0;
        }
        return ((presentycount * 100) / totalcount);
    }


}
