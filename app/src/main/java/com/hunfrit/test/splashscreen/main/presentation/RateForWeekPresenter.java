package com.hunfrit.test.splashscreen.main.presentation;

import android.os.AsyncTask;
import android.util.Log;

import com.hunfrit.test.splashscreen.Api.ApiService.ApiRateService;
import com.hunfrit.test.splashscreen.SetGet.SetGet;
import com.hunfrit.test.splashscreen.main.View.MainView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class RateForWeekPresenter extends AsyncTask<ApiRateService, Void, Boolean>{

    private MainView view;

    private List<SetGet> setGetForWeek;
    private SetGet postForWeek;

    private short checkOnFail = 0;

    private float[] resultByDate = new float[7];
    private String[] dayByDate = new String[7];

    public RateForWeekPresenter(MainView view){
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(ApiRateService... rate) {

        checkOnFail = 0;

        setGetForWeek = new ArrayList<>();

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter formatForGraph = DateTimeFormat.forPattern("dd.MM");
        DateTime now = DateTime.now();
        DateTime week = now.minusWeeks(1);
        if (getByDate(String.valueOf(format.print(now)), rate) != null) {
            for (int i = 0; i <= 6; i++) {
                now = week.plusDays(i + 1);     // i + 1 ~~~ because after minusWeek(1) we gonna get on one day earlier than necessary
                postForWeek = getByDate(String.valueOf(format.print(now)), rate).get(0);
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

    private List<SetGet> getByDate(String date, ApiRateService rate[]) {
        try {
            Response<List<SetGet>> response = rate[0].getRateByDate(date).execute();        // rate[0] ~~~ because AsyncTask use (ApiRateService... like massive). So we take first element
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
