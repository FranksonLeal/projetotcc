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

        // Agendar o Worker para rodar diariamente
//        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(PrazoAuxilioWorker.class, 1, TimeUnit.DAYS)
//                .build();
//
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//                "verificar_prazos_auxilio",
//                ExistingPeriodicWorkPolicy.KEEP, // Mantém o trabalho já existente
//                workRequest
//        );

        // Agendando o Worker para executar a cada 2 minutos
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(PrazoAuxilioWorker.class, 2, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);

        Log.d("WorkManager", "Worker agendado para cada 2 minutos");

        TextView textCadastro = findViewById(R.id.textCadastro);
        String text = "Cadastrar";
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textCadastro.setText(content);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewAuxilios);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        carregarAuxilios();
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
                        // Passar o listener para o Adapter
                        adapter = new AuxilioAdapter(auxilios, this::abrirUrl);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(MainActivity.this, "Erro ao carregar auxílios.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void abrirUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        // Implementar a lógica para abrir a URL aqui
    }
}
