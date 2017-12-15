package com.example.malartsoft.soundmeter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;


/**
 * Created by malartsoft on 11/26/17.
 */

public class Setting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        final NumberPicker numberPicker;
        Button btn_cancel;
        Button btn_ok;
        int minVal = 0;
        int maxVal = 180;
        // Get the Intent that started this activity and extract the string
        //Intent intent = getIntent();
       // String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        btn_cancel = (Button) findViewById((R.id.btn_cancel));
        btn_ok = (Button)findViewById(R.id.btn_ok);
        numberPicker = (NumberPicker) findViewById(R.id.limit_value);
        numberPicker.setMinValue(minVal);
        //Specify the maximum value/number of NumberPicker
        numberPicker.setMaxValue(maxVal);
        SharedPreferences prefs = getSharedPreferences("limit_value_file",MODE_PRIVATE);
        int limit_value = prefs.getInt("limit_value",999999);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        numberPicker.setWrapSelectorWheel(true);
        if(limit_value <= maxVal){
            numberPicker.setValue(limit_value);
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("limit_value_file", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("limit_value", numberPicker.getValue());
                editor.commit();
                MainActivity.setVibrate_attribute(numberPicker.getValue());
                Toast.makeText(getApplicationContext(), "Set value completed",Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
