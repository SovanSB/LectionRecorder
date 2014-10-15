package com.elegion.android_school.sovan.lectionrecorder.pickers;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Sovan on 15.10.2014.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    private TimePickerDialogListener mListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        mListener = getActivity() instanceof TimePickerDialogListener ? (TimePickerDialogListener) getActivity() : null;

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        long time = DateUtils.HOUR_IN_MILLIS * hourOfDay + DateUtils.MINUTE_IN_MILLIS * minute;
        if (mListener != null) mListener.onTimeSet(time);
    }

    public static interface TimePickerDialogListener {
        public void onTimeSet(long time);
    }

}
