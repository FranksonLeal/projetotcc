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

public class editar_noticia extends AppCompatActivity {

    private EditText editTitulo, editData;
    private Button btnSalvar;

    private FirebaseFirestore db;
    private String noticiaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_noticia);

        editTitulo = findViewById(R.id.editTitulo);
        editData = findViewById(R.id.editData);
        btnSalvar = findViewById(R.id.btnSalvar);

        db = FirebaseFirestore.getInstance();

        // Recebe os dados da notícia
        Intent intent = getIntent();
        noticiaId = intent.getStringExtra("id");
        String titulo = intent.getStringExtra("titulo");
        String data = intent.getStringExtra("data");

        // Preenche os campos com os dados atuais
        editTitulo.setText(titulo);
        editData.setText(data);

        btnSalvar.setOnClickListener(v -> salvarAlteracoes());
    }

    private void salvarAlteracoes() {
        String novoTitulo = editTitulo.getText().toString().trim();
        String novaData = editData.getText().toString().trim();

        if (novoTitulo.isEmpty() || novaData.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> dadosAtualizados = new HashMap<>();
        dadosAtualizados.put("titulo", novoTitulo);
        dadosAtualizados.put("data", novaData);

        db.collection("noticias").document(noticiaId)
                .update(dadosAtualizados)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Notícia atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha a tela de edição
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao atualizar notícia.", Toast.LENGTH_SHORT).show();
                });
    }
}
