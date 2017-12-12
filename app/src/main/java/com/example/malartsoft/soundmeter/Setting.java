package com.example.malartsoft.soundmeter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.NumberPicker;


/**
 * Created by malartsoft on 11/26/17.
 */

public class Setting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        // Get the Intent that started this activity and extract the string
        //Intent intent = getIntent();
       // String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.limit_value);
        numberPicker.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        numberPicker.setMaxValue(10);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setValue(7);
    }
}
