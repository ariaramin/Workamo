package com.ariaramin.workamo.ui.Activities.main;

import com.ariaramin.workamo.Adapter.TaskAdapter;
import com.ariaramin.workamo.Database.Task;

import java.util.List;

public interface MainActivityContract {

    interface View {
        void setUpView();

        void setTasksInRecyclerView(TaskAdapter taskAdapter, List<Task> tasks);

        void showMenu(android.view.View view);

        void showAddTaskDialog();

        void showEditTaskDialog(Task task);

        void scheduleTaskNotification(Task task);

        void showBackgroundImage();

        void hideBackgroundImage();

        void startFabAnimation();

        void stopFabAnimation();

        void raiseEmptyTaskError();
    }

    interface Presenter {
        List<Task> readTasks();

        void addNewTask(Task task);

        void updateTask(Task task);

        void deleteTask(Task task);

        void deleteAllTasks();

        void deleteCompletedTasks();

        void sortByPriority();

        void sortByDate();

        void showAddDialog();

        void showEditDialog(Task task);

        void showMenu(android.view.View view);

        void searchInTasks(String query);
    }
}
