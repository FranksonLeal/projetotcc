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
import com.example.educapoio.WebViewActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auxilios_fechados, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewAuxiliosFechados);
        progressBar = view.findViewById(R.id.progressBarLoading);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Buscando o TextView dentro da view inflada
        TextView txtNoAuxiliosFechados = view.findViewById(R.id.txtNoAuxiliosFechados);

        buscarAuxiliosFechados(view, txtNoAuxiliosFechados); // Passando a view e o TextView para o método buscarAuxiliosFechados
        return view;
    }

    private void buscarAuxiliosFechados(View view, TextView txtNoAuxiliosFechados) {
        // Exibe a ProgressBar enquanto os dados estão sendo carregados
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference auxiliosRef = db.collection("auxilios");

        auxiliosRef.get().addOnCompleteListener(task -> {
            // Ao finalizar a busca
            progressBar.setVisibility(View.GONE);  // Sempre escondemos a ProgressBar aqui

            if (task.isSuccessful()) {
                List<QueryDocumentSnapshot> documentosFechados = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String dataFimStr = document.getString("dataFim");
                    LocalDate dataFim = LocalDate.parse(dataFimStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    // Verifica se o auxílio está fechado
                    if (dataFim.isBefore(LocalDate.now())) {
                        documentosFechados.add(document);
                    }
                }

                // Converte para Map<String, Object> e cria o adapter
                List<Map<String, Object>> auxiliosFechadosMap = converterParaMap(documentosFechados);

                // Ajuste aqui: implementação anônima para interface com múltiplos métodos
                adapter = new AuxilioAdapterInscricao(auxiliosFechadosMap, new AuxilioAdapterInscricao.OnItemClickListener() {
                    @Override
                    public void onItemClick(String url) {
                        abrirUrl(url);
                    }

                    @Override
                    public void onShareClick(Map<String, Object> auxilio) {
                        compartilharAuxilio(auxilio);
                    }
                });




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
    private void compartilharAuxilio(Map<String, Object> auxilio) {
        String titulo = (String) auxilio.get("titulo");

        String url = (String) auxilio.get("url");

        String textoParaCompartilhar = "🚀 Oportunidade incrível para você!\n\n"
                + "📌 *" + titulo + "*\n";


        if (url != null && !url.isEmpty()) {
            textoParaCompartilhar += "\n🔗 Acesse aqui: " + url + "\n";
        }

        textoParaCompartilhar += "\n💡 Compartilhado via EducNews - Fique sempre por dentro das oportunidades acadêmicas!";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, textoParaCompartilhar);

        startActivity(Intent.createChooser(shareIntent, "Compartilhar auxílio via"));
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
        View bottomSheetView = getLayoutInflater().inflate(R.layout.toast_custom_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView dialogMessage = bottomSheetView.findViewById(R.id.dialog_message);
        dialogMessage.setText("Você tem certeza que deseja abrir o link?");

        Button buttonConfirm = bottomSheetView.findViewById(R.id.button_open_url);
        buttonConfirm.setOnClickListener(v -> {
            abrirWebView(url);  // Agora abre na WebView
            bottomSheetDialog.dismiss();
        });

        Button buttonCancel = bottomSheetView.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void abrirWebView(String url) {
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
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
