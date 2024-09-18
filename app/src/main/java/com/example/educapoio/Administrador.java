package com.example.educapoio;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.educapoio.databinding.ActivityAdministradorBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Administrador extends AppCompatActivity {
    private ActivityAdministradorBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.btnCriarConta.setOnClickListener(v -> {
            String titulo = binding.editNome.getText().toString().trim();
            String dataInicio = binding.editEmail.getText().toString().trim();
            String dataFim = binding.editSenha.getText().toString().trim();
            // Adicionar lógica para upload de imagem, se necessário
            // Exemplo: String imagemUrl = uploadImagem();

            if (!titulo.isEmpty() && !dataInicio.isEmpty() && !dataFim.isEmpty()) {
                adicionarAuxilio(titulo, dataInicio, dataFim);
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnVoltarLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private void adicionarAuxilio(String titulo, String dataInicio, String dataFim) {
        // Referência para a coleção 'auxilios' no Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = db.collection("auxilios").document().getId();

        // Dados para adicionar
        Map<String, Object> auxilio = new HashMap<>();
        auxilio.put("titulo", titulo);
        auxilio.put("dataInicio", dataInicio);
        auxilio.put("dataFim", dataFim);
        // Adicionar imagem se necessário

        // Adiciona o documento no Firestore
        db.collection("auxilios").document(id).set(auxilio)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Administrador.this, "Auxílio cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Administrador.this, "Erro ao cadastrar auxílio", Toast.LENGTH_SHORT).show();
                });
    }
}
