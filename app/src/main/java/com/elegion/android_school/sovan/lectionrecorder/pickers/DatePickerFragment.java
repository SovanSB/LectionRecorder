package com.elegion.android_school.sovan.lectionrecorder.pickers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Sovan on 15.10.2014.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = GregorianCalendar.getInstance();
        return new DatePickerDialog(getActivity(), this, c.get(GregorianCalendar.YEAR),
                c.get(GregorianCalendar.MONTH), c.get(GregorianCalendar.DAY_OF_MONTH));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = activity instanceof DatePickerDialogListener ? (DatePickerDialogListener) activity : null;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        if (mListener != null) {
            mListener.onDateSet(calendar.getTimeInMillis());
        }

    }

    public static interface DatePickerDialogListener {
        public void onDateSet(long date);
    }
}
