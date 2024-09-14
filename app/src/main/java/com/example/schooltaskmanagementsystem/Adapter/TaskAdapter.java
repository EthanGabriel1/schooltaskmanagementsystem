package com.example.schooltaskmanagementsystem.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltaskmanagementsystem.R;
import java.util.ArrayList;
import java.util.Collections;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private Context context;
    private ArrayList<String> tasks;
    private EditTaskListener editTaskListener;

    public TaskAdapter(Context context, ArrayList<String> tasks, EditTaskListener editTaskListener) {
        this.context = context;
        this.tasks = tasks;
        this.editTaskListener = editTaskListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        String task = tasks.get(position);
        String[] parts = task.split("\n", 2);
        String heading = parts[0];
        String description = parts.length > 1 ? parts[1] : "";

        holder.taskTextView.setText(heading);
        holder.taskDescriptionTextView.setText(description);

        holder.taskMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.task_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_edit) {
                    if (editTaskListener != null) {
                        editTaskListener.onEditTask(position);
                    }
                    return true;
                } else if (id == R.id.menu_delete) {
                    removeTask(position);
                    return true;
                } else if (id == R.id.menu_mark_done) {
                    showMarkAsDoneDialog(position);
                    return true;
                } else if (id == R.id.menu_prioritize) {
                    prioritizeTask(position);
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });
    }

    private void prioritizeTask(int position) {
        String task = tasks.remove(position);
        tasks.add(0, task); // Move the task to the top
        notifyItemMoved(position, 0); // Notify the adapter of the change
    }

    private void showMarkAsDoneDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mark as Done")
                .setMessage("Are you sure you want to mark this task as complete?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    removeTask(position);
                    showCongratsDialog();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showCongratsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("CONGRATS!")
                .setMessage("You have completed the task successfully!")
                .setPositiveButton("OK", (dialog, which) -> {})
                .show();
    }

    private void removeTask(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasks.size());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public interface EditTaskListener {
        void onEditTask(int position);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskDescriptionTextView;
        TextView taskTextView;
        ImageButton taskMenuButton;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTextView = itemView.findViewById(R.id.taskTextView);
            taskDescriptionTextView = itemView.findViewById(R.id.taskDescriptionTextView);
            taskMenuButton = itemView.findViewById(R.id.taskMenuButton);
        }
    }
}