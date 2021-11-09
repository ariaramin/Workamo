package com.example.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private TaskItemEventListener eventListener;

    public TaskAdapter(TaskItemEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bindTask(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void addItem(Task task) {
        tasks.add(0, task);
        notifyItemInserted(0);
    }

    public void addItemList(List<Task> tasks) {
        this.tasks.addAll(tasks);
        notifyDataSetChanged();
    }

    public void deleteItem(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void updateItem(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void clearItems() {
        tasks.clear();
        notifyDataSetChanged();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private View deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.taskCheckBox);
            deleteButton = itemView.findViewById(R.id.deleteTaskButton);
        }

        public void bindTask(Task task) {
            checkBox.setChecked(task.isCompleted());
            checkBox.setText(task.getTitle());
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventListener.OnDeleteButtonClick(task);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    eventListener.OnItemLongClick(task);
                    return false;
                }
            });
        }
    }

    public interface TaskItemEventListener{
        void OnDeleteButtonClick(Task task);
        void OnItemLongClick(Task task);
    }
}
