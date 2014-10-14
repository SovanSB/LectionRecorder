package com.elegion.android_school.sovan.lectionrecorder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


import com.elegion.android_school.sovan.lectionrecorder.R;
import com.elegion.android_school.sovan.lectionrecorder.tasks.RecorderTask;

import java.io.File;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TaskCreatorActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private String array_spinner[];
    private Spinner mSpinnerPeriod;
    private Spinner mSpinnerDuration;

    private EditText mEditPeriod;
    private EditText mEditTitle;
    private EditText mEditDuration;
    private RadioButton mRadioButton;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;


    private long mPeriodInterval = DateUtils.SECOND_IN_MILLIS;
    private long mDurationInterval = DateUtils.SECOND_IN_MILLIS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        array_spinner = new String[6];
        array_spinner[0] = "Seconds";
        array_spinner[1] = "Minutes";
        array_spinner[2] = "Hours";
        array_spinner[3] = "Days";
        array_spinner[4] = "Weeks";
        array_spinner[5] = "Years";
        setContentView(R.layout.test_task_picker);
        mSpinnerPeriod = (Spinner) findViewById(R.id.spinnerPeriod);
        mEditPeriod = (EditText) findViewById(R.id.editTextPeriod);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, array_spinner);
        mSpinnerPeriod.setAdapter(adapter);
        mSpinnerPeriod.setEnabled(false);

        mSpinnerDuration = (Spinner) findViewById(R.id.spinnerDuration);
        mEditDuration = (EditText) findViewById(R.id.editTextDuration);

//        ArrayAdapter adapter2 = new ArrayAdapter(this,
//                android.R.layout.simple_spinner_item, array_spinner);
        mSpinnerDuration.setAdapter(adapter);
        mSpinnerDuration.setEnabled(true);


        mEditTitle = (EditText) findViewById(R.id.editTitle);

        mRadioButton = (RadioButton) findViewById(R.id.radioPeriodical);
        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) findViewById(R.id.timePicker);

//        mSpinnerPeriod.setOnItemSelectedListener(this);
//        mSpinnerDuration.setOnItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_creator_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId()==R.id.spinnerPeriod) {
            switch (position) {
                case 0:
                    mPeriodInterval = DateUtils.SECOND_IN_MILLIS;
                    break;
                case 1:
                    mPeriodInterval = DateUtils.MINUTE_IN_MILLIS;
                    break;
                case 2:
                    mPeriodInterval = DateUtils.HOUR_IN_MILLIS;
                    break;
                case 3:
                    mPeriodInterval = DateUtils.DAY_IN_MILLIS;
                    break;
                case 4:
                    mPeriodInterval = DateUtils.WEEK_IN_MILLIS;
                    break;
                case 5:
                    mPeriodInterval = DateUtils.YEAR_IN_MILLIS;
                    break;
            }
        }
        if (parent.getId()==R.id.spinnerDuration) {
            switch (position) {
                case 0:
                    mDurationInterval = DateUtils.SECOND_IN_MILLIS;
                    break;
                case 1:
                    mDurationInterval = DateUtils.MINUTE_IN_MILLIS;
                    break;
                case 2:
                    mDurationInterval = DateUtils.HOUR_IN_MILLIS;
                    break;
                case 3:
                    mDurationInterval = DateUtils.DAY_IN_MILLIS;
                    break;
                case 4:
                    mDurationInterval = DateUtils.WEEK_IN_MILLIS;
                    break;
                case 5:
                    mDurationInterval = DateUtils.YEAR_IN_MILLIS;
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onRadioButtonClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Checks which radio button was clicked
        switch(view.getId()) {
            case R.id.radioOneShot:
                if (checked) {
                    mSpinnerPeriod.setEnabled(false);
                    mEditPeriod.setEnabled(false);
                }
                break;
            case R.id.radioPeriodical:
                if (checked) {
                    mSpinnerPeriod.setEnabled(true);
                    mEditPeriod.setEnabled(true);
                }
                break;
        }

    }

    public void onOkButtonClick(View view) {
        String title = mEditTitle.getText().toString();
        long currentMillis = Calendar.getInstance().getTimeInMillis();
        long currentSystem = System.currentTimeMillis();
        long diff = currentMillis - currentSystem;
        Calendar calendar = new GregorianCalendar(mDatePicker.getYear(), mDatePicker.getMonth(),
                mDatePicker.getDayOfMonth(), mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
        long startTime = calendar.getTimeInMillis();
        long period = 0;
        boolean isPeriodical = mRadioButton.isChecked();
        long runningTime = 1000;
        try {
            runningTime = Long.parseLong(mEditDuration.getText().toString()) * mDurationInterval;
            if (isPeriodical)
                period = Long.parseLong(mEditPeriod.getText().toString()) * mPeriodInterval;
        }
        catch (NumberFormatException e){
            Toast.makeText(this, "Check durations, please", Toast.LENGTH_SHORT).show();
        }
        if (!isPeriodical || runningTime < period) {
            RecorderTask task;
            // "/storage/sdcard0/Recording2/"
            String root = Environment.getExternalStorageDirectory().toString();
            File folder = new File(root + "/Recordings2/");



            // TODO: REPLACE THIS BICYCLE WITH NORMAL CODE!!!

            if (!folder.exists()) {
                folder.mkdir();
            }
            task = new RecorderTask(title, 1, root + "/Recordings2/" + title + "/", title,
                    startTime, runningTime, period, isPeriodical);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("task", task);
            this.setResult(RESULT_OK, intent);
            finish();
        }
        else {
            Toast.makeText(this, "Duration can't be greater than period", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSpinnerPeriod.setOnItemSelectedListener(this);
        mSpinnerDuration.setOnItemSelectedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSpinnerPeriod.setOnItemSelectedListener(null);
        mSpinnerDuration.setOnItemSelectedListener(null);
    }

    public void onCancelButtonClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        this.setResult(RESULT_CANCELED, intent);
        finish();
    }
}

