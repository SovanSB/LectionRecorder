package com.elegion.android_school.sovan.lectionrecorder.tasks;

import android.content.ContentValues;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.BaseColumns;

import com.elegion.android_school.sovan.lectionrecorder.BuildConfig;

import java.io.Serializable;

/**
 * Created by Sovan on 24.09.2014.
 */

public class RecorderTask implements Serializable, Comparable {
    public static final Uri URI = Uri.parse("content://" + BuildConfig.PACKAGE_NAME + "/tasks");

    private String mTitle;
    private int mId;
    private String mFileName;
    private String mPath;

    private long mStartTime;
    private long mRunningTime;
    private long mPeriod;

    public long getPeriod() {
        return mPeriod;
    }

    public void setPeriod(long period) {
        mPeriod = period;
    }

    private boolean mIsNumeric;
    private boolean mIsPeriodical;

    private int mAudioSource;
    private int mAudioEncoder;
    private int mOutputFormat;

    public RecorderTask(String title, int id, String path, String fileName, long startTime, long runningTime,
                        long period,  boolean isPeriodical, boolean isNumeric, int audioSource,
                        int audioEncoder, int outputFormat) {
        this.mTitle = title;
        this.mId = id;
        this.mFileName = fileName;
        this.mPath = path;
        this.mStartTime = startTime;
        this.mRunningTime = runningTime;
        this.mPeriod = period;
        this.mIsNumeric = isNumeric;
        this.mIsPeriodical = isPeriodical;
        this.mAudioSource = audioSource;
        this.mAudioEncoder = audioEncoder;
        this.mOutputFormat = outputFormat;
    }

    public RecorderTask(String title, int id, String path, String fileName, long startTime,
                        long runningTime, long period, boolean isPeriodical) {
        this(title, id, path, fileName, startTime, runningTime, period, isPeriodical, false,  MediaRecorder.AudioSource.MIC,
                MediaRecorder.AudioEncoder.AMR_NB, MediaRecorder.OutputFormat.THREE_GPP);
    }

    public ContentValues toValues() {
        final ContentValues values = new ContentValues();
        values.put(Columns.TITLE, mTitle);
        values.put(Columns.ID, mId);
        values.put(Columns.FILE_NAME, mFileName);
        values.put(Columns.PATH, mPath);
        values.put(Columns.START_TIME, mStartTime);
        values.put(Columns.RUNNING_TIME, mRunningTime);
        values.put(Columns.PERIOD, mPeriod);
        values.put(Columns.IS_PERIODICAL, mIsPeriodical);
        values.put(Columns.IS_NUMERIC, mIsNumeric);
        values.put(Columns.AUDIO_SOURCE, mAudioSource);
        values.put(Columns.AUDIO_ENCODER, mAudioEncoder);
        values.put(Columns.OUTPUT_FORMAT, mOutputFormat);
        return values;
    }

   public ContentValues startTimeToValues() {
       final ContentValues values = new ContentValues();
       values.put(Columns.START_TIME, mStartTime);
       return values;
   }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public long getRunningTime() {
        return mRunningTime;
    }

    public void setRunningTime(long runningTime) {
        mRunningTime = runningTime;
    }

    public boolean isNumeric() {
        return mIsNumeric;
    }

    public void setNumeric(boolean isNumeric) {
        this.mIsNumeric = isNumeric;
    }

    public int getAudioSource() {
        return mAudioSource;
    }

    public boolean isPeriodical() {
        return mIsPeriodical;
    }

    public void setPeriodical(boolean isPeriodical) {
        mIsPeriodical = isPeriodical;
    }

    public void setAudioSource(int audioSource) {
        mAudioSource = audioSource;
    }

    public int getAudioEncoder() {
        return mAudioEncoder;
    }

    public void setAudioEncoder(int audioEncoder) {
        mAudioEncoder = audioEncoder;
    }

    public int getOutputFormat() {
        return mOutputFormat;
    }

    public void setOutputFormat(int outputFormat) {
        mOutputFormat = outputFormat;
    }

    @Override
    public int compareTo(Object another) {
        RecorderTask b = (RecorderTask) another;
        if (this.getStartTime() == b.getStartTime())
            // To set task with less running task earlier
            return (int) (b.getRunningTime() - this.getRunningTime());
        return (int) (this.getStartTime() - b.getStartTime());
    }




    public static interface Columns extends BaseColumns {
        String TITLE = "title";
        String ID = "id";
        String FILE_NAME = "file_name";
        String PATH = "path";
        String START_TIME = "start_time";
        String RUNNING_TIME = "running_time";
        String PERIOD = "period";
        String IS_NUMERIC = "is_numeric";
        String IS_PERIODICAL = "is_periodical";
        String AUDIO_SOURCE = "audio_source";
        String AUDIO_ENCODER = "audio_encoder";
        String OUTPUT_FORMAT = "output_format";
    }
}
