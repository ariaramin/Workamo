package com.example.todo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.todo.Database.AppDatabase;
import com.example.todo.Dialog.AppDialog;
import com.example.todo.R;
import com.example.todo.Database.Task;
import com.example.todo.Adapter.TaskAdapter;
import com.example.todo.Database.TaskDao;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AppDialog.TaskDialogListener, TaskAdapter.TaskItemEventListener {

    ImageView starterImageView;
    private TaskDao taskDao;
    private final TaskAdapter taskAdapter = new TaskAdapter(this);
    private static final int ADD_TASK_DIALOG_ID = 1;
    private static final int EDIT_TASK_DIALOG_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskDao = AppDatabase.getAppDatabase(this).getTaskDao();

        starterImageView = findViewById(R.id.starterImageView);
        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    List<Task> tasks = taskDao.searchInTasks(charSequence.toString());
                    taskAdapter.setNewTasks(tasks);
                } else {
                    List<Task> tasks = taskDao.getTasks();
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
        List<Task> tasks = taskDao.getTasks();
        if (tasks.size() != 0) {
            starterImageView.setVisibility(View.GONE);
        }
        taskAdapter.addItemList(tasks);

        View newTaskButton = findViewById(R.id.addTaskButton);
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("status", ADD_TASK_DIALOG_ID);
                AppDialog addTaskDialog = new AppDialog();
                addTaskDialog.setArguments(bundle);
                addTaskDialog.show(getSupportFragmentManager(), null);
            }
        });

        View clearTaskButton = findViewById(R.id.clearTaskButton);
        clearTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskDao.clearAllTasks();
                taskAdapter.clearItems();
            }
        });
    }

    @Override
    public void OnAddNewTask(Task task) {
        long taskId = taskDao.addTask(task);
        if (taskId != -1) {
            task.setId(taskId);
            taskAdapter.addItem(task);
            starterImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnItemDeleteClick(Task task) {
        int result = taskDao.deleteTask(task);
        if (result > 0) {
            taskAdapter.deleteItem(task);
        }
        if (taskAdapter.getItemCount() <= 0) {
            starterImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void OnItemLongClick(Task task) {
        Bundle bundle = new Bundle();
        bundle.putInt("status", EDIT_TASK_DIALOG_ID);
        bundle.putParcelable("task", task);
        AppDialog dialog = new AppDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void OnItemCheckedChange(Task task) {
        taskDao.updateTask(task);
    }

    @Override
    public void OnEditText(Task task) {
        int result = taskDao.updateTask(task);
        if (result > 0) {
            taskAdapter.updateItem(task);
        }
    }
}