package com.elegion.android_school.sovan.lectionrecorder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.elegion.android_school.sovan.lectionrecorder.R;
import com.elegion.android_school.sovan.lectionrecorder.adapter.TaskListAdapter;
import com.elegion.android_school.sovan.lectionrecorder.pickers.DatePickerFragment;
import com.elegion.android_school.sovan.lectionrecorder.pickers.TimePickerFragment;
import com.elegion.android_school.sovan.lectionrecorder.tasks.RecorderTask;

public class TaskCreatorActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener,
        TimePickerFragment.TimePickerDialogListener, DatePickerFragment.DatePickerDialogListener {

    private String array_spinner[];
    private Spinner mSpinnerPeriod;
    private Spinner mSpinnerDuration;

    private EditText mEditPeriod;
    private EditText mEditTitle;
    private EditText mEditDuration;
    private RadioButton mRadioButton;


    private long mPeriodInterval = DateUtils.SECOND_IN_MILLIS;
    private long mDurationInterval = DateUtils.SECOND_IN_MILLIS;

    TextView mTvTime;
    TextView mTvDate;
    Button mButtonDelete;

    long mTimeInMillis = 0;
    long mDateInMillis = 0;

    int mId = 1;
    boolean isEdited = false;
    RecorderTask mTask;


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

        mSpinnerDuration.setAdapter(adapter);
        mSpinnerDuration.setEnabled(true);


        mEditTitle = (EditText) findViewById(R.id.editTitle);

        mRadioButton = (RadioButton) findViewById(R.id.radioPeriodical);

        mTvTime = (TextView) findViewById(R.id.tvTime);
        mTvDate = (TextView) findViewById(R.id.tvDate);

        mButtonDelete = (Button) findViewById(R.id.buttonDelete);

        Intent intent = getIntent();
        if (intent.getAction().equals(getString(R.string.edit_task_string))) {
            mTask = (RecorderTask) intent.getSerializableExtra("task");
            if (mTask != null) {
                setTask(mTask);
            }
        }
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
        return id == R.id.action_settings;
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
        long startTime = mTimeInMillis + mDateInMillis;
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
            String root = Environment.getExternalStorageDirectory().toString();
           // File folder = new File(root + "/Recordings2/");

            task = new RecorderTask(title, mId, root + "/Recordings2/" + title + "/", title,
                    startTime, runningTime, period, isPeriodical);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("task", task);
            intent.setAction(getString(R.string.edit_task_string));
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

    public void onDeleteButtonClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("task", mTask);
        intent.setAction(getString(R.string.delete_task_string));
        this.setResult(RESULT_OK, intent);
        finish();
    }

    public void inputStartTime(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    @Override
    public void onTimeSet(long time) {
        // TODO Auto-generated method stub
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(GregorianCalendar.HOUR_OF_DAY, (int)(time / DateUtils.HOUR_IN_MILLIS));
        calendar.set(GregorianCalendar.MINUTE,
                (int)((time % DateUtils.HOUR_IN_MILLIS) / DateUtils.MINUTE_IN_MILLIS));
        mTvTime.setText("Starting time: " + timeFormat.format(calendar.getTime()));
        mTimeInMillis = time;
        Log.i("TimePicker", "Time picker set!");
    }

    @Override
    public void onDateSet(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        mTvDate.setText("Starting date: " + dateFormat.format(new Date(date)));
        mDateInMillis = date;
    }

    public void inputStartDate(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    private void setTask(RecorderTask task) {
        isEdited = true;
        mId = task.getId();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(task.getStartTime());


        mButtonDelete.setEnabled(true);
        mTimeInMillis = calendar.get(GregorianCalendar.HOUR_OF_DAY) * DateUtils.HOUR_IN_MILLIS +
                calendar.get(GregorianCalendar.MINUTE) * DateUtils.MINUTE_IN_MILLIS;
        mTvTime.setText("Starting time: " + timeFormat.format(calendar.getTime()));
        mTvDate.setText("Starting date: " + dateFormat.format(new Date(task.getStartTime())));
        mDateInMillis = task.getStartTime() - mTimeInMillis;
        mEditTitle.setText(task.getTitle());
        //mEditDuration.setText(Long.toString(task.getRunningTime()));
        if (task.isPeriodical()) {
            mRadioButton.setChecked(true);
            mSpinnerPeriod.setEnabled(true);
            onItemSelected(mSpinnerPeriod, null, returnSpinnerIndex(task.getPeriod()),
                    returnSpinnerIndex(task.getPeriod()));
            mSpinnerPeriod.setSelection(returnSpinnerIndex(task.getPeriod()));
            mEditPeriod.setEnabled(true);
            mEditPeriod.setText(Long.toString(task.getPeriod() / mPeriodInterval));
        }
        mSpinnerDuration.setSelection(returnSpinnerIndex(task.getRunningTime()));
        onItemSelected(mSpinnerDuration, null, returnSpinnerIndex(task.getRunningTime()),
                returnSpinnerIndex(task.getRunningTime()));
        mEditDuration.setText(Long.toString(task.getRunningTime() / mDurationInterval));
    }

    private int returnSpinnerIndex(long time) {
        if (time % DateUtils.YEAR_IN_MILLIS == 0)
            return 5;
        if (time % DateUtils.WEEK_IN_MILLIS == 0)
            return 4;
        if (time % DateUtils.DAY_IN_MILLIS == 0)
            return 3;
        if (time % DateUtils.HOUR_IN_MILLIS == 0)
            return 2;
        if (time % DateUtils.MINUTE_IN_MILLIS == 0)
            return 1;
        return 0;
    }
}

