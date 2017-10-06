package com.openmind.min.openmindsoundmeter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST = 10;
    private static final String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private ProgressBar progress;
    private TextView txtDisplayDecibel;
    private TextView txtDisplayAmplitude;
    private Button buttonToggleRecording;
    private double amplitude;
    private double decibel;
    private Handler mainHandler = new Handler();
    private static int LAPSE = 100;
    private static double REFERENCE_PRESSURE = 1;
    private double[] sampleArray = new double[5];

    private MyMediaRecorder mediaRecorder = new MyMediaRecorder();
    private Thread myThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonToggleRecording = (Button) findViewById(R.id.buttonToggleRecording);
        buttonToggleRecording.setOnClickListener(this);
        txtDisplayAmplitude = (TextView) findViewById(R.id.textViewDisplayAmplitude);
        txtDisplayDecibel = (TextView) findViewById(R.id.textViewDisplayDecibel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    private Runnable recordingTask = new Runnable() {
        @Override
        public void run() {
            while(mediaRecorder.isRecording()){
                try {
                    for(int i=0; i<sampleArray.length; i++){
                        sampleArray[i] = mediaRecorder.getAmplitude();
                        Thread.sleep(LAPSE);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                amplitude = avgAmplitudeCal(sampleArray);
                decibel = mediaRecorder.soundDb(amplitude);
                mainHandler.post(updateUITask);
            }
        }
    };

    private double avgAmplitudeCal(double[] array) {
        double sum = 0;
        int length = array.length;
        for(int i=0; i<length ; i++){
            sum +=array[i];
        }
        return (sum/length);
    }

    private Runnable updateUITask = new Runnable() {
        @Override
        public void run() {
            String decibelInfo = String.valueOf(decibel);
            String amplitudeInfo = String.valueOf(amplitude);
            txtDisplayAmplitude.setText("Biên độ: " + amplitudeInfo);
            txtDisplayDecibel.setText("Decibel: " + decibelInfo);
        }
    };

    public void startRecording() {
        mediaRecorder.startRecorder();
        mediaRecorder.setRecordingStatus(true);
        myThread = new Thread(recordingTask);
        myThread.start();
    }

    public void stopRecording() {
        mediaRecorder.stopRecorder();
        mediaRecorder.setRecordingStatus(false);
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void requestNeededPermission() {
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    this.finishAffinity();
                }
            }
        }
    }

    public void checkPermission() {
        if(!hasPermissions(getApplicationContext(), PERMISSIONS)) {
            requestNeededPermission();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.buttonToggleRecording: {
                if(!mediaRecorder.isRecording()) {
                    startRecording();
                    buttonToggleRecording.setText("Click to PAUSE");
                }
                else {
                    stopRecording();
                    buttonToggleRecording.setText("Click to PLAY");
                }
                break;
            }

        }
    }
}
