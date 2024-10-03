package com.example.educapoio.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.educapoio.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class AuxiliosAbertosFragment extends Fragment {

    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auxilios_abertos, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutAuxilios); // Certifique-se de ter esse LinearLayout no XML

        buscarAuxiliosAbertos();
        return view;
    }

    private void buscarAuxiliosAbertos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference auxiliosRef = db.collection("auxilios");

        auxiliosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String dataFimStr = document.getString("dataFim");
                    LocalDate dataFim = LocalDate.parse(dataFimStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")); // Certifique-se de usar o formato correto

                    // Verifica se a data de fim é maior que a data atual
                    if (dataFim.isAfter(LocalDate.now())) {
                        adicionarAuxilioAoLayout(document);
                    }
                }
            }
        });
    }



    private void adicionarAuxilioAoLayout(QueryDocumentSnapshot document) {
        // Verifique se o fragmento está anexado
        if (!isAdded()) {
            return; // Não faz nada se não estiver anexado
        }

        String titulo = document.getString("titulo");
        String dataInicio = document.getString("dataInicio");
        String dataFim = document.getString("dataFim");

        // Inflate o layout do item
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View itemView = inflater.inflate(R.layout.item_auxilio, linearLayout, false);

        // Referenciar os TextViews do layout inflado
        TextView textTituloAuxilio = itemView.findViewById(R.id.textTituloAuxilio);
        TextView textDataInicio = itemView.findViewById(R.id.textDataInicio);
        TextView textDataFim = itemView.findViewById(R.id.textDataFim);

        // Definir os textos
        textTituloAuxilio.setText(titulo);
        textDataInicio.setText("Início: " + dataInicio);
        textDataFim.setText("Fim: " + dataFim);

        // Adicionar o itemView ao LinearLayout
        linearLayout.addView(itemView);
    }




}
