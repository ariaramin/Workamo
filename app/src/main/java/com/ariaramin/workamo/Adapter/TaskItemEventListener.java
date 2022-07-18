package com.ariaramin.workamo.Adapter;

import com.ariaramin.workamo.Database.Task;

public interface TaskItemEventListener {
    void OnItemDeleteClick(Task task);

    void OnItemLongClick(Task task);

    void OnItemCheckedChange(Task task);
}