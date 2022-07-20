package com.ariaramin.workamo.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert()
    long addTask(Task task);

    @Query("SELECT * FROM task_tbl ORDER BY date DESC")
    List<Task> getTasks();

    @Update
    int updateTask(Task task);

    @Delete
    int deleteTask(Task task);

    @Query("SELECT * FROM task_tbl WHERE title LIKE '%' || :query || '%'")
    List<Task> searchInTasks(String query);

    @Query("DELETE FROM task_tbl")
    void clearAllTasks();
}
