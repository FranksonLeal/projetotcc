package com.example.educapoio.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.educapoio.CircleCrop;
import com.example.educapoio.R;
import com.example.educapoio.configuracao;
import com.example.educapoio.editarPerfil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class perfilFragment extends Fragment {

    private TextView editNome, editEmail2, editTelefone, editCurso, editUsuario;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView imagemPerfil;
    private ProgressBar progressBar; // Adicionado

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializa os TextViews
        editNome = view.findViewById(R.id.editNome);
        editEmail2 = view.findViewById(R.id.editEmail2);
        editTelefone = view.findViewById(R.id.editTelefone);
        editCurso = view.findViewById(R.id.editCurso);
        editUsuario = view.findViewById(R.id.editUsuario);
        imagemPerfil = view.findViewById(R.id.mudarPerfil);
        progressBar = view.findViewById(R.id.progressBar); // Inicializa o ProgressBar

        // Inicializa FirebaseAuth e Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtém o usuário autenticado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Mostra o e-mail diretamente
            String email = user.getEmail();
            editEmail2.setText(email);

            // Carregar os dados do usuário
            carregarDadosUsuario(userId);
        }

        // Configuração e edição de perfil
        view.findViewById(R.id.imageConfig).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), configuracao.class));
        });

        view.findViewById(R.id.imageEdit).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), editarPerfil.class));
        });

        return view;
    }

    private void carregarDadosUsuario(String userId) {
        progressBar.setVisibility(View.VISIBLE); // Mostra a barra de progresso

        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE); // Esconde a barra de progresso

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String nome = document.getString("nome");
                    String telefone = document.getString("telefone");
                    String curso = document.getString("curso");
                    String tipoUsuario = document.getString("tipoUsuario");
                    String imagemUrl = document.getString("imagem"); // Usar a chave correta

                    editNome.setText(nome);
                    editTelefone.setText(telefone);
                    editCurso.setText(curso);
                    editUsuario.setText(tipoUsuario);

                    // Carregar a imagem de forma assíncrona
                    if (imagemUrl != null && !imagemUrl.isEmpty()) {
                        Glide.with(getContext())
                                .load(imagemUrl)
                                .transform(new CircleCrop())
                                .placeholder(R.drawable.placeholder_image) // Substitua pelo seu placeholder
                                .error(R.drawable.error_image) // Imagem de erro
                                .into(imagemPerfil);
                    } else {
                        mostrarInicial(nome); // Se não houver imagem, mostra a inicial
                    }

                } else {
                    editNome.setText("Dados não encontrados");
                }
            } else {
                editNome.setText("Erro ao carregar dados");
            }
        });
    }

    private void mostrarInicial(String nome) {
        // Obtém a inicial do nome
        String inicial = nome.substring(0, 1).toUpperCase();
        // Gera a imagem com a inicial
        Bitmap bitmap = Bitmap.createBitmap(140, 140, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE); // Cor de fundo
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(70, 70, 70, paint); // Desenha um círculo

        // Configura o texto
        paint.setColor(Color.WHITE); // Cor da inicial
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(inicial, 70, 90, paint); // Desenha a inicial

        // Define a imagem gerada no ImageView
        imagemPerfil.setImageBitmap(bitmap);
    }
}
