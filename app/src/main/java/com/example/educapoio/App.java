package com.example.educapoio;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializar a biblioteca ThreeTenABP
        AndroidThreeTen.init(this);
    }

    public static class AuxiliosFechadosFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_auxilios_fechados, container, false);

            // Exemplo de consulta ao Firestore para exibir auxílios fechados
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("auxilios").whereEqualTo("status", "fechado")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Aqui você exibe os auxílios fechados
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Exiba os dados de cada auxílio na sua interface (RecyclerView, por exemplo)
                            }
                        } else {
                            // Tratar erro de consulta
                            task.getException().printStackTrace();
                        }
                    });

            return view;
        }
    }
}
