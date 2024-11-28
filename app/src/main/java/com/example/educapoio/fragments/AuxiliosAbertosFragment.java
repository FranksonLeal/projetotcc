package com.example.educapoio.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.educapoio.AuxilioAdapterInscricao;
import com.example.educapoio.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
    private ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auxilios_abertos, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewAuxiliosAbertos);
        progressBar = view.findViewById(R.id.progressBarLoading);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        buscarAuxiliosAbertos();
        return view;
    }

    private void buscarAuxiliosAbertos() {
        // Exibe a ProgressBar enquanto os dados estão sendo carregados
        progressBar.setVisibility(View.VISIBLE);

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

                // Verifica se existem auxílios abertos
                if (documentosAbertos.isEmpty()) {
                    // Se não houver auxílios, exibe a mensagem
                    TextView noAuxiliosMessage = getView().findViewById(R.id.txtNoAuxilios);
                    noAuxiliosMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE); // Esconde o RecyclerView
                } else {
                    // Se houver auxílios, oculta a mensagem e exibe o RecyclerView
                    TextView noAuxiliosMessage = getView().findViewById(R.id.txtNoAuxilios);
                    noAuxiliosMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    // Converte para Map<String, Object> e cria o adapter
                    List<Map<String, Object>> auxiliosAbertosMap = converterParaMap(documentosAbertos);
                    adapter = new AuxilioAdapterInscricao(auxiliosAbertosMap, this::abrirUrl);
                    recyclerView.setAdapter(adapter);
                }

                // Esconde a ProgressBar após carregar os dados
                progressBar.setVisibility(View.GONE);
            } else {
                // Esconde a ProgressBar após carregar os dados
                progressBar.setVisibility(View.GONE);
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
        if (url != null && !url.isEmpty()) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            showConfirmationDialog(url);
        } else {
            showCustomMessage("URL inválida");
        }
    }


    private void showConfirmationDialog(final String url) {
        // Infla o layout do BottomSheetDialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.toast_custom_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Configura a mensagem de confirmação
        TextView dialogMessage = bottomSheetView.findViewById(R.id.dialog_message);
        dialogMessage.setText("Você tem certeza que deseja abrir o link?");

        // Configura os botões de confirmação
        Button buttonConfirm = bottomSheetView.findViewById(R.id.button_open_url);
        buttonConfirm.setOnClickListener(v -> {
            // Tenta abrir o link quando confirmado
            abrirUrlIntent(url);
            bottomSheetDialog.dismiss(); // Fecha o dialog após a ação
        });

        Button buttonCancel = bottomSheetView.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(v -> {
            // Fecha o dialog sem fazer nada
            bottomSheetDialog.dismiss();
        });

        // Exibe o BottomSheetDialog
        bottomSheetDialog.show();
    }

    private void abrirUrlIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Se não for possível abrir a URL
            showCustomMessage("Não foi possível abrir o link.");
        }
    }

    private void showCustomMessage(String message) {
        // Infla o layout do BottomSheetDialog para exibir a mensagem
        View bottomSheetView = getLayoutInflater().inflate(R.layout.toast_custom_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Configura a mensagem de erro
        TextView toastMessage = bottomSheetView.findViewById(R.id.dialog_message);
        toastMessage.setText(message);

        // Exibe o BottomSheetDialog
        bottomSheetDialog.show();
    }
}
