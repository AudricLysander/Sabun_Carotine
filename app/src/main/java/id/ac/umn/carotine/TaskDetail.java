package id.ac.umn.carotine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import id.ac.umn.carotine.Adapter.ToDoAdapter;
import id.ac.umn.carotine.Model.ToDoModel;
import id.ac.umn.carotine.Utils.DatabaseHandler;

public class TaskDetail extends AppCompatActivity implements DialogCloseListener{

    private ToDoAdapter adapter;

    private int position, id;
    private String task;

    public Button editTaskBtn, startTimerBtn;
    public TextView taskName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        getSupportActionBar().setTitle("Focus"); //set title di bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //biar ada tombol kembali

        adapter = new ToDoAdapter(this);

        startTimerBtn = findViewById(R.id.startTimerBtn);
        editTaskBtn = findViewById(R.id.editTaskBtn);

        taskName = findViewById(R.id.taskName);
        Intent mainIntent = getIntent();
        taskName.setText(mainIntent.getStringExtra("taskName"));

        id = mainIntent.getIntExtra("taskId", 0);
        task = mainIntent.getStringExtra("taskName");

        editTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTask(id, task);
            }
        });

    }

    public void startTimer() {
        Log.i("TAG", "startTimer: Mulai!");
    }

    public void editTask(int id, String task) {
        adapter.editTask(id, task);
//        AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {

    }
}