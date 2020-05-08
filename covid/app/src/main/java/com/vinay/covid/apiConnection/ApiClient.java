package com.vinay.covid.apiConnection;

import com.vinay.covid.model.CovidEntity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class ApiClient {
    private static String BASE_URL = "https://covid-19-data.p.rapidapi.com/";
    public static final String HEADER_KEY = "0904b15f55msh1c1552b93e3040bp12b225jsn9b2c9499dd6b";
    public static final String HEADER_HOST = "covid-19-data.p.rapidapi.com";
    private static Retrofit retrofit;

    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public  interface ApiInterface {
        @GET("country/all")
        Call<ArrayList<CovidEntity>> getAllData(@Header("x-rapidapi-key") String headerKey, @Header("x-rapidapi-host") String headerHost);

    }
}
