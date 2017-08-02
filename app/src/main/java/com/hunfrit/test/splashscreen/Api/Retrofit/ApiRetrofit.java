package com.hunfrit.test.splashscreen.Api.Retrofit;

import com.hunfrit.test.splashscreen.Constants.Constants;
import com.hunfrit.test.splashscreen.Interface.Link;
import com.hunfrit.test.splashscreen.main.presentation.MainView;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Artem Shapovalov on 02.08.2017.
 */

public class ApiRetrofit {

    MainView view;

    Constants constants;

    public ApiRetrofit(MainView view){
        this.view = view;
    }

    public Link getRetrofit(){

        Link rate;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        rate = retrofit.create(Link.class);

        return rate;
    }

}