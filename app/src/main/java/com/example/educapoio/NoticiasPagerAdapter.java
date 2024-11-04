package com.example.educapoio;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.educapoio.fragments.NoticiasCategoriaFragment;

public class NoticiasPagerAdapter extends FragmentStateAdapter {

    public NoticiasPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return NoticiasCategoriaFragment.newInstance("todas");
            case 1:
                return NoticiasCategoriaFragment.newInstance("esportes");
            case 2:
                return NoticiasCategoriaFragment.newInstance("tecnologia");
            case 3:
                return NoticiasCategoriaFragment.newInstance("entretenimento");
            case 4:
                return NoticiasCategoriaFragment.newInstance("saúde");
            default:
                return NoticiasCategoriaFragment.newInstance("todas");
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Total de tabs
    }
}
