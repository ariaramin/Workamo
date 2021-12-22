package com.example.todo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.todo.Database.AppDatabase;
import com.example.todo.Dialog.AppDialog;
import com.example.todo.R;
import com.example.todo.Database.Task;
import com.example.todo.Adapter.TaskAdapter;
import com.example.todo.Database.TaskDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AppDialog.TaskDialogListener, TaskAdapter.TaskItemEventListener {

    ImageView starterImageView;
    FloatingActionButton addTaskButton;
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
                    taskAdapter.setNewItems(tasks);
                } else {
                    List<Task> tasks = taskDao.getTasks();
                    taskAdapter.setNewItems(tasks);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Set tasks in recycler view
        RecyclerView tasksRv = findViewById(R.id.tasksRecyclerView);
        tasksRv.setAdapter(taskAdapter);
        tasksRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        tasksRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    addTaskButton.hide();
                } else {
                    addTaskButton.show();
                }
            }
        });
        List<Task> tasks = taskDao.getTasks();
        taskAdapter.addItemList(tasks);

        addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("status", ADD_TASK_DIALOG_ID);
                AppDialog addTaskDialog = new AppDialog();
                addTaskDialog.setArguments(bundle);
                addTaskDialog.show(getSupportFragmentManager(), null);
            }
        });

        View menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });

        if (tasks.size() <= 0) {
            starterImageView.setVisibility(View.VISIBLE);
            startButtonAnimation();
        }
    }

    @Override
    public void OnAddNewTask(Task task) {
        long taskId = taskDao.addTask(task);
        if (taskId != -1) {
            task.setId(taskId);
            taskAdapter.addItem(task);
            starterImageView.setVisibility(View.GONE);
            addTaskButton.setAnimation(null);
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
            startButtonAnimation();
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

    private void startButtonAnimation() {
        Animation pulseAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, .5f);
        pulseAnimation.setDuration(1000);
        pulseAnimation.setRepeatCount(Animation.INFINITE);
        pulseAnimation.setRepeatMode(Animation.REVERSE);
        addTaskButton.setAnimation(pulseAnimation);
    }

    private void showMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.deleteAllTasks) {
                    if (taskDao.getTasks().size() > 0) {
                        taskDao.clearAllTasks();
                        taskAdapter.clearItems();
                        starterImageView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_tasks_error), Toast.LENGTH_SHORT).show();
                    }
                    startButtonAnimation();
                } else if (item.getItemId() == R.id.sortByPriority) {
                    if (taskDao.getTasks().size() > 0)
                        taskAdapter.sortByPriority();
                    else
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_tasks_error), Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.sortByDate) {
                    if (taskDao.getTasks().size() > 0)
                        taskAdapter.sortByDate();
                    else
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_tasks_error), Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.deleteCompletedTasks) {
                    if (taskDao.getTasks().size() > 0) {
                        for (Task task :
                                taskDao.getTasks()) {
                            if (task.isCompleted()) {
                                int result = taskDao.deleteTask(task);
                                if (result > 0) {
                                    taskAdapter.deleteItem(task);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_completed_tasks_error), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_tasks_error), Toast.LENGTH_SHORT).show();
                    }
                    if (taskAdapter.getItemCount() <= 0) {
                        starterImageView.setVisibility(View.VISIBLE);
                        startButtonAnimation();
                    }
                }
                return false;
            }
        });
        popupMenu.show();
    }
}