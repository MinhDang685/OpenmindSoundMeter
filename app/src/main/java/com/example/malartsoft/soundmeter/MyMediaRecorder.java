package com.example.malartsoft.soundmeter;

import android.media.MediaRecorder;
import android.os.CountDownTimer;

/**
 * Created by MIB on 10/4/2017.
 */

public class MyMediaRecorder {
    static final private double EMA_FILTER = 0.6;
    private static double mEMA = 0.0;
    private MediaRecorder mRecorder;
    private boolean isRecording = false;
    private boolean canClicked = true;

    public MyMediaRecorder() {
        mRecorder = null;
    }

    private boolean checkClickPossible() {
        return canClicked;
    }

    private void setTimeoutForClickable() {
        new CountDownTimer(1000, 500) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                canClicked = true;
            }
        }.start();
    }

    public boolean startRecorder() {
        if (checkClickPossible() && mRecorder == null)
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
            canClicked = false;
            setTimeoutForClickable();
            return true;
            //mEMA = 0.0;
        } else {
            return false;
        }

    }

    public boolean stopRecorder() {
        if (checkClickPossible() && mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            setRecordingStatus(false);
            canClicked = false;
            setTimeoutForClickable();
            return true;
        } else {
            return false;
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
