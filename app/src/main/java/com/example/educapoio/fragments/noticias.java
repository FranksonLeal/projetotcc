package com.example.educapoio.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.educapoio.NoticiasPagerAdapter;
import com.example.educapoio.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class noticias extends Fragment {

    private TabLayout tabLayoutNoticias;
    private ViewPager2 viewPagerNoticias;
    private final String[] tabTitles = {"Todas", "Esportes", "Tecnologia", "Entretenimento", "Saúde"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noticias, container, false);

        tabLayoutNoticias = rootView.findViewById(R.id.tabLayoutNoticias);
        viewPagerNoticias = rootView.findViewById(R.id.viewPagerNoticias);

        NoticiasPagerAdapter adapter = new NoticiasPagerAdapter(requireActivity());
        viewPagerNoticias.setAdapter(adapter);

        // Definindo as cores do texto usando valores hexadecimais
        tabLayoutNoticias.setTabTextColors(
                Color.parseColor("#B0B0B0"),  // Cor do texto não selecionado (exemplo: cinza)
                Color.parseColor("#841FFD")   // Cor do texto selecionado
        );
        new TabLayoutMediator(tabLayoutNoticias, viewPagerNoticias,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        return rootView;
    }
}
