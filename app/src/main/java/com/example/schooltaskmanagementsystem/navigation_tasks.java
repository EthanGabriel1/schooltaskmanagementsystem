package com.example.schooltaskmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltaskmanagementsystem.Adapter.TaskAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

public class navigation_tasks extends Fragment {

    private static final String PREFS_NAME = "TasksPrefs";
    private static final String KEY_TASKS = "Tasks";

    private FloatingActionButton btnAdd;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<String> tasks;
    private int editingTaskPosition = -1; // Track the position of the task being edited

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        btnAdd = view.findViewById(R.id.btnAdd);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Set up GridLayoutManager with 2 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        tasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), tasks, this::showEditTaskBottomSheet);
        recyclerView.setAdapter(taskAdapter);

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load tasks from SharedPreferences
        loadTasks();

        // Show BottomSheetDialog when "+" button is clicked
        btnAdd.setOnClickListener(v -> showAddTaskBottomSheet());
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save tasks to SharedPreferences
        saveTasks();
    }

    private void saveTasks() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TASKS, String.join(",", tasks));
        editor.apply();
    }

    private void loadTasks() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String tasksString = sharedPreferences.getString(KEY_TASKS, "");
        if (!tasksString.isEmpty()) {
            tasks.addAll(new ArrayList<>(Arrays.asList(tasksString.split(","))));
            taskAdapter.notifyDataSetChanged();
        }
    }

    private void showAddTaskBottomSheet() {
        // Inflate the custom layout for the bottom sheet
        LayoutInflater inflater = getLayoutInflater();
        View bottomSheetView = inflater.inflate(R.layout.bottom_sheet_add_task, null);

        // Get references to EditText and Button
        EditText editTextHeading = bottomSheetView.findViewById(R.id.editTextHeading);
        EditText editTextDescription = bottomSheetView.findViewById(R.id.editTextDescription);
        Button buttonAddTask = bottomSheetView.findViewById(R.id.buttonAddTask);

        // Create and show the BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Set up the "Add Task" button in the bottom sheet
        buttonAddTask.setOnClickListener(v -> {
            String heading = editTextHeading.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (!heading.isEmpty() && !description.isEmpty()) {
                // Combine heading and description into a single task string
                String task = heading + "\n" + description;
                if (editingTaskPosition >= 0) {
                    // Update the task if editing
                    tasks.set(editingTaskPosition, task);
                    taskAdapter.notifyItemChanged(editingTaskPosition);
                    editingTaskPosition = -1; // Reset position
                } else {
                    // Add a new task
                    tasks.add(task);
                    taskAdapter.notifyItemInserted(tasks.size() - 1);
                }
                bottomSheetDialog.dismiss(); // Close the bottom sheet
            }
        });

        bottomSheetDialog.show();
    }

    public void showEditTaskBottomSheet(int position) {
        editingTaskPosition = position;
        String task = tasks.get(position);
        String[] parts = task.split("\n", 2);
        String heading = parts[0];
        String description = parts.length > 1 ? parts[1] : "";

        // Inflate the custom layout for the bottom sheet
        LayoutInflater inflater = getLayoutInflater();
        View bottomSheetView = inflater.inflate(R.layout.bottom_sheet_add_task, null);

        // Get references to EditText and Button
        EditText editTextHeading = bottomSheetView.findViewById(R.id.editTextHeading);
        EditText editTextDescription = bottomSheetView.findViewById(R.id.editTextDescription);
        Button buttonAddTask = bottomSheetView.findViewById(R.id.buttonAddTask);

        // Set the current task text
        editTextHeading.setText(heading);
        editTextDescription.setText(description);

        // Create and show the BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Set up the "Add Task" button in the bottom sheet
        buttonAddTask.setOnClickListener(v -> {
            String newHeading = editTextHeading.getText().toString().trim();
            String newDescription = editTextDescription.getText().toString().trim();

            if (!newHeading.isEmpty() && !newDescription.isEmpty()) {
                // Combine heading and description into a single task string
                String taskString = newHeading + "\n" + newDescription;
                // Update the task
                tasks.set(editingTaskPosition, taskString);
                taskAdapter.notifyItemChanged(editingTaskPosition);
                editingTaskPosition = -1; // Reset position
                bottomSheetDialog.dismiss(); // Close the bottom sheet
            }
        });

        bottomSheetDialog.show();
    }
}
