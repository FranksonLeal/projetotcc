package com.example.educapoio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educapoio.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class visualizarDados extends AppCompatActivity {

    private RecyclerView recyclerAuxilios, recyclerNoticias;

    private DadosAdapter auxilioAdapter;
    private NoticiasAdapterEditar noticiaAdapter;


    private ArrayList<Dados> listaAuxilios = new ArrayList<>();
    private ArrayList<Dados> listaNoticias = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_dados);

        recyclerAuxilios = findViewById(R.id.recyclerAuxilios);
        recyclerNoticias = findViewById(R.id.recyclerNoticias);
        recyclerNoticias.setAdapter(noticiaAdapter);

        Button btnVoltar = findViewById(R.id.btnVoltar);

        db = FirebaseFirestore.getInstance();

        // Configurar RecyclerViews
        recyclerAuxilios.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerNoticias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        auxilioAdapter = new DadosAdapter(listaAuxilios, this, "auxilios", new DadosAdapter.OnItemActionListener() {
            @Override
            public void onEditarClicked(Dados dados) {
                abrirTelaEdicao(dados, "auxilios");
            }

            @Override
            public void onExcluirClicked(Dados dados) {
                excluirDados(dados, "auxilios");
            }
        });

        noticiaAdapter = new NoticiasAdapterEditar(listaNoticias, new NoticiasAdapterEditar.OnItemClickListener() {
            @Override
            public void onEditarClick(Dados noticia) {
                atualizarNoticiaNoFirestore(noticia);
            }

            @Override
            public void onExcluirClick(Dados noticia) {
                excluirNoticiaNoFirestore(noticia);
            }
        });



        recyclerAuxilios.setAdapter(auxilioAdapter);
        recyclerNoticias.setAdapter(noticiaAdapter);

        carregarAuxilios();
        carregarNoticias();

        btnVoltar.setOnClickListener(v -> finish());
    }

    private void abrirTelaEdicao(Dados dados, String colecao) {
        // Aqui você pode abrir uma nova Activity para editar o item, enviando o objeto dados e a coleção (auxilios ou noticias)
        Intent intent = new Intent(this, EditarDados.class);
        intent.putExtra("id", dados.getId());
        intent.putExtra("titulo", dados.getTitulo());
        intent.putExtra("descricao", dados.getDescricao());
        intent.putExtra("colecao", colecao);
        startActivity(intent);
    }

    private void atualizarNoticiaNoFirestore(Dados noticia) {
        if (noticia.getId() == null || noticia.getId().isEmpty()) {
            return; // Sem ID, não tem como atualizar
        }

        Map<String, Object> dadosAtualizados = new HashMap<>();
        dadosAtualizados.put("titulo", noticia.getTitulo());
        dadosAtualizados.put("data", noticia.getData());

        db.collection("noticias").document(noticia.getId())
                .update(dadosAtualizados)
                .addOnSuccessListener(aVoid -> {
                    // Atualização feita com sucesso
                    Toast.makeText(visualizarDados.this, "Notícia atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Erro na atualização
                    Toast.makeText(visualizarDados.this, "Erro ao atualizar notícia.", Toast.LENGTH_SHORT).show();
                });
    }


    private void excluirNoticiaNoFirestore(Dados noticia) {
        if (noticia.getId() == null || noticia.getId().isEmpty()) {
            return; // Sem ID, não tem como excluir
        }

        db.collection("noticias").document(noticia.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaNoticias.remove(noticia);
                    noticiaAdapter.notifyDataSetChanged();
                    Toast.makeText(visualizarDados.this, "Notícia excluída com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(visualizarDados.this, "Erro ao excluir notícia.", Toast.LENGTH_SHORT).show();
                });
    }



    private void excluirDados(Dados dados, String colecao) {
        db.collection(colecao).document(dados.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    // Remover do array e atualizar adapter
                    if (colecao.equals("auxilios")) {
                        listaAuxilios.remove(dados);
                        auxilioAdapter.notifyDataSetChanged();
                    } else {
                        listaNoticias.remove(dados);
                        noticiaAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    // Tratar erro aqui (ex: Toast)
                });
    }

    private void carregarAuxilios() {
        db.collection("auxilios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaAuxilios.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Dados dados = document.toObject(Dados.class);
                    dados.setId(document.getId());
                    listaAuxilios.add(dados);
                }
                auxilioAdapter.notifyDataSetChanged();
            }
        });
    }

    private void carregarNoticias() {
        db.collection("noticias").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaNoticias.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Dados dados = document.toObject(Dados.class);
                    dados.setId(document.getId());
                    listaNoticias.add(dados);
                }
                noticiaAdapter.notifyDataSetChanged();
            }
        });
    }
}
