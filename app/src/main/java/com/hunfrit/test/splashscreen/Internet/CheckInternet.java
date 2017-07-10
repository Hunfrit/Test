package com.hunfrit.test.splashscreen.Internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;

/**
 * Created by Anoli on 07.07.2017.
 */

public class CheckInternet {
    public static boolean CheckConnection(@NonNull Context context){
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() !=null;
    }
}
