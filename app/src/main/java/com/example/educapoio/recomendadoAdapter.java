package com.example.educapoio;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class recomendadoAdapter extends RecyclerView.Adapter<recomendadoAdapter.NoticiaViewHolder> {

    private List<recomendado> noticiaList;

    public recomendadoAdapter(List<recomendado> noticiaList) {
        this.noticiaList = noticiaList;
    }

    @NonNull
    @Override
    public NoticiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noticia_recomendado, parent, false);
        return new NoticiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiaViewHolder holder, int position) {
        recomendado noticia = noticiaList.get(position);
        holder.titulo.setText(noticia.getTitulo());
        holder.data.setText(noticia.getDataPublicacao());

        // Botão compartilhar
        holder.btnCompartilhar.setOnClickListener(v -> {
            String textoCompartilhar = "Notícia:\n" + noticia.getTitulo() + "\n\nData: " + noticia.getDataPublicacao();

            if (noticia.getUrl() != null && !noticia.getUrl().isEmpty()) {
                textoCompartilhar += "\n\nLeia mais: " + noticia.getUrl();
            }

            textoCompartilhar += "\n\n💡 Compartilhado via EducNews - Fique sempre por dentro das oportunidades acadêmicas!";

            Intent compartilharIntent = new Intent();
            compartilharIntent.setAction(Intent.ACTION_SEND);
            compartilharIntent.putExtra(Intent.EXTRA_TEXT, textoCompartilhar);
            compartilharIntent.setType("text/plain");

            v.getContext().startActivity(Intent.createChooser(compartilharIntent, "Compartilhar notícia via"));
        });


        // Verificar se tem URL
        if (noticia.getUrl() != null && !noticia.getUrl().isEmpty()) {
            holder.btnVisualizar.setVisibility(View.VISIBLE);
            holder.btnVisualizar.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                intent.putExtra("url", noticia.getUrl());
                v.getContext().startActivity(intent);
            });

        } else {
            holder.btnVisualizar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return noticiaList.size();
    }

    public static class NoticiaViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, data;
        ImageView btnCompartilhar;
        View btnVisualizar;

        public NoticiaViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.noticia_titulo);
            data = itemView.findViewById(R.id.noticia_data);
            btnCompartilhar = itemView.findViewById(R.id.btn_compartilhar);
            btnVisualizar = itemView.findViewById(R.id.btn_visualizar);
        }
    }
}

