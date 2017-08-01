package com.hunfrit.test.splashscreen;


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
import com.hunfrit.test.splashscreen.main.presentation.MainView;
import com.hunfrit.test.splashscreen.main.presentation.RateForToday;
import com.hunfrit.test.splashscreen.main.presentation.RateForWeek;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MainView{

    private SwipeRefreshLayout swipeRefreshLayout;
    private RateForWeek rateForWeek;
    private RateForToday rateForToday;

    MainActivityCommunicator mSendToFragment;
    MainActivityCommunicatorForWeek mSendToFragmentForWeek;

    final int DIALOG = 1337;
    final int ServerErrorDialog = 1488;
    Dialog dialog;

    public short checkOnHide = 0;

    @Override
    public void resultForWeekIsSuccessful(float[] resultByDate, String[] dayByDate) {
        mSendToFragmentForWeek.valueChanged(resultByDate, dayByDate);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void resultForWeekIsFailure(short fail) {
        mSendToFragmentForWeek.checkOnFail(fail);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void resultForTodayIsSuccessful(float resultForToday, String date) {
        mSendToFragment.valueChanged(resultForToday, date);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(boolean check, int index) {
        if (check){
            showDialog(index);
            Log.d("TAGA", "is it work?");
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    interface MainActivityCommunicator {
        void valueChanged(Float res, String string);

        void checkOnHide(Short check);
    }

    interface MainActivityCommunicatorForWeek {
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
                rateForToday = new RateForToday(MainActivity.this);
                rateForToday.getValue();


                mSendToFragmentForWeek.checkOnHide(checkOnHide = 1);
                rateForWeek = new RateForWeek(MainActivity.this);
                rateForWeek.execute();

            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentForToday(), "TODAY");
        adapter.addFragment(new FragmentForWeek(), "1 WEEK");
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
                mSendToFragment.checkOnHide(checkOnHide = 1);       //SET (INTERFACE) checkOnHide for fragment for today - true
                rateForToday = new RateForToday(MainActivity.this);
                rateForToday.getValue();

                mSendToFragmentForWeek.checkOnHide(checkOnHide = 1);        //SET (INTERFACE) checkOnHide for fragment for week - true
                rateForWeek = new RateForWeek(MainActivity.this);
                rateForWeek.execute();
            }
        });

        rateForToday = new RateForToday(this);
        rateForToday.getValue();

        rateForWeek = new RateForWeek(this);
        rateForWeek.execute();
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Trouble with Internet");
            adb.setMessage("Please, check your network connection");
            adb.setPositiveButton("OK", null);
            dialog = adb.create();
        } else if (id == ServerErrorDialog) {
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

