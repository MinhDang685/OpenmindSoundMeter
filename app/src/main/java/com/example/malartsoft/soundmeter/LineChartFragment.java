package com.example.malartsoft.soundmeter;

import android.app.Fragment;

import com.github.mikephil.charting.data.Entry;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v7.view.*;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.R.attr.x;
import static android.R.attr.y;

public class LineChartFragment extends Fragment implements
        OnChartValueSelectedListener {

    // protected Typeface mTfLight;
    public static LineChart mChart;
    public static XAxis xAxis;
    public TextView tvX;
    public int _counts ;
    public int _min;
    public int _max;
    public ArrayList<Entry> values = new ArrayList<Entry>();
    protected Typeface mTfRegular;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.line_chart_fragment,container,false);
        mChart = (LineChart) view.findViewById(R.id.chart1);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(false );

        // set an alternative background color
        mChart.setBackgroundColor(Color.parseColor("#e3e3e3"));
        mChart.setViewPortOffsets(0f, 0f, 0f, 0f);
        // add data
        initsetData();
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        //xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(11, 11, 11));
        xAxis.setCenterAxisLabels(true);
        /*Yaxis*/
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(170f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(11, 11, 11));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        setMinMaxAxisX(0,100);
        return  view;
    }

    public void setMinMaxAxisX(int _min, int _max){
        xAxis.setAxisMaximum(_max);
        xAxis.setAxisMinimum(_min);
    }

    public void validation(){
        mChart.notifyDataSetChanged(); // let the chart know it's data changed
        mChart.invalidate();
    }
    private void initsetData() {
        values.add(new Entry());
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.RED);
        set1.setValueTextColor(Color.RED);
        set1.setLineWidth(1f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
        mChart.invalidate();
    }

    public void setData(Entry _entry){
        if(_entry.getX()> mChart.getXAxis().getAxisMaximum() - 5 ){
            values.remove((int)_entry.getX()%95);
            mChart.getXAxis().setAxisMinimum(mChart.getXAxis().getAxisMinimum() +1);
            mChart.getXAxis().setAxisMaximum(mChart.getXAxis().getAxisMaximum() +1);
        }
        values.add(_entry);
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.RED);
        set1.setValueTextColor(Color.RED);
        set1.setLineWidth(1f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.RED);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
        //  mChart.invalidate();
    }

    public void Clear() {
        values.clear();
        values = new ArrayList<Entry>();
        setMinMaxAxisX(0, 100);
        mChart.invalidate();
        mChart.clear();
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() {

    }
}
