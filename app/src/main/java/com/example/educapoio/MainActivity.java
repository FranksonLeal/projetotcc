package com.example.educapoio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.threetenabp.AndroidThreeTen;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.educapoio.fragments.perfilFragment;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AuxilioAdapter adapter;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

        // Adiciona o perfilFragment na MainActivity
        if (savedInstanceState == null) {
            Fragment perfilFragment = new perfilFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, perfilFragment) // Certifique-se de ter um contêiner de fragmento no layout
                    .commit();
        }

        // Inicializar o Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar o RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAuxilios);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        carregarAuxilios();

        // Iniciar o Worker para notificações periódicas
        iniciarNotificacoesPeriodicas();

        TextView textCadastro = findViewById(R.id.textCadastro);
        String text = "Cadastrar";
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textCadastro.setText(content);
    }

    private void iniciarNotificacoesPeriodicas() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(com.example.educapoio.PrazoAuxilioWorker.class, 1, TimeUnit.HOURS) // Aumentado para uma hora
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("notifications_list", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
        Log.d("MainActivity", "Worker de notificações agendado."); // Log adicionado
    }


    private void carregarAuxilios() {
        db.collection("aids")
                .orderBy("titulo", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> auxilios = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            auxilios.add(document.getData());
                        }
                        adapter = new AuxilioAdapter(auxilios, this::abrirUrl);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Exibir mensagem de erro e tentar novamente, se necessário
                        Toast.makeText(MainActivity.this, "Erro ao carregar auxílios: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        // Você pode implementar uma lógica de retry aqui, se desejado
                    }
                });
    }


    private void abrirUrl(String url) {
        if (url != null && !url.isEmpty()) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Você está prestes a abrir: " + url, Snackbar.LENGTH_LONG)
                    .setAction("OK", v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    });
            snackbar.show();
        } else {
            Toast.makeText(this, "URL inválida!", Toast.LENGTH_SHORT).show();
        }
    }





}
