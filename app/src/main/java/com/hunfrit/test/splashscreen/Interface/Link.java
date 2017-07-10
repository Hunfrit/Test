package com.hunfrit.test.splashscreen.Interface;

import com.hunfrit.test.splashscreen.SetGet.SetGet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Link {
    @GET("NBUStatService/v1/statdirectory/exchange?valcode=USD&json")
    Call<List<SetGet>> getRate();
}
