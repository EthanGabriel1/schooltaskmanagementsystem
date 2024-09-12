package com.example.schooltaskmanagementsystem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltaskmanagementsystem.Adapter.TaskAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;

public class Tasks extends AppCompatActivity implements TaskAdapter.EditTaskListener {

    private static final String PREFS_NAME = "TasksPrefs";
    private static final String KEY_TASKS = "Tasks";

    private Button btnAdd;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<String> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_navigation_tasks); // Ensure this is the correct layout file

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);

        tasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, tasks, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        // Load tasks from SharedPreferences
        loadTasks();

        // Show BottomSheetDialog when "+" button is clicked
        btnAdd.setOnClickListener(view -> showAddTaskBottomSheet());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save tasks to SharedPreferences
        saveTasks();
    }

    private void saveTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TASKS, String.join(",", tasks));
        editor.apply();
    }

    private void loadTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Set up the "Add Task" button in the bottom sheet
        buttonAddTask.setOnClickListener(v -> {
            String heading = editTextHeading.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (!heading.isEmpty() && !description.isEmpty()) {
                // Combine heading and description into a single task string
                String task = heading + "\n" + description;
                tasks.add(task);
                taskAdapter.notifyItemInserted(tasks.size() - 1);
                bottomSheetDialog.dismiss(); // Close the bottom sheet
            }
        });

        bottomSheetDialog.show();
    }

    @Override
    public void onEditTask(int position) {
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Set up the "Add Task" button in the bottom sheet
        buttonAddTask.setOnClickListener(v -> {
            String newHeading = editTextHeading.getText().toString().trim();
            String newDescription = editTextDescription.getText().toString().trim();

            if (!newHeading.isEmpty() && !newDescription.isEmpty()) {
                // Combine heading and description into a single task string
                String taskString = newHeading + "\n" + newDescription;
                // Update the task
                tasks.set(position, taskString);
                taskAdapter.notifyItemChanged(position);
                bottomSheetDialog.dismiss(); // Close the bottom sheet
            }
        });

        bottomSheetDialog.show();
    }
}
