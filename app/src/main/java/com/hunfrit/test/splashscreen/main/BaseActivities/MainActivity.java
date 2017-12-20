package com.hunfrit.test.splashscreen.main.BaseActivities;


import android.app.Dialog;
import android.content.DialogInterface;
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
import com.hunfrit.test.splashscreen.Api.Retrofit.ApiRetrofit;
import com.hunfrit.test.splashscreen.main.View.MainView;
import com.hunfrit.test.splashscreen.main.presentation.RateForTodayPresenter;
import com.hunfrit.test.splashscreen.main.presentation.RateForWeekPresenter;

import java.util.ArrayList;
import java.util.List;

import static com.hunfrit.test.splashscreen.Constants.Constants.DIALOG;
import static com.hunfrit.test.splashscreen.Constants.Constants.SERVER_ERROR_DIALOG;


public class MainActivity extends AppCompatActivity implements MainView{

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RateForWeekPresenter mRateForWeekPresenter;
    private RateForTodayPresenter mRateForTodayPresenter;

    private ApiRetrofit mApi;

    MainActivityCommunicator mSendToFragment;
    MainActivityCommunicatorForWeek mSendToFragmentForWeek;

    private Dialog dialog;

    private short checkOnHide = 0;

    @Override
    public void resultForWeekIsSuccessful(float[] resultByDate, String[] dayByDate) {
        mSendToFragmentForWeek.valueChanged(resultByDate, dayByDate);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void resultForWeekIsFailure(short fail) {
        mSendToFragmentForWeek.checkOnFail(fail);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void resultForTodayIsSuccessful(float resultForToday, String date) {
        mSendToFragment.valueChanged(resultForToday, date);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(boolean check, int index) {
        if (check){
            showDialog(index);
            Log.d("TAGA", "is it work?");
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    interface MainActivityCommunicator {
        void valueChanged(float res, String string);

        void checkOnHide(short check);
    }

    interface MainActivityCommunicatorForWeek {
        void valueChanged(float result[], String dates[]);

        void checkOnFail(short checkOnFail);

        void checkOnHide(short check);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mRateForTodayPresenter = new RateForTodayPresenter(MainActivity.this);
        mRateForWeekPresenter = new RateForWeekPresenter(MainActivity.this);

        Toast.makeText(getApplicationContext(), R.string.refreshOnTab, Toast.LENGTH_LONG).show();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSendToFragment.checkOnHide(checkOnHide = 1);
                mRateForTodayPresenter.getValue(mApi.getRetrofit());


                mSendToFragmentForWeek.checkOnHide(checkOnHide = 1);
                mRateForWeekPresenter = new RateForWeekPresenter(MainActivity.this);
                mRateForWeekPresenter.execute(mApi.getRetrofit());

            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new ForTodayFragment(), "TODAY");
        adapter.addFragment(new ForWeekFragment(), "1 WEEK");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mSendToFragment.checkOnHide(checkOnHide = 1);       //SET (INTERFACE) checkOnHide for fragment for today - true
                mRateForTodayPresenter.getValue(mApi.getRetrofit());

                mSendToFragmentForWeek.checkOnHide(checkOnHide = 1);        //SET (INTERFACE) checkOnHide for fragment for week - true
                mRateForWeekPresenter = new RateForWeekPresenter(MainActivity.this);
                mRateForWeekPresenter.execute(mApi.getRetrofit());
            }
        });

        mApi = new ApiRetrofit();

        mRateForTodayPresenter.getValue(mApi.getRetrofit());
        mRateForWeekPresenter = new RateForWeekPresenter(MainActivity.this);
        mRateForWeekPresenter.execute(mApi.getRetrofit());
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Trouble with Internet");
            adb.setMessage("Please, check your network connection");
            adb.setPositiveButton("OK", null);
            dialog = adb.create();
        } else if (id == SERVER_ERROR_DIALOG) {
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

}

