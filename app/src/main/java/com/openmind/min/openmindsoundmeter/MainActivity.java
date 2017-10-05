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
    private static int LAPSE = 100;
    private static double REFERENCE_PRESSURE = 1;
    private double[] sampleArray = new double[5];

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
}
