package com.example.educapoio.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.educapoio.ApiClient;
import com.example.educapoio.Article;
import com.example.educapoio.NewsApiService;
import com.example.educapoio.NewsResponse;
import com.example.educapoio.NoticiasAdapter;
import com.example.educapoio.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticiasCategoriaFragment extends Fragment {

    private static final String ARG_CATEGORIA = "category";
    private RecyclerView recyclerViewNoticias;
    private NoticiasAdapter noticiasAdapter;

    private NoticiasCallback callback;


    public static NoticiasCategoriaFragment newInstance(String categoria) {
        NoticiasCategoriaFragment fragment = new NoticiasCategoriaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORIA, categoria);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noticias_categoria, container, false);

        recyclerViewNoticias = rootView.findViewById(R.id.recyclerViewNoticias);
        recyclerViewNoticias.setLayoutManager(new LinearLayoutManager(getContext()));

        String categoria = getArguments() != null ? getArguments().getString(ARG_CATEGORIA) : "";

        if ("Recomendado".equalsIgnoreCase(categoria)) {
            buscarNoticiasDoFirebase();
        } else {
            // Qualquer outro valor será considerado como o nome do curso e irá buscar notícias da API
            buscarNoticiasDaAPI(categoria);
        }



        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof NoticiasCallback) {
            callback = (NoticiasCallback) getParentFragment();
        }
    }


    // Buscar notícias recomendadas no Firebase
    private void buscarNoticiasDoFirebase() {
        FirebaseFirestore.getInstance().collection("noticias")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            List<Article> artigosFirebase = new ArrayList<>();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Article artigo = doc.toObject(Article.class);
                                if (artigo != null) {
                                    artigosFirebase.add(artigo);
                                }
                            }
                            noticiasAdapter = new NoticiasAdapter(artigosFirebase);
                            recyclerViewNoticias.setAdapter(noticiasAdapter);

                            // Mostrar Snackbar de sucesso
                            Snackbar snackbar = Snackbar.make(recyclerViewNoticias, "Notícias carregadas", Snackbar.LENGTH_SHORT);
                            View snackbarView = snackbar.getView();
                            snackbarView.setBackgroundColor(Color.parseColor("#C1A9FF"));  // Roxo mais claro

                            int snackbarTextId = getResources().getIdentifier("snackbar_text", "id", "com.google.android.material");
                            TextView textView = snackbarView.findViewById(snackbarTextId);
                            if (textView != null) {
                                textView.setTextColor(Color.WHITE);
                            }

                            snackbar.show();

                        } else {
                            Snackbar.make(recyclerViewNoticias, "Nenhuma notícia recomendada encontrada", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(recyclerViewNoticias, "Erro ao buscar notícias recomendadas", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }


    // Buscar notícias da API com base em uma query (curso)
    private void buscarNoticiasDaAPI(String curso) {
        if (callback != null) callback.onNoticiasCarregando();

        NewsApiService apiService = ApiClient.getClient().create(NewsApiService.class);
        Call<NewsResponse> call = apiService.searchNews(curso, "pt");


        call.enqueue(new Callback<NewsResponse>() {

            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (callback != null) callback.onNoticiasCarregadas();

                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    noticiasAdapter = new NoticiasAdapter(articles);
                    recyclerViewNoticias.setAdapter(noticiasAdapter);

                    // Mostrar Snackbar após o carregamento com cor roxa clara e texto branco
                    Snackbar snackbar = Snackbar.make(recyclerViewNoticias, "Notícias carregadas", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#C1A9FF"));  // Roxo mais claro

                    // Ajusta margem inferior para empurrar Snackbar para cima (64dp)
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
                    int bottomMarginPx = (int) (64 * recyclerViewNoticias.getResources().getDisplayMetrics().density);
                    params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMarginPx);
                    snackbarView.setLayoutParams(params);

                    // Pega o id do texto do Snackbar dinamicamente para evitar erro R
                    int snackbarTextId = recyclerViewNoticias.getResources().getIdentifier("snackbar_text", "id", "com.google.android.material");
                    TextView textView = snackbarView.findViewById(snackbarTextId);
                    if (textView != null) {
                        textView.setTextColor(Color.WHITE);
                    }

                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(recyclerViewNoticias, "Erro ao carregar notícias", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }





            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                if (callback != null) callback.onNoticiasCarregadas();
                Toast.makeText(getContext(), "Erro na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }




    public interface NoticiasCallback {
        void onNoticiasCarregando();
        void onNoticiasCarregadas();
    }

}
