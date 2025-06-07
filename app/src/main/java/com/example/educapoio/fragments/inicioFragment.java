package com.example.educapoio.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.educapoio.Ajuda;
import com.example.educapoio.AuxilioAdapter;
import com.example.educapoio.R;
import com.example.educapoio.ThemeHelper;
import com.example.educapoio.WebViewActivity;
import com.example.educapoio.configuracao;
import com.facebook.shimmer.ShimmerFrameLayout;
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

    private View rootView;  // Adicionado aqui
    private ProgressBar progressBar;  // Adiciona a ProgressBar

    private SwipeRefreshLayout swipeRefreshLayout;

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
                    String tipoUsuario = document.getString("tipoUsuario"); // Corrigido para "tipoUsuario"

                    // Log para depuração
                    Log.d("UserInfo", "Nome: " + nome + ", Tipo: " + tipoUsuario);

                    if (nome != null) {
                        // Garantindo que a comparação ignore maiúsculas e minúsculas
                        String saudacao = "Olá, ";
                        if (tipoUsuario != null && tipoUsuario.equalsIgnoreCase("Professor")) {
                            saudacao = "Olá, Professor ";
                        }

                        // Aplicando cor ao nome do usuário
                        SpannableString spannable = new SpannableString(saudacao + nome);
                        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#841FFD")),
                                saudacao.length(), // Início do nome
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

    ShimmerFrameLayout shimmerLayout;
    LinearLayout layoutSemAuxilios; // Para o "Nenhuma oportunidade encontrada"




    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflando a view corretamente
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        layoutSemAuxilios = view.findViewById(R.id.layoutSemAuxilios);
        recyclerViewAuxilios = view.findViewById(R.id.recyclerViewAuxilios);

        recyclerViewAuxilios.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        shimmerLayout.startShimmer();
        layoutSemAuxilios.setVisibility(View.GONE);
        recyclerViewAuxilios.setVisibility(View.VISIBLE);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshInicio);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            atualizarTela();
            swipeRefreshLayout.setRefreshing(false);
        });


        ConstraintLayout rootLayoutInicio = view.findViewById(R.id.rootLayoutInicio);
        ThemeHelper.aplicarModoEscuro(requireContext(), rootLayoutInicio);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textoOla = view.findViewById(R.id.texto2);
        progressBar = view.findViewById(R.id.progressBar);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            carregarNomeUsuario(userId);
        }

        TextView textoAppName = view.findViewById(R.id.texto1);
        String appName = "educNews";
        SpannableString spannableAppName = new SpannableString(appName);

        spannableAppName.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAppName.setSpan(new ForegroundColorSpan(Color.parseColor("#841FFD")), 4, appName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textoAppName.setText(spannableAppName);

        // Configuração e edição de perfil
        view.findViewById(R.id.imageAjuda).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Ajuda.class))
        );

        // Já configurado anteriormente, não precisa repetir
        // recyclerViewAuxilios = view.findViewById(R.id.recyclerViewAuxilios);
        // recyclerViewAuxilios.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        progressBar.setVisibility(View.VISIBLE);
        buscarAuxiliosDoFirestore();

        configurarImagemClique(view);

        return view;
    }


    private void atualizarTela() {
        buscarAuxiliosDoFirestore();
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

        // Novo botão Página do ICET
        LinearLayout btnIcet = rootView.findViewById(R.id.btn_icet);
        btnIcet.setOnClickListener(v -> abrirUrl("https://www.icet.ufam.edu.br/"));

        // Novo botão eCampus
        LinearLayout btnEcampus = rootView.findViewById(R.id.btn_ecampus);
        btnEcampus.setOnClickListener(v -> abrirUrl("https://ecampus.ufam.edu.br/ecampus/home/login"));

        // As imagens continuam funcionando normalmente
        rootView.findViewById(R.id.imagemTi).setOnClickListener(v -> abrirUrl("https://www.grancursosonline.com.br/cursos/carreira/tecnologia-da-informacao"));
        rootView.findViewById(R.id.imagemSaude).setOnClickListener(v -> abrirUrl("https://www.estrategiaconcursos.com.br/blog/concursos-area-da-saude/"));
        rootView.findViewById(R.id.imagemAdm).setOnClickListener(v -> abrirUrl("https://jcconcursos.com.br/concursos/por-cargo/administrador"));
        rootView.findViewById(R.id.imagemContabilidade).setOnClickListener(v -> abrirUrl("https://www.estrategiaconcursos.com.br/blog/concursos-area-contabilidade"));
    }


    private void abrirUrl(String url) {
        if (url != null && !url.isEmpty()) {
            // Adiciona http:// caso não tenha o prefixo adequado
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
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

        // Botão Confirmar
        Button buttonConfirm = bottomSheetView.findViewById(R.id.button_open_url);
        buttonConfirm.setOnClickListener(v -> {
            abrirUrlWebView(url); // 👉 Abre na WebView
            bottomSheetDialog.dismiss();
        });

        // Botão Cancelar
        Button buttonCancel = bottomSheetView.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }


    private void abrirUrlWebView(String url) {
        try {
            Intent intent = new Intent(getContext(), WebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        } catch (Exception e) {
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
        okButton.setTextColor(Color.WHITE);

        // Cria fundo arredondado preto para o botão
        GradientDrawable buttonBackground = new GradientDrawable();
        buttonBackground.setColor(Color.BLACK);
        buttonBackground.setCornerRadius(30f); // raio do botão
        okButton.setBackground(buttonBackground);

        okButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        layout.addView(messageText);
        layout.addView(okButton);

        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.show();
    }



    private void buscarAuxiliosDoFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Mostra a progressBar e o shimmer no começo da busca
        progressBar.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        shimmerLayout.setVisibility(View.VISIBLE);
        recyclerViewAuxilios.setVisibility(View.GONE);
        layoutSemAuxilios.setVisibility(View.GONE);

        // Buscar o curso do usuário logado
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("curso")) {
                        String cursoUsuario = documentSnapshot.getString("curso");
                        carregarAuxiliosFiltrados(cursoUsuario);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Erro ao obter curso do usuário.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Erro ao carregar dados do usuário.", Toast.LENGTH_SHORT).show();
                });
    }

    private void compartilharAuxilio(String texto) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(shareIntent, "Compartilhar auxílio via"));
    }


    private void carregarAuxiliosFiltrados(String cursoUsuario) {
        db.collection("auxilios")
                .get()
                .addOnCompleteListener(task -> {
                    // Parar shimmer e esconder progressBar somente após resultado
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        List<Map<String, Object>> auxiliosList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            boolean publicoGeral = document.contains("publicoGeral") && document.getBoolean("publicoGeral");

                            if (publicoGeral) {
                                auxiliosList.add(document.getData());
                            } else {
                                List<String> cursos = (List<String>) document.get("cursos");
                                if (cursos != null && cursos.contains(cursoUsuario)) {
                                    auxiliosList.add(document.getData());
                                }
                            }
                        }

                        if (auxiliosList.isEmpty()) {
                            layoutSemAuxilios.setVisibility(View.VISIBLE);
                            recyclerViewAuxilios.setVisibility(View.GONE);
                        } else {
                            layoutSemAuxilios.setVisibility(View.GONE);
                            recyclerViewAuxilios.setVisibility(View.VISIBLE);

                            adapter = new AuxilioAdapter(auxiliosList, new AuxilioAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(String url) {
                                    if (url != null && !url.isEmpty()) {
                                        abrirUrl(url);
                                    } else {
                                        Toast.makeText(getContext(), "URL não disponível", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onShareClick(String textoParaCompartilhar) {
                                    compartilharAuxilio(textoParaCompartilhar);
                                }
                            });

                            recyclerViewAuxilios.setAdapter(adapter);
                        }
                    } else {
                        layoutSemAuxilios.setVisibility(View.VISIBLE);
                        recyclerViewAuxilios.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Erro ao carregar auxílios.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
