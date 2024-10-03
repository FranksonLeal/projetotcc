package com.example.educapoio;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class editarPerfil extends AppCompatActivity {

    private EditText editNome, editTelefone, editCurso;
    private Button btnSalvar;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        editNome = findViewById(R.id.editNome);
        editTelefone = findViewById(R.id.editTelefone);
        editCurso = findViewById(R.id.editCurso);
        btnSalvar = findViewById(R.id.btnSalvar);
        ImageView imageVoltar = findViewById(R.id.imageVoltarEditar);
        // Configurar o clique do ícone de voltar
        imageVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finaliza a Activity de configurações, retornando à Activity anterior
                finish();
            }
        });

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obter o usuário autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid(); // ID do usuário autenticado
            carregarDadosUsuario();
        } else {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
        }

        btnSalvar.setOnClickListener(v -> {
            String nome = editNome.getText().toString();
            String telefone = editTelefone.getText().toString();
            String curso = editCurso.getText().toString();

            if (!nome.isEmpty() && !telefone.isEmpty() && !curso.isEmpty()) {
                atualizarDadosUsuario(nome, telefone, curso);
            } else {
                Toast.makeText(editarPerfil.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarDadosUsuario() {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String nome = document.getString("nome");
                                String telefone = document.getString("telefone");
                                String curso = document.getString("curso");

                                // Preencher os EditTexts com os dados
                                editNome.setText(nome);
                                editTelefone.setText(telefone);
                                editCurso.setText(curso);
                            } else {
                                Toast.makeText(editarPerfil.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(editarPerfil.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void atualizarDadosUsuario(String nome, String telefone, String curso) {
        db.collection("users").document(userId)
                .update("nome", nome, "telefone", telefone, "curso", curso)
                .addOnSuccessListener(aVoid -> Toast.makeText(editarPerfil.this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(editarPerfil.this, "Erro ao atualizar dados", Toast.LENGTH_SHORT).show());
    }
}
