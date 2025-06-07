package com.example.educapoio;

import static android.app.PendingIntent.getActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class configuracao extends AppCompatActivity {

    private ProgressBar progressBar;
    private Switch switchDarkMode;

    private View rootView;  // Adicionado aqui

    Switch switchModoEscuro;
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        rootView = findViewById(R.id.rootLayout);
        switchDarkMode.setChecked(isDarkModeEnabled);

        // Aplica a cor inicial ao abrir a Activity
        int corInicial = isDarkModeEnabled ? ContextCompat.getColor(this, R.color.colorPrimaryDark)
                : ContextCompat.getColor(this, R.color.colorPrimaryLight);
        rootView.setBackgroundColor(corInicial);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            recreate(); // Reinicia a Activity para aplicar o tema
        });



        ImageView imageAjuda = findViewById(R.id.imageAjuda);
        imageAjuda.setOnClickListener(v -> {
            Intent intent = new Intent(configuracao.this, Ajuda.class);
            startActivity(intent);
        });


        // Botão de voltar
        ImageView imageVoltar = findViewById(R.id.imageVoltar);
        imageVoltar.setOnClickListener(v -> finish());


        TextView textViewSobre = findViewById(R.id.textView13);
        textViewSobre.setOnClickListener(v -> {
            Intent intent = new Intent(configuracao.this, sobre.class);
            startActivity(intent);
        });




    progressBar = findViewById(R.id.progressBar);


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
        String mensagem = "Confira o aplicativo EducNews🚀📲! Ele pode te ajudar a encontrar oportunidades e notícias acadêmicas muito importantes pra você. Baixe agora! 😉";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mensagem);
        startActivity(Intent.createChooser(intent, "Compartilhar com"));
    }

    private void exibirDialogoConfirmacaoExclusaoConta() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.toast_custom_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView dialogMessage = bottomSheetView.findViewById(R.id.dialog_message);
        dialogMessage.setText("Tem certeza de que deseja excluir sua conta 😢? Esta ação é irreversível.");

        Button buttonConfirm = bottomSheetView.findViewById(R.id.button_open_url);
        buttonConfirm.setText("Sim");
        buttonConfirm.setOnClickListener(v -> {
            excluirConta();
            bottomSheetDialog.dismiss();
        });

        Button buttonCancel = bottomSheetView.findViewById(R.id.button_cancel);
        buttonCancel.setText("Cancelar");
        buttonCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }
    private void exibirDialogoConfirmacaoSair() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.toast_custom_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView dialogMessage = bottomSheetView.findViewById(R.id.dialog_message);
        dialogMessage.setText("Tem certeza de que deseja sair? Você precisará fazer login novamente.");

        Button buttonConfirm = bottomSheetView.findViewById(R.id.button_open_url);
        buttonConfirm.setText("Sim");
        buttonConfirm.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(configuracao.this, login.class));
            finish();
            bottomSheetDialog.dismiss();
        });

        Button buttonCancel = bottomSheetView.findViewById(R.id.button_cancel);
        buttonCancel.setText("Cancelar");
        buttonCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

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
                                Toast.makeText(configuracao.this, "Erro ao excluir a conta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}
