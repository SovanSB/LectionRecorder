package com.elegion.android_school.sovan.lectionrecorder.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
//import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import com.elegion.android_school.sovan.lectionrecorder.R;
import com.elegion.android_school.sovan.lectionrecorder.services.RecorderIntentService;
import com.elegion.android_school.sovan.lectionrecorder.tasks.RecorderTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sovan on 04.10.2014.
 */
public class TaskReceiver extends BroadcastReceiver {
    //public final long DELAY_CONSTANT = -10000;
    static List<RecorderTask> mTaskList;
    private AlarmManager alarmMgr;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(context.getString(R.string.start_service_string))) {
            Intent serviceIntent = new Intent(context, RecorderIntentService.class);
            RecorderTask task = (RecorderTask) intent.getSerializableExtra("task");
            if (task != null) {
                serviceIntent.setAction(context.getString(R.string.start_service_string));
                serviceIntent.putExtra("task", task);
                context.startService(serviceIntent);
            }
        } else {
            if (intent.getAction().equals(context.getString(R.string.scheldure_tasks_string)))
            {
                alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manageTasks(context);
                for (RecorderTask task : mTaskList) {
                    // Scheduling tasks
                    Intent taskIntent = new Intent(context, TaskReceiver.class);
                    taskIntent.putExtra("task", task);
                    taskIntent.setAction(context.getString(R.string.start_service_string));
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, task.getId(),
                            taskIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, task.getStartTime(), alarmIntent);
                    // Changing startTime in database
                    if (task.isPeriodical()) {
                        ContentResolver db = context.getContentResolver();

                    // TODO: Ask, whether it is normal method to update or not:)
                        db.update(RecorderTask.URI, task.startTimeToValues(), RecorderTask.Columns.ID + "=?",
                                new String[]{Integer.toString(task.getId())});
                    }
                }

                  Toast.makeText(context, "Tasks scheduled", Toast.LENGTH_SHORT).show();
            }
            else
            if (intent.getAction().equals(context.getString(R.string.stop_all_tasks))) {
                alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manageTasks(context);
                for (RecorderTask task : mTaskList) {
                    Intent taskIntent = new Intent(context, TaskReceiver.class);
                    taskIntent.putExtra("task", task);
                    taskIntent.setAction(context.getString(R.string.start_service_string));
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, task.getId(),
                            taskIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmMgr.cancel(alarmIntent);
                }
            }
        }
    }

    private void manageTasks(Context context) {
        ContentResolver db = context.getContentResolver();
        Cursor cursor = db.query(RecorderTask.URI, null, null, null, null);
        cursor.moveToFirst();
        mTaskList = new ArrayList<RecorderTask>();
        while (!cursor.isAfterLast()) {
            RecorderTask task = getTask(cursor);
            if (!task.isPeriodical()) {
                // If One-shot activity should be executed in future, we add it
                if (task.getStartTime() + task.getRunningTime() > System.currentTimeMillis()) {
                    mTaskList.add(task);
                }
                // If don't - we ignore it
            } else {
                // If task is planned in future, we also add it
                if (task.getStartTime() + task.getRunningTime() > System.currentTimeMillis()) {
                    mTaskList.add(task);
                }
                // If don't, we roll up it's period and add it
                else {
                    long curTime = System.currentTimeMillis();
                    long round = (curTime - task.getRunningTime() - task.getStartTime()) / task.getPeriod();
                    task.setStartTime(task.getStartTime() + task.getPeriod() * (round + 1));
                    mTaskList.add(task);
                }
            }

            cursor.moveToNext();
        }
        Collections.sort(mTaskList);
        cursor.close();

    }

    private RecorderTask getTask(Cursor c) {
        String title = c.getString(c.getColumnIndex(RecorderTask.Columns.TITLE));
        String path = c.getString(c.getColumnIndex(RecorderTask.Columns.PATH));
        String fileName = c.getString(c.getColumnIndex(RecorderTask.Columns.FILE_NAME));
        long startTime = c.getLong(c.getColumnIndex(RecorderTask.Columns.START_TIME));
        long runningTime = c.getLong(c.getColumnIndex(RecorderTask.Columns.RUNNING_TIME));
        long period = c.getLong(c.getColumnIndex(RecorderTask.Columns.PERIOD));
        boolean isPeriodical = (c.getInt(c.getColumnIndex(RecorderTask.Columns.IS_PERIODICAL)) != 0);
        boolean isNumeric = (c.getInt(c.getColumnIndex(RecorderTask.Columns.IS_NUMERIC)) != 0);
        int id = c.getInt(c.getColumnIndex(RecorderTask.Columns.ID));
        int audioSource = c.getInt(c.getColumnIndex(RecorderTask.Columns.AUDIO_SOURCE));
        int audioEncoder = c.getInt(c.getColumnIndex(RecorderTask.Columns.AUDIO_ENCODER));
        int outputFormat = c.getInt(c.getColumnIndex(RecorderTask.Columns.OUTPUT_FORMAT));
        return new RecorderTask(title, id, path, fileName, startTime, runningTime, period,
                isPeriodical, isNumeric, audioSource, audioEncoder, outputFormat);
    }

}
