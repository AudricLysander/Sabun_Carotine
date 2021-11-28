package id.ac.umn.carotine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import id.ac.umn.carotine.Adapter.ToDoAdapter;
import id.ac.umn.carotine.Model.ToDoModel;
import id.ac.umn.carotine.Utils.DatabaseHandler;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private RecyclerView taskRecyclerView;
    private ToDoAdapter taskAdapter;
    private FloatingActionButton fab;

    private List<ToDoModel> taskList;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("To Do List");

        db = new DatabaseHandler(this);
        db.openDatabase();

        taskList = new ArrayList<>();

        taskRecyclerView = findViewById(R.id.tasksRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new ToDoAdapter(db, this);
        taskRecyclerView.setAdapter(taskAdapter);

        fab = findViewById(R.id.fab);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskAdapter.setTasks(taskList);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskAdapter.setTasks(taskList);
        taskAdapter.notifyDataSetChanged();
    }
}