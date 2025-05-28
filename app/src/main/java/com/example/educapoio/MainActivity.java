package com.example.educapoio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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

import com.example.educapoio.fragments.perfilFragment;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AuxilioAdapter adapter;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Recupera o estado salvo antes de definir o layout
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

        // Adiciona o perfilFragment na MainActivity
        if (savedInstanceState == null) {
            Fragment perfilFragment = new perfilFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, perfilFragment) // Certifique-se de que o container existe
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

        // Configurando o ViewPager e o adapter
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this); // Certifique-se de passar o contexto correto
        viewPager.setAdapter(sectionsPagerAdapter);

        // Configurando o TabLayout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Definir o layout customizado para cada aba
            tab.setCustomView(R.layout.tab_item);  // Certifique-se de que tab_item.xml é usado como layout

            Button tabButton = tab.getCustomView().findViewById(R.id.tab_button);
            if (position == 0) {
                tabButton.setText("Abertos");
            } else if (position == 1) {
                tabButton.setText("Fechados");
            }
        }).attach();


    }

    private void iniciarNotificacoesPeriodicas() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(com.example.educapoio.PrazoAuxilioWorker.class, 1, TimeUnit.HOURS)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("notifications_list", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
        Log.d("MainActivity", "Worker de notificações agendado.");
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
                        adapter = new AuxilioAdapter(auxilios, new AuxilioAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(String url) {
                                abrirUrl(url);
                            }

                            @Override
                            public void onShareClick(String textoParaCompartilhar) {
                                // Implementar ação de compartilhar, por exemplo:
                                compartilharTexto(textoParaCompartilhar);
                            }
                        });

                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(MainActivity.this, "Erro ao carregar auxílios: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void compartilharTexto(String texto) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(shareIntent, "Compartilhar oportunidade"));
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
