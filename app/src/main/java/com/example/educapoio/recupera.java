package com.example.educapoio;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.educapoio.databinding.ActivityRecuperaBinding;
import com.google.firebase.auth.FirebaseAuth;

public class recupera extends AppCompatActivity {

    private ActivityRecuperaBinding binding;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperaBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btnRecuperaConta.setOnClickListener(v -> validaDados());

        // Referência ao TextView
        TextView texto1 = findViewById(R.id.texto1);

// Configura o texto com duas partes coloridas
        String educ = "educ";
        String news = "News";

// Usa Spannable para aplicar cores diferentes nas partes do texto
        SpannableStringBuilder spannable = new SpannableStringBuilder();

// Adiciona "educ" em preto
        SpannableString educPart = new SpannableString(educ);
        educPart.setSpan(new ForegroundColorSpan(Color.BLACK), 0, educ.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.append(educPart);

// Adiciona "News" em roxo
        SpannableString newsPart = new SpannableString(news);
        newsPart.setSpan(new ForegroundColorSpan(Color.parseColor("#841FFD")), 0, news.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.append(newsPart);

// Define o texto formatado no TextView
        texto1.setText(spannable);


        Button btnVoltarLogin = findViewById(R.id.btnVoltarLogin);
        btnVoltarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ao clicar no botão, iniciar a atividade de login com animação
                Intent intent = new Intent(recupera.this, login.class);
                TransitionUtil.startActivityWithAnimation(recupera.this, intent);
                finish(); // Finalizar a atividade atual para evitar que o usuário volte para ela
            }
        });

    }

    private void validaDados() {
        String email = binding.editEmail.getText().toString().trim();

        if (!email.isEmpty()) {
            binding.progressBar.setVisibility(View.VISIBLE);

            recuperaContaFirebase(email);
        } else {
            Toast.makeText(this, "Informe seu email", Toast.LENGTH_SHORT).show();
        }
    }
    private void recuperaContaFirebase(String email){
        mAuth.sendPasswordResetEmail(
                email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Verifique o email que foi enviado para você.", Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(this, "Ocorreu algum erro!", Toast.LENGTH_SHORT).show();
            }
            binding.progressBar.setVisibility(View.GONE);

        });



    }
}