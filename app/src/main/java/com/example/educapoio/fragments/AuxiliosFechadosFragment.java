package com.example.educapoio.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.educapoio.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class AuxiliosFechadosFragment extends Fragment {

    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auxilios_fechados, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutAuxilios); // Certifique-se de ter esse LinearLayout no XML

        buscarAuxiliosFechados();
        return view;
    }

    private void buscarAuxiliosFechados() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference auxiliosRef = db.collection("auxilios");

        auxiliosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String dataFimStr = document.getString("dataFim");
                    LocalDate dataFim = LocalDate.parse(dataFimStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    // Verifica se a data de fim é menor ou igual à data atual
                    if (dataFim.isBefore(LocalDate.now()) || dataFim.isEqual(LocalDate.now())) {
                        adicionarAuxilioAoLayout(document);
                    }
                }
            }
        });
    }

    private void adicionarAuxilioAoLayout(QueryDocumentSnapshot document) {
        String titulo = document.getString("titulo");
        TextView textView = new TextView(getContext());
        textView.setText(titulo);
        linearLayout.addView(textView); // Adiciona o título do auxílio ao LinearLayout
    }
}
