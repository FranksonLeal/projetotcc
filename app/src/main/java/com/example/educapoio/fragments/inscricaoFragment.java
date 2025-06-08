package com.example.educapoio.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
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
