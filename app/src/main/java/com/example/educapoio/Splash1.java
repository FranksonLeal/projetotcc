package com.example.educapoio;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Splash1 extends AppCompatActivity {

    private static final long TEMPO_DE_ATRASO = 3000;
    private FirebaseAuth mAuth;
    private TextView progressDots; // Declare o TextView para os pontos animados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash1);

        mAuth = FirebaseAuth.getInstance();
        progressDots = findViewById(R.id.progressDots); // Referência ao TextView dos pontos
        TextView textoEducNews = findViewById(R.id.texto_educ_news);

        // Usar SpannableString para colorir "educ" de preto e "News" de roxo
        String text = "educNews";
        SpannableString spannableText = new SpannableString(text);
        spannableText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "educ" em preto
        spannableText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple_500)), 4, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "News" em roxo

        textoEducNews.setText(spannableText);

        // Animação de "slide-in" para o texto
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(textoEducNews, "translationX", -500f, 0f);
        slideIn.setDuration(1000);

        // Animação de "zoom-in" para o texto
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(textoEducNews, "scaleX", 0.5f, 1f);
        scaleUpX.setDuration(1000);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(textoEducNews, "scaleY", 0.5f, 1f);
        scaleUpY.setDuration(1000);

        // Animação de rotação
        ObjectAnimator rotate = ObjectAnimator.ofFloat(textoEducNews, "rotation", -45f, 0f);
        rotate.setDuration(1000);

        // Conjunto de animações
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(slideIn, scaleUpX, scaleUpY, rotate);
        animSet.start();

        // Iniciar a animação dos pontos
        startDotAnimation();

        // Temporizador para a próxima tela após a animação
        new Handler().postDelayed(() -> {
            Intent intent;
            if (mAuth.getCurrentUser() != null) {
                intent = new Intent(Splash1.this, inicio.class);
            } else {
                intent = new Intent(Splash1.this, splash2.class);
            }
            startActivity(intent);
            finish();
        }, TEMPO_DE_ATRASO);
    }

    // Função para iniciar a animação de pontos
    private void startDotAnimation() {
        final String[] dotAnimations = new String[]{"", ".", "..", "..."};
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int index = 0;
            @Override
            public void run() {
                // Atualiza o texto com a animação de pontos
                progressDots.setText(dotAnimations[index]);
                index = (index + 1) % dotAnimations.length; // Loop através dos pontos

                // Chama o próximo ciclo de animação
                handler.postDelayed(this, 500); // Altera a cada 500ms
            }
        };
        handler.post(runnable);
    }
}
