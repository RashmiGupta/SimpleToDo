package com.le.simpletodo.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by Akansha Gupta on 16-02-2017.
 */
public class MyAlertDialogFragment extends DialogFragment {

    // 1. Defines the listener interface with a method passing back data result.

    public interface AlertDialogListener {
        void onDialogAlert();
    }

    public MyAlertDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static MyAlertDialogFragment newInstance(String title, String priority) {

        MyAlertDialogFragment frag = new MyAlertDialogFragment();

        Bundle args = new Bundle();

        args.putString("title", title);
        args.putString("priority", priority);

        frag.setArguments(args);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString("title");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle(title);

        alertDialogBuilder.setMessage("Are you deleting ToDo Item of priority " +
                getArguments().getString("priority")+ "?");

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                Log.d("Feeding", "Delete row");
                AlertDialogListener listener = (AlertDialogListener) getActivity();
                listener.onDialogAlert();
            }

        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }

        });
        return alertDialogBuilder.create();
    }
}