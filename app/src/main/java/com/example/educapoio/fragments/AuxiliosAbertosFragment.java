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

public class AuxiliosAbertosFragment extends Fragment {

    private RecyclerView recyclerView;
    private AuxilioAdapterInscricao adapter;
    private List<QueryDocumentSnapshot> auxiliosList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auxilios_abertos, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewAuxiliosAbertos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        buscarAuxiliosAbertos();
        return view;
    }

    private void buscarAuxiliosAbertos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference auxiliosRef = db.collection("auxilios");

        auxiliosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<QueryDocumentSnapshot> documentosAbertos = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Adiciona os auxílios abertos à lista
                    if (isAuxilioAberto(document)) {
                        documentosAbertos.add(document);
                    }
                }

                // Converte para Map<String, Object> e cria o adapter
                List<Map<String, Object>> auxiliosAbertosMap = converterParaMap(documentosAbertos);
                adapter = new AuxilioAdapterInscricao(auxiliosAbertosMap, this::abrirUrl);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Erro ao carregar auxílios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isAuxilioAberto(QueryDocumentSnapshot document) {
        String dataFimStr = document.getString("dataFim");
        LocalDate dataFim = LocalDate.parse(dataFimStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return dataFim.isAfter(LocalDate.now()); // Verifica se a data de fim é futura
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
