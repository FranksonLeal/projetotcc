package com.example.educapoio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.educapoio.R;
import com.example.educapoio.fragments.AuxiliosAbertosFragment;
import com.example.educapoio.fragments.AuxiliosFechadosFragment;

public class inscricaoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Infla o layout do fragmento
        View view = inflater.inflate(R.layout.fragment_inscricao, container, false);

        // Botões de navegação
        Button btnAbertos = view.findViewById(R.id.btn_abertos);
        Button btnFechados = view.findViewById(R.id.btn_fechados);

        // Inicialmente, mostrar os auxílios abertos
        loadFragment(new AuxiliosAbertosFragment());

        // Listener para o botão "Abertos"
        btnAbertos.setOnClickListener(v -> loadFragment(new AuxiliosAbertosFragment()));

        // Listener para o botão "Fechados"
        btnFechados.setOnClickListener(v -> loadFragment(new AuxiliosFechadosFragment()));

        return view; // Retorna a View inflada
    }

    // Método para carregar o fragmento
    private void loadFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frame_layout_content, fragment)
                .commit();
    }
}
