package com.example.educapoio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class editarPerfil extends AppCompatActivity {

    private EditText editEmail;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        editEmail = findViewById(R.id.editEmail2);
        btnSalvar = findViewById(R.id.btnSalvar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Define o email atual do usuário no campo de edição
            editEmail.setText(user.getEmail());
        }

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtém o novo email inserido pelo usuário
                String novoEmail = editEmail.getText().toString().trim();

                // Verifica se o campo de email não está vazio
                if (!novoEmail.isEmpty()) {
                    // Atualiza o email do usuário no Firebase Authentication
                    user.updateEmail(novoEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Email atualizado com sucesso
                                        Toast.makeText(editarPerfil.this, "Email atualizado com sucesso", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Falha ao atualizar o email
                                        Toast.makeText(editarPerfil.this, "Falha ao atualizar o email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Caso o campo de email esteja vazio
                    Toast.makeText(editarPerfil.this, "Por favor, insira um novo email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
