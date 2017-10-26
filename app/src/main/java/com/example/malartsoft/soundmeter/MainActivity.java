package com.example.malartsoft.soundmeter;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {
    private static final int MY_PERMISSIONS_REQUEST = 10;
    private static final String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
    LineChartFragment lineChart ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        buttonToggleRecording = (Button) findViewById(R.id.buttonToggleRecording);
        buttonToggleRecording.setOnClickListener(this);
        txtDisplayAmplitude = (TextView) findViewById(R.id.textViewDisplayAmplitude);
        txtDisplayDecibel = (TextView) findViewById(R.id.textViewDisplayDecibel);

        lineChart = new LineChartFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fr_line,lineChart).commit();
        // startActivity(new Intent(this, LineChartFragment.class));

    }


    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        startRecording();
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
                lineChart.receiveData((int)amplitude);
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
        myThread = new Thread(recordingTask);
        myThread.start();
    }

    public void stopRecording() {
        mediaRecorder.stopRecorder();
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
//            case MY_PERMISSIONS_REQUEST: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                } else {
//                    this.finishAffinity();
//                }
//            }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
