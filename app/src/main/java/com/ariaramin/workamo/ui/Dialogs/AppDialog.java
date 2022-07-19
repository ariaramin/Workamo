package com.ariaramin.workamo.ui.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import android.os.Build;
import android.os.Bundle;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ariaramin.workamo.Database.Task;
import com.ariaramin.workamo.Utils.Constants;
import com.ariaramin.workamo.R;
import com.ariaramin.workamo.databinding.AppDialogBinding;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;


public class AppDialog extends DialogFragment {

    AppDialogBinding binding;
    private TaskDialogListener callback;
    int STATUS_ID;
    Task task;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (TaskDialogListener) context;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            STATUS_ID = bundle.getInt(Constants.STATUS);
        }
        if (STATUS_ID == 2) {
            task = getArguments().getParcelable(Constants.TASK);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        binding = AppDialogBinding.inflate(getLayoutInflater());
        binding.dateChip.setOnClickListener(v -> showDatePicker());
        binding.priorityChip.setOnClickListener(v -> showPopupMenu());
        if (STATUS_ID == 2 && task != null) {
            setTaskInfo();
        }
        setCurrentDate();
        binding.saveTaskButton.setOnClickListener(view -> {
            if (binding.titleDialogEditText.length() > 0) {
                if (STATUS_ID == 2) {
                    task.setTitle(binding.titleDialogEditText.getText().toString());
                    task.setCompleted(false);
                    task.setDate(binding.dateChip.getTag().toString());
                    task.setPriority(getPriority());
                    callback.OnEditText(task);
                } else {
                    Task newTask = new Task();
                    newTask.setTitle(binding.titleDialogEditText.getText().toString());
                    newTask.setCompleted(false);
                    newTask.setDate(binding.dateChip.getTag().toString());
                    newTask.setPriority(getPriority());
                    callback.OnAddNewTask(newTask);
                }
                dismiss();
            } else {
                binding.titleDialogEditTextLayout.setError(getResources().getString(R.string.field_error_text));
            }
        });
        builder.setView(binding.getRoot());
        return builder.create();
    }

    private void setTaskInfo() {
        if (task != null) {
            binding.titleDialogEditText.setText(task.getTitle());
            setPriority(task.getPriority());
            binding.dateChip.setTag(task.getDate());
            binding.dateChip.setText(Constants.convertLongDate(task.getDate()));
        }
    }

    private int getPriority() {
        String priority = binding.priorityChip.getText().toString();
        if (priority.equals(getResources().getString(R.string.intermediate))) {
            return 2;
        } else if (priority.equals(getResources().getString(R.string.important))) {
            return 3;
        }
        return 1;
    }

    private void setPriority(int id) {
        if (id == 3) {
            binding.priorityChip.setText(getResources().getString(R.string.important));
            binding.priorityChip.setChipIcon(getResources().getDrawable(R.drawable.ic_important));
        } else if (id == 2) {
            binding.priorityChip.setText(getResources().getString(R.string.intermediate));
            binding.priorityChip.setChipIcon(getResources().getDrawable(R.drawable.ic_intermediate));
        } else {
            binding.priorityChip.setText(getResources().getString(R.string.normal));
            binding.priorityChip.setChipIcon(getResources().getDrawable(R.drawable.ic_normal));
        }
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), binding.priorityChip);
        popupMenu.getMenuInflater().inflate(R.menu.priority_menu, popupMenu.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            binding.priorityChip.setText(item.getTitle());
            binding.priorityChip.setChipIcon(item.getIcon());
            return false;
        });
        popupMenu.show();
    }

    private void setCurrentDate() {
        if (STATUS_ID != 2) {
            PersianDate persianDate = new PersianDate();
            PersianDateFormat dateFormat = new PersianDateFormat("Y-m-j");
            PersianDateFormat longDateFormat = new PersianDateFormat("j F Y", PersianDateFormat.PersianDateNumberCharacter.FARSI);
            binding.dateChip.setTag(dateFormat.format(persianDate));
            binding.dateChip.setText(longDateFormat.format(persianDate));
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
                        String date = String.format("%s-%s-%s",
                                persianPickerDate.getPersianYear(),
                                persianPickerDate.getPersianMonth() < 10 ? "0" + persianPickerDate.getPersianMonth() : persianPickerDate.getPersianMonth(),
                                persianPickerDate.getPersianDay());
                        binding.dateChip.setTag(date);
                        String longDate = String.format("%s %s %s",
                                Constants.convertPersianNumber(String.valueOf(persianPickerDate.getPersianDay())),
                                persianPickerDate.getPersianMonthName(),
                                Constants.convertPersianNumber(String.valueOf(persianPickerDate.getPersianYear())));
                        binding.dateChip.setText(longDate);
                    }

                    @Override
                    public void onDismissed() {

                    }
                });
        picker.show();
    }
}
