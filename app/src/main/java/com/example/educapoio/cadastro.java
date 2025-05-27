package com.example.educapoio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.educapoio.databinding.ActivityCadastroBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class cadastro extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityCadastroBinding binding;

    private String cursoSelecionado; // Para armazenar o curso selecionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Inicializa o Firestore

        binding.btnCriarConta.setOnClickListener(v -> validaDados());

        // Configurando o Spinner
        configurarSpinner();

        // Botão para voltar à tela de login
        Button btnVoltarLogin = findViewById(R.id.btnVoltarLogin);
        btnVoltarLogin.setOnClickListener(v -> {
            Intent intent = new Intent(cadastro.this, login.class);
            TransitionUtil.startActivityWithAnimation(cadastro.this, intent);
            finish(); // Finaliza a tela de cadastro para evitar retorno
        });
    }

    private void configurarSpinner() {
        Spinner spinnerCurso = binding.spinnerCurso;

        // Lista de cursos existentes
        String[] cursos = {"Selecione um curso:","Sistemas de Informação", "Engenharia de Software", "Engenharia de Produção", "Matemática e física", "Pedagogia", "Química e biologia", "Farmácia", "Engenharia sanitária", "Agronomia"};

        // Configurando o adaptador do Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cursos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurso.setAdapter(adapter);

        // Listener para capturar o curso selecionado
        spinnerCurso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cursoSelecionado = cursos[position];
                if (position == 0) {
                    cursoSelecionado = ""; // Nenhum curso selecionado
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cursoSelecionado = "";
            }
        });
    }

    private void validaDados() {
        String nome = binding.editNome.getText().toString().trim();
        String email = binding.editEmail.getText().toString().trim();
        String senha = binding.editSenha.getText().toString().trim();
        String telefone = binding.editTelefone.getText().toString().trim();

        // Captura o valor selecionado no RadioGroup
        int selectedId = binding.radioGroup.getCheckedRadioButtonId();
        String tipoUsuario = "";
        if (selectedId == R.id.radioButton1) {
            tipoUsuario = "Aluno";
        } else if (selectedId == R.id.radioButton2) {
            tipoUsuario = "Professor";
        }

        String mensagemErro = "";
        if (nome.isEmpty()) {
            mensagemErro = "Preencha o campo Nome!";
            binding.editNome.setBackgroundResource(R.drawable.bg_edittext_error); // Adiciona borda vermelha
        } else {
            binding.editNome.setBackgroundResource(R.drawable.bg_edittext); // Remove a borda vermelha
        }

        if (email.isEmpty()) {
            mensagemErro = "Preencha o campo Email!";
            binding.editEmail.setBackgroundResource(R.drawable.bg_edittext_error); // Adiciona borda vermelha
        } else {
            binding.editEmail.setBackgroundResource(R.drawable.bg_edittext); // Remove a borda vermelha
        }

        if (senha.isEmpty()) {
            mensagemErro = "Preencha o campo Senha!";
            binding.editSenha.setBackgroundResource(R.drawable.bg_edittext_error); // Adiciona borda vermelha
        } else {
            binding.editSenha.setBackgroundResource(R.drawable.bg_edittext); // Remove a borda vermelha
        }

        if (telefone.isEmpty()) {
            mensagemErro = "Preencha o campo Telefone!";
            binding.editTelefone.setBackgroundResource(R.drawable.bg_edittext_error); // Adiciona borda vermelha
        } else {
            binding.editTelefone.setBackgroundResource(R.drawable.bg_edittext); // Remove a borda vermelha
        }

        if (cursoSelecionado.isEmpty()) {
            mensagemErro = "Selecione um curso!";
        }

        if (tipoUsuario.isEmpty()) {
            mensagemErro = "Selecione um tipo de usuário!";
        }

        if (!mensagemErro.isEmpty()) {
            mostrarMensagemErro(mensagemErro, false); // false indica que é um erro
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        criarContaFirebase(nome, email, senha, telefone, cursoSelecionado, tipoUsuario);
    }

    private void criarContaFirebase(String nome, String email, String senha, String telefone, String curso, String tipoUsuario) {
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            salvarDadosFirestore(user.getUid(), nome, email, telefone, curso, tipoUsuario); // Inclui tipo de usuário
                        }
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        mostrarMensagemErro("Erro ao criar conta: " + task.getException().getMessage(), false);
                    }
                });
    }
    private void salvarCursoUsuario(String curso) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("cursoUsuario", curso);
        editor.apply();
    }

    private void salvarDadosFirestore(String userId, String nome, String email, String telefone, String curso, String tipoUsuario) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("nome", nome);
        userData.put("email", email);
        userData.put("telefone", telefone);
        userData.put("curso", curso);
        userData.put("tipoUsuario", tipoUsuario);

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    salvarCursoUsuario(curso); // Salva o curso no SharedPreferences
                    binding.progressBar.setVisibility(View.GONE);
                    mostrarMensagemErro("Usuário cadastrado com sucesso!", true);
                    startActivity(new Intent(cadastro.this, mensagemCadastro.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    mostrarMensagemErro("Erro ao salvar dados: " + e.getMessage(), false);
                });
    }


    private void mostrarMensagemErro(String mensagem, boolean sucesso) {
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
}
