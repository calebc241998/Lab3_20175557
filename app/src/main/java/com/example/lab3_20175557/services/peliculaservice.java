package com.example.lab3_20175557.services;

import retrofit2.Call;
import retrofit2.http.GET;
import com.example.lab3_20175557.dto.peliculadto;

public interface peliculaservice {
    @GET()
    Call<peliculadto> obtenerPelicula();

}
