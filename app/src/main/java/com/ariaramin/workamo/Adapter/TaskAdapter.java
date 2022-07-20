package com.ariaramin.workamo.Adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ariaramin.workamo.Database.Task;
import com.ariaramin.workamo.R;
import com.ariaramin.workamo.Utils.Constants;
import com.ariaramin.workamo.databinding.TaskItemBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private final TaskItemEventListener eventListener;

    public TaskAdapter(TaskItemEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TaskItemBinding binding = TaskItemBinding.inflate(layoutInflater, parent, false);
        return new TaskViewHolder(binding);
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

    public void sortByPriority() {
        Collections.sort(tasks, (o1, o2) -> o1.getPriority() > o2.getPriority() ? -1 : 0);
        notifyDataSetChanged();
    }

    public void sortByDate() {
        Collections.sort(tasks, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        notifyDataSetChanged();
    }

    public void setNewItems(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        TaskItemBinding binding;

        public TaskViewHolder(@NonNull TaskItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void bindTask(Task task) {
            binding.dateTextView.setText(Constants.convertLongDate(task.getDate()));
            binding.priority.setBackgroundColor(getPriorityColor(task));
            binding.taskCheckBox.setOnCheckedChangeListener(null);
            binding.taskCheckBox.setChecked(task.isCompleted());
            setTextLineThrough(binding.taskCheckBox);
            binding.taskCheckBox.setText(task.getTitle());
            binding.taskCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
                task.setCompleted(b);
                setTextLineThrough(binding.taskCheckBox);
                eventListener.OnItemCheckedChange(task);
            });
            binding.deleteTaskButton.setOnClickListener(view -> eventListener.OnItemDeleteClick(task));
            itemView.setOnLongClickListener(view -> {
                eventListener.OnItemLongClick(task);
                return false;
            });
        }

        private int getPriorityColor(Task task) {
            int taskPriority = task.getPriority();
            if (taskPriority == 3) {
                return itemView.getResources().getColor(R.color.colorPrimaryDark);
            } else if (taskPriority == 2) {
                return itemView.getResources().getColor(R.color.colorSecondary);
            }
            return itemView.getResources().getColor(R.color.yellow);
        }

        private void setTextLineThrough(CheckBox checkBox) {
            if (checkBox.isChecked()) {
                checkBox.setPaintFlags(checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                checkBox.setPaintFlags(checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }
}
