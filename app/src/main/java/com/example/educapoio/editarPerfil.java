package com.example.educapoio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class editarPerfil extends AppCompatActivity {

    private Button btnSalvar;
    private ImageView imageVoltarEditar; // Declara o ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        btnSalvar = findViewById(R.id.btnSalvar);
        imageVoltarEditar = findViewById(R.id.imageVoltarEditar); // Inicializa o ImageView

        // Lógica para o botão de voltar
        imageVoltarEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Encerra a Activity e retorna à Activity anterior
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Adicione a lógica para editar o perfil aqui, se necessário
    }
}
