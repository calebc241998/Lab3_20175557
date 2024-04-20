package com.example.lab3_20175557;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.lab3_20175557.services.OmdbApiService;
import com.example.lab3_20175557.dto.pelicula;

public class MainActivity extends AppCompatActivity {


    private EditText editTextMovieId;

    // Instancia de Retrofit para llamar a la API de OMDb
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // Tu servicio de Retrofit para la API de OMDb
    private final OmdbApiService omdbApiService = retrofit.create(OmdbApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        checkInternetConnection();
        Toast.makeText(this, "Estás en la pantalla principal", Toast.LENGTH_SHORT).show();

        Button btnVisualizar = findViewById(R.id.buttonVisualizar);
        btnVisualizar.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, contador_primos.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Inicializa tus Views
        editTextMovieId = findViewById(R.id.idPelicula);
        Button btnBuscar = findViewById(R.id.button_buscar_pelicula);

        // Establecer el click listener para el botón buscar
        btnBuscar.setOnClickListener(view -> {
            String movieId = editTextMovieId.getText().toString();
            buscarPelicula(movieId);
        });

    }
    private void buscarPelicula(String imdbId) {
        if (imdbId.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce un ID de película.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aquí se realiza la llamada a la API usando Retrofit
        omdbApiService.getMovieDetails(imdbId, OmdbApiService.API_KEY).enqueue(new Callback<pelicula>() {
            @Override
            public void onResponse(@NonNull Call<pelicula> call,@NonNull Response<pelicula> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pelicula pelicula = response.body();
                    // Aquí manejarías la película recibida, mostrándola en la UI
                    // Inicia MovieDetailsActivity y pasa los detalles de la película
                    Intent intent = new Intent(MainActivity.this, mostrador_peliculas.class);
                    intent.putExtra("EXTRA_MOVIE_DETAILS", pelicula); // Asegúrate de que pelicula implementa Serializable o Parcelable
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Código incorrecto o película no encontrada", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<pelicula> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error en la conexión o al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            Toast.makeText(this, "Conexión a Internet disponible", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_LONG).show();
        }
    }

}