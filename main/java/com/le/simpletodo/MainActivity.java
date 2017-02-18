package com.le.simpletodo;

import android.content.ContentValues;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.le.simpletodo.adapter.MyTodoAdapter;
import com.le.simpletodo.dao.TaskContract;
import com.le.simpletodo.dao.TodoItemDatabase;
import com.le.simpletodo.fragments.EditDialogFragment;
import com.le.simpletodo.fragments.MyAlertDialogFragment;
import com.le.simpletodo.model.ListModel;
import com.le.simpletodo.model.TaskModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        EditDialogFragment.EditDialogListener,
        MyAlertDialogFragment.AlertDialogListener {

    EditDialogFragment editDialogFragment;
    MyAlertDialogFragment alertDialog;
    MyTodoAdapter adapter;
    ListView lvItems;
    ListModel.TodoItem clickedTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Populates the list model with to do items from db
        readItemsFromDb();
        lvItems = (ListView) findViewById(R.id.lvItems);
        // Create the adapter to convert the array to views
        adapter = new MyTodoAdapter(this, ListModel.ITEMS);
        lvItems.setAdapter(adapter);
        //adapter.addAll(todo_list);
        setupListViewListener();
    }

    private void setupListViewListener() {

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedTask = (ListModel.TodoItem) parent.getItemAtPosition(position);
                showEditDialog();
            }
        });

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                clickedTask = (ListModel.TodoItem) parent.getItemAtPosition(position);
                showAlertDialog();
                boolean alerted = true;
                return alerted;
            }
        });

    }

    private void showAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        alertDialog = MyAlertDialogFragment.newInstance("Delete Item", clickedTask.priority);
        alertDialog.show(fm, "fragment_alert");
    }

    @Override
    public void onDialogAlert() {
        TaskModel task = ListModel.ITEM_MAP.get(Integer.valueOf(clickedTask.detailId));
        Log.d("TAg", "RowId to delete "+ task.rowId);
        TodoItemDatabase dbHelper = new TodoItemDatabase(this);
        dbHelper.deleteEntry(task.rowId);
        ListModel.ITEM_MAP.remove(Integer.valueOf(clickedTask.detailId));
        int location = ListModel.ITEMS.indexOf(clickedTask);
        ListModel.ITEMS.remove(location);
        adapter.notifyDataSetChanged();
        alertDialog.dismiss();
        clickedTask = null;
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        editDialogFragment = EditDialogFragment.newInstance(clickedTask.detailId);
        editDialogFragment.show(fm, "fragment_details_edit");
    }

    @Override
    public void onDialogUpdates(TaskModel task, Integer key) {
        ContentValues map = new ContentValues();
        Log.d("TAg", "RowId to update"+ task.rowId);

        map.put(TaskContract.TaskEntry.ITEM_TEXT,task.getItemText() );
        map.put(TaskContract.TaskEntry.NOTE,task.getItemNote() );
        map.put(TaskContract.TaskEntry.DUE_DATE,task.getDueDate() );
        map.put(TaskContract.TaskEntry.PRIORITY,task.getPriority() );
        TodoItemDatabase dbHelper = new TodoItemDatabase(this);
        dbHelper.editEntry(map, task.rowId);
        ListModel.ITEM_MAP.put(key, task);
        int location = ListModel.ITEMS.indexOf(clickedTask);
        clickedTask.content= task.getItemText();
        clickedTask.priority= task.getPriority();
        clickedTask.due = task.getDueDate();
        ListModel.ITEMS.set(location, clickedTask);
        adapter.notifyDataSetChanged();
        editDialogFragment.dismiss();
        clickedTask = null;
    }


    private void readItemsFromDb() {
        TodoItemDatabase dbHelper = new TodoItemDatabase(this);
        dbHelper.getInformation();
    }

    private TaskModel writeNewItemToDb(String itemText) {
        TodoItemDatabase dbHelper = new TodoItemDatabase(this);
        TaskModel task = new TaskModel();
        task.itemText=itemText;
        task.dueDate=getDateTime();
        task.itemNote="Add detailed notes if any";
        task.priority="High";
        task.status="TO_DO";
        long id = dbHelper.put_information(task);
        task.rowId = (int) id;
        return task;
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String content = etNewItem.getText().toString();
        etNewItem.setText("");
        TaskModel item = writeNewItemToDb(content);
        int mapHash = ListModel.ITEM_MAP.size()+1;
        ListModel.ITEM_MAP.put(Integer.valueOf(mapHash), item);
        ListModel.ITEMS.add(new ListModel.TodoItem(mapHash, content, "High",getDateTime()));
        adapter.notifyDataSetChanged();
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, hh:00", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


}
