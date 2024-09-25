package com.example.educapoio.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.educapoio.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

public class inscricaoFragment extends Fragment {
    private Button btnAbertos;
    private Button btnFechados;
    private FrameLayout frameLayoutContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("InscricaoFragment", "onCreateView chamado");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inscricao, container, false);
        Log.d("InscricaoFragment", "Layout inflado com sucesso");

        // Inicializa os botões e o layout onde os fragmentos serão carregados
        btnAbertos = view.findViewById(R.id.btn_abertos);
        btnFechados = view.findViewById(R.id.btn_fechados);
        frameLayoutContent = view.findViewById(R.id.frame_layout_content);

        // Carrega inicialmente o fragmento de auxílios abertos
        loadFragment(new AuxiliosAbertosFragment());

        // Define o comportamento dos botões de navegação
        btnAbertos.setOnClickListener(v -> {
            Log.d("InscricaoFragment", "Botão Abertos clicado");
            btnAbertos.setBackgroundTintList(getResources().getColorStateList(R.color.black));
            btnFechados.setBackgroundTintList(getResources().getColorStateList(R.color.black));
            loadFragment(new AuxiliosAbertosFragment());
        });

        btnFechados.setOnClickListener(v -> {
            Log.d("InscricaoFragment", "Botão Fechados clicado");
            btnFechados.setBackgroundTintList(getResources().getColorStateList(R.color.black));
            btnAbertos.setBackgroundTintList(getResources().getColorStateList(R.color.black));
            loadFragment(new AuxiliosFechadosFragment());
        });

        // Verifica automaticamente quando o prazo expirar
        verificarPrazo();

        return view;
    }

    // Função que carrega o fragmento correspondente
    private void loadFragment(Fragment fragment) {
        Log.d("InscricaoFragment", "Carregando fragmento: " + fragment.getClass().getSimpleName());
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
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Status atualizado com sucesso"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Erro ao atualizar status", e));
                        }
                        // Atualiza a interface se necessário
                        if (btnFechados.isPressed()) {
                            loadFragment(new AuxiliosFechadosFragment());
                        }
                    } else {
                        Log.e("Firestore", "Erro ao buscar auxílios: ", task.getException());
                    }
                });
    }
}
