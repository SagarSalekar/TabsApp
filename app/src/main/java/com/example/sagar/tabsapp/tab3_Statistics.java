package com.example.sagar.tabsapp;

/**
 * Created by sagar on 2/2/18.
 */


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class tab3_Statistics extends Fragment {

    private View rootView;
    private DBHandler db;

    private static boolean saveExcelFile(Context context, Workbook wb, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;

        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/TabsApp");
        dir.mkdirs();
        // Create a path where we will place our List of objects on external storage
        File file = new File(dir, fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab3_statistics, container, false);
        displayClassData();
        rootView.findViewById(R.id.generatereportbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatereport();
            }
        });
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
        rootView.findViewById(R.id.tab3noclasstext).setVisibility(View.INVISIBLE);
        final ArrayList<ClassListItem> item = new ArrayList<ClassListItem>();
        String clsnm, subnm;
        long id;
        db = new DBHandler(getContext(), null, null, 1);
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
            rootView.findViewById(R.id.tab3noclasstext).setVisibility(View.VISIBLE);

        }
        ClassCustomArrayAdapter adapter = new ClassCustomArrayAdapter(getContext(), item, 1, 3);
        ListView list = rootView.findViewById(R.id.tab3list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent tab3intent = new Intent(getContext(), attendancestatistics.class);
                ClassListItem clickeditem = item.get(i);
                tab3intent.putExtra("classid", clickeditem.getid());

                //free up space for efficient memory usage
                freeupmemoryclass f = new freeupmemoryclass();
                f.freeMemory();

                startActivity(tab3intent);
            }
        });
    }

    public void generatereport() {
        DBHandler db = new DBHandler(getContext(), null, null, 1);
        String studname, classname, subname, date;
        int rollno, presenty;
        long classid, studid;


        //New Workbook
        Workbook attendanceWorkbook = new XSSFWorkbook();

        String queryclasssheet = "SELECT * FROM dbclasses";
        SQLiteDatabase sqlitedb = db.getReadableDatabase();
        Cursor cursorclasssheet = sqlitedb.rawQuery(queryclasssheet, null);
        if (cursorclasssheet.moveToFirst()) {

            do {
                classname = cursorclasssheet.getString(cursorclasssheet.getColumnIndex("dbclassname"));
                subname = cursorclasssheet.getString(cursorclasssheet.getColumnIndex("dbsubname"));
                classid = cursorclasssheet.getLong(cursorclasssheet.getColumnIndex("dbclassid"));

                //New Sheet for new class
                Sheet writesheet = null;
                writesheet = attendanceWorkbook.createSheet(classname + "-" + subname);

                // Generate column headings
                Row writerowheading = writesheet.createRow(0);

                Cell c = null;

                c = writerowheading.createCell(0);
                c.setCellValue("Roll No");

                c = writerowheading.createCell(1);
                c.setCellValue("Name");

                int row = 1;

                String querystudrow = "SELECT * FROM dbstudents WHERE dbclassid =" + classid;
                Cursor cursorstudrow = sqlitedb.rawQuery(querystudrow, null);

                if (cursorstudrow.moveToFirst()) {

                    do {
                        studname = cursorstudrow.getString(cursorstudrow.getColumnIndex("dbstudname"));
                        rollno = cursorstudrow.getInt(cursorstudrow.getColumnIndex("dbrollno"));
                        studid = cursorstudrow.getLong(cursorstudrow.getColumnIndex("dbstudid"));

                        int presentycount = 0;
                        int totalcount = 0;

                        // Generate column headings
                        Row writerow = writesheet.createRow(row);

                        int column = 2;

                        c = writerow.createCell(0);
                        c.setCellValue(String.valueOf(rollno));
                        c = writerow.createCell(1);
                        c.setCellValue(studname);

                        row++;

                        String queryattendancecolumn = "SELECT * FROM dbattendancerecord WHERE dbclassid=" + classid + " AND dbstudid=" + studid;
                        Cursor cursorattendancecolumn = sqlitedb.rawQuery(queryattendancecolumn, null);

                        if (cursorattendancecolumn.moveToFirst()) {

                            do {
                                date = cursorattendancecolumn.getString(cursorattendancecolumn.getColumnIndex("dbattendancedate"));
                                presenty = cursorattendancecolumn.getInt(cursorattendancecolumn.getColumnIndex("dbattendance"));
                                totalcount++;

                                c = writerowheading.createCell(column);
                                c.setCellValue(date);

                                c = writerow.createCell(column);
                                if (presenty == 1) {
                                    c.setCellValue("P");
                                    presentycount++;
                                } else {
                                    c.setCellValue("A");
                                }

                                column++;

                            } while (cursorattendancecolumn.moveToNext());
                        }

                        c = writerowheading.createCell(column);
                        c.setCellValue(totalcount);

                        c = writerow.createCell(column);
                        c.setCellValue(presentycount);

                        c = writerowheading.createCell(column + 1);
                        c.setCellValue("100%");

                        c = writerow.createCell(column + 1);
                        if (presentycount == 0) {
                            c.setCellValue(String.valueOf("0%"));
                        } else {
                            c.setCellValue(String.valueOf(((presentycount * 100) / totalcount)) + "%");
                        }

                    } while (cursorstudrow.moveToNext());
                }

            } while (cursorclasssheet.moveToNext());
        }

        boolean saved = saveExcelFile(getContext(), attendanceWorkbook, "AttendanceRecord.xlsx");
        if (saved) {
            Toast.makeText(getContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Operation Failed!", Toast.LENGTH_SHORT).show();
        }
    }




}