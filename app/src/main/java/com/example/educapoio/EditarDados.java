package com.example.educapoio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarDados extends AppCompatActivity {

    private EditText editTitulo, editDescricao;
    private Button btnSalvar;
    private FirebaseFirestore db;
    private String id, colecao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_dados);

        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        btnSalvar = findViewById(R.id.btnSalvar);

        db = FirebaseFirestore.getInstance();

        // Recebe dados via Intent
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        colecao = intent.getStringExtra("colecao");
        String titulo = intent.getStringExtra("titulo");
        String descricao = intent.getStringExtra("descricao");

        editTitulo.setText(titulo);
        editDescricao.setText(descricao);

        btnSalvar.setOnClickListener(v -> salvarAlteracoes());
    }

    private void salvarAlteracoes() {
        String novoTitulo = editTitulo.getText().toString().trim();
        String novaDescricao = editDescricao.getText().toString().trim();

        if (novoTitulo.isEmpty() || novaDescricao.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> dadosAtualizados = new HashMap<>();
        dadosAtualizados.put("titulo", novoTitulo);
        dadosAtualizados.put("descricao", novaDescricao);

        db.collection(colecao).document(id)
                .update(dadosAtualizados)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // Volta para visualizarDados
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao atualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
