package com.example.lab1.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoldApiService {
    @GET("scripts/xml_metall.asp")
    Call<MetalRates> getMetalRates(
            @Query("date_req1") String dateFrom,
            @Query("date_req2") String dateTo
    );
}