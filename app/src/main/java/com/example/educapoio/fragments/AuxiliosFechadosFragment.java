package com.example.educapoio.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.educapoio.AuxilioAdapterInscricao;
import com.example.educapoio.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuxiliosFechadosFragment extends Fragment {

    private RecyclerView recyclerView;
    private AuxilioAdapterInscricao adapter;
    private List<QueryDocumentSnapshot> auxiliosFechados;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auxilios_fechados, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewAuxiliosFechados);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Buscando o TextView dentro da view inflada
        TextView txtNoAuxiliosFechados = view.findViewById(R.id.txtNoAuxiliosFechados);

        buscarAuxiliosFechados(view, txtNoAuxiliosFechados); // Passando a view e o TextView para o método buscarAuxiliosFechados
        return view;
    }

    private void buscarAuxiliosFechados(View view, TextView txtNoAuxiliosFechados) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference auxiliosRef = db.collection("auxilios");

        auxiliosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<QueryDocumentSnapshot> documentosFechados = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String dataFimStr = document.getString("dataFim");
                    LocalDate dataFim = LocalDate.parse(dataFimStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    if (dataFim.isBefore(LocalDate.now()) || dataFim.isEqual(LocalDate.now())) {
                        documentosFechados.add(document);
                    }
                }

                // Converte para Map<String, Object> e cria o adapter
                List<Map<String, Object>> auxiliosFechadosMap = converterParaMap(documentosFechados);
                adapter = new AuxilioAdapterInscricao(auxiliosFechadosMap, this::abrirUrl);
                recyclerView.setAdapter(adapter);

                // Verifica se há auxílios fechados e atualiza a visibilidade do TextView
                if (auxiliosFechadosMap.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    txtNoAuxiliosFechados.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    txtNoAuxiliosFechados.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(getContext(), "Erro ao carregar auxílios fechados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Map<String, Object>> converterParaMap(List<QueryDocumentSnapshot> auxilios) {
        List<Map<String, Object>> listaAuxilios = new ArrayList<>();
        for (QueryDocumentSnapshot document : auxilios) {
            listaAuxilios.add(document.getData()); // Adiciona o Map do documento à lista
        }
        return listaAuxilios;
    }

    private void abrirUrl(String url) {
        // Implementação para abrir a URL
    }
}
