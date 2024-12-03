package com.neuhex.aidmex;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.common.internal.IAccountAccessor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class VitalData extends AppCompatActivity{

    private static final String TAG="VitalDataActivity";

    private LineChart   mChart1;
    private BarChart    mChart2;
    private BarChart    mChart3;

    int HRdata0,HRdata1,HRdata2,HRdata3,HRdata4,HRdata5,HRdata6,HRdata7,HRdata8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_data);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);


        mChart1=(LineChart) findViewById(R.id.lineChart1);
        mChart1.setDragEnabled(true);
        mChart1.setScaleEnabled(false);

        ArrayList<Entry> yValues= new ArrayList<>();

        /*
        //Random Data for testing
        for(int a=1; a<=7; a++){
            int rand= new Random().nextInt((120-60)+1)+60;
            yValues.add(new Entry(a, rand));
        }
        */



        String HRdaily= "HRdata";
        int HRdData;
        for(int a=1; a<=8;a++){
            HRdData=prefs.getInt(HRdaily+String.valueOf(a),255);
            if(HRdData!=255){
                yValues.add(new Entry((a-1),HRdData));
            }
        }




        LineDataSet set1= new LineDataSet(yValues, "Heart Rate (BPM)");
        set1.setFillAlpha(110);
        set1.setColor(Color.rgb(255,82,92));
        set1.setLineWidth(3);
        mChart1.getAxisRight().setEnabled(false);
        ArrayList<ILineDataSet> dataSets= new ArrayList<>();
        dataSets.add(set1);
        LineData line_data= new LineData(dataSets);
        mChart1.setData(line_data);

        String[] values=new String[] {"12am", "03am", "06am", "09am", "12pm", "03pm","06pm","09pm","12am"};

        mChart1.getXAxis().setValueFormatter(new IndexAxisValueFormatter(values));
        mChart1.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart1.getDescription().setText(getDateToday());
        mChart1.getDescription().setTextSize(15);
        mChart1.setVisibleXRange(0,8);
        mChart1.getAxisLeft().setStartAtZero(true);
        mChart1.getAxisLeft().setAxisMaximum(180);

        //########################################################################################

        mChart2=(BarChart) findViewById(R.id. barChart1);
        mChart2.setDrawBarShadow(false);
        mChart2.setDrawValueAboveBar(true);
        mChart2.setMaxVisibleValueCount(180);
        mChart2.setPinchZoom(false);
        mChart2.setDrawGridBackground(true);

        ArrayList<BarEntry> barEntries= new ArrayList<>();


        String HRdaily_label="HRdaily";
        String HRdate;
        int HRdaily_ave;
        for(int a=1; a<=31; a++){
            if(a<10){
                HRdate="0"+String.valueOf(a);
            }else{
                HRdate=String.valueOf(a);
            }
            HRdaily_ave=prefs.getInt(HRdaily_label+HRdate,255);
            if(HRdaily_ave!=255){
                barEntries.add(new BarEntry(a, HRdaily_ave));
            }
        }

        /*
        //Random data for testing only
        for(int a=1;a<=29;a++){
            int rand= new Random().nextInt((110-60)+1)+60;
            barEntries.add(new BarEntry(a,rand));
        }
        */

        BarDataSet barDataSet= new BarDataSet(barEntries, "Daily H.R. Average");
        barDataSet.setColor(Color.rgb(255,82,92));
        BarData bar_data= new BarData(barDataSet);
        bar_data.setBarWidth(0.5f);
        mChart2.getDescription().setText(getMonth_str());
        mChart2.getDescription().setTextSize(15);

        mChart2.setData(bar_data);

        mChart2.getAxisRight().setEnabled(false);
        mChart2.getXAxis().setLabelCount(7,true);
        mChart2.getXAxis().setAxisMinimum(0);
        mChart2.getXAxis().setAxisMaximum(31);
        mChart2.getAxisLeft().setStartAtZero(true);
        mChart2.getAxisLeft().setAxisMaximum(180);

        //########################################################################################
        mChart3=(BarChart) findViewById(R.id. barChart2);
        mChart3.setDrawBarShadow(false);
        mChart3.setDrawValueAboveBar(true);
        mChart3.setMaxVisibleValueCount(10000);
        mChart3.setPinchZoom(false);
        mChart3.setDrawGridBackground(true);
        mChart3.setClickable(false);

        ArrayList<BarEntry> step_barEntries= new ArrayList<>();


        //Random data for testing only
        for(int a=1;a<=30;a++){
            int rand= new Random().nextInt((9000-2000)+1)+2000;
            step_barEntries.add(new BarEntry(a,rand));
        }



        BarDataSet step_barDataSet= new BarDataSet(step_barEntries, "Daily Step Count");
        step_barDataSet.setColor(Color.rgb(23,230,210));
        BarData step_bar_data= new BarData(step_barDataSet);
        step_bar_data.setBarWidth(0.5f);
        mChart3.getDescription().setText(getMonth_str());
        mChart3.getDescription().setTextSize(15);

        mChart3.setData(step_bar_data);

        mChart3.getAxisRight().setEnabled(false);
        mChart3.getXAxis().setLabelCount(7,true);
        mChart3.getXAxis().setAxisMinimum(0);
        mChart3.getXAxis().setAxisMaximum(31);
        mChart3.getAxisLeft().setStartAtZero(true);
        mChart3.getAxisLeft().setAxisMaximum(10000);







    }


    private static String getMonth_str() {
        String monthnum, monthname;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM");
        sdf.setTimeZone(TimeZone.getDefault());
        monthnum=sdf.format(Calendar.getInstance().getTime());
        switch (monthnum){
            case "01":
                monthname="January";
                break;
            case "02":
                monthname="February";
                break;
            case "03":
                monthname="March";
                break;
            case "04":
                monthname="April";
                break;
            case "05":
                monthname="May";
                break;
            case "06":
                monthname="June";
                break;
            case "07":
                monthname="July";
                break;
            case "08":
                monthname="August";
                break;
            case "09":
                monthname="September";
                break;
            case "10":
                monthname="October";
                break;
            case "11":
                monthname="November";
                break;
            case "12":
                monthname="December";
                break;
            default:
                monthname="Unknown";
                break;
        }
        return monthname;
    }

    private static String getDateToday() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

}