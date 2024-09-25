package com.example.educapoio.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.educapoio.App;
import com.example.educapoio.R;
import com.example.educapoio.cadastro;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

public class InscricoesFragment extends Fragment {

    private Button btnAbertos;
    private Button btnFechados;
    private FrameLayout frameLayoutContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inscricao, container, false);

        // Inicializa os botões e o layout onde os fragmentos serão carregados
        btnAbertos = view.findViewById(R.id.btn_abertos);
        btnFechados = view.findViewById(R.id.btn_fechados);
        frameLayoutContent = view.findViewById(R.id.frame_layout_content);

        // Carrega inicialmente o fragmento de auxílios abertos
        loadFragment(new cadastro.AuxiliosAbertosFragment());

        // Define o comportamento dos botões de navegação
        btnAbertos.setOnClickListener(v -> {
            // Altera a aparência dos botões (selecionado e não selecionado)
            btnAbertos.setBackgroundTintList(getResources().getColorStateList(R.color.black));
            btnFechados.setBackgroundTintList(getResources().getColorStateList(R.color.black));

            // Carrega o fragmento de auxílios abertos
            loadFragment(new cadastro.AuxiliosAbertosFragment());
        });

        btnFechados.setOnClickListener(v -> {
            // Altera a aparência dos botões (selecionado e não selecionado)
            btnFechados.setBackgroundTintList(getResources().getColorStateList(R.color.black));
            btnAbertos.setBackgroundTintList(getResources().getColorStateList(R.color.black));

            // Carrega o fragmento de auxílios fechados
            loadFragment(new App.AuxiliosFechadosFragment());
        });

        // Verifica automaticamente quando o prazo expirar e move os auxílios para fechados
        verificarPrazo();

        return view;
    }

    // Função que carrega o fragmento correspondente
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_content, fragment);
        transaction.commit();
    }

    // Função que verifica o prazo de expiração dos auxílios e atualiza o status no Firestore
    private void verificarPrazo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference auxiliosRef = db.collection("auxilios");

        // Busca auxílios com data de expiração passada
        auxiliosRef.whereLessThanOrEqualTo("dataFim", new Date())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Atualiza o status do auxílio para "fechado"
                            document.getReference().update("status", "fechado")
                                    .addOnSuccessListener(aVoid -> {
                                        // Sucesso ao mover o auxílio para "fechado"
                                    })
                                    .addOnFailureListener(e -> {
                                        // Erro ao atualizar status
                                        e.printStackTrace();
                                    });
                        }

                        // Se a tela de auxílios fechados estiver sendo exibida, atualiza a interface
                        if (btnFechados.isPressed()) {
                            loadFragment(new App.AuxiliosFechadosFragment());
                        }
                    } else {
                        // Tratamento de erro na busca
                        task.getException().printStackTrace();
                    }
                });
    }
}
