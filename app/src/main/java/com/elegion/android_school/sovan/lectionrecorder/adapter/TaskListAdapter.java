package com.elegion.android_school.sovan.lectionrecorder.adapter;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.elegion.android_school.sovan.lectionrecorder.R;
import com.elegion.android_school.sovan.lectionrecorder.tasks.RecorderTask;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sovan on 11.10.2014.
 */
public class TaskListAdapter extends ResourceCursorAdapter {

    private int mSelectedId = -1;

    public TaskListAdapter(Context context, Cursor c) {
        super(context,  R.layout.li_item, c,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    public void setSelectedId(int selectedId) {
        mSelectedId = selectedId;
        notifyDataSetChanged();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.textTitle)).
                setText(cursor.getString(cursor.getColumnIndex(RecorderTask.Columns.TITLE)));
        long startTime = cursor.getLong(cursor.getColumnIndex(RecorderTask.Columns.START_TIME));
        long runningTime = cursor.getLong(cursor.getColumnIndex(RecorderTask.Columns.RUNNING_TIME));
        ((TextView) view.findViewById(R.id.textDuration)).setText(getHoursString(runningTime));
        long period = cursor.getLong(cursor.getColumnIndex(RecorderTask.Columns.PERIOD));
        int isPeriodical = cursor.getInt(cursor.getColumnIndex(RecorderTask.Columns.IS_PERIODICAL));
        TextView tvPeriod = (TextView) view.findViewById(R.id.textPeriod);
        if (isPeriodical == 0) {
            long test = System.currentTimeMillis();
            if (startTime+runningTime < System.currentTimeMillis()) {
                tvPeriod.setTextColor(Color.RED);
                tvPeriod.setText("One shot");
            }
            else {
                tvPeriod.setTextColor(Color.parseColor("#ffffface"));
                tvPeriod.setText("One shot");
            }
        }
        else {
            tvPeriod.setTextColor(Color.parseColor("#ffffface"));
            tvPeriod.setText(getHoursString(period));

        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm");
        ((TextView) view.findViewById(R.id.textStartTime)).setText(dateFormat.format(new Date(startTime)));

    }

    public static String getHoursString(long time) {
        long years = time / DateUtils.YEAR_IN_MILLIS;
        time = time % DateUtils.YEAR_IN_MILLIS;
        long weeks = time / DateUtils.WEEK_IN_MILLIS;
        time = time % DateUtils.WEEK_IN_MILLIS;
        long days = time / DateUtils.DAY_IN_MILLIS;
        time = time % DateUtils.DAY_IN_MILLIS;
        long hours = time / DateUtils.HOUR_IN_MILLIS;
        time = time % DateUtils.HOUR_IN_MILLIS;
        long minutes = time / DateUtils.MINUTE_IN_MILLIS;
        time = time % DateUtils.MINUTE_IN_MILLIS;
        long seconds = time / DateUtils.SECOND_IN_MILLIS;
        StringBuilder sb = new StringBuilder();
        if (years != 0) {
            sb.append(years);
            sb.append("y ");
        }
        if (weeks != 0) {
            sb.append(weeks);
            sb.append("w ");
        }
        if (days != 0) {
            sb.append(days);
            sb.append("d ");
        }
        if (hours != 0) {
            sb.append(hours);
            sb.append("h ");
        }
        if (minutes != 0) {
            sb.append(minutes);
            sb.append("m ");
        }
        if (seconds != 0) {
            sb.append(seconds);
            sb.append("s ");
        }
        return sb.toString();
    }

}

