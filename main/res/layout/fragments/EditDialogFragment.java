package com.le.simpletodo.fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.le.simpletodo.MainActivity;
import com.le.simpletodo.R;
import com.le.simpletodo.model.ListModel;
import com.le.simpletodo.model.TaskModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Akansha Gupta on 13-02-2017.
 */
public class EditDialogFragment extends DialogFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The task this fragment is presenting.
     */
    CheckBox chkPriority, chkLow, chkMedium, chkHigh;
    private DatePicker mDP;
    private TextView mTextDate;
    private TaskModel mItem;
    private Integer mKey;
    private String mPriority;

    // 1. Defines the listener interface with a method passing back data result.

    public interface EditDialogListener {
        void onDialogUpdates(TaskModel task, Integer key);
    }
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditDialogFragment() {     }

    public static EditDialogFragment newInstance(int id) {

        EditDialogFragment frag = new EditDialogFragment();

        Bundle args = new Bundle();

        args.putInt(ARG_ITEM_ID, id);

        frag.setArguments(args);

        return frag;

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View dView = inflater.inflate(R.layout.fragment_details_edit, container,false);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mKey = Integer.valueOf(getArguments().getInt(ARG_ITEM_ID));
            mItem = ListModel.ITEM_MAP.get(mKey);
        }
        return dView;
    }


    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        final EditText mEditText = (EditText) view.findViewById(R.id.taskName);
        final EditText mEditNote = (EditText) view.findViewById(R.id.taskNote);

        Button mSave = (Button) view.findViewById(R.id.save_button);
        Button mDismiss = (Button) view.findViewById(R.id.cancel_button);
        mDP = (DatePicker) getView().findViewById(R.id.dp);
        mTextDate = (TextView) view.findViewById(R.id.taskDue);

        mEditText.setText(mItem.getItemText());
        mTextDate.setText(mItem.getDueDate());
        mEditNote.setText(mItem.getItemNote());
        mTextDate.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = setDatePicker(mTextDate.getText().toString());
                int year = c.get(Calendar.YEAR);
                int monthOfYear = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

                mDP.init( year,monthOfYear, dayOfMonth, new MyOnDateChangedListener() );
                mDP.setVisibility(View.VISIBLE);

            }
        });
        mPriority = mItem.getPriority();
        chkPriority = (CheckBox) getView().findViewById(R.id.chkHigh);
        switch (mPriority) {
            case ("Low"): chkPriority=(CheckBox) view.findViewById(R.id.chkLow); break;
            case ("Medium"): chkPriority=(CheckBox) view.findViewById(R.id.chkMedium); break;
            default:  break;
        }
        if (chkPriority != null) {
            chkPriority.setEnabled(false);
            chkPriority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case (R.id.chkLow):
                            setCheckPriorityView(v, 1);
                            break;
                        case (R.id.chkMedium):
                            setCheckPriorityView(v, 2);
                            break;
                        case (R.id.chkHigh):
                            setCheckPriorityView(v, 3);
                            break;
                    }
                }
            });
        }

        mSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mItem.itemText = mEditText.getText().toString();
                    mItem.itemNote = mEditNote.getText().toString();
                    mItem.dueDate = mTextDate.getText().toString();
                    mItem.priority = mPriority;
                    Log.d("Feeding", "Update"+ mItem.rowId + mEditNote.getText().toString());
                    EditDialogListener listener = (EditDialogListener) getActivity();
                    listener.onDialogUpdates(mItem, mKey);
                }catch (ClassCastException cce){}
            }
        });

        mDismiss.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog and return back to the parent activity
                dismiss();
            }
        });
       // getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    private class MyOnDateChangedListener implements DatePicker.OnDateChangedListener {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            StringBuilder picker=new StringBuilder();
            picker.append((monthOfYear + 1)+"/");//month is 0 based  
            picker.append(dayOfMonth+"/");
            picker.append(year);
            mTextDate.setText(getDatePicker(picker.toString()));
            mDP.setVisibility(View.GONE);
        }
    };

    private Calendar setDatePicker(String str_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, hh:00", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        Date date = new Date();
        try {
            date = dateFormat.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        return c;
    }

    private String getDatePicker(String picker) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, 00:00", Locale.getDefault());
        SimpleDateFormat savedFormat = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
        Date date = new Date();
        try {
            date = savedFormat.parse(picker);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.format(date);
    }

    private void setCheckPriorityView(View dView, int i) {
        switch (i) {
            case (1):
                chkPriority.setEnabled(true);
                chkPriority.toggle();
                chkPriority = (CheckBox) dView.findViewById(R.id.chkLow);
                chkPriority.toggle();
                chkPriority.setEnabled(false);
                mPriority = "Low";
                break;

            case (2):
                chkPriority.setEnabled(true);
                chkPriority.toggle();
                chkPriority = (CheckBox) dView.findViewById(R.id.chkMedium);
                chkPriority.toggle();
                chkPriority.setEnabled(false);
                mPriority = "Medium";
                break;

            case (3):
                chkPriority.setEnabled(true);
                chkPriority.toggle();
                chkPriority = (CheckBox) dView.findViewById(R.id.chkHigh);
                chkPriority.toggle();
                chkPriority.setEnabled(false);
                mPriority = "High";
                break;
        }

    }
}
