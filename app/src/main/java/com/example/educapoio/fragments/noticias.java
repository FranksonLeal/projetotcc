package com.example.educapoio.fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
                // Simula carregamento das notícias (substitua isso por sua lógica real de carregamento)
                Thread.sleep(1500);

                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    finalizarCarregamento();
                    if (mostrarToast) {
                        Toast.makeText(requireContext(), "Dados atualizados!", Toast.LENGTH_SHORT).show();
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
