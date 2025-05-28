package com.example.educapoio;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoticiasAdapterEditar extends RecyclerView.Adapter<NoticiasAdapterEditar.NoticiaViewHolder> {

    private List<Dados> listaNoticias;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditarClick(Dados noticia);
        void onExcluirClick(Dados noticia);
    }

    public NoticiasAdapterEditar(List<Dados> lista, OnItemClickListener listener) {
        this.listaNoticias = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoticiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noticia_editavel, parent, false);
        return new NoticiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiaViewHolder holder, int position) {
        Dados noticia = listaNoticias.get(position);

        holder.editTitulo.setText(noticia.getTitulo());
        holder.editData.setText(noticia.getData());

        // Obtendo o contexto do itemView
        final View itemView = holder.itemView;

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(itemView.getContext(), editar_noticia.class);
            intent.putExtra("id", noticia.getId());
            intent.putExtra("titulo", noticia.getTitulo());
            intent.putExtra("data", noticia.getData());
            itemView.getContext().startActivity(intent);
        });

        holder.btnExcluir.setOnClickListener(v -> listener.onExcluirClick(noticia));
    }

    @Override
    public int getItemCount() {
        return listaNoticias.size();
    }

    public static class NoticiaViewHolder extends RecyclerView.ViewHolder {

        EditText editTitulo, editData;
        ImageButton btnEditar, btnExcluir;

        public NoticiaViewHolder(@NonNull View itemView) {
            super(itemView);
            editTitulo = itemView.findViewById(R.id.noticia_titulo);
            editData = itemView.findViewById(R.id.noticia_data);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}
