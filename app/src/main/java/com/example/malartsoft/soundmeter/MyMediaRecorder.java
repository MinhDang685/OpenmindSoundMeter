package com.example.malartsoft.soundmeter;

import android.media.MediaRecorder;

/**
 * Created by MIB on 10/4/2017.
 */

public class MyMediaRecorder {
    private MediaRecorder mRecorder;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
    private boolean isRecording = false;

    public MyMediaRecorder() {
        mRecorder = null;
    }

    public void startRecorder(){
        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();
            }catch (java.io.IOException ioe) {
                android.util.Log.e("[MyMediaRecord]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));

            }catch (SecurityException e) {
                android.util.Log.e("[MyMediaRecord]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try
            {
                mRecorder.start();
            }catch (SecurityException e) {
                android.util.Log.e("[MyMediaRecord]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            setRecordingStatus(true);
            //mEMA = 0.0;
        }

    }

    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            setRecordingStatus(false);
        }
    }

    public double soundDb(double ampl){
        return  20.0 * Math.log10(ampl);
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecordingStatus(boolean recording) {
        isRecording = recording;
    }
}
