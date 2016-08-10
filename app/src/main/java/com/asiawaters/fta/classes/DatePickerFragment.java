package com.asiawaters.fta.classes;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.asiawaters.fta.LoginActivity;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private Date ResultDate;
    private Date InitialDate;

    public void setInitialDate(Date initialDate) {
        InitialDate = initialDate;
    }

    public Date getResultDate() {
        return ResultDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        if (InitialDate!=null) c.setTime(InitialDate);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        ResultDate = new Date(c.getTimeInMillis());

        LoginActivity LA = (LoginActivity) getActivity();
        LA.runDatePickerCompliting();
    }
}