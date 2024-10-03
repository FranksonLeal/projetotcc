package com.example.educapoio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.threetenabp.AndroidThreeTen;
import androidx.appcompat.app.AppCompatActivity;
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
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(PrazoAuxilioWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("NotificacoesAuxilios", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);

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
                        Toast.makeText(MainActivity.this, "Erro ao carregar auxílios.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void abrirUrl(String url) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Você está prestes a abrir: " + url, Snackbar.LENGTH_LONG)
                .setAction("OK", v -> {
                    // Abre a URL quando o botão é clicado
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                });

        snackbar.show(); // Exibe o Snackbar
    }




}
