package com.example.educapoio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Splash1 extends AppCompatActivity {

    private static final long TEMPO_DE_ATRASO = 3000;
    private FirebaseAuth mAuth;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash1);

        mAuth = FirebaseAuth.getInstance();
        logo = findViewById(R.id.logo);

        // Iniciar a animação de pulsação
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
        logo.startAnimation(pulseAnimation);

        // Navegar para a próxima tela após o atraso
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(Splash1.this, inicio.class);
                    TransitionUtil.startActivityWithAnimation(Splash1.this, intent);
                } else {
                    Intent intent = new Intent(Splash1.this, splash2.class);
                    TransitionUtil.startActivityWithAnimation(Splash1.this, intent);
                }
                finish();
            }
        }, TEMPO_DE_ATRASO);
    }
}
