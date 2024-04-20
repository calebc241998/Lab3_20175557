package com.example.lab3_20175557;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3_20175557.dto.primos;
import com.example.lab3_20175557.services.Typicodeservice;
import com.example.lab3_20175557.databinding.ActivityMainBinding;

import java.util.concurrent.ExecutorService;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class contador_primos extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Typicodeservice typicodeService;
    private ExecutorService executorService;
    private Handler handler = new Handler(Looper.getMainLooper());

    private boolean isCountingUp = true; // Comienza ascendiendo por defecto
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toast.makeText(this, "Estás en el contador de números primos", Toast.LENGTH_SHORT).show();

        setContentView(R.layout.contador_numeros);

        Button descenderButton = findViewById(R.id.descender_button);
        descenderButton.setOnClickListener(v -> toggleCountingDirection());

        Button pausarButton = findViewById(R.id.pausar_button);
        pausarButton.setOnClickListener(v -> togglePauseAndResume());

        EditText editTextBuscarPrimo = findViewById(R.id.buscar_primo);
        Button botonBuscar = findViewById(R.id.button_buscar_primo); // Asegúrate de que este es el ID correcto

        // Define el comportamiento del botón Buscar
        botonBuscar.setOnClickListener(v -> {
            String ordenTexto = editTextBuscarPrimo.getText().toString();
            if (!ordenTexto.isEmpty()) {
                int orden = Integer.parseInt(ordenTexto);
                if (orden >= 1 && orden <= 999) {
                    currentIndex = orden - 1; // Restar 1 porque los índices del array son base 0
                    isCountingUp = true; // El contador asciende
                    updateCounterState(); // Actualiza el estado del contador y la UI
                    handler.removeCallbacks(updateRunnable); // Eliminar cualquier callback existente
                    handler.post(updateRunnable); // Continuar con el contador
                } else {
                    Toast.makeText(this, "Por favor, introduce un número entre 1 y 999.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // Utilizando Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://prime-number-api.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        typicodeService = retrofit.create(Typicodeservice.class);

        // Usando ExecutorService
        ApplicationThreads application = (ApplicationThreads) getApplication();
        executorService = application.executorService;

        // Llamada a la API para obtener números primos
        if (isConnectedToInternet()) {
            fetchPrimes();
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
        }
        resetAndStartCounting();
    }

    private int currentIndex = 0; // Un índice para rastrear el número primo actual en el array
    private primos[] primosArray; // Array para almacenar los números primos
    private final long PRIME_NUMBER_DISPLAY_INTERVAL = 1000; // Intervalo de tiempo entre mostrar números en milisegundos

    private void fetchPrimes() {
        typicodeService.getPrimos().enqueue(new Callback<primos[]>() {
            @Override
            public void onResponse(Call<primos[]> call, Response<primos[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    primosArray = response.body();
                    currentIndex = isCountingUp ? 0 : primosArray.length - 1; // Comenzar al principio o al final basado en la dirección
                    handler.post(updateRunnable); // Comenzar a mostrar números primos
                }
            }

            @Override
            public void onFailure(Call<primos[]> call, Throwable t) {
                // Manejo del error
            }
        });

    }

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            // Asegúrate de que primosArray no sea null y que currentIndex esté en el rango correcto
            if (primosArray != null && currentIndex >= 0 && currentIndex < primosArray.length) {
                updatePrimeNumberInView(primosArray[currentIndex].getNumber());
                // Ajustar el currentIndex para el siguiente número primo, asegurándote de que no se salga de los límites
                currentIndex += isCountingUp ? 1 : -1;
                if (currentIndex >= primosArray.length || currentIndex < 0) {
                    // Aquí puedes elegir si deseas reiniciar el contador o cambiar de dirección
                    currentIndex = isCountingUp ? 0 : primosArray.length - 1;
                }
            } else {
                // Manejar el caso donde primosArray es null o currentIndex está fuera de los límites
                // Por ejemplo, puedes detener el Runnable o intentar recargar los números primos
                handler.removeCallbacks(this);
            }
            // Programar la siguiente actualización solo si no estamos pausados
            if (!isPaused) {
                handler.postDelayed(this, PRIME_NUMBER_DISPLAY_INTERVAL);
            }
        }
    };

    private void updatePrimeNumberInView(int primeNumber) {
        TextView primoResultado = findViewById(R.id.primo_resultado);
        primoResultado.setText(String.valueOf(primeNumber));
    }

    private void toggleCountingDirection() {
        isCountingUp = !isCountingUp; // Cambia la dirección del conteo
        updateCounterState(); // Actualiza el estado del contador y la UI
    }

    private void togglePauseAndResume() {
        isPaused = !isPaused;
        updateCounterState(); // Actualiza el estado del contador y la UI
    }

    private void resetAndStartCounting() {
        currentIndex = 0; // Reiniciar el índice
        isCountingUp = true; // Resetear a ascender
        isPaused = false;
        updateCounterState(); // Actualiza el estado del contador y la UI
        handler.post(updateRunnable); // Iniciar el contador
    }



    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(updateRunnable); // Cancelar el Runnable cuando la actividad se detiene
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable); // Cancelar el Runnable cuando la actividad se destruye
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void updateCounterState() {
        Button descenderButton = findViewById(R.id.descender_button);
        Button pausarButton = findViewById(R.id.pausar_button);
        TextView mensajeTextView = findViewById(R.id.mensaje);

        if (isPaused) {
            pausarButton.setText("Reiniciar");
            mensajeTextView.setText("Actualmente el contador está pausado");
            handler.removeCallbacks(updateRunnable); // Detiene el contador
        } else {
            pausarButton.setText("Pausar");
            descenderButton.setText(isCountingUp ? "Descender" : "Ascender");
            mensajeTextView.setText(isCountingUp ? "Actualmente el contador está ascendiendo" : "Actualmente el contador está descendiendo");
            handler.post(updateRunnable); // Reanuda el contador
        }
    }


}


//Button button_olvido = findViewById(R.id.buttonOlvidoContraseña);
//button_olvido.setOnClickListener(view -> {

//    Intent intent = new Intent(inicio_sesion.this, olvido_contrasena.class);
//  startActivity(intent);
//});