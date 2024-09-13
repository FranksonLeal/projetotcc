package com.example.educapoio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.example.educapoio.R;
import com.example.educapoio.configuracao;
import com.example.educapoio.editarPerfil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

            // Busca os dados adicionais no Firestore
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
        // Referência ao documento do usuário no Firestore
        DocumentReference docRef = db.collection("users").document(userId);

        // Busca o documento do Firestore
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Extrai os dados do Firestore
                    String nome = document.getString("nome");
                    String telefone = document.getString("telefone");
                    String curso = document.getString("curso");
                    String tipoUsuario = document.getString("tipoUsuario");

                    // Preenche os TextViews com os dados do Firestore
                    editNome.setText(nome);
                    editTelefone.setText(telefone);
                    editCurso.setText(curso);
                    editUsuario.setText(tipoUsuario);
                } else {
                    // Documento não existe
                    editNome.setText("Dados não encontrados");
                }
            } else {
                // Erro ao acessar o Firestore
                editNome.setText("Erro ao carregar dados");
            }
        });
    }
}
