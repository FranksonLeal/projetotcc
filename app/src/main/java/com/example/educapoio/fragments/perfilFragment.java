package com.example.educapoio.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.educapoio.CircleCrop;
import com.example.educapoio.R;
import com.example.educapoio.ThemeHelper;
import com.example.educapoio.configuracao;
import com.example.educapoio.editarPerfil;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.snackbar.Snackbar;
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
    private ProgressBar progressBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshInicio);
        View rootView = view.findViewById(R.id.rootLayoutPerfil);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            atualizarTela();
            swipeRefreshLayout.setRefreshing(false);
        });

        ConstraintLayout rootLayoutPerfil = view.findViewById(R.id.rootLayoutPerfil);
        ThemeHelper.aplicarModoEscuro(requireContext(), rootLayoutPerfil);


        // Inicializa os TextViews e outros componentes da UI
        editNome = view.findViewById(R.id.editNome);
        editEmail2 = view.findViewById(R.id.editEmail2);
        editTelefone = view.findViewById(R.id.editTelefone);
        editCurso = view.findViewById(R.id.editCurso);
        editUsuario = view.findViewById(R.id.editUsuario);
        imagemPerfil = view.findViewById(R.id.mudarPerfil);
        progressBar = view.findViewById(R.id.progressBar);

        // Inicializa FirebaseAuth e Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtém o usuário autenticado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String email = user.getEmail();
            editEmail2.setText(email);

            // Carregar os dados do usuário
            carregarDadosUsuario(userId);
        }

        // Configuração e edição de perfil
        view.findViewById(R.id.imageConfig).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), configuracao.class))
        );

        view.findViewById(R.id.imageEdit).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), editarPerfil.class))
        );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean tutorialPerfilJaMostrado = prefs.getBoolean("tutorial_perfil_mostrado", false);

        if (!tutorialPerfilJaMostrado) {
            View configIcon = view.findViewById(R.id.imageConfig);
            View editIcon = view.findViewById(R.id.imageEdit);

            // Delay para garantir que as views estejam prontas
            new Handler().postDelayed(() -> {
                TapTargetSequence sequence = new TapTargetSequence(requireActivity())
                        .targets(
                                TapTarget.forView(
                                                configIcon,
                                                "Configurações",
                                                "Aqui você acessa as configurações do app.")
                                        .outerCircleColor(R.color.purple_500)
                                        .targetCircleColor(android.R.color.white)
                                        .titleTextSize(20)
                                        .descriptionTextSize(16)
                                        .titleTextColor(android.R.color.white)
                                        .descriptionTextColor(android.R.color.white)
                                        .cancelable(false)
                                        .tintTarget(false)
                                        .transparentTarget(true)
                                        .drawShadow(false)
                                        .id(1),

                                TapTarget.forView(
                                                editIcon,
                                                "Editar Perfil",
                                                "Aqui você pode editar seus dados pessoais.")
                                        .outerCircleColor(R.color.purple_500)
                                        .targetCircleColor(android.R.color.white)
                                        .titleTextSize(20)
                                        .descriptionTextSize(16)
                                        .titleTextColor(android.R.color.white)
                                        .descriptionTextColor(android.R.color.white)
                                        .cancelable(false)
                                        .tintTarget(false)
                                        .transparentTarget(true)
                                        .drawShadow(false)
                                        .id(2)
                        )
                        .listener(new TapTargetSequence.Listener() {
                            @Override
                            public void onSequenceFinish() {
                                prefs.edit().putBoolean("tutorial_perfil_mostrado", true).apply();

                                View rootView = requireActivity().findViewById(android.R.id.content);
                                Snackbar snackbar = Snackbar.make(rootView, "Tutorial do perfil finalizado! 🎉", Snackbar.LENGTH_LONG);

                                View snackbarView = snackbar.getView();
                                snackbarView.setBackgroundColor(Color.parseColor("#4CAF50")); // Verde

                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
                                params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 100);
                                snackbarView.setLayoutParams(params);

                                TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);
                                textView.setTextSize(16);
                                textView.setCompoundDrawablePadding(16);

                                snackbar.show();
                            }

                            @Override
                            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                                // Pode deixar vazio
                            }

                            @Override
                            public void onSequenceCanceled(TapTarget lastTarget) {
                                Toast.makeText(requireContext(), "Tutorial cancelado.", Toast.LENGTH_SHORT).show();
                            }
                        });

                sequence.start();
            }, 800);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            carregarDadosUsuario(user.getUid());
        }
    }


    private void atualizarTela() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            carregarDadosUsuario(user.getUid());
        }

    }


    private void carregarDadosUsuario(String userId) {
        if (!isAdded()) return; // Fragment não está anexado, cancela a execução.

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (!isAdded()) return; // Verifica novamente quando o listener retorna.

            if (!task.isSuccessful()) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                editNome.setText("Erro ao carregar dados");

                // Mostrar Snackbar de erro
                Snackbar.make(requireView(), "Erro ao carregar dados do usuário", Snackbar.LENGTH_SHORT).show();
                return;
            }

            DocumentSnapshot document = task.getResult();
            if (document == null || !document.exists()) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                editNome.setText("Dados não encontrados");

                // Mostrar Snackbar de aviso
                Snackbar.make(requireView(), "Dados do usuário não encontrados", Snackbar.LENGTH_SHORT).show();
                return;
            }

            String nome = document.getString("nome");
            String telefone = document.getString("telefone");
            String curso = document.getString("curso");
            String tipoUsuario = document.getString("tipoUsuario");
            String imagemUrl = document.getString("imagem");

            if (nome != null) editNome.setText(nome);
            if (telefone != null) editTelefone.setText(telefone);
            if (curso != null) editCurso.setText(curso);
            if (tipoUsuario != null) editUsuario.setText(tipoUsuario);

            if (imagemUrl != null && !imagemUrl.isEmpty()) {
                if (isAdded()) {
                    Glide.with(requireContext())
                            .load(imagemUrl)
                            .transform(new CircleCrop())
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, Object model,
                                                            com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                                                            boolean isFirstResource) {
                                    if (isAdded() && progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model,
                                                               com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                                                               com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                    if (isAdded() && progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    return false;
                                }
                            })
                            .into(imagemPerfil);
                }
            } else {
                if (nome != null) {
                    mostrarInicial(nome);
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            // Mostrar Snackbar de sucesso ao finalizar o carregamento
            Snackbar snackbar = Snackbar.make(requireView(), "Dados do usuário carregados", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#C1A9FF"));  // Roxo claro

            // Ajusta margem inferior para empurrar Snackbar para cima
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
            int bottomMarginPx = (int) (64 * getResources().getDisplayMetrics().density); // 64dp para pixels
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMarginPx);
            snackbarView.setLayoutParams(params);

            int snackbarTextId = getResources().getIdentifier("snackbar_text", "id", "com.google.android.material");
            TextView textView = snackbarView.findViewById(snackbarTextId);
            
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMarginPx);
            if (textView != null) {
                textView.setTextColor(Color.WHITE);
            }

            snackbar.show();
        });
    }



    private void mostrarInicial(String nome) {
        String inicial = nome.substring(0, 1).toUpperCase();

        Bitmap bitmap = Bitmap.createBitmap(140, 140, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(70, 70, 70, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(inicial, 70, 90, paint);

        imagemPerfil.setImageBitmap(bitmap);
    }
}
