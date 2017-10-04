package com.openmind.min.openmindsoundmeter;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progress;
    private TextView text;
    private TextView txtDisplayDecibel;
    private TextView txtDisplayAmplitude;
    private Button buttonToggleRecording;
    private double amplitude;
    private double decibel;
    private Handler mainHandler = new Handler();
    private static int LAPSE = 250;
    private static double REFERENCE_PRESSURE = 1;

    private MyMediaRecorder mediaRecorder = new MyMediaRecorder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (ProgressBar) findViewById(R.id.progressBar1);
        text = (TextView) findViewById(R.id.textView1);
        buttonToggleRecording = (Button) findViewById(R.id.buttonToggleRecording);
        txtDisplayAmplitude = (TextView) findViewById(R.id.textViewDisplayAmplitude);
        txtDisplayDecibel = (TextView) findViewById(R.id.textViewDisplayDecibel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaRecorder.startRecorder();
        mediaRecorder.setRecording(true);
        Thread myThread = new Thread(recordingTask);
        myThread.start();
    }

    private Runnable recordingTask = new Runnable() {
        @Override
        public void run() {
            while(mediaRecorder.isRecording()){
                amplitude = mediaRecorder.getAmplitude();
                decibel = mediaRecorder.soundDb(amplitude);
                mainHandler.post(updateUITask);
                try {
                    Thread.sleep(LAPSE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable updateUITask = new Runnable() {
        @Override
        public void run() {
            String decibelInfo = String.valueOf(decibel);
            String amplitudeInfo = String.valueOf(amplitude);
            txtDisplayAmplitude.setText(amplitudeInfo);
            txtDisplayDecibel.setText(decibelInfo);
        }
    };
}
