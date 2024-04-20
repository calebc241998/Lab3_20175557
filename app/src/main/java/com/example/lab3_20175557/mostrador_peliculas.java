package com.example.lab3_20175557;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab3_20175557.dto.pelicula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class mostrador_peliculas extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ArrayList<pelicula> peliculasList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrador_peliculas);
        Toast.makeText(this, "Estás en el visualizador de películas", Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.recycler_view_movies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pelicula movieDetails = (pelicula) getIntent().getSerializableExtra("EXTRA_MOVIE_DETAILS");
        if (movieDetails != null) {
            movieAdapter = new MovieAdapter(this, Collections.singletonList(movieDetails));
            recyclerView.setAdapter(movieAdapter);
        }

    }

    public void updateMovieList(List<pelicula> newMovies) {
        peliculasList.clear();
        peliculasList.addAll(newMovies);
        movieAdapter.notifyDataSetChanged();
    }
}
