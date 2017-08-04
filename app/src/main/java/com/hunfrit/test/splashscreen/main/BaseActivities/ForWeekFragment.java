package com.hunfrit.test.splashscreen.main.BaseActivities;

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

public class ForWeekFragment extends Fragment implements MainActivity.MainActivityCommunicatorForWeek{

    private LineChart mChart;

    private ProgressBar mProgressBarInGraph;

    private XAxis mXAxis;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmnet_for_week, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBarInGraph = (ProgressBar) view.findViewById(R.id.progressBar3);

        mChart = (LineChart) view.findViewById(R.id.chart1);
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

        mXAxis = mChart.getXAxis();
        mXAxis.setDrawGridLines(true);
        mXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxis.setGranularity(1f);

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

        mXAxis.setValueFormatter(new IAxisValueFormatter() {
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

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        ....For normal work with activity....
     */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).mSendToFragmentForWeek = this;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Context context = getActivity();
        ((MainActivity) context).mSendToFragmentForWeek = null;
    }

    @Override
    public void valueChanged(float[] result, String dates[]) {
        setData(result, dates);
    }

    @Override
    public void checkOnFail(short check) {
        if (check == 1){
            setDataFail();
        }
    }

    @Override
    public void checkOnHide(short check) {
        if (check == 1) {
            hideElement();
        }
    }

    private void showElement(){
        mProgressBarInGraph.setVisibility(View.GONE);
        mChart.setVisibility(View.VISIBLE);
    }

    private void hideElement(){
        mChart.setVisibility(View.GONE);
        mProgressBarInGraph.setVisibility(View.VISIBLE);
    }

    private void setDataFail(){
        float values[] = {Float.valueOf(0), Float.valueOf(0), Float.valueOf(0), Float.valueOf(0), Float.valueOf(0), Float.valueOf(0), Float.valueOf(0)};
        String Dates[] = {"00/00", "00/00", "00/00", "00/00", "00/00", "00/00", "00/00"};
        setData(values, Dates);
    }
}
