package com.example.sagar.tabsapp;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by sagar on 2/2/18.
 */

public class tab2_Attendance extends Fragment {
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab2_attendance, container, false);
        displayClassData();
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            displayClassData();
        }
    }


    public void displayClassData() {
        rootView.findViewById(R.id.tab2noclasstext).setVisibility(View.INVISIBLE);
        final ArrayList<ClassListItem> item = new ArrayList<ClassListItem>();
        String clsnm, subnm;
        long id;
        DBHandler db = new DBHandler(getContext(), null, null, 1);
        String query = "SELECT * FROM dbclasses";
        SQLiteDatabase sqlitedb = db.getReadableDatabase();
        Cursor cursor = sqlitedb.rawQuery(query, null);
        if (cursor.moveToFirst()) {

            do {
                clsnm = cursor.getString(cursor.getColumnIndex("dbclassname"));
                subnm = cursor.getString(cursor.getColumnIndex("dbsubname"));
                id = cursor.getLong(cursor.getColumnIndex("dbclassid"));

                //displays class list
                item.add(new ClassListItem(clsnm, subnm, id));

            } while (cursor.moveToNext());

        } else {
            rootView.findViewById(R.id.tab2noclasstext).setVisibility(View.VISIBLE);

        }
        ClassCustomArrayAdapter adapter = new ClassCustomArrayAdapter(getContext(), item, 0, 2);
        ListView list = rootView.findViewById(R.id.tab2list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent tab2intent = new Intent(getContext(), markattendance.class);
                ClassListItem clickeditem = item.get(i);
                tab2intent.putExtra("classid", clickeditem.getid());
                tab2intent.putExtra("classname", clickeditem.getclassnm() + " : " + clickeditem.getsubnm());

                //free up space for efficient memory usage
                freeupmemoryclass f = new freeupmemoryclass();
                f.freeMemory();

                startActivity(tab2intent);
            }
        });
    }
}
