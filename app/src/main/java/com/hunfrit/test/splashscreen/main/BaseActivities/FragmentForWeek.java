package com.hunfrit.test.splashscreen.main.BaseActivities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.hunfrit.test.R;

import java.util.ArrayList;

public class FragmentForWeek extends Fragment implements MainActivity.MainActivityCommunicatorForWeek{

    private LineChart mChart;

    ProgressBar progressBarInGraph;

    XAxis xAxis;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmnet_for_week, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        progressBarInGraph = (ProgressBar) getActivity().findViewById(R.id.progressBar3);

        mChart = (LineChart) getActivity().findViewById(R.id.chart1);
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setEnabled(false);

        xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        mChart.animateY(1000);
        mChart.getAxisLeft().setEnabled(true);
    }

    private void setData(float values[], final String dates[]) {

        showElement();

        ArrayList<Entry> entryValues = new ArrayList<Entry>();

        for(int i = 0; i <= 6; i++){
            entryValues.add(new Entry(i, values[i]));
        }

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates[(int) value % dates.length];
            }
        });

        LineDataSet set1;


        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {

            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(entryValues);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();

        } else {

            // create a dataset and give it a type
            set1 = new LineDataSet(entryValues,"Exchange rates history");
            set1.setDrawIcons(false);

            set1.setCircleColor(getResources().getColor(android.R.color.holo_blue_bright));
            set1.setCircleRadius(5f);
            set1.setDrawCircleHole(true);
            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);



            if (Utils.getSDKInt() >= 18) {

                // fill drawable only supported on api level 18 and above

                set1.setFillColor(getResources().getColor(android.R.color.holo_blue_light));

            } else {

                set1.setFillColor(Color.MAGENTA);

            }


            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets

            LineData data = new LineData(dataSets);


            // set data

            mChart.setData(data);
            mChart.invalidate();

        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Context context = getActivity();
        ((MainActivity) context).mSendToFragmentForWeek = this;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Context context = getActivity();
        ((MainActivity) context).mSendToFragmentForWeek = this;
    }

    @Override
    public void valueChanged(float[] result, String dates[]) {
        setData(result, dates);
    }

    @Override
    public void checkOnFail(Short check) {
        if (check == 1){
            setDataFail();
        }
    }

    @Override
    public void checkOnHide(Short check) {
        if (check == 1) {
            hideElement();
        }
    }

    private void showElement(){
        progressBarInGraph.setVisibility(View.GONE);
        mChart.setVisibility(View.VISIBLE);
    }

    private void hideElement(){
        mChart.setVisibility(View.GONE);
        progressBarInGraph.setVisibility(View.VISIBLE);
    }

    private void setDataFail(){
        float values[] = {Float.valueOf(0), Float.valueOf(0), Float.valueOf(0), Float.valueOf(0), Float.valueOf(0), Float.valueOf(0), Float.valueOf(0)};
        String Dates[] = {"00/00", "00/00", "00/00", "00/00", "00/00", "00/00", "00/00"};
        setData(values, Dates);
    }
}
