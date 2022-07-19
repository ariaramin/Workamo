package com.ariaramin.workamo.ui.Activities.main;

import android.view.View;

import com.ariaramin.workamo.Adapter.TaskAdapter;
import com.ariaramin.workamo.Database.Task;
import com.ariaramin.workamo.Database.TaskDao;

import java.util.List;

public class MainActivityPresenter implements MainActivityContract.Presenter {

    TaskDao taskDao;
    MainActivityContract.View view;
    TaskAdapter taskAdapter;

    public MainActivityPresenter(MainActivityContract.View view, TaskDao taskDao, TaskAdapter taskAdapter) {
        this.view = view;
        this.taskDao = taskDao;
        this.taskAdapter = taskAdapter;
        view.setUpView();
        view.setTasksInRecyclerView(taskAdapter, readTasks());
    }

    @Override
    public List<Task> readTasks() {
        return taskDao.getTasks();
    }

    @Override
    public void addNewTask(Task task) {
        long taskId = taskDao.addTask(task);
        if (taskId != -1) {
            task.setId(taskId);
            taskAdapter.addItem(task);
            view.scheduleTaskNotification(task);
            view.hideBackgroundImage();
            view.stopFabAnimation();
        }
    }

    @Override
    public void updateTask(Task task) {
        int result = taskDao.updateTask(task);
        if (result > 0) {
            taskAdapter.updateItem(task);
        }
    }

    @Override
    public void deleteTask(Task task) {
        int result = taskDao.deleteTask(task);
        if (result > 0) {
            taskAdapter.deleteItem(task);
        }
        if (taskAdapter.getItemCount() <= 0) {
            view.showBackgroundImage();
            view.startFabAnimation();
        }
    }

    @Override
    public void deleteAllTasks() {
        if (taskDao.getTasks().size() > 0) {
            taskDao.clearAllTasks();
            taskAdapter.clearItems();
            view.showBackgroundImage();
            view.startFabAnimation();
        } else {
            view.raiseEmptyTaskError();
        }
    }

    @Override
    public void deleteCompletedTasks() {
        if (taskDao.getTasks().size() > 0) {
            for (Task task :
                    taskDao.getTasks()) {
                if (task.isCompleted()) {
                    taskDao.deleteTask(task);
                    taskAdapter.deleteItem(task);
                }
            }
        } else {
            view.raiseEmptyTaskError();
        }
        if (taskAdapter.getItemCount() <= 0) {
            view.showBackgroundImage();
            view.startFabAnimation();
        }
    }

    @Override
    public void sortByPriority() {
        if (taskDao.getTasks().size() > 0)
            taskAdapter.sortByPriority();
        else
            view.raiseEmptyTaskError();
    }

    @Override
    public void sortByDate() {
        if (taskDao.getTasks().size() > 0)
            taskAdapter.sortByDate();
        else
            view.raiseEmptyTaskError();
    }

    @Override
    public void showAddDialog() {
        view.showAddTaskDialog();
    }

    @Override
    public void showEditDialog(Task task) {
        view.showEditTaskDialog(task);
    }

    @Override
    public void showMenu(View view) {
        this.view.showMenu(view);
    }

    @Override
    public void searchInTasks(String query) {
        if (query.length() > 0) {
            List<Task> tasks = taskDao.searchInTasks(query);
            taskAdapter.setNewItems(tasks);
        } else {
            List<Task> tasks = taskDao.getTasks();
            taskAdapter.setNewItems(tasks);
        }
    }
}
