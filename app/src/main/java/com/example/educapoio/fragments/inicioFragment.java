package com.example.educapoio.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.educapoio.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.educapoio.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link inicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class inicioFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public inicioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment inicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static inicioFragment newInstance(String param1, String param2) {
        inicioFragment fragment = new inicioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private TextView textoOla;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private void carregarNomeUsuario(String userId) {
        // Referência ao documento do usuário no Firestore
        DocumentReference docRef = db.collection("users").document(userId);

        // Busca o documento do Firestore
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Extrai o nome do usuário
                    String nome = document.getString("nome");

                    // Exibe o nome do usuário ao lado de "Olá"
                    textoOla.setText("Olá, " + nome);
                } else {
                    // Documento não existe, exibe mensagem padrão
                    textoOla.setText("Olá, usuário!");
                }
            } else {
                // Erro ao acessar o Firestore, exibe mensagem padrão
                textoOla.setText("Olá, usuário!");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inicio, container, false);

        // Inicializa FirebaseAuth e Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializa o TextView do 'Olá, Nome'
        textoOla = rootView.findViewById(R.id.texto2);

        // Obtém o usuário autenticado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            carregarNomeUsuario(userId);
        }






        ImageView imagemSuper = rootView.findViewById(R.id.imagemSuper);
        ImageView imagemADM = rootView.findViewById(R.id.imagemADM);
        ImageView imagemEstagio = rootView.findViewById(R.id.imagemEstagio);
        ImageView imagemBemol = rootView.findViewById(R.id.imagemBemol);

        ImageView imagemTi = rootView.findViewById(R.id.imagemTi);
        ImageView imagemSaude = rootView.findViewById(R.id.imagemSaude);
        ImageView imagemAdm = rootView.findViewById(R.id.imagemAdm);
        ImageView imagemContabilidade = rootView.findViewById(R.id.imagemContabilidade);
        ImageView imagemDireito = rootView.findViewById(R.id.imagemDireito);
        ImageView imagemBanco = rootView.findViewById(R.id.imagemBanco);


        // Adicionando OnClickListener à ImageView
        imagemADM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para um determinado endereço web
                String url = "https://docs.google.com/forms/d/e/1FAIpQLScJj5amCFBdYHt1UBgB339qmRLXlcP_TojuW339i6RZuh-Aow/viewform";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // Adicionando OnClickListener à ImageView
        imagemSuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para um determinado endereço web
                String url = "https://super.ufam.edu.br/editais/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // Adicionando OnClickListener à ImageView
        imagemEstagio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para um determinado endereço web
                String url = "https://docs.google.com/forms/d/e/1FAIpQLSeZDbhgm4Tx0PJdfLE3vr2mKsTzULuvXoErgAQSl7a3khEK9Q/viewform";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // Adicionando OnClickListener à ImageView
        imagemBemol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para um determinado endereço web
                String url = "https://bemoldigital.gupy.io/jobs/6842989?jobBoardSource=gupy_public_page";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        //SEÇÃO DE CONCURSOS
        imagemTi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para um determinado endereço web
                String url = "https://www.grancursosonline.com.br/cursos/carreira/tecnologia-da-informacao?utm_medium=ppc&utm_campaign=&utm_term=&gad_source=1&gclid=CjwKCAiA0bWvBhBjEiwAtEsoW5GwNxDHSc3LSIluryHJmvVpm44-E6YIkdZcsCeWDIq5I__9IcSI1BoCqUUQAvD_BwE&gclsrc=aw.ds";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        imagemSaude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para um determinado endereço web
                String url = "https://www.estrategiaconcursos.com.br/blog/concursos-area-da-saude/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        imagemAdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para um determinado endereço web
                String url = "https://jcconcursos.com.br/concursos/por-cargo/administrador";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        imagemContabilidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para um determinado endereço web
                String url = "https://www.estrategiaconcursos.com.br/blog/concursos-contabilidade/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        imagemDireito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para um determinado endereço web
                String url = "https://cj.estrategia.com/portal/concursos-de-direito/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        imagemBanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar o usuário para um determinado endereço web
                String url = "https://www.estrategiaconcursos.com.br/blog/concursos-bancarios/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        return rootView;
    }

}