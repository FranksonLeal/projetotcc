package com.example.educapoio;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.educapoio.fragments.NoticiasCategoriaFragment;

public class NoticiasPagerAdapter extends FragmentStateAdapter {

    private final String cursoUsuario; // Curso do usuário obtido do banco de dados

    public NoticiasPagerAdapter(@NonNull FragmentActivity fragmentActivity, String cursoUsuario) {
        super(fragmentActivity);
        this.cursoUsuario = cursoUsuario; // Inicializa o curso do usuário
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // Envia o curso do usuário para o fragmento de "Recomendado"
                return NoticiasCategoriaFragment.newInstance(cursoUsuario);

            case 1:
                return NoticiasCategoriaFragment.newInstance("education");

            case 2:
                return NoticiasCategoriaFragment.newInstance("tech");
            case 3:
                return NoticiasCategoriaFragment.newInstance("entertainment");
            case 4:
                return NoticiasCategoriaFragment.newInstance("health");
            case 5:
                return NoticiasCategoriaFragment.newInstance("sport");
            default:
                return NoticiasCategoriaFragment.newInstance("news");
        }
    }

    @Override
    public int getItemCount() {
        return 6; // Adicionamos a aba "Recomendado"
    }
}
