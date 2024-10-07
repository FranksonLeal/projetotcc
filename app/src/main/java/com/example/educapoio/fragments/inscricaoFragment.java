package com.example.educapoio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.educapoio.R;
import com.example.educapoio.fragments.AuxiliosAbertosFragment;
import com.example.educapoio.fragments.AuxiliosFechadosFragment;

public class inscricaoFragment extends Fragment {

    private View underline; // View para a linha embaixo dos botões
    private Button btnAbertos;
    private Button btnFechados;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Infla o layout do fragmento
        View view = inflater.inflate(R.layout.fragment_inscricao, container, false);

        // Botões de navegação
        btnAbertos = view.findViewById(R.id.btn_abertos);
        btnFechados = view.findViewById(R.id.btn_fechados);
        underline = view.findViewById(R.id.underline); // Referência à linha

        // Inicialmente, mostrar os auxílios abertos
        loadFragment(new AuxiliosAbertosFragment());
        underline.setVisibility(View.GONE); // Oculte a linha inicialmente

        // Listener para o botão "Abertos"
        btnAbertos.setOnClickListener(v -> {
            loadFragment(new AuxiliosAbertosFragment());
            underline.setVisibility(View.VISIBLE); // Mostra a linha
            btnAbertos.setTextColor(getResources().getColor(R.color.white)); // Define a cor do botão ativo
            btnFechados.setTextColor(getResources().getColor(R.color.inactive_button_color)); // Define a cor do botão inativo
            underline.setX(btnAbertos.getX()); // Alinha a linha com o botão "Abertos"
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) underline.getLayoutParams();
            params.width = btnAbertos.getWidth(); // Ajusta a largura para o botão ativo
            underline.setLayoutParams(params);
        });

        // Listener para o botão "Fechados"
        btnFechados.setOnClickListener(v -> {
            loadFragment(new AuxiliosFechadosFragment());
            underline.setVisibility(View.VISIBLE); // Mostra a linha
            btnFechados.setTextColor(getResources().getColor(R.color.white)); // Define a cor do botão ativo
            btnAbertos.setTextColor(getResources().getColor(R.color.inactive_button_color)); // Define a cor do botão inativo
            underline.setX(btnFechados.getX()); // Alinha a linha com o botão "Fechados"
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) underline.getLayoutParams();
            params.width = btnFechados.getWidth(); // Ajusta a largura para o botão ativo
            underline.setLayoutParams(params);
        });

        return view; // Retorna a View inflada
    }

    // Método para carregar o fragmento
    private void loadFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frame_layout_content, fragment)
                .commit();
    }
}
