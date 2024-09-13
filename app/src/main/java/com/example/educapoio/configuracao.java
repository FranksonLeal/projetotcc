package com.example.educapoio;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.educapoio.R;
import com.example.educapoio.login;
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

        TextView textViewApagarConta = findViewById(R.id.textView15);
        textViewApagarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exibe um diálogo de confirmação antes de excluir a conta
                exibirDialogoConfirmacaoExclusaoConta();
            }
        });

        TextView textViewSair = findViewById(R.id.textView16);
        textViewSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exibe um diálogo de confirmação antes de sair
                exibirDialogoConfirmacaoSair();
            }
        });
    }

    private void exibirDialogoConfirmacaoExclusaoConta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar exclusão de conta");
        builder.setMessage("Tem certeza de que deseja excluir sua conta? Esta ação é irreversível.");

        // Botão para confirmar a exclusão da conta
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Excluir a conta do usuário no Firebase Authentication
                excluirConta();
            }
        });

        // Botão para cancelar a exclusão da conta
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancela a exclusão da conta
                dialog.dismiss();
            }
        });

        // Mostrar o diálogo
        builder.create().show();
    }

    private void exibirDialogoConfirmacaoSair() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar saída");
        builder.setMessage("Tem certeza de que deseja sair?");

        // Botão para confirmar a saída
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Exibir ProgressBar
                progressBar.setVisibility(View.VISIBLE);
                // Fazer logout do usuário
                FirebaseAuth.getInstance().signOut();
                // Redirecionar para a tela de login
                startActivity(new Intent(configuracao.this, login.class));
                // Finaliza a atividade atual
                finish();
            }
        });

        // Botão para cancelar a saída
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Fecha o diálogo
                dialog.dismiss();
            }
        });

        // Mostrar o diálogo
        builder.create().show();
    }

    private void excluirConta() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Exibir ProgressBar
            progressBar.setVisibility(View.VISIBLE);
            // Exclui a conta do usuário
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Conta excluída com sucesso
                                Toast.makeText(configuracao.this, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();
                                // Redirecionar para a tela de login
                                startActivity(new Intent(configuracao.this, login.class));
                                finish();
                            } else {
                                // Falha ao excluir a conta
                                Toast.makeText(configuracao.this, "Falha ao excluir a conta: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            // Ocultar ProgressBar
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            // Usuário não autenticado
            Toast.makeText(configuracao.this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            // Ocultar ProgressBar
            progressBar.setVisibility(View.GONE);
        }
    }
}
