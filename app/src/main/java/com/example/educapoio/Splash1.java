package com.example.educapoio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Splash1 extends AppCompatActivity {

    private static final long TEMPO_DE_ATRASO = 3000;
    private FirebaseAuth mAuth;
    private ImageView logo;
    private ProgressBar progressBar; // Declare a ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash1);

        mAuth = FirebaseAuth.getInstance();
        logo = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progressBar); // Find ProgressBar

        // Iniciar a animação de pulsação
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
        logo.startAnimation(pulseAnimation);

        // Mostrar a ProgressBar
        progressBar.setVisibility(ProgressBar.VISIBLE);

        // Navegar para a próxima tela após o atraso
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {
                    // Usuário autenticado, vai para a tela inicial
                    Intent intent = new Intent(Splash1.this, inicio.class);
                    TransitionUtil.startActivityWithAnimation(Splash1.this, intent);
                } else {
                    // Usuário não autenticado, vai para a tela de splash2
                    Intent intent = new Intent(Splash1.this, splash2.class);
                    TransitionUtil.startActivityWithAnimation(Splash1.this, intent);
                }

                // Esconder a ProgressBar após a navegação
                progressBar.setVisibility(ProgressBar.GONE);

                finish(); // Finaliza a SplashScreen
            }
        }, TEMPO_DE_ATRASO); // Define o tempo de espera para a próxima tela
    }
}
