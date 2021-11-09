package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AddTaskDialog.AddNewTaskCallback, UpdateTaskDialog.EditTextCallback, TaskAdapter.TaskItemEventListener{

    private SQLiteHelper sqLiteHelper;
    private final TaskAdapter taskAdapter = new TaskAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    List<Task> tasks = sqLiteHelper.searchInTasks(charSequence.toString());
                    taskAdapter.setNewTasks(tasks);
                } else {
                    List<Task> tasks = sqLiteHelper.getTasks();
                    taskAdapter.setNewTasks(tasks);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        RecyclerView tasksRv = findViewById(R.id.tasksRecyclerView);
        tasksRv.setAdapter(taskAdapter);
        tasksRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        sqLiteHelper = new SQLiteHelper(this);
        List<Task> tasks = sqLiteHelper.getTasks();
        taskAdapter.addItemList(tasks);

        View newTaskButton = findViewById(R.id.addTaskButton);
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTaskDialog addTaskDialog = new AddTaskDialog();
                addTaskDialog.show(getSupportFragmentManager(), null);
            }
        });

        View clearTaskButton = findViewById(R.id.clearTaskButton);
        clearTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteHelper.clearAllTasks();
                taskAdapter.clearItems();
            }
        });
    }

    @Override
    public void OnAddNewTask(Task task) {
        long taskId = sqLiteHelper.addTask(task);

        if (taskId != -1) {
            task.setId(taskId);
            taskAdapter.addItem(task);
        }
    }

    @Override
    public void OnItemDeleteClick(Task task) {
        int result = sqLiteHelper.deleteTask(task);
        if (result > 0) {
            taskAdapter.deleteItem(task);
        }
    }

    @Override
    public void OnItemLongClick(Task task) {
        UpdateTaskDialog dialog = new UpdateTaskDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", task);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void OnItemCheckedChange(Task task) {
        sqLiteHelper.updateTask(task);
    }

    @Override
    public void OnEditText(Task task) {
        int result = sqLiteHelper.updateTask(task);
        if (result > 0) {
            taskAdapter.updateItem(task);
        }
    }
}