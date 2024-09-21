package com.example.educapoio.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.Toast;

import com.example.educapoio.AuxilioAdapter;
import com.example.educapoio.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
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

        // Buscar dados do Firestore
        buscarAuxiliosDoFirestore();

        ImageView imageAcess = rootView.findViewById(R.id.imageAcess);
        imageAcess.setOnClickListener(v -> {
            mostrarMensagemIndisponibilidade();
        });

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
            String url = "https://www.grancursosonline.com.br/cursos/carreira/tecnologia-da-informacao?utm_medium=ppc&utm_campaign=&utm_term=&gad_source=1&gclsrc=aw.ds";
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

    private void mostrarMensagemIndisponibilidade() {
        new AlertDialog.Builder(getContext())
                .setTitle("Atenção")
                .setMessage("Essa funcionalidade ainda não está disponível 😢")
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void buscarAuxiliosDoFirestore() {
        db.collection("auxilios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Map<String, Object>> auxilios = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    Map<String, Object> auxilio = document.getData();
                    auxilios.add(auxilio);
                }
                // Passa os dados para o Adapter com o listener
                adapter = new AuxilioAdapter(auxilios, this::abrirUrl);
                recyclerViewAuxilios.setAdapter(adapter);
            } else {
                // Caso ocorra algum erro
                Toast.makeText(getContext(), "Erro ao carregar auxílios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para abrir a URL
    private void abrirUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
