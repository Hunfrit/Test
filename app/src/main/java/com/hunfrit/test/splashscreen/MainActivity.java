package com.hunfrit.test.splashscreen;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hunfrit.test.R;
import com.hunfrit.test.splashscreen.Interface.Link;
import com.hunfrit.test.splashscreen.SetGet.SetGet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;


public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;

    MainActivityCommunicator mSendToFragment;
    MainActivityCommunicatorForWeek mSendToFragmentForWeek;

    static final String URL = "https://bank.gov.ua/";

    final int DIALOG = 1337;
    final int ServerErrorDialog = 1488;
    Dialog dialog;

    Link rate;

    getRateByDate byDate;

    public short checkOnHide = 0;
    public short checkOnFail = 0;

    public Float result;
    float[] resultByDate = new float[7];
    String[] dayByDate = new String[7];
    public String today;

    List<SetGet> setGet;
    SetGet post;
    List<SetGet> setGetForWeek;
    SetGet postForWeek;

    interface MainActivityCommunicator{
        void valueChanged(Float res,String string);
        void checkOnHide(Short check);
    }

    interface MainActivityCommunicatorForWeek{
        void valueChanged(float result[], String dates[]);
        void checkOnFail(Short checkOnFail);
        void checkOnHide(Short check);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toast.makeText(getApplicationContext(), R.string.refreshOnTab, Toast.LENGTH_LONG).show();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSendToFragment.checkOnHide(checkOnHide = 1);
                getValue();

                mSendToFragmentForWeek.checkOnHide(checkOnHide = 1);
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentForToday(), "TODAY");
        adapter.addFragment(new FragmentForWeek(), "WEEK");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mSendToFragment.checkOnHide(checkOnHide = 1);       //SET (INTERFACE) checkOnHide - true
                getValue();
            }
        });


        getValue();

        byDate = new getRateByDate();
        byDate.execute();
    }




    public void getValue() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        rate = retrofit.create(Link.class);
        setGet = new ArrayList<>();


            rate.getRate().enqueue(new Callback<List<SetGet>>() {
            @Override
            public void onResponse(retrofit2.Call<List<SetGet>> call, Response<List<SetGet>> response) {

                Log.d("myLogs", "FOR TODAY: ");
                //Check responce on successful

                if (response.isSuccessful()){
                    setGet.addAll(response.body());
                    post = setGet.get(0);
                    result = post.getRate();
                    today = post.getExchangedate();
                    checkOnHide = 0;            // SET (INTERFACE) checkOnHide - false;
                    mSendToFragment.valueChanged(result, today);
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d("myLogs" , "WORK " + result);
                } else
                    switch (response.code()){
                        case HTTP_NOT_FOUND:
                        case HTTP_UNAVAILABLE:
                        case HTTP_INTERNAL_ERROR:
                            showDialog(ServerErrorDialog);
                            swipeRefreshLayout.setRefreshing(false);
                            break;
                        default:
                            showDialog(ServerErrorDialog);
                            swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(retrofit2.Call<List<SetGet>> call, Throwable t) {
                if (t instanceof EOFException){
                    showDialog(DIALOG);
                }else if (t instanceof IOException){
                    showDialog(DIALOG);
                }else {
                    showDialog(DIALOG);
                }
                Log.d("myLogs", String.valueOf(t));
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id){

        if(id == DIALOG){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Trouble with Internet");
            adb.setMessage("Please, check your network connection");
            adb.setPositiveButton("OK", null);
            dialog = adb.create();
        }else
            if (id == ServerErrorDialog){
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Trouble with server");
                adb.setMessage("We`re sorry, but server is down. Please, try it later");
                adb.setPositiveButton("OK", null);
                dialog = adb.create();
            }

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                }
            });

        return dialog;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
            private final List<Fragment> mFragmentList = new ArrayList<>();
            private final List<String> mFragmentTitleList = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager) {
                super(manager);
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            public void addFragment(Fragment fragment, String title) {
                mFragmentList.add(fragment);
                mFragmentTitleList.add(title);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentTitleList.get(position);
            }
        }

    class getRateByDate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");
            DateTimeFormatter formatForGraph = DateTimeFormat.forPattern("dd.MM");
            DateTime now = DateTime.now();
            DateTime week = now.minusWeeks(1);
            for (int i = 0; i<=6; i++){
                now = week.plusDays(i+1);
                postForWeek = getByDate(String.valueOf(String.valueOf(format.print(now)))).get(0);
                Log.d("myLogs" , "GG" + String.valueOf(postForWeek.getRate()));
                resultByDate[i] = postForWeek.getRate();
                dayByDate[i] = formatForGraph.print(now);
            }
            checkOnHide = 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("myLogs", "WTF");
            mSendToFragmentForWeek.valueChanged(resultByDate, dayByDate);
        }
    }

    public List<SetGet> getByDate(String date){
        try {
            Response<List<SetGet>> response = rate.getRateByDate(date).execute();
            if (response.isSuccessful()){
                setGetForWeek = response.body();
                postForWeek = setGetForWeek.get(0);
                Log.d("myLogs", "WTF" + postForWeek.getTxt() + postForWeek.getExchangedate());
                return setGetForWeek;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    }

