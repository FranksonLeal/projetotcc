package com.example.educapoio;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.educapoio.databinding.ActivityLoginBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        binding.textRecuperaConta.setOnClickListener(v ->
                startActivity(new Intent(this, recupera.class)));

        binding.btnLogin.setOnClickListener(v -> validaDados());
    }

    private void validaDados() {
        String email = binding.editEmail.getText().toString().trim();
        String senha = binding.editSenha.getText().toString().trim();

        boolean hasError = false; // Para rastrear se há erros

        // Verifica o email
        if (email.isEmpty()) {
            binding.editEmail.setBackgroundResource(R.drawable.bg_edittext_error); // Define borda vermelha
            mostrarMensagem("Informe seu email");
            hasError = true;
        } else if (!isEmailValid(email)) {
            binding.editEmail.setBackgroundResource(R.drawable.bg_edittext_error); // Define borda vermelha
            mostrarMensagem("Email inválido");
            hasError = true;
        } else {
            binding.editEmail.setBackgroundResource(R.drawable.bg_edittext); // Reseta a borda
        }

        // Verifica a senha
        if (senha.isEmpty()) {
            binding.editSenha.setBackgroundResource(R.drawable.bg_edittext_error); // Define borda vermelha
            mostrarMensagem("Informe sua senha");
            hasError = true;
        } else {
            binding.editSenha.setBackgroundResource(R.drawable.bg_edittext); // Reseta a borda
        }

        // Se não houver erro, prossegue com o login
        if (!hasError) {
            binding.progressBar.setVisibility(View.VISIBLE);
            loginFirebase(email, senha);
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void loginFirebase(String email, String senha) {
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && "admin@gmail.com".equals(user.getEmail())) {
                            // Se for o admin, redirecione para a tela de administração
                            Intent intent = new Intent(this, Administrador.class); // Alterado para AdminActivity
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            // Se não for o admin, redirecione para a tela inicial comum
                            Intent intent = new Intent(this, inicio.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        finish(); // Finaliza a atividade de login para evitar que o usuário volte para ela pressionando o botão "voltar"
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        mostrarMensagem("Ocorreu algum erro!");
                    }
                });
    }

    private void mostrarMensagem(String mensagem) {
        // Cria o BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Infla um layout simples programaticamente
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(90, 90, 90, 90); // Ajuste o padding conforme necessário

        // Cria um GradientDrawable para bordas arredondadas
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(Color.WHITE); // Cor de fundo
        backgroundDrawable.setCornerRadius(20f); // Raio dos cantos
        layout.setBackground(backgroundDrawable); // Aplica o fundo ao layout

        // Adiciona o texto da mensagem
        TextView messageText = new TextView(this);
        messageText.setText(mensagem);
        messageText.setTextSize(18); // Tamanho do texto
        messageText.setTextColor(Color.BLACK); // Cor do texto
        messageText.setPadding(0, 0, 0, 80); // Padding inferior

        // Adiciona o botão de OK
        Button okButton = new Button(this);
        okButton.setText("Entendi");
        okButton.setBackgroundColor(Color.BLACK); // Cor de fundo do botão
        okButton.setTextColor(Color.WHITE); // Cor do texto do botão
        okButton.setPadding(20, 20, 20, 20); // Padding do botão

        // Ação do botão OK
        okButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Adiciona os componentes ao layout
        layout.addView(messageText);
        layout.addView(okButton);

        // Define o layout inflado no BottomSheetDialog
        bottomSheetDialog.setContentView(layout);

        // Exibe o BottomSheetDialog
        bottomSheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        // Impede que o usuário volte para a tela anterior pressionando o botão de voltar
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
