package com.example.educapoio;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class configuracao extends AppCompatActivity {

    private ProgressBar progressBar;
    private Switch switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        switchDarkMode = findViewById(R.id.textView14);
        // Obter o estado atual do modo escuro
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            switchDarkMode.setChecked(true);
        } else {
            switchDarkMode.setChecked(false);
        }
        // Alterar o tema quando o switch for alterado
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });


        progressBar = findViewById(R.id.progressBar);
        ImageView imageVoltar = findViewById(R.id.imageVoltar);

        // Configurar o clique do ícone de voltar
        imageVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finaliza a Activity de configurações, retornando à Activity anterior
                finish();
            }
        });

        // Configuração do clique para compartilhar
        TextView textViewRecomendar = findViewById(R.id.textView10);
        textViewRecomendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilharRecomendacao();
            }
        });

        // Configurações e edição de perfil
        TextView textViewApagarConta = findViewById(R.id.textView15);
        textViewApagarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibirDialogoConfirmacaoExclusaoConta();
            }
        });

        TextView textViewSair = findViewById(R.id.textView16);
        textViewSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibirDialogoConfirmacaoSair();
            }
        });
    }

    private void compartilharRecomendacao() {
        String mensagem = "Confira o aplicativo EducApoio! Ele pode te ajudar a encontrar oportunidades de projetos e informações úteis. Baixe agora! 😉";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mensagem);
        startActivity(Intent.createChooser(intent, "Compartilhar com"));
    }


    private void exibirDialogoConfirmacaoExclusaoConta() {
        // Cria o BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Infla um layout simples programaticamente
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40); // Ajuste o padding conforme necessário

        // Cria um GradientDrawable para bordas arredondadas
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(Color.WHITE); // Cor de fundo
        backgroundDrawable.setCornerRadius(20f); // Raio dos cantos
        layout.setBackground(backgroundDrawable); // Aplica o fundo ao layout

        // Adiciona o texto da mensagem
        TextView messageText = new TextView(this);
        messageText.setText("Tem certeza de que deseja excluir sua conta 😢? Esta ação é irreversível.");
        messageText.setTextSize(18); // Tamanho do texto
        messageText.setTextColor(Color.BLACK); // Cor do texto
        messageText.setPadding(0, 0, 0, 40); // Padding inferior

        // Adiciona o botão de confirmação
        Button confirmButton = new Button(this);
        confirmButton.setText("Sim");
        confirmButton.setBackgroundColor(Color.BLACK); // Cor de fundo do botão
        confirmButton.setTextColor(Color.WHITE); // Cor do texto do botão
        confirmButton.setPadding(40, 20, 40, 20); // Padding do botão

        // Ação do botão de confirmação
        confirmButton.setOnClickListener(v -> {
            excluirConta(); // Chama a função para excluir a conta
            bottomSheetDialog.dismiss(); // Fecha o dialog após a ação
        });

        // Adiciona o botão de cancelamento
        Button cancelButton = new Button(this);
        cancelButton.setText("Cancelar");
        cancelButton.setBackgroundColor(Color.GRAY); // Cor de fundo do botão
        cancelButton.setTextColor(Color.WHITE); // Cor do texto do botão
        cancelButton.setPadding(40, 20, 40, 20); // Padding do botão

        // Ação do botão de cancelamento
        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss()); // Apenas fecha o dialog

        // Adiciona os componentes ao layout
        layout.addView(messageText);
        layout.addView(confirmButton);
        layout.addView(cancelButton);

        // Define o layout inflado no BottomSheetDialog
        bottomSheetDialog.setContentView(layout);

        // Exibe o BottomSheetDialog
        bottomSheetDialog.show();
    }


    private void exibirDialogoConfirmacaoSair() {
        // Cria o BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Infla um layout simples programaticamente
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40); // Ajuste o padding conforme necessário

        // Cria um GradientDrawable para bordas arredondadas
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(Color.WHITE); // Cor de fundo
        backgroundDrawable.setCornerRadius(20f); // Raio dos cantos
        layout.setBackground(backgroundDrawable); // Aplica o fundo ao layout

        // Adiciona o texto da mensagem
        TextView messageText = new TextView(this);
        messageText.setText("Tem certeza de que deseja sair? Você precisará fazer login novamente.");
        messageText.setTextSize(18); // Tamanho do texto
        messageText.setTextColor(Color.BLACK); // Cor do texto
        messageText.setPadding(0, 0, 0, 40); // Padding inferior

        // Adiciona o botão de confirmação
        Button confirmButton = new Button(this);
        confirmButton.setText("Sim");
        confirmButton.setBackgroundColor(Color.BLACK); // Cor de fundo do botão
        confirmButton.setTextColor(Color.WHITE); // Cor do texto do botão
        confirmButton.setPadding(40, 20, 40, 20); // Padding do botão

        // Ação do botão de confirmação
        confirmButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(configuracao.this, login.class));
            finish();
            bottomSheetDialog.dismiss(); // Fecha o dialog após a ação
        });

        // Adiciona o botão de cancelamento
        Button cancelButton = new Button(this);
        cancelButton.setText("Cancelar");
        cancelButton.setBackgroundColor(Color.GRAY); // Cor de fundo do botão
        cancelButton.setTextColor(Color.WHITE); // Cor do texto do botão
        cancelButton.setPadding(40, 20, 40, 20); // Padding do botão

        // Ação do botão de cancelamento
        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss()); // Apenas fecha o dialog

        // Adiciona os componentes ao layout
        layout.addView(messageText);
        layout.addView(confirmButton);
        layout.addView(cancelButton);

        // Define o layout inflado no BottomSheetDialog
        bottomSheetDialog.setContentView(layout);

        // Exibe o BottomSheetDialog
        bottomSheetDialog.show();
    }


    private void excluirConta() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(configuracao.this, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(configuracao.this, login.class));
                                finish();
                            } else {
                                Toast.makeText(configuracao.this, "Falha ao excluir a conta: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(configuracao.this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }
}
