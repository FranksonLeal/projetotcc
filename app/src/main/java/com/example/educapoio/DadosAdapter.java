package com.example.educapoio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DadosAdapter extends RecyclerView.Adapter<DadosAdapter.ViewHolder> {

    private List<Dados> listaDados;
    private Context context;
    private String tipo;

    public interface OnItemActionListener {
        void onEditarClicked(Dados dados);
        void onExcluirClicked(Dados dados);
    }

    private OnItemActionListener actionListener;

    public DadosAdapter(List<Dados> listaDados, Context context, String tipo, OnItemActionListener listener) {
        this.listaDados = listaDados;
        this.context = context;
        this.tipo = tipo;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dados_editavel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dados dados = listaDados.get(position);

        holder.titulo.setText(dados.getTitulo());
        holder.descricaoInicio.setText("Início: " + dados.getDataInicio());
        holder.descricaoFim.setText("Fim: " + dados.getDataFim());

        // Carregar imagem usando Glide
        if (dados.getImagemUrl() != null && !dados.getImagemUrl().isEmpty()) {
            Glide.with(context)
                    .load(dados.getImagemUrl())
                    .placeholder(R.drawable.placeholder_image) // coloque uma imagem placeholder se quiser
                    .into(holder.imagemAuxilio);
        } else {
            holder.imagemAuxilio.setImageResource(R.drawable.placeholder_image); // imagem padrão
        }

        holder.btnEditar.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onEditarClicked(dados);
        });

        holder.btnExcluir.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onExcluirClicked(dados);
        });
    }

    @Override
    public int getItemCount() {
        return listaDados.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView descricaoInicio, descricaoFim;
        Button btnEditar, btnExcluir;
        ImageView imagemAuxilio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTituloAuxilio);
            descricaoInicio = itemView.findViewById(R.id.textDataInicio);
            descricaoFim = itemView.findViewById(R.id.textDataFim);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            imagemAuxilio = itemView.findViewById(R.id.imagemAuxilio);
        }
    }
}
