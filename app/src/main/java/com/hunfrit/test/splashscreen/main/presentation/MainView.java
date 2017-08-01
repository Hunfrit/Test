package com.hunfrit.test.splashscreen.main.presentation;

/**
 * Created by Anoli on 01.08.2017.
 */

public interface MainView {

    void resultForWeekIsSuccessful(float[] resultByDate, String[] dayByDate);

    void resultForWeekIsFailure(short fail);

    void resultForTodayIsSuccessful(float resultForToday, String date);

    void showError(boolean check, int index);

}
