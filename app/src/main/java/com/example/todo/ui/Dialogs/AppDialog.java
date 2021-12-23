package com.example.todo.ui.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todo.Database.Task;
import com.example.todo.R;
import com.example.todo.Utils.Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;


public class AppDialog extends DialogFragment {

    private TaskDialogListener callback;
    TextInputEditText titleEditText;
    Chip dateChip;
    Chip priorityChip;
    int STATUS_ID;
    Task task;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (TaskDialogListener) context;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            STATUS_ID = bundle.getInt("status");
        }
        if (STATUS_ID == 2) {
            task = getArguments().getParcelable("task");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.app_dialog, null, false);
        titleEditText = view.findViewById(R.id.titleDialogEditText);
        TextInputLayout titleEditTextLayout = view.findViewById(R.id.titleDialogEditTextLayout);
        dateChip = view.findViewById(R.id.dateChip);
        dateChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        priorityChip = view.findViewById(R.id.priorityChip);
        priorityChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });
        if (STATUS_ID == 2 && task != null) {
            setTaskInfo();
        }
        setCurrentDate();

        Button saveButton = view.findViewById(R.id.saveTaskButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleEditText.length() > 0) {
                    if (STATUS_ID == 2) {
                        task.setTitle(titleEditText.getText().toString());
                        task.setCompleted(false);
                        task.setDate(dateChip.getTag().toString());
                        task.setPriority(getPriority());
                        callback.OnEditText(task);
                    } else {
                        Task newTask = new Task();
                        newTask.setTitle(titleEditText.getText().toString());
                        newTask.setCompleted(false);
                        newTask.setDate(dateChip.getTag().toString());
                        newTask.setPriority(getPriority());
                        callback.OnAddNewTask(newTask);
                    }
                    dismiss();
                } else {
                    titleEditTextLayout.setError(getResources().getString(R.string.field_error_text));
                }
            }
        });
        builder.setView(view);
        return builder.create();
    }

    private void setTaskInfo() {
        if (task != null) {
            Utils util = new Utils();
            titleEditText.setText(task.getTitle());
            setPriority(task.getPriority());
            dateChip.setText(util.convertLongDate(task.getDate()));
        }
    }

    private int getPriority() {
        String priority = priorityChip.getText().toString();
        if (priority.equals(getResources().getString(R.string.intermediate))) {
            return 2;
        } else if (priority.equals(getResources().getString(R.string.important))) {
            return 3;
        }
        return 1;
    }

    private void setPriority(int id) {
        if (id == 3) {
            priorityChip.setText(getResources().getString(R.string.important));
            priorityChip.setChipIcon(getResources().getDrawable(R.drawable.ic_important));
        } else if (id == 2) {
            priorityChip.setText(getResources().getString(R.string.intermediate));
            priorityChip.setChipIcon(getResources().getDrawable(R.drawable.ic_intermediate));
        } else {
            priorityChip.setText(getResources().getString(R.string.normal));
            priorityChip.setChipIcon(getResources().getDrawable(R.drawable.ic_normal));
        }
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), priorityChip);
        popupMenu.getMenuInflater().inflate(R.menu.priority_menu, popupMenu.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                priorityChip.setText(item.getTitle());
                priorityChip.setChipIcon(item.getIcon());
                return false;
            }
        });
        popupMenu.show();
    }

    private void setCurrentDate() {
        if (STATUS_ID != 2) {
            PersianDate persianDate = new PersianDate();
            PersianDateFormat dateFormat = new PersianDateFormat("Y-m-j");
            PersianDateFormat longDateFormat = new PersianDateFormat("j F Y", PersianDateFormat.PersianDateNumberCharacter.FARSI);
            dateChip.setTag(dateFormat.format(persianDate));
            dateChip.setText(longDateFormat.format(persianDate));
        }
    }

    private void showDatePicker() {
        PersianDatePickerDialog picker = new PersianDatePickerDialog(getActivity())
                .setPositiveButtonString(getResources().getString(R.string.ok))
                .setNegativeButton(getResources().getString(R.string.cancel))
                .setTodayButton(getResources().getString(R.string.today))
                .setActionTextColorResource(R.color.yellow)
                .setAllButtonsTextSize(16)
                .setTodayButtonVisible(true)
                .setMinYear(1300)
                .setMaxYear(1500)
                .setMaxMonth(12)
                .setMaxDay(31)
                .setInitDate(PersianDatePickerDialog.THIS_YEAR,
                        PersianDatePickerDialog.THIS_MONTH,
                        PersianDatePickerDialog.THIS_DAY)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(new PersianPickerListener() {
                    @Override
                    public void onDateSelected(PersianPickerDate persianPickerDate) {
                        Utils util = new Utils();
                        String date = String.format("%s-%s-%s",
                                persianPickerDate.getPersianYear(),
                                persianPickerDate.getPersianMonth() < 10 ? "0" + persianPickerDate.getPersianMonth() : persianPickerDate.getPersianMonth(),
                                persianPickerDate.getPersianDay());
                        dateChip.setTag(date);
                        String longDate = String.format("%s %s %s",
                                util.convertPersianNumber(String.valueOf(persianPickerDate.getPersianDay())),
                                persianPickerDate.getPersianMonthName(),
                                util.convertPersianNumber(String.valueOf(persianPickerDate.getPersianYear())));
                        dateChip.setText(longDate);
                    }

                    @Override
                    public void onDismissed() {

                    }
                });
        picker.show();
    }

    public interface TaskDialogListener {
        void OnAddNewTask(Task task);

        void OnEditText(Task task);
    }
}
