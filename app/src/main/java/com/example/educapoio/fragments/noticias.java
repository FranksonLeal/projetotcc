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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class noticias extends Fragment {

    private TabLayout tabLayoutNoticias;
    private ViewPager2 viewPagerNoticias;
    private ProgressBar progressBar;
    private final String[] tabTitles = {"Recomendado","Educação", "Tecnologia", "Saúde", "Entretenimento", "Esporte" };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noticias, container, false);

        tabLayoutNoticias = rootView.findViewById(R.id.tabLayoutNoticias);
        viewPagerNoticias = rootView.findViewById(R.id.viewPagerNoticias);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Buscar o curso do usuário antes de inicializar o adapter
        carregarCursoUsuario();
        carregarNoticias(); // Chamada aqui

        return rootView;
    }

    private void carregarCursoUsuario() {
        progressBar.setVisibility(View.VISIBLE);

        // Simulação de consulta ao Firebase para obter o curso do usuário
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String cursoUsuario = documentSnapshot.getString("curso");
                        if (cursoUsuario != null) {
                            inicializarViewPager(cursoUsuario);
                        } else {
                            Toast.makeText(requireContext(), "Curso não encontrado!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Erro ao carregar o curso!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void inicializarViewPager(String cursoUsuario) {
        NoticiasPagerAdapter adapter = new NoticiasPagerAdapter(requireActivity(), cursoUsuario);
        viewPagerNoticias.setAdapter(adapter);

        // Configurar as cores do TabLayout
        tabLayoutNoticias.setTabTextColors(
                Color.parseColor("#B0B0B0"), // Cinza
                Color.parseColor("#841FFD") // Roxo
        );

        // Conectar o TabLayout ao ViewPager
        new TabLayoutMediator(tabLayoutNoticias, viewPagerNoticias,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
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
