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
import android.support.v7.app.AlertDialog;
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
    private DBHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1_edit, container, false);
        db = new DBHandler(getContext(), null, null, 1);
        displayClassData();
        dynamicView(rootView);
        return rootView;
    }

    public void dynamicView(final View rootView) {
        FloatingActionButton createvbtn = rootView.findViewById(R.id.addview);
        createvbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDialog(false, null);
            }
        });
    }

    public void openDialog(boolean update, ClassListItem clickedItem) {
        AddClassDialog d1 = new AddClassDialog();
        Bundle args = new Bundle();
        args.putBoolean("update", update);
        if (update) {
            args.putString("className", clickedItem.getclassnm());
            args.putString("subName", clickedItem.getsubnm());
            args.putLong("classID", clickedItem.getid());
        }
        d1.setArguments(args);
        if (update) {
            d1.show(getFragmentManager(), "Update Class");

        } else {
            d1.show(getFragmentManager(), "New Class");
        }
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

                //free up space for efficient memory usage
                freeupmemoryclass f = new freeupmemoryclass();
                f.freeMemory();

                startActivity(tab1intent);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClassListItem tempitem = item.get(i);
                deleteoreditclass(tempitem);
                return true;
            }
        });


        cursor.close();

    }

    public void deleteoreditclass(final ClassListItem clickedItem) {
        LinearLayout askaction = new LinearLayout(getContext());
        askaction.setOrientation(LinearLayout.VERTICAL);
        TextView editaction = new TextView(getContext());
        TextView deleteaction = new TextView(getContext());
        editaction.setText("Edit");
        editaction.setTextSize(20);
        editaction.setPadding(20, 40, 20, 20);
        deleteaction.setText("Delete");
        deleteaction.setTextSize(20);
        deleteaction.setPadding(20, 20, 20, 40);
        askaction.addView(editaction);
        askaction.addView(deleteaction);

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setView(askaction);
        final AlertDialog dialog = builder.show();

        editaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(true, clickedItem);
                dialog.dismiss();
            }
        });
        deleteaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletedialog(clickedItem);
                dialog.dismiss();
            }
        });
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
                        db.deleteClassdb(tempitem);
                        displayClassData();
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
