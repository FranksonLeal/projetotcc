package com.example.educapoio.fragments;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.educapoio.R;
import com.example.educapoio.SectionsPagerAdapter;
import com.example.educapoio.ThemeHelper;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class inscricaoFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflando a view do fragmento
        View view = inflater.inflate(R.layout.fragment_inscricao, container, false);

        // Recupera a preferência do modo escuro
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

        LinearLayout rootLayoutInicio = view.findViewById(R.id.rootLayoutInicio);
        ThemeHelper.aplicarModoEscuro(requireContext(), rootLayoutInicio);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ThemeHelper.aplicarModoEscuro(requireContext(), tabLayout);



        // Inicializa componentes
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Verifica se os componentes foram encontrados
        if (viewPager == null || tabLayout == null || swipeRefreshLayout == null) {
            throw new NullPointerException("Erro: Componentes não encontrados no XML.");
        }

        // Configura o adapter do ViewPager2
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(requireActivity());
        viewPager.setAdapter(sectionsPagerAdapter);

        // Conecta o TabLayout ao ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Abertos" : "Encerrados");
        }).attach();

        // Define as cores do texto do TabLayout
        tabLayout.setTabTextColors(
                Color.parseColor("#B0B0B0"),  // Cor do texto não selecionado
                Color.parseColor("#841FFD")   // Cor do texto selecionado
        );

        // Configura ação de "puxar para atualizar"
        swipeRefreshLayout.setOnRefreshListener(() -> {

            atualizarTela();

            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // SharedPreferences para o tutorial da inscrição
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean tutorialInscricaoJaMostrado = prefs.getBoolean("tutorial_inscricao_mostrado", false);

        if (!tutorialInscricaoJaMostrado) {
            new Handler().postDelayed(() -> {
                TapTargetSequence sequence = new TapTargetSequence(requireActivity())
                        .targets(
                                TapTarget.forView(
                                                // Target do Tab "Abertos" (posição 0)
                                                ((TabLayout) view.findViewById(R.id.tabLayout))
                                                        .getTabAt(0)
                                                        .view,
                                                "Oportunidades abertas",
                                                "Aqui você pode ver todos as oportunidades que ainda estão abertos para inscrição.")
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
                                                // Target do Tab "Encerrados" (posição 1)
                                                ((TabLayout) view.findViewById(R.id.tabLayout))
                                                        .getTabAt(1)
                                                        .view,
                                                "Oportunidades encerrados",
                                                "Aqui você pode consultar as oportunidades que já estão encerrados.")
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
                                // Marca que tutorial foi mostrado
                                prefs.edit().putBoolean("tutorial_inscricao_mostrado", true).apply();

                                // Exibe snackbar de sucesso
                                View rootView = requireActivity().findViewById(android.R.id.content);
                                Snackbar snackbar = Snackbar.make(rootView, "Tutorial da inscrição finalizado! 🎉", Snackbar.LENGTH_LONG);

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
                                // Aqui pode implementar ações entre steps, se precisar
                            }

                            @Override
                            public void onSequenceCanceled(TapTarget lastTarget) {
                                Toast.makeText(requireContext(), "Tutorial cancelado.", Toast.LENGTH_SHORT).show();
                            }
                        });

                sequence.start();
            }, 1000);
        }
    }


    private void atualizarTela() {
        if (!isAdded()) return;

        View rootView = requireView();

        Snackbar snackbar = Snackbar.make(rootView, "Oportunidades atualizadas!", Snackbar.LENGTH_SHORT);

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



    // Função para recarregar os fragments
    private void reloadFragments() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        // Reconecta o TabLayout após atualizar
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Abertos" : "Encerrados");
        }).attach();
    }
}
