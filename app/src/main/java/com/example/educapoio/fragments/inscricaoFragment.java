package com.example.educapoio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.educapoio.R;
import com.example.educapoio.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class inscricaoFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Infla o layout do fragmento
        View view = inflater.inflate(R.layout.fragment_inscricao, container, false);

        // Inicializa o ViewPager2 e TabLayout
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this);
        viewPager.setAdapter(sectionsPagerAdapter);

        // Conecta o TabLayout com o ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Abertos");
            } else {
                tab.setText("Fechados");
            }
        }).attach();

        return view; // Retorna a View inflada
    }
}
