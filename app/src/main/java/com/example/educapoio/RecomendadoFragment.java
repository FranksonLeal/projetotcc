package com.example.educapoio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecomendadoFragment extends Fragment {

    private RecyclerView recyclerView;
    private recomendadoAdapter adapter;
    private List<recomendado> noticiaList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recomendado, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewRecomendado);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new recomendadoAdapter(noticiaList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        carregarNoticias();

        return view;
    }

    private void carregarNoticias() {
        db.collection("noticias")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        noticiaList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            recomendado noticia = doc.toObject(recomendado.class);
                            noticia.setId(doc.getId());
                            noticiaList.add(noticia);
                            Log.d("RecomendadoFragment", "Notícia carregada: " + noticia.getTitulo() + " - " + noticia.getDataPublicacao());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("RecomendadoFragment", "Erro ao carregar notícias", task.getException());
                    }
                });
    }


}
