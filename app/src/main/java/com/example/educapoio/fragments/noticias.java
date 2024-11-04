package com.example.educapoio.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.educapoio.ApiClient;
import com.example.educapoio.Article;
import com.example.educapoio.NewsApiService;
import com.example.educapoio.NewsResponse;
import com.example.educapoio.NoticiasAdapter;
import com.example.educapoio.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class noticias extends Fragment {

    private RecyclerView recyclerViewNoticias;
    private NoticiasAdapter noticiasAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noticias, container, false);

        recyclerViewNoticias = rootView.findViewById(R.id.recyclerViewNoticias);
        recyclerViewNoticias.setLayoutManager(new LinearLayoutManager(getContext()));

        // Chamar método para buscar notícias da API
        buscarNoticiasDaAPI("tecnologia");

        return rootView;
    }

    private void buscarNoticiasDaAPI(String query) {
        NewsApiService service = ApiClient.getClient().create(NewsApiService.class);

        Call<NewsResponse> call = service.searchNews(query, "pt");
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    if (articles != null && !articles.isEmpty()) {
                        noticiasAdapter = new NoticiasAdapter(articles);
                        recyclerViewNoticias.setAdapter(noticiasAdapter);
                    } else {
                        Toast.makeText(getContext(), "Nenhuma notícia encontrada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Erro ao carregar notícias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
