package com.example.educapoio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;
import java.util.Map;

public class AuxilioAdapter extends RecyclerView.Adapter<AuxilioAdapter.AuxilioViewHolder> {

    private List<Map<String, Object>> auxilios;

    public AuxilioAdapter(List<String> auxilios) {
        this.auxilios = auxilios;
    }

    @NonNull
    @Override
    public AuxilioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_auxilio, parent, false);
        return new AuxilioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuxilioViewHolder holder, int position) {
        Map<String, Object> auxilio = auxilios.get(position);
        holder.title.setText((String) auxilio.get("titulo"));
        holder.dates.setText("Início: " + auxilio.get("dataInicio") + " - Fim: " + auxilio.get("dataFim"));

        // Carregar imagem se disponível, usando Picasso ou Glide
        // Exemplo com Picasso:
        // Picasso.get().load((String) auxilio.get("imagemUrl")).into(holder.image);
        holder.image.setImageResource(R.drawable.default_image); // Imagem padrão se não houver URL
    }

    @Override
    public int getItemCount() {
        return auxilios.size();
    }

    public static class AuxilioViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView dates;

        public AuxilioViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.auxilio_image);
            title = itemView.findViewById(R.id.auxilio_title);
            dates = itemView.findViewById(R.id.auxilio_dates);
        }
    }
}
