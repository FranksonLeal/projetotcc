package com.example.educapoio;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class configuracao extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

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
        String mensagem = "Confira o aplicativo EducApoio! Ele pode te ajudar a encontrar auxílios e informações úteis. Baixe agora!";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mensagem);
        startActivity(Intent.createChooser(intent, "Compartilhar com"));
    }

    private void exibirDialogoConfirmacaoExclusaoConta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar exclusão de conta");
        builder.setMessage("Tem certeza de que deseja excluir sua conta? Esta ação é irreversível.");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                excluirConta();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void exibirDialogoConfirmacaoSair() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar saída");
        builder.setMessage("Tem certeza de que deseja sair?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(configuracao.this, login.class));
                finish();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
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
