package com.example.educapoio;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.educapoio.R;
import com.example.educapoio.inicio;
import com.example.educapoio.splash2;
import com.google.firebase.auth.FirebaseAuth;

public class Splash1 extends AppCompatActivity {

    private static final long TEMPO_DE_ATRASO = 4000; // Tempo de atraso em milissegundos (4 segundos)
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash1);

        mAuth = FirebaseAuth.getInstance();

        // Verifica se o usuário está autenticado
        if (mAuth.getCurrentUser() != null) {
            // Se estiver autenticado, inicia a tela de menu após o atraso
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash1.this, inicio.class);
                    startActivity(intent);
                    finish(); // Finaliza esta atividade para evitar que o usuário volte para ela pressionando o botão "voltar"
                }
            }, TEMPO_DE_ATRASO);
        } else {
            // Se não estiver autenticado, inicia a Splash2 após o atraso
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash1.this, splash2.class);
                    startActivity(intent);
                    finish(); // Finaliza esta atividade para evitar que o usuário volte para ela pressionando o botão "voltar"
                }
            }, TEMPO_DE_ATRASO);
        }
    }
}
