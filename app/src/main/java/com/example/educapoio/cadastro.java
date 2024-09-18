package com.example.educapoio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.educapoio.databinding.ActivityCadastroBinding;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class cadastro extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityCadastroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();  // Inicializa o Firestore
        binding.btnCriarConta.setOnClickListener(v -> validaDados());

        Button btnVoltarLogin = findViewById(R.id.btnVoltarLogin);
        btnVoltarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(cadastro.this, login.class));
                finish();
            }
        });
    }

    private void validaDados() {
        String nome = binding.editNome.getText().toString().trim();
        String email = binding.editEmail.getText().toString().trim();
        String senha = binding.editSenha.getText().toString().trim();
        String telefone = binding.editTelefone.getText().toString().trim();
        String curso = binding.editCurso.getText().toString().trim();

        // Captura o valor selecionado no RadioGroup
        int selectedId = binding.radioGroup.getCheckedRadioButtonId();
        String tipoUsuario = "";
        if (selectedId == R.id.radioButton1) {
            tipoUsuario = "Aluno";
        } else if (selectedId == R.id.radioButton2) {
            tipoUsuario = "Professor";
        }

        if (!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty() && !telefone.isEmpty() && !curso.isEmpty() && !tipoUsuario.isEmpty()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            criarContaFirebase(nome, email, senha, telefone, curso, tipoUsuario);
        } else {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
    }

    private void criarContaFirebase(String nome, String email, String senha, String telefone, String curso, String tipoUsuario) {
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            salvarDadosFirestore(user.getUid(), nome, email, telefone, curso, tipoUsuario);  // Inclui tipo de usuário
                        }
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Erro ao criar conta: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void salvarDadosFirestore(String userId, String nome, String email, String telefone, String curso, String tipoUsuario) {
        // Cria um mapa com os dados do usuário
        Map<String, Object> userData = new HashMap<>();
        userData.put("nome", nome);
        userData.put("email", email);
        userData.put("telefone", telefone);
        userData.put("curso", curso);
        userData.put("tipoUsuario", tipoUsuario);  // Adiciona o tipo de usuário (Aluno/Professor)

        // Salva no Firestore
        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(cadastro.this, mensagemCadastro.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Erro ao salvar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

