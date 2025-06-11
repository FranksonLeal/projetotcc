package com.example.educapoio;

import static com.example.educapoio.fragments.notificacaoFragment.NOTIFICATION_PREFS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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



        binding.editTelefone.addTextChangedListener(new android.text.TextWatcher() {
            boolean isUpdating;



            // Máscara: (##) #####-####
            private final String mask = "(##) #####-####";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                String str = s.toString().replaceAll("[^\\d]", ""); // Remove tudo que não for número
                StringBuilder formatted = new StringBuilder();

                int i = 0;
                for (char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > i) {
                        formatted.append(m);
                    } else {
                        try {
                            formatted.append(str.charAt(i));
                        } catch (Exception e) {
                            break;
                        }
                        i++;
                    }
                }

                isUpdating = true;
                binding.editTelefone.setText(formatted.toString());
                binding.editTelefone.setSelection(formatted.length());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

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


        EditText editSenha = findViewById(R.id.editSenha);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            editSenha.setTextCursorDrawable(R.drawable.cursor_roxo);
        } else {
            try {
                java.lang.reflect.Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                f.setAccessible(true);
                f.set(editSenha, R.drawable.cursor_roxo);
            } catch (Exception ignored) {}
        }

        // Referência ao TextView do título
        TextView texto1 = findViewById(R.id.texto1); // certifique-se que o ID é o mesmo ou ajuste aqui

// Configura o texto com duas partes coloridas
        String educ = "educ";
        String news = "News";

// Usa Spannable para aplicar cores diferentes nas partes do texto
        SpannableStringBuilder spannable = new SpannableStringBuilder();

// Adiciona "educ" em preto
        SpannableString educPart = new SpannableString(educ);
        educPart.setSpan(new ForegroundColorSpan(Color.BLACK), 0, educ.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.append(educPart);

// Adiciona "News" em roxo
        SpannableString newsPart = new SpannableString(news);
        newsPart.setSpan(new ForegroundColorSpan(Color.parseColor("#841FFD")), 0, news.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.append(newsPart);

// Define o texto formatado no TextView
        texto1.setText(spannable);


    }

    private void configurarSpinner() {
        Spinner spinnerCurso = binding.spinnerCurso;

        String[] cursos = {
                "Selecione um curso:",
                "Sistemas de Informação",
                "Engenharia de Software",
                "Engenharia de Produção",
                "Matemática e física",
                "Pedagogia",
                "Química e biologia",
                "Farmácia",
                "Engenharia sanitária",
                "Agronomia"
        };

        // Usa seu layout customizado para o item do spinner (o que você criou)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,  // seu layout customizado aqui
                cursos
        );

        // Layout para a lista suspensa (dropdown)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinnerCurso.setAdapter(adapter);

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

                    // Salva notificação local com título e mensagem
                    salvarNotificacao("Cadastro realizado", "Bem-vindo " + nome + " ao educNews! Fique por dentro agora das oportunidades acadêmicas🚀.");
                    startActivity(new Intent(cadastro.this, mensagemCadastro.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    mostrarMensagemErro("Erro ao salvar dados: " + e.getMessage(), false);
                });
    }

    private void salvarNotificacao(String titulo, String mensagem) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        String existingNotifications = prefs.getString("notifications_list", "");
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String updatedNotifications = existingNotifications.isEmpty()
                ? uniqueId + "|" + mensagem
                : existingNotifications + "|||" + uniqueId + "|" + mensagem;

        prefs.edit().putString("notifications_list", updatedNotifications).apply();

        // Envia broadcast para atualizar a interface da aba de notificações
        Intent intent = new Intent("NOTICIA_ATUALIZADA");
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
