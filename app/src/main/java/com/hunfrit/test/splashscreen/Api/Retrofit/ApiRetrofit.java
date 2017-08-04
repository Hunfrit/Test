package com.hunfrit.test.splashscreen.Api.Retrofit;

import com.hunfrit.test.splashscreen.Api.ApiService.ApiRateService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hunfrit.test.splashscreen.Constants.Constants.URL;

/**
 * Created by Artem Shapovalov on 02.08.2017.
 */

public class ApiRetrofit {

    public static ApiRateService getRetrofit(){

        ApiRateService rate;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        rate = retrofit.create(ApiRateService.class);

        return rate;
    }

}