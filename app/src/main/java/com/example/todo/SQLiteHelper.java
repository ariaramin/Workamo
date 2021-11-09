package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TASK_TABLE = "task_tbl";

    public SQLiteHelper(@Nullable Context context) {
        super(context, "db_todo", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create Table
        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY AUToINCREMENT, title TEXT, isCompleted BOOLEAN);", TASK_TABLE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long addTask(Task task) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", task.getTitle());
        contentValues.put("isCompleted", task.isCompleted());
        long result = sqLiteDatabase.insert(TASK_TABLE, null, contentValues);
        sqLiteDatabase.close();
        return result;
    }

    public List<Task> getTasks() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM %s", TASK_TABLE), null);
        List<Task> tasks = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getLong(0));
                task.setTitle(cursor.getString(1));
                task.setCompleted(cursor.getInt(2) == 1);
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return tasks;
    }

    public int updateTask(Task task) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", task.getTitle());
        contentValues.put("isCompleted", task.isCompleted());
        int result = sqLiteDatabase.update(TASK_TABLE, contentValues, "id = ?", new String[]{
                String.valueOf(task.getId())
        });
        sqLiteDatabase.close();
        return result;
    }

    public int deleteTask(Task task) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int result = sqLiteDatabase.delete(TASK_TABLE, "id = ?", new String[]{
                String.valueOf(task.getId())
        });
        sqLiteDatabase.close();
        return result;
    }

    public void clearAllTasks() {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.execSQL(String.format("DELETE FROM %s", TASK_TABLE));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> searchInTasks(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+TASK_TABLE+" WHERE title LIKE '%"+query+"%'", null);
        List<Task> tasks = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getLong(0));
                task.setTitle(cursor.getString(1));
                task.setCompleted(cursor.getInt(2) == 1);
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return tasks;
    }
}
