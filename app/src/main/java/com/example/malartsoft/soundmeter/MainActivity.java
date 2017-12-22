package com.example.malartsoft.soundmeter;

import android.Manifest;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.github.anastr.speedviewlib.*;
import android.os.Vibrator;
import android.widget.Toast;
import android.app.AlertDialog;

import com.github.mikephil.charting.data.Entry;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {
    private static final int MY_PERMISSIONS_REQUEST = 10;
    private static final String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static int LAPSE = 100;
    private static double REFERENCE_PRESSURE = 1;
    private static int vibrator = 100;
    LineChartFragment lineChart;
    Bundle _bundle = new Bundle();
    int indexChangeBG = 0;
    private ProgressBar progress;
    private TextView txtDisplayDecibel;
    private TextView txtDisplayAmplitude;
    private TextView alert;
    private ImageButton buttonToggleRecording;
    private ImageButton buttonToggleRecordingPause;
    private ImageButton changeBg;
    private double amplitude;
    private double decibel;
    private Handler mainHandler = new Handler();
    private int STATUS = 0;
    private double[] sampleArray = new double[5];
    private Gauge gauge;
    private MyMediaRecorder mediaRecorder = new MyMediaRecorder();
    private Thread myThread = null;
    private int _min = 0;
    private int _max = 100;
    private double argDeciben = 0;
    private int time = 0;
    private boolean statusAlertClick = false;
    private LinearLayout linearLayoutAlert;// = (LinearLayout) findViewById(R.id.linearAlertLists);
    private LinearLayout linearLayoutChart;// = (LinearLayout) findViewById(R.id.linearLayoutChart);
    private ImageButton resetBtn ;//= (ImageButton) findViewById(R.id.buttonReset);
    private ImageButton alertBtn;// = (ImageButton) findViewById(R.id.buttonShowAlertList );
    private ImageButton showChart;
    private List<TextView> alertLists;
    private TextView alert_1;
    private TextView alert_2;
    private TextView alert_3;
    private TextView alert_4;
    private TextView alert_5;
    private TextView alert_6;
    private TextView alert_7;
    private TextView alert_8;
    private TextView alert_9;
    private TextView alert_10;
    private TextView alert_11;
    private TextView alert_12;
    private TextView alert_13;
    private int count_greater  = 0;
    private  boolean arlet_isshow = false;
    private Runnable updateUITask = new Runnable() {
        @Override
        public void run() {
            String decibelInfo = String.valueOf(decibel);
            String amplitudeInfo = String.valueOf(amplitude);
            Log.i("AMP", String.valueOf(amplitude));
            Log.i("DB", String.valueOf(decibel));
            txtDisplayAmplitude.setText("Biên độ: " + amplitudeInfo);
            txtDisplayDecibel.setText("Decibel: " + decibelInfo);
            gauge.speedTo((float) decibel);

            showAlert(decibel);

            lineChart.validation();
        }
    };
    private Runnable recordingTask = new Runnable() {
        @Override
        public void run() {
            Log.i("Calculate", "start");
            int check = 0;
            double tmp_deci = 0;
            while (mediaRecorder.isRecording()) {
                try {
                    for (int i = 0; i < sampleArray.length; i++) {
                        sampleArray[i] = mediaRecorder.getAmplitude();
                        Thread.sleep(LAPSE);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                amplitude = avgAmplitudeCal(sampleArray);
                decibel = mediaRecorder.soundDb(amplitude);


                if (check % 2 == 0) {
//                    if(time + 5 > _max){
//                        chart.setMinMaxAxisX(_min + 1, _max +1);
//                    }
                    lineChart.setData(new Entry(time, (int) ((tmp_deci + decibel) / (tmp_deci == 0 ? 1 : 2))));
                    //  chart.validation();
                    time++;
                }
                tmp_deci = decibel;
                mainHandler.post(updateUITask);
            }
            amplitude = 0;
            decibel = 0;
            mainHandler.post(updateUITask);
            Log.i("Calculate", "stop");
        }
    };

    public static void setVibrate_attribute(int _vibrate) {
        vibrator = _vibrate;
    }

    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        linearLayoutAlert = (LinearLayout) findViewById(R.id.linearAlertLists);
        linearLayoutChart = (LinearLayout) findViewById(R.id.linearLayoutChart);

        resetBtn = (ImageButton) findViewById(R.id.buttonReset);

        resetBtn.setOnClickListener(this);
         alertBtn = (ImageButton) findViewById(R.id.buttonShowAlertList );


        alertBtn.setOnClickListener(this);

        showChart = (ImageButton) findViewById(R.id.buttonShowAlertListChart );
        showChart.setOnClickListener(this);

        buttonToggleRecording = (ImageButton) findViewById(R.id.buttonToggleRecording);
        buttonToggleRecording.setOnClickListener(this);
        buttonToggleRecordingPause = (ImageButton) findViewById(R.id.buttonToggleRecordingPause);
        buttonToggleRecordingPause.setOnClickListener(this);
        changeBg = (ImageButton) findViewById(R.id.changeBg);
        changeBg.setOnClickListener(this);
        txtDisplayAmplitude = (TextView) findViewById(R.id.textViewDisplayAmplitude);
        alert = (TextView) findViewById(R.id.alert);
        txtDisplayDecibel = (TextView) findViewById(R.id.textViewDisplayDecibel);
        gauge = (Gauge) findViewById(R.id.speedometer);
        gauge.setUnit("dB");
        lineChart = new LineChartFragment();



        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fr_line,lineChart).commit();
        // startActivity(new Intent(this, LineChartFragment.class));
        setAlert();
        initVibrate();
    }

    public void setVibrator(boolean iscancel){
        Vibrator v  = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);;
        if(!iscancel){
            long[] pattern = { 0, 100, 200, 100, 200, 100, 200};
            v.vibrate(pattern , 3);
        }
        else{
            v.cancel();
        }
    }

    public void setAlert(){

        alertLists=new ArrayList<TextView>();
        alert_1 = (TextView) findViewById(R.id.alert_1);
        alert_2 =(TextView) findViewById(R.id.alert_2);
        alert_3= (TextView) findViewById(R.id.alert_3);
        alert_4 =(TextView) findViewById(R.id.alert_4);
        alert_5=(TextView) findViewById(R.id.alert_5);
        alert_6=(TextView) findViewById(R.id.alert_6);
        alert_7=(TextView) findViewById(R.id.alert_7);
        alert_8=(TextView) findViewById(R.id.alert_8);
        alert_9=(TextView) findViewById(R.id.alert_9);
        alert_10=(TextView) findViewById(R.id.alert_10);
        alert_11=(TextView) findViewById(R.id.alert_11);
        alert_12=(TextView) findViewById(R.id.alert_12);
        alert_13=(TextView) findViewById(R.id.alert_13);
        alertLists.add(alert_1);
        alertLists.add(alert_2);
        alertLists.add(alert_3);
        alertLists.add(alert_4);
        alertLists.add(alert_5);
        alertLists.add(alert_6);
        alertLists.add(alert_7);
        alertLists.add(alert_8);
        alertLists.add(alert_9);
        alertLists.add(alert_10);
        alertLists.add(alert_11);
        alertLists.add(alert_12);
        alertLists.add(alert_13);



    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private double avgAmplitudeCal(double[] array) {
        double sum = 0;
        int length = array.length;
        for(int i=0; i<length ; i++){
            sum +=array[i];
        }
        return (sum/length);
    }

    public void SetColorTextAlert(int index){
        for(int i=0;i<alertLists.size();i++){
            if(i==index-1){
                alertLists.get(i).setTextColor(Color.rgb(255,0,0));
            }else{
                alertLists.get(i).setTextColor(Color.rgb(11,11,11));
            }
        }
    }

    public void showAlert(double decibel){

        if(decibel>vibrator){
                setVibrator(false);
        }
        else{
            count_greater = 0;
            setVibrator(true);
        }

        if(decibel>=0 && decibel <= 20){
            alert.setText((int)decibel+"dB : " + "Rustling leaves, Ticking watch");
            SetColorTextAlert(13);
        }
        else if(decibel>20 && decibel <= 30){
            alert.setText((int)decibel+"dB : " + "Quiet whisper at 3 ft, Library");
            SetColorTextAlert(12);
        }
        else if(decibel>30 && decibel <= 40){
            alert.setText((int)decibel+"dB : " + "Quiet residential area, Park");
            SetColorTextAlert(11);
        }
        else if(decibel>40 && decibel <= 50){
            alert.setText((int)decibel+"dB : " + "Quiet office, Quiet street");
            SetColorTextAlert(10);
        }
        else if(decibel>50 && decibel <= 60){
            alert.setText((int)decibel+"dB : " + "Normal conversation at 3 ft.");
            SetColorTextAlert(9);
        }
        else if(decibel>60 && decibel <= 70){
            alert.setText((int)decibel+"dB : " + "Busy traffic, Phone ringtone");
            SetColorTextAlert(8);
        }
        else if(decibel>70 && decibel <= 80){
            alert.setText((int)decibel+"dB : " + "Busy street, Alarm clock");
            SetColorTextAlert(7);
        }
        else if(decibel>80 && decibel <= 90){
            alert.setText((int)decibel+"dB : " + "Factory machinery at 3 ft.");
            SetColorTextAlert(6);
        }
        else if(decibel>90 && decibel <= 100){
            alert.setText((int)decibel+"dB : " + "Subway train, Blow dryer");
            SetColorTextAlert(5);
        }
        else if(decibel>100 && decibel <= 110){
            alert.setText((int)decibel+"dB : " + "Rock music, Screaming child");
            SetColorTextAlert(4);
        }
        else if(decibel>110 && decibel <= 120){
            alert.setText((int)decibel+"dB : " + "Threshold of pain, Thunder");
            SetColorTextAlert(3);
        }
        else if(decibel>120 && decibel <= 130){
            alert.setText((int)decibel+"dB : " + "Jet engine at 100ft.");
            SetColorTextAlert(2);
        }
        else {
            alert.setText((int)decibel+"dB : " + "Space shuttle lift-off");
            SetColorTextAlert(1);
        }

    }

    public boolean startRecording() {
        if (!mediaRecorder.startRecorder())
            return false;

        myThread = new Thread(recordingTask);
        myThread.start();
        STATUS = 0;
        return true;
    }

    public boolean stopRecording() {
        if (!mediaRecorder.stopRecorder())
            return false;
        STATUS = 1;
        return true;
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
        } else {
            if (STATUS == 0)
                startRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (STATUS == 0)
                        startRecording();
                } else {
                    this.finishAffinity();
                }
            }
        }
    }

    public void checkPermission() {
        if(!hasPermissions(getApplicationContext(), PERMISSIONS)) {
            requestNeededPermission();
        } else {
            if (STATUS == 0)
                startRecording();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.buttonToggleRecording: {
                if(!mediaRecorder.isRecording()) {
                    if (!startRecording()) {
                        Toast.makeText(this, "You're clicking too fast", Toast.LENGTH_LONG).show();
                        break;
                    }
                    buttonToggleRecording.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    //buttonToggleRecording.setText("Click to PAUSE");
                }
                else {
                    if (!stopRecording()) {
                        Toast.makeText(this, "You're clicking too fast", Toast.LENGTH_LONG).show();
                        break;
                    }
                    buttonToggleRecording.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    //buttonToggleRecording.setText("Click to PAUSE");
                }
                break;
            }
            case R.id.buttonShowAlertList:{
                //if(!statusAlertClick){
                // statusAlertClick = true;
                linearLayoutAlert.setVisibility(View.VISIBLE);
                linearLayoutChart.setVisibility(View.GONE);
                alert.setVisibility(View.GONE);
                alertBtn.setVisibility(View.GONE);
                showChart.setVisibility(View.VISIBLE);


                buttonToggleRecording.setVisibility(View.GONE);

                //alertBtn.setBackgroundResource(R.drawable.chart);
                //alertBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                }else{
//                    statusAlertClick =false;
//                    linearLayoutAlert.setVisibility(View.GONE);
//                    linearLayoutChart.setVisibility(View.VISIBLE);
//                    alert.setVisibility(View.VISIBLE);
//                   // alertBtn.setBackgroundResource(R.drawable.ic_t);
//                    //linearLayoutAlert.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
//                }
                break;
            }
            case R.id.buttonShowAlertListChart:{
                //statusAlertClick =false;
                linearLayoutAlert.setVisibility(View.GONE);
                linearLayoutChart.setVisibility(View.VISIBLE);
                alert.setVisibility(View.VISIBLE);

                alertBtn.setVisibility(View.VISIBLE);
                showChart.setVisibility(View.GONE);


                buttonToggleRecording.setVisibility(View.VISIBLE);
                // alertBtn.setBackgroundResource(R.drawable.ic_t);
                //linearLayoutAlert.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                break;
            }
            case R.id.buttonReset: {
                time = 0;
                lineChart.Clear();
                break;
            }
            case R.id.changeBg: {
                indexChangeBG++;
                if (indexChangeBG > 3)
                    indexChangeBG = 0;
                LinearLayout layout = (LinearLayout) findViewById(R.id.parentLayout);
                switch (indexChangeBG) {

                    case 0:


                        layout.setBackgroundColor(Color.rgb(250, 250, 250));
                        break;
                    case 1:
                        layout.setBackgroundColor(Color.rgb(227, 227, 227));
                        break;
                    case 2:
                        layout.setBackgroundColor(Color.rgb(200, 200, 200));
                        break;
                    case 3:
                        layout.setBackgroundColor(Color.rgb(215, 215, 215));
                        break;
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
        if (id == R.id.menu_item) {
            takeScreenshot();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            Toast.makeText(this, "Screen captured: "+mPath,
                    Toast.LENGTH_SHORT).show();
            //openScreenshot(imageFile);
            Toast.makeText(this, "Screen captured: " + mPath,
             Toast.LENGTH_SHORT).show();
            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    public  void ShareApp(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "https://play.google.com/store/apps/details?id=kr.sira.sound";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SoundMeter");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            String dialog_title = "About";
            String dialog_message = "Openmind team - Group 4 - TH2014";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(dialog_message)
                    .setTitle(dialog_title);
            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (id == R.id.nav_gallery) {
            String email = "openmindhcmus@gmail.com";
            String subject = "Feedback - OpenmindSoundMeter";
            String body = "";
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", email, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } else if (id == R.id.nav_slideshow) {
            String video_id = "kYSYv5u_88g";
            watchYoutubeVideo(this, video_id);
        } else if (id == R.id.nav_manage) {

            startActivity(new Intent(MainActivity.this, Setting.class));
            //drawer.closeDrawers();

            // Intent myIntent = new Intent(this, Setting.class);
            //myIntent.putExtra("key", value); //Optional parameters
            //this.startActivity(myIntent);
        } else if (id == R.id.nav_share) {
            ShareApp();
        } else if (id == R.id.nav_send) {
            String email = "openmindhcmus@gmail.com";
            String subject = "Feedback - OpenmindSoundMeter";
            String body = "";
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", email, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initVibrate(){
        SharedPreferences prefs = getSharedPreferences("limit_value_file",MODE_PRIVATE);
        int limit_value = prefs.getInt("limit_value",100);
        this.setVibrate_attribute(limit_value);
    }


}
