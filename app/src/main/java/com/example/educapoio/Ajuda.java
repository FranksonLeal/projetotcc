package com.example.educapoio;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

public class Ajuda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajuda);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout rootLayoutInicio = findViewById(R.id.rootLayoutInicio);
        ThemeHelper.aplicarModoEscuro(this, rootLayoutInicio);

        NestedScrollView scrollView = findViewById(R.id.main);
        ThemeHelper.aplicarModoEscuro(this, scrollView);

        // Botão de voltar
        ImageView imageVoltar = findViewById(R.id.imageVoltar);
        imageVoltar.setOnClickListener(v -> finish());

        // Perguntas e respostas: abrir/fechar resposta ao clicar na pergunta
        LinearLayout layout1 = findViewById(R.id.layout_pergunta1);
        TextView resposta1 = findViewById(R.id.resposta1);
        layout1.setOnClickListener(v -> {
            resposta1.setVisibility(resposta1.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        LinearLayout layout2 = findViewById(R.id.layout_pergunta2);
        TextView resposta2 = findViewById(R.id.resposta2);
        layout2.setOnClickListener(v -> {
            resposta2.setVisibility(resposta2.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        LinearLayout layout3 = findViewById(R.id.layout_pergunta3);
        TextView resposta3 = findViewById(R.id.resposta3);
        layout3.setOnClickListener(v -> {
            resposta3.setVisibility(resposta3.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        LinearLayout layout4 = findViewById(R.id.layout_pergunta4);
        TextView resposta4 = findViewById(R.id.resposta4);
        layout4.setOnClickListener(v -> {
            resposta4.setVisibility(resposta4.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });
    }

}