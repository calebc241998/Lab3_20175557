package com.example.lab3_20175557;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lab3_20175557.dto.pelicula;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final List<pelicula> peliculasList;
    private final LayoutInflater inflater;

    // Constructor
    public MovieAdapter(Context context, List<pelicula> peliculasList) {
        this.inflater = LayoutInflater.from(context);
        this.peliculasList = peliculasList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_pelicula, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        pelicula pelicula = peliculasList.get(position);
        holder.bind(pelicula);

        CheckBox checkBoxConfirm = holder.itemView.findViewById(R.id.item_movie_checkbox);
        Button buttonReturn = holder.itemView.findViewById(R.id.item_movie_return_button);

        buttonReturn.setEnabled(false); // Inicialmente el botón está deshabilitado

        checkBoxConfirm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonReturn.setEnabled(isChecked); // Habilita o deshabilita el botón según el checkbox
        });

        buttonReturn.setOnClickListener(v -> {
            // Intención de iniciar MainActivity
            Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Esto asegura que se limpie la pila de actividades
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return peliculasList.size();
    }

    // ViewHolder interno
    static class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewPoster;
        private final TextView textViewTitle;
        private final TextView textViewDirector;
        private final TextView textViewActors;
        private final TextView textViewReleaseDate;
        private final TextView textViewGenre;
        private final TextView textViewWriters;
        private final TextView textViewPlot;
        private final TextView textViewIMDbRating;
        private final TextView textViewRottenTomatoesRating;
        private final TextView textViewMetacriticRating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.item_movie_poster);
            textViewTitle = itemView.findViewById(R.id.item_movie_title);
            textViewDirector = itemView.findViewById(R.id.item_movie_director);
            textViewActors = itemView.findViewById(R.id.item_movie_actors);
            textViewReleaseDate = itemView.findViewById(R.id.item_movie_release_date);
            textViewGenre = itemView.findViewById(R.id.item_movie_genre);
            textViewWriters = itemView.findViewById(R.id.item_movie_writers);
            textViewPlot = itemView.findViewById(R.id.item_movie_plot);
            textViewIMDbRating = itemView.findViewById(R.id.item_movie_rating_imdb);
            textViewRottenTomatoesRating = itemView.findViewById(R.id.item_movie_rating_rotten_tomatoes);
            textViewMetacriticRating = itemView.findViewById(R.id.item_movie_rating_metacritic);


            // Inicializar más vistas aquí
        }

        public void bind(pelicula pelicula) {
            textViewTitle.setText(pelicula.getTitle());
            textViewDirector.setText("Director: " + pelicula.getDirector());
            textViewActors.setText("Actores: " + pelicula.getActors());
            textViewReleaseDate.setText("Fecha de Estreno: " + pelicula.getReleased());
            textViewGenre.setText("Género: " + pelicula.getGenre());
            textViewWriters.setText("Escritores: " + pelicula.getWriter());
            textViewPlot.setText(pelicula.getPlot());

            // Configura las valoraciones
            //StringBuilder ratingsBuilder = new StringBuilder();
            if (pelicula.getRatings() != null && !pelicula.getRatings().isEmpty()) {
                for (pelicula.RatingDTO rating : pelicula.getRatings()) {
                    String source = rating.getSource();
                    String value = rating.getValue();

                    switch (source) {
                        case "Internet Movie Database":
                            textViewIMDbRating.setText("IMDb Rating: " + value);
                            break;
                        case "Rotten Tomatoes":
                            textViewRottenTomatoesRating.setText("Rotten Tomatoes Rating: " + value);
                            break;
                        case "Metacritic":
                            textViewMetacriticRating.setText("Metacritic Rating: " + value);
                            break;
                        // Considera añadir un caso default o manejar otros posibles ratings
                    }
                }
            }
            else {
                textViewIMDbRating.setText("IMDb Rating: N/A");
                textViewRottenTomatoesRating.setText("Rotten Tomatoes Rating: N/A");
                textViewMetacriticRating.setText("Metacritic Rating: N/A");
            }
            // Usando Picasso para cargar la imagen
            if (pelicula.getPoster() != null && !pelicula.getPoster().isEmpty()) {
                Picasso.get()
                        .load(pelicula.getPoster())
                        .placeholder(R.drawable.ic_launcher_background) // puedes poner un drawable como placeholder
                        .error(R.drawable.ic_launcher_background) // imagen de error si no puede cargarla
                        .into(imageViewPoster);
            } else {
                imageViewPoster.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
}
