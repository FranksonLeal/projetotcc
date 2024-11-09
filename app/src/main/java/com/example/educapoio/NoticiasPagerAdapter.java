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
                return NoticiasCategoriaFragment.newInstance("education");
            case 1:
                return NoticiasCategoriaFragment.newInstance("sport");
            case 2:
                return NoticiasCategoriaFragment.newInstance("tech");
            case 3:
                return NoticiasCategoriaFragment.newInstance("entertainment");
            case 4:
                return NoticiasCategoriaFragment.newInstance("health");
            default:
                return NoticiasCategoriaFragment.newInstance("news");
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Total de tabs
    }
}
