package com.hunfrit.test.splashscreen;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hunfrit.test.R;
import com.hunfrit.test.splashscreen.Interface.Link;
import com.hunfrit.test.splashscreen.Internet.CheckInternet;
import com.hunfrit.test.splashscreen.SetGet.SetGet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends FragmentActivity {

    static final String URL = "https://bank.gov.ua/";

    final int DIALOG = 1337;
    Dialog dialog;

    public Float result;
    public TextView value, today, text;
    public ProgressBar progressBar;

    List<SetGet> setGet;
    SetGet post;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        value = (TextView) findViewById(R.id.value);
        today = (TextView) findViewById(R.id.today);
        text = (TextView) findViewById(R.id.text);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if(CheckInternet.CheckConnection(MainActivity.this)){

            //Check Internet state

            GetValue();   //Get values from site and set text in field

        }else{
            showDialog(DIALOG);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckInternet.CheckConnection(MainActivity.this)){
                    Toast.makeText(getApplicationContext(), "Refresh", Toast.LENGTH_SHORT).show();
                    hideElements();
                    GetValue();
                }else{
                    showDialog(DIALOG);
                }

            }
        });
    }


    public void GetValue(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        Link rate = retrofit.create(Link.class);
        setGet = new ArrayList<>();


        rate.getRate().enqueue(new Callback<List<SetGet>>() {

            @Override
            public void onResponse(retrofit2.Call<List<SetGet>> call, Response<List<SetGet>> response) {

                //Check responce on successful

                if (response.isSuccessful()){
                    setGet.addAll(response.body());
                    post = setGet.get(0);
                    result = post.getRate();
                    today.setText("Date: " + post.getExchangedate());
                    value.setText(String.valueOf(result));
                    showElements();
                    Log.d("myLogs" , "WORK " + result);
                } else
                    Log.d("myLogs" , "double GG");
            }

            @Override
            public void onFailure(retrofit2.Call<List<SetGet>> call, Throwable t) {

                Log.d("myLogs", String.valueOf(t));
            }
        });
    }

    public void showElements(){
        progressBar.setVisibility(View.INVISIBLE);
        value.setVisibility(View.VISIBLE);
        today.setVisibility(View.VISIBLE);
        text.setVisibility(View.VISIBLE);
    }

    public void hideElements(){
        value.setVisibility(View.INVISIBLE);
        today.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Trouble with Internet");
            adb.setMessage("Please, check your network connection");
            adb.setPositiveButton("OK", null);
            dialog = adb.create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                }
            });
        }
        return dialog;
    }
}
