package com.hunfrit.test.splashscreen.Interface;

import com.hunfrit.test.splashscreen.SetGet.SetGet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Link {
    @GET("NBUStatService/v1/statdirectory/exchange?valcode=USD&json")
    Call<List<SetGet>> getRate();

    @GET("NBUStatService/v1/statdirectory/exchange?valcode=USD&json")
    Call<List<SetGet>> getRateByDate(@Query("date") String date);
    /**
     * For dynamic query parameters use @Query.
     * FINAL URL ~~~ NBUStatService/v1/statdirectory/exchange?date={YOUR DATE WILL BE PLACED HERE FROM MAIN ACTIVITY}&valcode=USD&json
     */
}
