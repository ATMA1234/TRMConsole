package com.example.ticketingtool_library.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.ticketingtool_library.SplashActivity.PACKAGE_NAME;
import static com.example.ticketingtool_library.values.constant.PROD_APP;
import static com.example.ticketingtool_library.values.constant.PROD_URL;
import static com.example.ticketingtool_library.values.constant.TEST_APP;
import static com.example.ticketingtool_library.values.constant.TEST_URL;

public class RetroClient {
    private static String BASE_URL = PROD_URL;

    public RetroClient() {
        if (PACKAGE_NAME.equals(PROD_APP)) {
            BASE_URL = PROD_URL;
        } else if (PACKAGE_NAME.equals(TEST_APP)) {
            BASE_URL = TEST_URL;
        }
    }

    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public RegisterAPI getApiService() {
        return getRetrofitInstance().create(RegisterAPI.class);
    }
}