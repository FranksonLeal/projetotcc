package com.example.educapoio.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    private ProgressBar progressBar;  // ProgressBar para exibir enquanto carrega
    private final String[] tabTitles = {"Educação", "Esportes", "Tecnologia", "Entretenimento", "Saúde"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noticias, container, false);

        tabLayoutNoticias = rootView.findViewById(R.id.tabLayoutNoticias);
        viewPagerNoticias = rootView.findViewById(R.id.viewPagerNoticias);
        progressBar = rootView.findViewById(R.id.progressBar);  // Inicia a ProgressBar

        NoticiasPagerAdapter adapter = new NoticiasPagerAdapter(requireActivity());
        viewPagerNoticias.setAdapter(adapter);

        // Definindo as cores do texto usando valores hexadecimais
        tabLayoutNoticias.setTabTextColors(
                Color.parseColor("#B0B0B0"),  // Cor do texto não selecionado (exemplo: cinza)
                Color.parseColor("#841FFD")   // Cor do texto selecionado
        );

        // Associando o TabLayout com o ViewPager2
        new TabLayoutMediator(tabLayoutNoticias, viewPagerNoticias,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // Simulando o carregamento das notícias
        carregarNoticias();

        return rootView;
    }

    private void carregarNoticias() {
        // Mostrar a ProgressBar enquanto as notícias estão sendo carregadas
        progressBar.setVisibility(View.VISIBLE);

        // Simulação de carregamento (substitua com carregamento real, como uma chamada de API)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Simulando tempo de carregamento (exemplo: 2 segundos)
                    Thread.sleep(2000);

                    // Após o carregamento, esconder a ProgressBar
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);  // Esconder a ProgressBar

                            // Aqui você pode atualizar o ViewPager com os dados reais
                            Toast.makeText(requireContext(), "Notícias carregadas!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
