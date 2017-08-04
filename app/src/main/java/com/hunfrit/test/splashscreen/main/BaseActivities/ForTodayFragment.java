package com.hunfrit.test.splashscreen.main.BaseActivities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunfrit.test.R;

public class ForTodayFragment extends Fragment implements MainActivity.MainActivityCommunicator {

    TextView value, today, text;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_for_today, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        value = (TextView) view.findViewById(R.id.value);
        today = (TextView) view.findViewById(R.id.today);
        text = (TextView) view.findViewById(R.id.text);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }

    public ForTodayFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showElements(){
        progressBar.setVisibility(View.GONE);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Context context = getActivity();
        ((MainActivity) context).mSendToFragment = this;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Context context = getActivity();
        ((MainActivity) context).mSendToFragment = this;
    }

    @Override
    public void valueChanged(float res, String string) {
        value.setText(String.valueOf(res));
        today.setText(string);
        showElements();
    }

    @Override
    public void checkOnHide(short check) {
        if (check == 1){
            hideElements();
        }
    }
}
