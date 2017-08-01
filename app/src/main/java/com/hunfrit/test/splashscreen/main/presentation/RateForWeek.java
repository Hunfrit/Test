package com.hunfrit.test.splashscreen.main.presentation;

import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.hunfrit.test.splashscreen.Interface.Link;
import com.hunfrit.test.splashscreen.SetGet.SetGet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Anoli on 01.08.2017.
 */

public class RateForWeek extends AsyncTask<Void, Void, Boolean>{

    private MainView view;

    static final String URL = "https://bank.gov.ua/";

    List<SetGet> setGetForWeek;
    SetGet postForWeek;

    Short checkOnFail = 0;

    Link rate;

    float[] resultByDate = new float[7];
    String[] dayByDate = new String[7];

    public RateForWeek(MainView view){
        this.view = view;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        checkOnFail = 0;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        rate = retrofit.create(Link.class);
        setGetForWeek = new ArrayList<>();

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter formatForGraph = DateTimeFormat.forPattern("dd.MM");
        DateTime now = DateTime.now();
        DateTime week = now.minusWeeks(1);
        if (getByDate(String.valueOf(format.print(now))) != null) {
            for (int i = 0; i <= 6; i++) {
                now = week.plusDays(i + 1);     // i + 1 ~~~ because after minusWeek(1) we gonna get on one day earlier than necessary
                postForWeek = getByDate(String.valueOf(format.print(now))).get(0);
                resultByDate[i] = postForWeek.getRate();
                dayByDate[i] = formatForGraph.print(now);
                Log.d("TAGA", String.valueOf(resultByDate[i]));
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            view.resultForWeekIsSuccessful(resultByDate, dayByDate);
        } else {
            view.resultForWeekIsFailure(checkOnFail = 1);
        }
    }

    public List<SetGet> getByDate(String date) {
        try {
            Response<List<SetGet>> response = rate.getRateByDate(date).execute();
            if (response.isSuccessful()) {
                if (String.valueOf(response.body()) == "[]") {

                    // If NBU don`t have time to fill data, we will get the data like '[]', so we gonna get crash

                    return null;
                }
                setGetForWeek = response.body();
                postForWeek = setGetForWeek.get(0);
                return setGetForWeek;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("myLogs", e.getMessage());
        }
        return null;

    }

}
