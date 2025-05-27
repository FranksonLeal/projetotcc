package com.example.educapoio;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.educapoio.fragments.NoticiasCategoriaFragment;

public class NoticiasPagerAdapter extends FragmentStateAdapter {

    private final String cursoUsuario;

    public NoticiasPagerAdapter(@NonNull FragmentActivity fragmentActivity, String cursoUsuario) {
        super(fragmentActivity);
        this.cursoUsuario = cursoUsuario;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RecomendadoFragment(); // aqui usa o fragmento que puxa do Firestore

            case 1:
                return NoticiasCategoriaFragment.newInstance(cursoUsuario);

            default:
                return new RecomendadoFragment();
        }
    }





    @Override
    public int getItemCount() {
        return 2; // Apenas duas abas
    }
}
