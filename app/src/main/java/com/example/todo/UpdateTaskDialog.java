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

public class UpdateTaskDialog extends DialogFragment {

    private EditTextCallback callback;
    private Task task;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (EditTextCallback) context;
        task = getArguments().getParcelable("task");
        if (task == null) {
            dismiss();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.update_task_dialog, null, false);
        TextInputEditText titleEditText = view.findViewById(R.id.titleUpdateDialogEditText);
        titleEditText.setText(task.getTitle());
        TextInputLayout titleEditTextLayout = view.findViewById(R.id.titleUpdateDialogEditTextLayout);
        Button saveButton = view.findViewById(R.id.editTaskButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleEditText.length() > 0) {
                    task.setTitle(titleEditText.getText().toString());
                    callback.OnEditText(task);
                    dismiss();
                } else {
                    titleEditTextLayout.setError("The title should not be empty!");
                }
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public interface EditTextCallback{
        void OnEditText(Task task);
    }
}
