package com.elegion.android_school.sovan.lectionrecorder.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import com.elegion.android_school.sovan.lectionrecorder.R;
import com.elegion.android_school.sovan.lectionrecorder.receiver.TaskReceiver;
import com.elegion.android_school.sovan.lectionrecorder.tasks.RecorderTask;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RecorderIntentService extends IntentService {

    RecorderTask mTask;
    static MediaRecorder mRecorder;
    Timer mTimerStop = new Timer();
    NotificationManager nm;
    static ArrayList<RecorderTask> mTaskArrayList;

    @Override
    public void onCreate() {
        super.onCreate();
        mTaskArrayList = new ArrayList<RecorderTask>();
    }

    public RecorderIntentService() {
        super("RecorderIntentService");
    }


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RecorderIntentService(String name) {
        super(name);
    }


    private String getMediaExtension(int outputFormat) {
        switch (outputFormat) {
            case MediaRecorder.OutputFormat.THREE_GPP:
                return "3gp";
            case MediaRecorder.OutputFormat.MPEG_4:
                return "mp4";
            default: {
                Log.e("Media encoder", "Unknown output format.");
                return getString(R.string.error_string);
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mTask = (RecorderTask) intent.getSerializableExtra("task");
        if (mTask != null) {

            File folder = new File(mTask.getPath());

            // Checking, whether time set in the task is correct
            boolean success = (mTask.getStartTime() <= System.currentTimeMillis()) &&
                    (mTask.getStartTime() + mTask.getRunningTime() >= System.currentTimeMillis());
            if (!folder.exists()) {
                success = success && folder.mkdir();
            }
            if (success) {
                String mFileName;
                String ext = getMediaExtension(mTask.getOutputFormat());

                if (!ext.equals(getString(R.string.error_string))) {

                    mFileName = mTask.getPath() + mTask.getFileName();// + "." + ext;
                    if (!mTask.isNumeric())
                        mFileName = mFileName + calendarToString() + "." + ext;



                    if (mRecorder == null) {

                        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        Notification notif = new Notification.Builder(this)
                                .setContentTitle(mTask.getTitle())
                                .setContentText("Recorder Started: " + mTask.getFileName())
                                .setSmallIcon(R.drawable.ic_launcher)

                                .build();
//

                        mRecorder = new MediaRecorder();
                        mRecorder.setAudioSource(mTask.getAudioSource());
                        mRecorder.setOutputFormat(mTask.getOutputFormat());
                        mRecorder.setOutputFile(mFileName);
                        mRecorder.setAudioEncoder(mTask.getAudioEncoder());

                        try {
                            mRecorder.prepare();
                        } catch (IOException e) {
                            Log.e("RECORDING ", "prepare() failed");
                        }

                        // TODO: no check after prepare(), should be fixed
                        mRecorder.start();
                        nm.notify(mTask.getId(), notif);
                        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_SHORT).show();

                        Log.d("TIMER_START ", "Recording started.");

                        // We correct the time in case if task started not in time
                        long runningRealTime = mTask.getStartTime() + mTask.getRunningTime()
                                - System.currentTimeMillis();

                        // TODO: Add handler here
                        mTimerStop.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (mRecorder != null) {
                                    nm.cancel(mTask.getId());
                                    mRecorder.stop();
                                    mRecorder.release();
                                    mRecorder = null;



                                    Log.d("TIMER_STOP ", "Recording stopped.");

                                    if (!mTaskArrayList.isEmpty()) {
                                        Collections.sort(mTaskArrayList);
                                        mTask = mTaskArrayList.get(0);
                                        mTaskArrayList.remove(0);
                                        Intent serviceIntent = new Intent(getApplicationContext(),
                                                RecorderIntentService.class);
                                        serviceIntent.setAction("start_service");
                                        serviceIntent.putExtra("task", mTask);
                                        getApplicationContext().startService(serviceIntent);
                                    }
                                    else {
                                        Context context = getApplicationContext();
                                        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(
                                                Context.ALARM_SERVICE);
                                        Intent taskIntent = new Intent(context, TaskReceiver.class);
                                        taskIntent.setAction(context.getString(R.string.scheldure_tasks_string));
                                        PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
                                                1, taskIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                        alarmMgr.set(AlarmManager.RTC_WAKEUP, 1, alarmIntent);
                                    }
                                }

                            }
                        }, runningRealTime);
                    }
                    else{
                        mTaskArrayList.add(mTask);
                    }
                }
            }
            // In case if task from queue became invalid due to time or path, try to take another
            else if (mRecorder == null) {
                if (!mTaskArrayList.isEmpty()) {
                    Collections.sort(mTaskArrayList);
                    mTask = mTaskArrayList.get(0);
                    mTaskArrayList.remove(0);
                    Intent serviceIntent = new Intent(getApplicationContext(),
                            RecorderIntentService.class);
                    serviceIntent.setAction("start_service");
                    serviceIntent.putExtra("task", mTask);
                    getApplicationContext().startService(serviceIntent);
                }
                else {
                    Context context = getApplicationContext();
                    AlarmManager alarmMgr = (AlarmManager) context.getSystemService(
                            Context.ALARM_SERVICE);
                    Intent taskIntent = new Intent(context, TaskReceiver.class);
                    taskIntent.setAction(context.getString(R.string.scheldure_tasks_string));
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
                            1, taskIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, 1, alarmIntent);
                }

            }

        }
        //  stopSelf();


    }

    private String calendarToString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd_HH.mm.ss");
        return dateFormat.format(new Date());
//        Calendar cal = Calendar.getInstance();
//
//
//
//        String result = "-" + cal.get(Calendar.HOUR_OF_DAY) + "." + cal.get(Calendar.MINUTE) + "_" +
//                cal.get(Calendar.DAY_OF_MONTH)+ "-" + (cal.get(Calendar.MONTH) + 1) + "-" +
//                cal.get(Calendar.YEAR);
//        return result;
    }

}

