package com.example.educapoio.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.educapoio.NoticiasPagerAdapter;
import com.example.educapoio.R;
import com.example.educapoio.ThemeHelper;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class noticias extends Fragment {

    private TabLayout tabLayoutNoticias;
    private ViewPager2 viewPagerNoticias;
    private ProgressBar progressBar;

    private SwipeRefreshLayout swipeRefreshLayout;
    private final String[] tabTitles = {"Recomendado", "Sobre seu curso"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noticias, container, false);

        tabLayoutNoticias = rootView.findViewById(R.id.tabLayoutNoticias);
        viewPagerNoticias = rootView.findViewById(R.id.viewPagerNoticias);
        progressBar = rootView.findViewById(R.id.progressBar);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshInicio);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            carregarTudo(true);
        });

        LinearLayout rootLayoutInicio = rootView.findViewById(R.id.rootLayoutInicio);
        ThemeHelper.aplicarModoEscuro(requireContext(), rootLayoutInicio);


        TabLayout tabLayout = rootView.findViewById(R.id.tabLayoutNoticias);
        ThemeHelper.aplicarModoEscuro(requireContext(), tabLayout);



        carregarTudo(false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean tutorialNoticiasJaMostrado = prefs.getBoolean("tutorial_noticias_mostrado", false);

        if (!tutorialNoticiasJaMostrado) {
            new Handler().postDelayed(() -> {
                TapTargetSequence sequence = new TapTargetSequence(requireActivity())
                        .targets(
                                TapTarget.forView(
                                                tabLayoutNoticias.getTabAt(0).view,
                                                "Aba Recomendado",
                                                "Aqui você vê notícias recomendadas para você.")
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
                                                tabLayoutNoticias.getTabAt(1).view,
                                                "Aba Sobre seu curso",
                                                "Aqui você encontra notícias relacionadas ao seu curso.")
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
                                prefs.edit().putBoolean("tutorial_noticias_mostrado", true).apply();

                                View rootView = requireActivity().findViewById(android.R.id.content);
                                Snackbar snackbar = Snackbar.make(rootView, "Tutorial das notícias finalizado! 🎉", Snackbar.LENGTH_LONG);

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
                                // Se quiser alguma ação durante o tutorial
                            }

                            @Override
                            public void onSequenceCanceled(TapTarget lastTarget) {
                                Toast.makeText(requireContext(), "Tutorial cancelado.", Toast.LENGTH_SHORT).show();
                            }
                        });

                sequence.start();
            }, 1000); // Delay para garantir que o layout esteja pronto
        }
    }


    private void carregarTudo(boolean mostrarToast) {
        if (!isAdded()) return;

        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!isAdded()) return;

                    if (documentSnapshot.exists()) {
                        String cursoUsuario = documentSnapshot.getString("curso");
                        if (cursoUsuario != null) {
                            inicializarViewPager(cursoUsuario, mostrarToast);
                        } else {
                            Toast.makeText(requireContext(), "Curso não encontrado!", Toast.LENGTH_SHORT).show();
                            finalizarCarregamento();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Dados do usuário não encontrados!", Toast.LENGTH_SHORT).show();
                        finalizarCarregamento();
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Erro ao carregar o curso!", Toast.LENGTH_SHORT).show();
                    finalizarCarregamento();
                });
    }


    public void onNoticiasCarregando() {
        if (isAdded()) progressBar.setVisibility(View.VISIBLE);
    }


    public void onNoticiasCarregadas() {
        if (isAdded()) progressBar.setVisibility(View.GONE);
    }

    private void inicializarViewPager(String cursoUsuario, boolean mostrarToast) {
        if (!isAdded()) return;

        NoticiasPagerAdapter adapter = new NoticiasPagerAdapter(requireActivity(), cursoUsuario);
        viewPagerNoticias.setAdapter(adapter);

        tabLayoutNoticias.setTabTextColors(
                Color.parseColor("#B0B0B0"), // Cinza
                Color.parseColor("#841FFD")  // Roxo
        );

        new TabLayoutMediator(tabLayoutNoticias, viewPagerNoticias,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        carregarNoticias(mostrarToast);
    }

    private void carregarNoticias(boolean mostrarToast) {
        if (!isAdded()) return;

        new Thread(() -> {
            try {
                Thread.sleep(1500);

                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    finalizarCarregamento();

                    if (mostrarToast) {
                        View rootView = requireView();

                        Snackbar snackbar = Snackbar.make(rootView, "Dados atualizados!", Snackbar.LENGTH_SHORT);

                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(Color.parseColor("#C1A9FF"));

                        // Ajusta margem inferior para empurrar Snackbar para cima
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
                        int bottomMarginPx = (int) (64 * getResources().getDisplayMetrics().density); // 64dp para pixels
                        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMarginPx);
                        snackbarView.setLayoutParams(params);

                        int snackbarTextId = getResources().getIdentifier("snackbar_text", "id", "com.google.android.material");
                        TextView textView = snackbarView.findViewById(snackbarTextId);
                        if (textView != null) {
                            textView.setTextColor(Color.WHITE);
                        }

                        snackbar.show();
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(this::finalizarCarregamento);
            }
        }).start();
    }



    private void finalizarCarregamento() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }
}
