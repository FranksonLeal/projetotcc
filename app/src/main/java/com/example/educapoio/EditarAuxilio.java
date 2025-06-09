package com.example.educapoio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarAuxilio extends AppCompatActivity {

    private EditText editTitulo, editDataInicio, editDataFim;
    private Button btnSalvar;
    private FirebaseFirestore db;

    private String idAuxilio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_auxilio);

        editTitulo = findViewById(R.id.editTitulo);
        editDataInicio = findViewById(R.id.editDataInicio);
        editDataFim = findViewById(R.id.editDataFim);
        btnSalvar = findViewById(R.id.btnSalvar);
        db = FirebaseFirestore.getInstance();

        // Recuperar dados do Intent
        idAuxilio = getIntent().getStringExtra("id");
        String titulo = getIntent().getStringExtra("titulo");
        String dataInicio = getIntent().getStringExtra("dataInicio");
        String dataFim = getIntent().getStringExtra("dataFim");

        // Preencher campos
        editTitulo.setText(titulo);
        editDataInicio.setText(dataInicio);
        editDataFim.setText(dataFim);

        btnSalvar.setOnClickListener(v -> atualizarAuxilio());
    }

    private void atualizarAuxilio() {
        String titulo = editTitulo.getText().toString().trim();
        String dataInicio = editDataInicio.getText().toString().trim();
        String dataFim = editDataFim.getText().toString().trim();

        if (titulo.isEmpty() || dataInicio.isEmpty() || dataFim.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> dadosAtualizados = new HashMap<>();
        dadosAtualizados.put("titulo", titulo);
        dadosAtualizados.put("dataInicio", dataInicio);
        dadosAtualizados.put("dataFim", dataFim);

        db.collection("auxilios").document(idAuxilio)
                .update(dadosAtualizados)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Auxílio atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha a activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao atualizar auxílio.", Toast.LENGTH_SHORT).show();
                });
    }
}
