package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    private EditText search;
    private Button fab;

//    private static  String testUrl = "https://init.kz/wp-json/myplugin/v1/leads?key=6FD2E542986A40269016469F8CA4E360";
//   RequestQueue queue;

    private RecyclerView taskRV;
    private ToDoAdapter tasksAdapter;
    private List<ToDoModel> taskList = new ArrayList<>();
    private DatabaseHandler db;
    private Button btnSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab  = findViewById(R.id.fab);



        getSupportActionBar().hide();
        db = new DatabaseHandler(this);
        db.openDatabase();


        taskList = new ArrayList<>();
        taskRV = (RecyclerView) findViewById(R.id.tasksRV);
        taskRV.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(db, this);
        taskRV.setAdapter(tasksAdapter);

        ToDoModel task = new ToDoModel();
        task.setTask("Boards");
        task.setStatus(0);
        task.setId(1);
        taskList.add(task);

        taskList.add(task);
        taskList.add(task);
        taskList.add(task);
        taskList.add(task);
        taskList.add(task);
        taskList.add(task);
        ////  taskList.add(1, "something");
        tasksAdapter.setTasks(taskList);
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(taskRV);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        search = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerms = search.getText().toString();
                if (!searchTerms.equals("")) {
                    searchNet(searchTerms);
                }
            }
        });
    }


    private void searchNet(String words) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.putExtra(SearchManager.QUERY, words);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            searchNetCompat(words);
        }

    }

    private void searchNetCompat(String words) {
        try {
            Uri uri = Uri.parse("https://init.kz/dashboard/leads" + words);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}

