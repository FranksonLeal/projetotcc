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
                // Ao clicar no botão, iniciar a atividade de login com animação
                Intent intent = new Intent(splash4.this, login.class);
                TransitionUtil.startActivityWithAnimation(splash4.this, intent);
                finish(); // Finalizar a atividade atual para evitar o retorno
            }
        });

        Button voltar = findViewById(R.id.voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ao clicar no botão, iniciar a atividade Splash3 com animação
                Intent intent = new Intent(splash4.this, splash3.class);
                TransitionUtil.startActivityWithAnimation(splash4.this, intent);
                finish(); // Finalizar a atividade atual para evitar o retorno
            }
        });
    }
}
