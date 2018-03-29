package com.example.sagar.tabsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class importStudentsActivity extends AppCompatActivity {

    static String TAG = "ExcelLog";
    ListView lvInternalStorage;
    String lastDirectory;
    ArrayList<String> pathHistory;
    int count = 0;
    File file;
    DBHandler db;
    long classid;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;

    private static boolean readExcelFile(Context context, String filePath, DBHandler db, long classid) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        File file = new File(filePath);
        FileInputStream myInput = null;
        try {
            myInput = new FileInputStream(file);
            // Create a workbook using the File System
            XSSFWorkbook myWorkBook = new XSSFWorkbook(myInput);

            // Get the first sheet from workbook
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);

            int rowcount = mySheet.getPhysicalNumberOfRows();

            for (int i = 1; i < rowcount; i++) {
                XSSFRow row = mySheet.getRow(i);
                int columncount = row.getPhysicalNumberOfCells();
                if (columncount > 2) {
                    Toast.makeText(context, "Excel file has invalid column format!", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    XSSFCell rollnocell = row.getCell(0);
                    rollnocell.setCellType(Cell.CELL_TYPE_NUMERIC);
                    Double drollno = rollnocell.getNumericCellValue();
                    int rollno = drollno.intValue();
                    XSSFCell namecell = row.getCell(1);
                    String name = namecell.toString();

                    db.addNewStudentdb(0, name, rollno, classid, false);

                }
            }
            Toast.makeText(context, "Completed!", Toast.LENGTH_SHORT).show();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_students);

        db = new DBHandler(importStudentsActivity.this, null, null, 1);

        Intent i = getIntent();
        classid = i.getLongExtra("classid", 0);

        lvInternalStorage = (ListView) findViewById(R.id.lvInternalStorage);
        //Opens the SDCard or phone memory
        count = 0;
        pathHistory = new ArrayList<String>();
        pathHistory.add(count, System.getenv("EXTERNAL_STORAGE"));
        Log.d(TAG, "btnSDCard: " + pathHistory.get(count));
        checkInternalStorage();


        lvInternalStorage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lastDirectory = pathHistory.get(count);
                if (lastDirectory.equals(adapterView.getItemAtPosition(i))) {
                    Log.d(TAG, "lvInternalStorage: Selected a file for upload: " + lastDirectory);

                    //Execute method for reading the excel data.
                    readExcelFile(importStudentsActivity.this, lastDirectory, db, classid);
                    importStudentsActivity.this.finish();
                    //free up space for efficient memory usage
                    freeupmemoryclass f = new freeupmemoryclass();
                    f.freeMemory();

                } else {
                    count++;
                    pathHistory.add(count, (String) adapterView.getItemAtPosition(i));
                    checkInternalStorage();
                    Log.d(TAG, "lvInternalStorage: " + pathHistory.get(count));
                }
            }
        });
    }

    private void checkInternalStorage() {
        Log.d(TAG, "checkInternalStorage: Started.");
        try {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, "\"No SD card found.\"", Toast.LENGTH_SHORT).show();
            } else {
                // Locate the image folder in your SD Car;d
                file = new File(pathHistory.get(count));
                Log.d(TAG, "checkInternalStorage: directory path: " + pathHistory.get(count));
            }

            listFile = file.listFiles();

            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];

            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
            }

            for (int i = 0; i < listFile.length; i++) {
                Log.d("Files", "FileName:" + listFile[i].getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FilePathStrings);
            lvInternalStorage.setAdapter(adapter);
        } catch (NullPointerException e) {
            Log.e(TAG, "checkInternalStorage: NULLPOINTEREXCEPTION " + e.getMessage());
        }
    }
}
