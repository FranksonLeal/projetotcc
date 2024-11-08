package com.example.educapoio;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class editarPerfil extends AppCompatActivity {

    private EditText editNome, editTelefone, editCurso;
    private Button btnSalvar;
    private ImageView mudarPerfil;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String userId;
    private Uri imagemUri;
    private TextView removerImagem;
    private ProgressBar progressBarCarregar, progressBarSalvar, progressBarRemover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        editNome = findViewById(R.id.editNome);
        editTelefone = findViewById(R.id.editTelefone);
        editCurso = findViewById(R.id.editCurso);
        btnSalvar = findViewById(R.id.btnSalvar);
        mudarPerfil = findViewById(R.id.mudarPerfil);
        ImageView imageVoltar = findViewById(R.id.imageVoltarEditar);
        removerImagem = findViewById(R.id.removerImagem);

        // Progress bars
        progressBarCarregar = findViewById(R.id.progressBar);
        progressBarSalvar = findViewById(R.id.progressBar);
        progressBarRemover = findViewById(R.id.progressBar);

        // Configurar o clique no TextView "Remover"
        removerImagem.setOnClickListener(v -> {
            removerImagemPerfil();
        });

        // Configurar o clique do ícone de voltar
        imageVoltar.setOnClickListener(v -> finish());

        // Inicializar Firestore e Firebase Storage
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Obter o usuário autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid(); // ID do usuário autenticado
            carregarDadosUsuario();
        } else {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
        }

        // Configurar clique na imagem de perfil
        mudarPerfil.setOnClickListener(v -> escolherImagem());

        btnSalvar.setOnClickListener(v -> {
            String nome = editNome.getText().toString();
            String telefone = editTelefone.getText().toString();
            String curso = editCurso.getText().toString();

            if (!nome.isEmpty() && !telefone.isEmpty() && !curso.isEmpty()) {
                atualizarDadosUsuario(nome, telefone, curso);
            } else {
                Toast.makeText(editarPerfil.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removerImagemPerfil() {
        new AlertDialog.Builder(this)
                .setTitle("Remover Imagem")
                .setMessage("Tem certeza que deseja remover a imagem?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    progressBarRemover.setVisibility(View.VISIBLE); // Exibir ProgressBar ao remover imagem
                    // Primeiro, atualize o Firestore para remover a URL da imagem
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("imagem", null); // Remover a imagem (definindo como null)

                    db.collection("users").document(userId)
                            .update(userData)
                            .addOnSuccessListener(aVoid -> {
                                // Agora, remova a imagem do Firebase Storage
                                StorageReference filePath = storageRef.child("profile_images").child(userId + ".jpg");
                                filePath.delete().addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(editarPerfil.this, "Imagem removida com sucesso", Toast.LENGTH_SHORT).show();
                                    mudarPerfil.setImageResource(R.drawable.placeholder_image); // Exibir placeholder após remoção
                                    progressBarRemover.setVisibility(View.GONE); // Esconder ProgressBar após remoção
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(editarPerfil.this, "Erro ao remover a imagem do Storage", Toast.LENGTH_SHORT).show();
                                    progressBarRemover.setVisibility(View.GONE); // Esconder ProgressBar após erro
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(editarPerfil.this, "Erro ao remover a imagem do Firestore", Toast.LENGTH_SHORT).show();
                                progressBarRemover.setVisibility(View.GONE); // Esconder ProgressBar após erro
                            });
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void carregarDadosUsuario() {
        progressBarCarregar.setVisibility(View.VISIBLE); // Exibir ProgressBar ao carregar dados
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    progressBarCarregar.setVisibility(View.GONE); // Esconder ProgressBar após carregar dados
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String nome = document.getString("nome");
                            String telefone = document.getString("telefone");
                            String curso = document.getString("curso");
                            String imagem = document.getString("imagem"); // Obtendo a URL da imagem

                            // Preencher os EditTexts com os dados
                            editNome.setText(nome);
                            editTelefone.setText(telefone);
                            editCurso.setText(curso);

                            // Carregar a imagem do perfil se disponível
                            if (imagem != null) {
                                Glide.with(this)
                                        .load(imagem)
                                        .transform(new CircleCrop()) // Aplicar transformação circular
                                        .placeholder(R.drawable.placeholder_image) // Substitua pelo seu placeholder
                                        .into(mudarPerfil);
                                removerImagem.setVisibility(View.VISIBLE); // Mostrar o TextView "Remover"
                            } else {
                                mudarPerfil.setImageResource(R.drawable.placeholder_image); // Defina um placeholder se não houver imagem
                                removerImagem.setVisibility(View.GONE); // Esconder o TextView "Remover"
                            }
                        } else {
                            Toast.makeText(editarPerfil.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(editarPerfil.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void escolherImagem() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imagemUri = data.getData();
            Glide.with(this)
                    .load(imagemUri)
                    .transform(new CircleCrop()) // Aplicar transformação circular
                    .placeholder(R.drawable.placeholder_image) // Substitua pelo seu placeholder
                    .into(mudarPerfil);
        }
    }

    private void atualizarDadosUsuario(String nome, String telefone, String curso) {
        progressBarSalvar.setVisibility(View.VISIBLE); // Exibir ProgressBar ao salvar dados
        Map<String, Object> userData = new HashMap<>();
        userData.put("nome", nome);
        userData.put("telefone", telefone);
        userData.put("curso", curso);

        if (imagemUri != null) {
            StorageReference filePath = storageRef.child("profile_images").child(userId + ".jpg");
            filePath.putFile(imagemUri).addOnSuccessListener(taskSnapshot -> {
                filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    userData.put("imagem", uri.toString());
                    salvarDadosNoFirestore(userData);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(editarPerfil.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                progressBarSalvar.setVisibility(View.GONE); // Esconder ProgressBar após erro
            });
        } else {
            salvarDadosNoFirestore(userData);
        }
    }

    private void salvarDadosNoFirestore(Map<String, Object> userData) {
        db.collection("users").document(userId)
                .update(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(editarPerfil.this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show();
                    progressBarSalvar.setVisibility(View.GONE); // Esconder ProgressBar após sucesso
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(editarPerfil.this, "Erro ao salvar dados", Toast.LENGTH_SHORT).show();
                    progressBarSalvar.setVisibility(View.GONE); // Esconder ProgressBar após erro
                });
    }
}
