package com.example.educapoio.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.educapoio.AuxilioAdapter;
import com.example.educapoio.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.List;

public class inicioFragment extends Fragment {

    private TextView textoOla;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewAuxilios;
    private AuxilioAdapter adapter;

    public inicioFragment() {
        // Required empty public constructor
    }

    public static inicioFragment newInstance(String param1, String param2) {
        inicioFragment fragment = new inicioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void carregarNomeUsuario(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String nome = document.getString("nome");
                    textoOla.setText("Olá, " + nome);
                } else {
                    textoOla.setText("Olá, usuário!");
                }
            } else {
                textoOla.setText("Olá, usuário!");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inicio, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textoOla = rootView.findViewById(R.id.texto2);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            carregarNomeUsuario(userId);
        }

        recyclerViewAuxilios = rootView.findViewById(R.id.recyclerViewAuxilios);
        recyclerViewAuxilios.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Criando uma lista de exemplos para o RecyclerView
        List<String> auxilios = Arrays.asList("Auxílio 1", "Auxílio 2", "Auxílio 3");
        adapter = new AuxilioAdapter(auxilios);
        recyclerViewAuxilios.setAdapter(adapter);

        ImageView imagemTi = rootView.findViewById(R.id.imagemTi);
        ImageView imagemSaude = rootView.findViewById(R.id.imagemSaude);
        ImageView imagemAdm = rootView.findViewById(R.id.imagemAdm);
        ImageView imagemContabilidade = rootView.findViewById(R.id.imagemContabilidade);
        ImageView imagemDireito = rootView.findViewById(R.id.imagemDireito);
        ImageView imagemBanco = rootView.findViewById(R.id.imagemBanco);
        TextView textSite = rootView.findViewById(R.id.textSite);

        textSite.setOnClickListener(v -> {
            String url = "https://www.icet.ufam.edu.br/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        imagemTi.setOnClickListener(v -> {
            String url = "https://www.grancursosonline.com.br/cursos/carreira/tecnologia-da-informacao?utm_medium=ppc&utm_campaign=&utm_term=&gad_source=1&gclid=CjwKCAiA0bWvBhBjEiwAtEsoW5GwNxDHSc3LSIluryHJmvVpm44-E6YIkdZcsCeWDIq5I__9IcSI1BoCqUUQAvD_BwE&gclsrc=aw.ds";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        imagemSaude.setOnClickListener(v -> {
            String url = "https://www.estrategiaconcursos.com.br/blog/concursos-area-da-saude/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        imagemAdm.setOnClickListener(v -> {
            String url = "https://jcconcursos.com.br/concursos/por-cargo/administrador";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        imagemContabilidade.setOnClickListener(v -> {
            String url = "https://www.estrategiaconcursos.com.br/blog/concursos-contabilidade/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        imagemDireito.setOnClickListener(v -> {
            String url = "https://cj.estrategia.com/portal/concursos-de-direito/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        imagemBanco.setOnClickListener(v -> {
            String url = "https://www.estrategiaconcursos.com.br/blog/concursos-bancarios/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        return rootView;
    }
}
