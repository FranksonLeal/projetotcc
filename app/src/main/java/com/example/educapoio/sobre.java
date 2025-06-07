package com.example.educapoio;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class sobre extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sobre);

        ConstraintLayout rootLayoutPerfil = findViewById(R.id.rootLayoutPerfil);
        ThemeHelper.aplicarModoEscuro(this, rootLayoutPerfil);

        // Botão de voltar
        ImageView imageVoltar = findViewById(R.id.imageVoltar);
        imageVoltar.setOnClickListener(v -> finish());


    }
}
