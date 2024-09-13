package com.example.educapoio;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class mensagemCadastro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagem_cadastro);

        Button btnVoltarLogin = findViewById(R.id.btnVoltarLogin);
        btnVoltarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para a tela de login
                Intent intent = new Intent(mensagemCadastro.this, login.class);
                startActivity(intent);
                finish(); // Finaliza a atividade atual para evitar que o usuário volte para ela pressionando o botão "voltar"
            }
        });
    }
}
