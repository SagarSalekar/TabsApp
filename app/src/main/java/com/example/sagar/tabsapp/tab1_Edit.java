package com.example.sagar.tabsapp;

/**
 * Created by sagar on 2/2/18.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class tab1_Edit extends Fragment {

    private View rootView;
    private ArrayList<ClassListItem> classitem = new ArrayList<ClassListItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1_edit, container, false);
        displayClassData();
        dynamicView(rootView);
        return rootView;
    }

    public void dynamicView(final View rootView) {
        FloatingActionButton createvbtn = rootView.findViewById(R.id.addview);
        createvbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    public void openDialog() {
        AddClassDialog d1 = new AddClassDialog();
        d1.show(getFragmentManager(), "New Class");
    }

    public void addNewClass(String classname, String subname, long id) {

        classitem.add(new ClassListItem(classname, subname, id));
        Toast.makeText(getContext(), "New Class " + classname + " added sucessfully!", Toast.LENGTH_LONG).show();
        displayClassData();
    }

    public void displayClassData() {
        rootView.findViewById(R.id.noclasstext).setVisibility(View.INVISIBLE);
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
            rootView.findViewById(R.id.noclasstext).setVisibility(View.VISIBLE);

        }

        ClassCustomArrayAdapter adapter = new ClassCustomArrayAdapter(getContext(), item, 0, 1);
        ListView list = rootView.findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClassListItem tempitem = item.get(i);
                Intent tab1intent = new Intent(getContext(), AddStudentsActivity.class);
                tab1intent.putExtra("classid", tempitem.getid());
                startActivity(tab1intent);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClassListItem tempitem = item.get(i);
                deletedialog(tempitem);
                return true;
            }
        });


        cursor.close();

    }

    public void deleteoreditclass(ClassListItem clickedItem) {
        DBHandler db = new DBHandler(getContext(), null, null, 1);
        db.deleteClassdb(clickedItem);
        Toast.makeText(getContext(), clickedItem.getclassnm() + " class deleted successfully! ", Toast.LENGTH_SHORT).show();
        displayClassData();
    }


    //display delete dialog
    public void deletedialog(final ClassListItem tempitem) {
        LinearLayout l1 = new LinearLayout(getContext());
        TextView t1 = new TextView(getContext());
        l1.addView(t1);
        t1.setText("Are you sure you want to delete this class?");
        t1.setPadding(20, 20, 20, 20);
        t1.setTextSize(16);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setView(l1)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteoreditclass(tempitem);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }
}
