package com.example.imprimirrepos2;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.widget.TextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.List;

class Repositorio {
    public final String name;
    public final String description;
    public final Integer stargazers_count;

    public Repositorio(String name, String description, int stargazers_count) {
        this.name = name;
        this.description = description;
        this.stargazers_count = stargazers_count;
    }
}
interface GitHub {
    @GET("/users/{owner}/repos")
    Call<List<Repositorio>> Repositorios(@Path("owner") String owner);
}
public class MainActivity extends AppCompatActivity {
    public static final String API_URL = "https://api.github.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textView = findViewById(R.id.Repositories_text);

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();


        GitHub github = retrofit.create(GitHub.class);

        Call<List<Repositorio>> call = github.Repositorios("blaucamarasa");

        call.enqueue(new Callback<List<Repositorio>>() {
            @Override
            public void onResponse(Call<List<Repositorio>> call, Response<List<Repositorio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (Repositorio repo : response.body()) {
                        sb.append("Nombre repositorio: ").append(repo.name)
                                .append("\nDescripción: ").append(repo.description)
                                .append("\nNumero de stargazers: ").append(repo.stargazers_count)
                                .append("\n\n");
                    }
                    textView.setText(sb.toString());

                } else {
                    Log.e("API Error", "Error al obtener repositorios");
                }
            }
            public void onFailure(Call<List<Repositorio>> call, Throwable t) {
                Log.e("API Error", "Error en la llamada: " + t.getMessage());
            }
        });
    }
}