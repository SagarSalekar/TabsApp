package com.example.sagar.tabsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sagar on 17/3/18.
 */

public class studentattendanceadapter extends ArrayAdapter<StudentListItem> {

    private int displaypresentyview;
    private ArrayList<StudentListItem> studentitem;
    private View listItemView;
    private long classid;
    private ListView list;

    private DBHandler db = new DBHandler(getContext(), null, null, 1);

    public studentattendanceadapter(Context context, ArrayList<StudentListItem> studentitem, long classid, ListView list) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, studentitem);
        this.studentitem = studentitem;
        this.classid = classid;
        this.list = list;
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
                list.smoothScrollToPosition(position + 1);
            }
        });

        listItemView.findViewById(R.id.attendanceabsentbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Absent " + studentitem.get(position).getstudentsnm(), Toast.LENGTH_SHORT).show();
                db.markabsent(studentitem.get(position).getid(), classid);
                list.smoothScrollToPosition(position + 1);
            }
        });


        // Return the whole list item layout (containing 2 TextViews)
        // so that it can be shown in the ListView
        return listItemView;
    }


}
