package com.example.educapoio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.NoticiaViewHolder> {

    private List<Article> articles;

    public NoticiasAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @NonNull
    @Override
    public NoticiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noticia, parent, false);
        return new NoticiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiaViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());

        // Carregar imagem da notícia usando Glide
        Glide.with(holder.itemView.getContext())
                .load(article.getUrlToImage())
                .into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return articles.size();
    }

    class NoticiaViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView imageView;

        public NoticiaViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.noticia_titulo);
            description = itemView.findViewById(R.id.noticia_descricao);
            imageView = itemView.findViewById(R.id.noticia_imagem);
        }
    }
}
