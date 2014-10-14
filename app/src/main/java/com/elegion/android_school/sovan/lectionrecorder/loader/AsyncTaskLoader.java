package com.elegion.android_school.sovan.lectionrecorder.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.elegion.android_school.sovan.lectionrecorder.tasks.RecorderTask;

public class AsyncTaskLoader extends CursorLoader {
    public AsyncTaskLoader(Context context) {
        super(context, RecorderTask.URI, null, null, null, null);
    }

    @Override
    public Cursor loadInBackground() {

        return super.loadInBackground();
    }
}
