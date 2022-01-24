package com.ariaramin.workamo.ui.Activities.main;

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
import com.ariaramin.workamo.Adapter.TaskAdapter;
import com.ariaramin.workamo.Database.AppDatabase;
import com.ariaramin.workamo.Database.Task;
import com.ariaramin.workamo.Database.TaskDao;
import com.ariaramin.workamo.ui.Dialogs.AppDialog;
import com.ariaramin.workamo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View, AppDialog.TaskDialogListener, TaskAdapter.TaskItemEventListener {

    ImageView starterImageView;
    FloatingActionButton addTaskButton;
    MainActivityPresenter presenter;
    private static final int ADD_TASK_DIALOG_ID = 1;
    private static final int EDIT_TASK_DIALOG_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TaskDao taskDao = AppDatabase.getAppDatabase(this).getTaskDao();
        TaskAdapter taskAdapter = new TaskAdapter(this);
        presenter = new MainActivityPresenter(this, taskDao, taskAdapter);

        if (presenter.readTasks().size() <= 0) {
            showBackgroundImage();
            startFabAnimation();
        }
    }

    @Override
    public void OnAddNewTask(Task task) {
        presenter.addNewTask(task);
    }

    @Override
    public void OnItemDeleteClick(Task task) {
        presenter.deleteTask(task);
    }

    @Override
    public void OnItemLongClick(Task task) {
        presenter.showEditDialog(task);
    }

    @Override
    public void OnItemCheckedChange(Task task) {
        presenter.updateTask(task);
    }

    @Override
    public void OnEditText(Task task) {
        presenter.updateTask(task);
    }

    @Override
    public void setUpView() {
        starterImageView = findViewById(R.id.starterImageView);
        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                presenter.searchInTasks(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.showAddDialog();
            }
        });

        View menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.showMenu(view);
            }
        });

    }

    @Override
    public void setTasksInRecyclerView(TaskAdapter taskAdapter, List<Task> tasks) {
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
        taskAdapter.addItemList(tasks);
    }

    @Override
    public void showMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.deleteAllTasks) {
                    presenter.deleteAllTasks();
                } else if (item.getItemId() == R.id.sortByPriority) {
                    presenter.sortByPriority();
                } else if (item.getItemId() == R.id.sortByDate) {
                    presenter.sortByDate();
                } else if (item.getItemId() == R.id.deleteCompletedTasks) {
                    presenter.deleteCompletedTasks();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void showAddTaskDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt("status", ADD_TASK_DIALOG_ID);
        AppDialog dialog = new AppDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void showEditTaskDialog(Task task) {
        Bundle bundle = new Bundle();
        bundle.putInt("status", EDIT_TASK_DIALOG_ID);
        bundle.putParcelable("task", task);
        AppDialog dialog = new AppDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void showBackgroundImage() {
        starterImageView.animate().alpha(1f);
    }

    @Override
    public void hideBackgroundImage() {
        starterImageView.animate().alpha(0f);
    }

    @Override
    public void startFabAnimation() {
        Animation pulseAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, .5f);
        pulseAnimation.setDuration(1000);
        pulseAnimation.setRepeatCount(Animation.INFINITE);
        pulseAnimation.setRepeatMode(Animation.REVERSE);
        addTaskButton.setAnimation(pulseAnimation);
    }

    @Override
    public void stopFabAnimation() {
        addTaskButton.setAnimation(null);
    }

    @Override
    public void raiseEmptyTaskError() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_tasks_error), Toast.LENGTH_SHORT).show();
    }
}