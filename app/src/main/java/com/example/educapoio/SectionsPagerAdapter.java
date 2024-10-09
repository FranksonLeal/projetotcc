package com.example.educapoio;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.educapoio.fragments.AuxiliosAbertosFragment;
import com.example.educapoio.fragments.AuxiliosFechadosFragment;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    public SectionsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Retorna o fragmento apropriado com base na posição
        switch (position) {
            case 0:
                return new AuxiliosAbertosFragment();
            case 1:
                return new AuxiliosFechadosFragment();
            default:
                return new AuxiliosAbertosFragment(); // Fragmento padrão
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Número total de fragmentos
    }
}
