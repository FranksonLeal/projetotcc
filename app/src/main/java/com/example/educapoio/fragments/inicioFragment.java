package com.example.educapoio.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

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

    private Handler handler = new Handler();
    private Runnable runnable;
    private ProgressBar progressBar;  // Adiciona a ProgressBar

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
        progressBar = rootView.findViewById(R.id.progressBar);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            carregarNomeUsuario(userId);
        }

        // Configura o TextView para exibir 'educApoio' com cores diferentes
        TextView textoAppName = rootView.findViewById(R.id.texto1);
        String appName = "educNews";
        SpannableString spannableAppName = new SpannableString(appName);

        // Aplicar cor preta para "educ"
        spannableAppName.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Aplicar cor roxa para "Apoio"
        spannableAppName.setSpan(new ForegroundColorSpan(Color.parseColor("#841FFD")), 4, appName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Definir o texto formatado no TextView
        textoAppName.setText(spannableAppName);

        recyclerViewAuxilios = rootView.findViewById(R.id.recyclerViewAuxilios);
        recyclerViewAuxilios.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        // Mostrar a ProgressBar antes de iniciar o carregamento dos dados
        progressBar.setVisibility(View.VISIBLE);
        // Buscar dados do Firestore
        buscarAuxiliosDoFirestore();

        // Outros códigos de eventos de clique em imagens
        configurarImagemClique(rootView);

        return rootView;
    }

    private void iniciarSlideAutomatico() {
        if (adapter == null || recyclerViewAuxilios.getLayoutManager() == null) {
            return;  // Não iniciar o slide se o adapter ou o layout manager não estiverem prontos
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                // Verifica a posição atual
                int position = ((LinearLayoutManager) recyclerViewAuxilios.getLayoutManager()).findFirstVisibleItemPosition();
                int nextPosition = position + 1;

                // Se a próxima posição ultrapassar o número de itens, reinicia o slide
                if (nextPosition >= adapter.getItemCount()) {
                    nextPosition = 0;
                }

                // Cria o scroller suave para a rolagem
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                    @Override
                    protected int getHorizontalSnapPreference() {
                        return SNAP_TO_START; // Se você precisar alinhar à esquerda
                    }

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        // Ajuste a velocidade da rolagem
                        float inchesPerPixel = super.calculateSpeedPerPixel(displayMetrics);
                        return inchesPerPixel * 10; // Aumente esse valor para desacelerar a rolagem
                    }
                };

                smoothScroller.setTargetPosition(nextPosition);
                recyclerViewAuxilios.getLayoutManager().startSmoothScroll(smoothScroller);

                // Aguardar 3 segundos antes de mover para o próximo item
                handler.postDelayed(this, 3000);
            }
        };

        handler.post(runnable); // Iniciar o slide automático
    }


    @Override
    public void onResume() {
        super.onResume();
        iniciarSlideAutomatico();  // Iniciar o slide quando o fragmento for visível
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);  // Parar o slide quando o fragmento for pausado
    }

    // Configurar eventos de clique nas imagens
    private void configurarImagemClique(View rootView) {
        ImageView imageAcess = rootView.findViewById(R.id.imageAcess);
        imageAcess.setOnClickListener(v -> mostrarMensagemIndisponibilidade());

        TextView textSite = rootView.findViewById(R.id.textSite);
        textSite.setOnClickListener(v -> abrirUrl("https://www.icet.ufam.edu.br/"));

        rootView.findViewById(R.id.imagemTi).setOnClickListener(v -> abrirUrl("https://www.grancursosonline.com.br/cursos/carreira/tecnologia-da-informacao"));
        rootView.findViewById(R.id.imagemSaude).setOnClickListener(v -> abrirUrl("https://www.estrategiaconcursos.com.br/blog/concursos-area-da-saude/"));
        rootView.findViewById(R.id.imagemAdm).setOnClickListener(v -> abrirUrl("https://jcconcursos.com.br/concursos/por-cargo/administrador"));
        rootView.findViewById(R.id.imagemContabilidade).setOnClickListener(v -> abrirUrl("https://www.estrategiaconcursos.com.br/blog/concursos-area-contabilidade"));
    }

    private void abrirUrl(String url) {
        if (url != null && !url.isEmpty()) {
            // Adiciona http:// caso não tenha o prefixo adequado
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;  // ou "https://", dependendo da URL
            }

            // Exibe a confirmação antes de abrir o link
            showConfirmationDialog(url);
        } else {
            // Se a URL for inválida
            showCustomMessage("URL inválida");
        }
    }

    private void showConfirmationDialog(final String url) {
        // Infla o layout do BottomSheetDialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.toast_custom_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Configura a mensagem de confirmação
        TextView dialogMessage = bottomSheetView.findViewById(R.id.dialog_message);
        dialogMessage.setText("Você tem certeza que deseja abrir o link?");

        // Configura os botões de confirmação
        Button buttonConfirm = bottomSheetView.findViewById(R.id.button_open_url);
        buttonConfirm.setOnClickListener(v -> {
            // Tenta abrir o link quando confirmado
            abrirUrlIntent(url);
            bottomSheetDialog.dismiss(); // Fecha o dialog após a ação
        });

        Button buttonCancel = bottomSheetView.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(v -> {
            // Fecha o dialog sem fazer nada
            bottomSheetDialog.dismiss();
        });

        // Exibe o BottomSheetDialog
        bottomSheetDialog.show();
    }

    private void abrirUrlIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Se não for possível abrir a URL
            showCustomMessage("Não foi possível abrir o link.");
        }
    }

    private void showCustomMessage(String message) {
        // Infla o layout do BottomSheetDialog para exibir a mensagem
        View bottomSheetView = getLayoutInflater().inflate(R.layout.toast_custom_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Configura a mensagem de erro
        TextView toastMessage = bottomSheetView.findViewById(R.id.dialog_message);
        toastMessage.setText(message);

        // Exibe o BottomSheetDialog
        bottomSheetDialog.show();
    }



    public void onItemClick(String url) {
        if (url != null && !url.isEmpty()) {
            abrirUrl(url);
        } else {
            Toast.makeText(getContext(), "URL não disponível", Toast.LENGTH_SHORT).show();
        }
    }


    private void mostrarMensagemIndisponibilidade() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(90, 90, 90, 90);

        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(Color.WHITE);
        backgroundDrawable.setCornerRadius(20f);
        layout.setBackground(backgroundDrawable);

        TextView messageText = new TextView(getContext());
        messageText.setText("Essa funcionalidade ainda não está disponível 😢");
        messageText.setTextSize(18);
        messageText.setTextColor(Color.BLACK);
        messageText.setPadding(0, 0, 0, 80);

        Button okButton = new Button(getContext());
        okButton.setText("OK");
        okButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        layout.addView(messageText);
        layout.addView(okButton);

        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.show();
    }

    private void buscarAuxiliosDoFirestore() {
        db.collection("auxilios")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> auxiliosList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            auxiliosList.add(document.getData());
                        }

                        adapter = new AuxilioAdapter(auxiliosList, url -> {
                            if (url != null && !url.isEmpty()) {
                                abrirUrl(url);
                            } else {
                                Toast.makeText(getContext(), "URL não disponível", Toast.LENGTH_SHORT).show();
                            }
                        });
                        recyclerViewAuxilios.setAdapter(adapter);

                        // Oculta a ProgressBar após o carregamento dos dados
                        progressBar.setVisibility(View.GONE);
                    } else {
                        // Em caso de falha, ocultar a ProgressBar e mostrar uma mensagem
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Erro ao carregar auxílios.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
