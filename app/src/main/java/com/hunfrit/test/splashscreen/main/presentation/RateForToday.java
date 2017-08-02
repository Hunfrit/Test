package com.hunfrit.test.splashscreen.main.presentation;

import android.util.Log;

import com.hunfrit.test.splashscreen.Interface.Link;
import com.hunfrit.test.splashscreen.SetGet.SetGet;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hunfrit.test.splashscreen.Constants.Constants.DIALOG;
import static com.hunfrit.test.splashscreen.Constants.Constants.ServerErrorDialog;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

public class RateForToday {

    boolean check;      //checking on necessary to show dialog

    MainView view;

    public RateForToday(MainView view){
        this.view = view;
    }

    List<SetGet> setGet;
    SetGet post;

    public void getValue(Link rate){

        setGet = new ArrayList<>();

        rate.getRate().enqueue(new Callback<List<SetGet>>() {
            @Override
            public void onResponse(Call<List<SetGet>> call, Response<List<SetGet>> response) {
                check = false;
                Log.d("myLogs", "FOR TODAY: ");
                //Check responce on successful

                if (response.isSuccessful()) {
                    Log.d("TAGA", String.valueOf(response.isSuccessful()));
                    if (String.valueOf(response.body()) == "[]") {

                        // If NBU don`t have time to fill data, we will get the data like '[]', so we gonna get crash
                        check = true;

                        view.showError(check, ServerErrorDialog);

                        return;
                    }
                    setGet.addAll(response.body());
                    post = setGet.get(0);
                    view.resultForTodayIsSuccessful(post.getRate(), post.getExchangedate());
                    Log.d("myLogs", "WORK " + post.getRate());
                } else
                    check = true;
                    Log.d("TAGA", String.valueOf(response.code()));
                    switch (response.code()) {
                        case HTTP_NOT_FOUND:
                        case HTTP_UNAVAILABLE:
                        case HTTP_INTERNAL_ERROR:
                            view.showError(check, ServerErrorDialog);
                            break;
                        default:
                            view.showError(check, ServerErrorDialog);
                    }

            }

            @Override
            public void onFailure(Call<List<SetGet>> call, Throwable t) {
                check = true;
                if (t instanceof EOFException) {
                    view.showError(check, DIALOG);
                } else if (t instanceof IOException) {
                    view.showError(check, DIALOG);
                } else {
                    view.showError(check, DIALOG);
                }
                Log.d("myLogs", String.valueOf(t));

            }
        });

    }
}
