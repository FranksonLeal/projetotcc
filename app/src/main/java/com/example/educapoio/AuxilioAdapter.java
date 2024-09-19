package com.example.educapoio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
    private OnItemClickListener onItemClickListener;

    // Construtor atualizado
    public AuxilioAdapter(List<Map<String, Object>> auxilios, OnItemClickListener listener) {
        this.auxilios = auxilios;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public AuxilioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auxilio_horizontal, parent, false);
        return new AuxilioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuxilioViewHolder holder, int position) {
        Map<String, Object> auxilio = auxilios.get(position);

        String titulo = auxilio.get("titulo").toString();
        holder.titulo.setText(titulo);
        holder.dataInicio.setText("Início: " + auxilio.get("dataInicio").toString());
        holder.dataFim.setText("Fim: " + auxilio.get("dataFim").toString());

        String inicial = titulo.substring(0, 1).toUpperCase();
        holder.imagemInicial.setImageDrawable(getCircularImage(holder.itemView.getContext(), inicial));

        holder.itemView.setOnClickListener(v -> {
            String url = auxilio.get("url").toString(); // Supondo que a URL esteja armazenada como "url"
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return auxilios.size();
    }

    public static class AuxilioViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, dataInicio, dataFim;
        ImageView imagemInicial;

        public AuxilioViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTituloAuxilio);
            dataInicio = itemView.findViewById(R.id.textDataInicio);
            dataFim = itemView.findViewById(R.id.textDataFim);
            imagemInicial = itemView.findViewById(R.id.imagemInicial);
        }
    }

    private Drawable getCircularImage(Context context, String text) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paintBg = new Paint();
        paintBg.setColor(Color.BLUE);
        paintBg.setAntiAlias(true);
        canvas.drawCircle(50, 50, 50, paintBg);

        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(40);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);
        canvas.drawText(text, 50, 65, paintText);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    // Interface para clique no item
    public interface OnItemClickListener {
        void onItemClick(String url);
    }
}
