package com.elegion.android_school.sovan.lectionrecorder.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


import com.elegion.android_school.sovan.lectionrecorder.R;
import com.elegion.android_school.sovan.lectionrecorder.adapter.TaskListAdapter;
import com.elegion.android_school.sovan.lectionrecorder.loader.AsyncTaskLoader;
import com.elegion.android_school.sovan.lectionrecorder.receiver.TaskReceiver;
import com.elegion.android_school.sovan.lectionrecorder.tasks.RecorderTask;

//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
import java.util.List;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
   // private List<RecorderTask> mTaskList;

    // Shared Preferences file name
    public static final String APP_PREFERENCES = "mysettings";
    // Shared preferences parameter name
    public static final String APP_PREFERENCES_COUNTER = "counter";

    SharedPreferences mSettings;

    private int mCounter;

    ListView mListView;
    //  CursorAdapter mCursorAdapter;
    TaskListAdapter mTaskListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        this.mListView = (ListView) findViewById(R.id.listView);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        mTaskListAdapter = new TaskListAdapter(this, null);
        mListView.setAdapter(mTaskListAdapter);
        getLoaderManager().initLoader(R.id.recorder_manager, Bundle.EMPTY, this);

    }


    public void onCreateTaskClick(View view) {
        Intent intent = new Intent(this, TaskCreatorActivity.class);
        startActivityForResult(intent, R.id.DATE_TIME_SET);


    }

    public void onAlarmClick(View view) {

        Context context = getApplicationContext();
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TaskReceiver.class);
        intent.setAction(context.getString(R.string.scheldure_tasks_string));
        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, 1, alarmIntent);

    }

    public void onClearClick(View view) {
        ContentResolver db = getContentResolver();
        db.delete(RecorderTask.URI, null, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == R.id.recorder_manager) {
            return new AsyncTaskLoader(getApplicationContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == R.id.recorder_manager) {
            mTaskListAdapter.swapCursor(data);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == R.id.recorder_manager) {
            mTaskListAdapter.swapCursor(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == R.id.DATE_TIME_SET) {
            if (resultCode == RESULT_OK) {
                RecorderTask task = (RecorderTask) data.getSerializableExtra("task");
                task.setId(mCounter);
                mCounter++;
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(APP_PREFERENCES_COUNTER, mCounter);
                editor.apply();
                ContentResolver db = getContentResolver();
                db.insert(RecorderTask.URI, task.toValues());

                Context context = getApplicationContext();
                alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, TaskReceiver.class);
                intent.setAction(context.getString(R.string.scheldure_tasks_string));
                alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, 1, alarmIntent);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_COUNTER, mCounter);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCounter = mSettings.getInt(APP_PREFERENCES_COUNTER, 1);
    }

    public void onStopClick(View view) {
        Context context = getApplicationContext();
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TaskReceiver.class);
        intent.setAction(getString(R.string.stop_all_tasks));
        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, 1, alarmIntent);
        Toast.makeText(this, "All later tasks are now stopped", Toast.LENGTH_SHORT).show();
    }




}
