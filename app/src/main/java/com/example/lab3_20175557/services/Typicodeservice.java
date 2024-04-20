package com.example.lab3_20175557.services;

import com.example.lab3_20175557.dto.primos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Typicodeservice {

    @GET("primeNumbers?len=999&order=1")
    Call<primos[]> getPrimos();
}
