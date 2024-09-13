package com.example.educapoio;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.educapoio.cadastro;
import com.example.educapoio.databinding.ActivityLoginBinding;
import com.example.educapoio.inicio;
import com.example.educapoio.recupera;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        // Verifica se o usuário está autenticado
        if (mAuth.getCurrentUser() != null) {
            // Se estiver autenticado, redirecione-o diretamente para a tela de menu
            startActivity(new Intent(this, inicio.class));
            finish(); // Finaliza a atividade de login para evitar que o usuário volte para ela pressionando o botão "voltar"
        }

        binding.textCadastro.setOnClickListener(view -> {
            startActivity(new Intent(this, cadastro.class));
        });

        binding.textRecuperaConta.setOnClickListener( v ->
                startActivity(new Intent(this, recupera.class)));

        binding.btnLogin.setOnClickListener(v -> validaDados());
    }

    private void validaDados() {
        String email = binding.editEmail.getText().toString().trim();
        String senha = binding.editSenha.getText().toString().trim();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {
                binding.progressBar.setVisibility(View.VISIBLE);
                loginFirebase(email, senha);
            } else {
                Toast.makeText(this, "Informe sua senha", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Informe seu email", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginFirebase(String email, String senha){
        mAuth.signInWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                // Limpa a pilha de atividades e inicia a tela inicial
                Intent intent = new Intent(this, inicio.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finaliza a atividade de login para evitar que o usuário volte para ela pressionando o botão "voltar"
            } else {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Ocorreu algum erro!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Impede que o usuário volte para a tela anterior pressionando o botão de voltar
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
