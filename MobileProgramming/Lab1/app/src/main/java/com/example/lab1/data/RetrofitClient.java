package com.example.lab1.data;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://www.cbr.ru/";
    private static Retrofit retrofit = null;

    public static GoldApiService getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
        }
        return retrofit.create(GoldApiService.class);
    }
}