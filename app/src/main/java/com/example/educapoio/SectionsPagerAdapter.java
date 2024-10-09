package com.example.educapoio;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.educapoio.fragments.AuxiliosAbertosFragment; // Altere para o nome do seu fragmento de auxílios abertos
import com.example.educapoio.fragments.AuxiliosFechadosFragment; // Altere para o nome do seu fragmento de auxílios fechados

public class SectionsPagerAdapter extends FragmentStateAdapter {

    public SectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AuxiliosAbertosFragment(); // Retorne o fragmento de auxílios abertos
            case 1:
                return new AuxiliosFechadosFragment(); // Retorne o fragmento de auxílios fechados
            default:
                return new AuxiliosAbertosFragment(); // Retorno padrão (caso necessário)
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Número de abas (Abertos e Fechados)
    }
}
