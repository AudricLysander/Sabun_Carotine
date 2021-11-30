package id.ac.umn.carotine.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import id.ac.umn.carotine.AddNewTask;
import id.ac.umn.carotine.MainActivity;
import id.ac.umn.carotine.Model.ToDoModel;
import id.ac.umn.carotine.R;
import id.ac.umn.carotine.TaskDetail;
import id.ac.umn.carotine.Utils.DatabaseHandler;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private ArrayList<Integer> listCheckedCheckBox = new ArrayList<>();

    private List<ToDoModel> todoList;
    private MainActivity activity;
    private DatabaseHandler db;

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
                int currPos = holder.getAdapterPosition();

                if(isChecked) {
                    db.updateStatus(item.getId(), 1);
                    Log.d("TAG", "onCheckedChanged: TerKlikk");
                    holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    deleteItem(currPos);
                } else {
                    db.updateStatus(item.getId(), 0);
                    Log.d("TAG", "onCheckedChanged: Batal TerKlikk");
                    holder.task.setPaintFlags(0);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("It's Completed!");
        builder.setMessage("Do you want to delete this task?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            db.deleteTask(item.getId());
            todoList.remove(position);
            notifyItemRemoved(position);
                }
            });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
                notifyItemChanged(position);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showItemDetail(int position) {
        Log.d("TAG", "editItem: " + position);
        Intent intent = new Intent(this.activity, TaskDetail.class);
        ToDoModel item = todoList.get(position);
        intent.putExtra("taskId", item.getId());
        intent.putExtra("taskName", item.getTask());
        activity.startActivity(intent);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }
    }
}
