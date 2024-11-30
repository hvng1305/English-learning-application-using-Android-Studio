package com.example.appeng;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static final String BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/";
    private static Retrofit retrofit;

    // Phương thức để khởi tạo Retrofit instance
    private static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Tạo một instance của DictionaryApi từ Retrofit
    public static DictionaryApi getDictionaryApi() {
        return getInstance().create(DictionaryApi.class);
    }
}

