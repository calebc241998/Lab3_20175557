package com.example.lab3_20175557.services;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.example.lab3_20175557.dto.pelicula;

public interface OmdbApiService {
    // Define tu API key aquí
    String API_KEY = "bf81d461";

    @GET("/")
    Call<pelicula> getMovieDetails(@Query("i") String imdbId, @Query("apikey") String apiKey);
}
