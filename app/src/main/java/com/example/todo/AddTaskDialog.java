package com.example.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddTaskDialog extends DialogFragment {

    private AddNewTaskCallback callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (AddNewTaskCallback) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_task_dialog, null, false);
        TextInputEditText titleEditText = view.findViewById(R.id.titleDialogEditText);
        TextInputLayout titleEditTextLayout = view.findViewById(R.id.titleDialogEditTextLayout);
        Button saveButton = view.findViewById(R.id.saveTaskButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleEditText.length() > 0) {
                    Task task = new Task();
                    task.setTitle(titleEditText.getText().toString());
                    task.setCompleted(false);
                    callback.OnAddNewTask(task);
                    dismiss();
                } else {
                    titleEditTextLayout.setError("The title should not be empty!");
                }
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public interface AddNewTaskCallback {
        void OnAddNewTask(Task task);
    }
}
