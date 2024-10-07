package com.example.educapoio.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.educapoio.AuxilioAdapter;
import com.example.educapoio.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

    // Método para carregar o nome do usuário a partir do Firestore
    private void carregarNomeUsuario(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String nome = document.getString("nome");
                    if (nome != null) {
                        // Aplicando cor ao nome do usuário
                        SpannableString spannable = new SpannableString("Olá, " + nome);
                        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#841FFD")),
                                5, // Início do nome
                                spannable.length(), // Até o final
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textoOla.setText(spannable);
                    } else {
                        textoOla.setText("Olá, usuário!");
                    }
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

        // Outros códigos de eventos de clique em imagens
        configurarImagemClique(rootView);

        return rootView;
    }

    // Configurar eventos de clique nas imagens
    private void configurarImagemClique(View rootView) {
        ImageView imageAcess = rootView.findViewById(R.id.imageAcess);
        imageAcess.setOnClickListener(v -> mostrarMensagemIndisponibilidade());

        rootView.findViewById(R.id.imagemTi).setOnClickListener(v -> abrirUrl("https://www.grancursosonline.com.br/cursos/carreira/tecnologia-da-informacao"));
        rootView.findViewById(R.id.imagemSaude).setOnClickListener(v -> abrirUrl("https://www.estrategiaconcursos.com.br/blog/concursos-area-da-saude/"));
        rootView.findViewById(R.id.imagemAdm).setOnClickListener(v -> abrirUrl("https://jcconcursos.com.br/concursos/por-cargo/administrador"));
        rootView.findViewById(R.id.imagemContabilidade).setOnClickListener(v -> abrirUrl("https://www.estrategiaconcursos.com.br/blog/concursos-contabilidade/"));
        rootView.findViewById(R.id.imagemDireito).setOnClickListener(v -> abrirUrl("https://cj.estrategia.com/portal/concursos-de-direito/"));
        rootView.findViewById(R.id.imagemBanco).setOnClickListener(v -> abrirUrl("https://www.estrategiaconcursos.com.br/blog/concursos-bancarios/"));
    }

    private void mostrarMensagemIndisponibilidade() {
        // Cria o BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

        // Infla um layout simples programaticamente
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(90, 90, 90, 90); // Ajuste o padding conforme necessário

        // Cria um GradientDrawable para bordas arredondadas
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(Color.WHITE); // Cor de fundo
        backgroundDrawable.setCornerRadius(20f); // Raio dos cantos
        layout.setBackground(backgroundDrawable); // Aplica o fundo ao layout

        // Adiciona o texto da mensagem
        TextView messageText = new TextView(getContext());
        messageText.setText("Essa funcionalidade ainda não está disponível 😢");
        messageText.setTextSize(18); // Tamanho do texto
        messageText.setTextColor(Color.BLACK); // Cor do texto
        messageText.setPadding(0, 0, 0, 80); // Padding inferior

        // Adiciona o botão de OK
        Button okButton = new Button(getContext());
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



    // Buscar auxílios do Firestore e exibir no RecyclerView
    private void buscarAuxiliosDoFirestore() {
        db.collection("auxilios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Map<String, Object>> auxilios = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    Map<String, Object> auxilio = document.getData();
                    auxilios.add(auxilio);
                }
                // Passa os dados para o Adapter com o listener para abrir URL
                adapter = new AuxilioAdapter(auxilios, this::abrirUrl);
                recyclerViewAuxilios.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Erro ao carregar auxílios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para abrir uma URL após confirmação no Dialog
    private void abrirUrl(String url) {
        // Infla o layout do BottomSheetDialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.toast_custom_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Configura a mensagem a ser exibida
        TextView dialogMessage = bottomSheetView.findViewById(R.id.dialog_message);
        dialogMessage.setText("Você está prestes a abrir: " + url);

        // Botão para abrir a URL
        Button buttonOpenUrl = bottomSheetView.findViewById(R.id.button_open_url);
        buttonOpenUrl.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });

        // Botão para cancelar
        Button buttonCancel = bottomSheetView.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Exibe o BottomSheetDialog
        bottomSheetDialog.show();
    }

}
