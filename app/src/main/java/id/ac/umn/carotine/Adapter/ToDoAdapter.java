package id.ac.umn.carotine.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import id.ac.umn.carotine.AddNewTask;
import id.ac.umn.carotine.MainActivity;
import id.ac.umn.carotine.Model.ToDoModel;
import id.ac.umn.carotine.R;
import id.ac.umn.carotine.TaskDetail;
import id.ac.umn.carotine.Utils.DatabaseHandler;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private MainActivity activity;
    private TaskDetail activityTD;
    private DatabaseHandler db;

    public ToDoAdapter(TaskDetail activity) {
        this.activityTD = activity;
    }

    public ToDoAdapter(MainActivity activity) {
        this.activity = activity;
    }

    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db.openDatabase();
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    db.updateStatus(item.getId(), 1);
                    Log.d("TAG", "onCheckedChanged: TerKlikk");
                } else {
                    db.updateStatus(item.getId(), 0);
                    Log.d("TAG", "onCheckedChanged: Batal TerKlikk");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    private boolean toBoolean(int n) {
        return n!=0;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return activity;
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        Log.d("TAG", "editItem: " + position);
        Intent intent = new Intent(this.activity, TaskDetail.class);
        ToDoModel item = todoList.get(position);
        intent.putExtra("taskId", item.getId());
        intent.putExtra("taskName", item.getTask());
        activity.startActivity(intent);

    }
    
    public void editTask(int id, String taskName) {
        Log.d("TAG", "editTask: Oke");

        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("task", taskName);
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activityTD.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }
    }
}
