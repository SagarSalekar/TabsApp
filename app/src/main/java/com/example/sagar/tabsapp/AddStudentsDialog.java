package com.example.sagar.tabsapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by sagar on 9/3/18.
 */

public class AddStudentsDialog extends DialogFragment {
    private EditText studname;
    private EditText rollno;
    private View v1;
    private AddStudentsDialog.AddStudent addc;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v1 = inflater.inflate(R.layout.add_students_dialog, null);
        builder.setView(v1)
                .setTitle("Create new entry")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        studname = v1.findViewById(R.id.studentname);
        rollno = v1.findViewById(R.id.rollnumber);

        //dialog box is created with builder.create
        final AlertDialog dialog = builder.create();

        //dialog box request softInputMode i.e virtual keyboard focus
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String studnm = studname.getText().toString();
                        int srollno = Integer.parseInt(rollno.getText().toString());
                        if (!studnm.isEmpty() && srollno != 0) {
                            addc.createstudent(studnm, srollno);
                            dialog.dismiss();
                        } else if (studnm.isEmpty()) {
                            studname.setError("Student name required!");
                            studname.requestFocus();
                        } else {
                            rollno.setError("Roll no required!");
                            rollno.requestFocus();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            addc = (AddStudentsDialog.AddStudent) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddClass");
        }
    }


    public interface AddStudent {
        void createstudent(String studentname, int rollno);
    }


}
