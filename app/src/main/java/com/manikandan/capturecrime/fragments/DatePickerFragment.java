package com.manikandan.capturecrime.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.manikandan.capturecrime.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment implements DialogInterface.OnClickListener, DatePicker.OnDateChangedListener {
    private static final String ARG_DATE = "DATE";
    private static final String ARG_REQUEST_CODE = "requestCode";
    public static final String RESULT_DATE_KEY = "resultDateKey";
    private DatePicker datePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View dateView = LayoutInflater.from(getActivity())
                .inflate(R.layout.date_picker, null);
        datePicker = dateView.findViewById(R.id.date_picker);

        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(year, month, day, this);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Date of Crime : ")
                .setPositiveButton("ok", this)
                .setView(dateView)
                .create();
    }

    public static DatePickerFragment newInstance(Date date, String requestCode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE, date);
        bundle.putString(ARG_REQUEST_CODE, requestCode);
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(bundle);
        return datePickerFragment;
    }

    private void sendResult(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT_DATE_KEY, date);
        String resultRequestCode = getArguments().getString(ARG_REQUEST_CODE, "");
        this.getParentFragmentManager().setFragmentResult(resultRequestCode, bundle);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        Date date = new GregorianCalendar(year, month, day).getTime();
        sendResult(date);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }
}
