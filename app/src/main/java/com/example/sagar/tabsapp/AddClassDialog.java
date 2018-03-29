package com.example.sagar.tabsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by sagar on 12/2/18.
 */

public class AddClassDialog extends AppCompatDialogFragment {
    private EditText classname;
    private EditText subname;
    private boolean update;
    private long classID;
    private View v1;
    private AddClass addc;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v1 = inflater.inflate(R.layout.add_class_dialog, null);
        update = getArguments().getBoolean("update");

        builder.setView(v1)
                .setTitle("Create new entry")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        if (update) {
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        } else {
            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }


        classname = v1.findViewById(R.id.classname);
        subname = v1.findViewById(R.id.subname);

        if (update) {
            classname.setText(getArguments().getString("className"));
            subname.setText(getArguments().getString("subName"));
            classID = getArguments().getLong("classID");
        }

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
                        String classnm = classname.getText().toString();
                        String subnm = subname.getText().toString();
                        if (!classnm.isEmpty() && !subnm.isEmpty()) {
                            addc.createclass(classID, classnm, subnm, update);
                            dialog.dismiss();
                        } else if (classnm.isEmpty()) {
                            classname.setError("Class name required!");
                            classname.requestFocus();
                        } else {
                            subname.setError("Subject name required!");
                            subname.requestFocus();
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
            addc = (AddClass) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddClass");
        }
    }


    public interface AddClass {
        void createclass(long classId, String classname, String subname, boolean update);
    }


}
