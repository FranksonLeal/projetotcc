package com.example.educapoio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class splash4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash4);
        Button continuar = findViewById(R.id.continuar);
        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ao clicar no botão, iniciar a atividade Splash3
                startActivity(new Intent(splash4.this, login.class));
                // Finalizar a atividade atual para não retornar a ela ao pressionar o botão "voltar"
            }
        });

        Button voltar = findViewById(R.id.voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ao clicar no botão, iniciar a atividade Splash3
                startActivity(new Intent(splash4.this, splash3.class));
                // Finalizar a atividade atual para não retornar a ela ao pressionar o botão "voltar"
            }
        });
    }
}