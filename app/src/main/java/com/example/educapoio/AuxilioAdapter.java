package com.example.educapoio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    public AuxilioAdapter(List<Map<String, Object>> auxilios) {
        this.auxilios = auxilios;
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

        // Definir o título
        String titulo = auxilio.get("titulo").toString();
        holder.titulo.setText(titulo);

        // Definir as datas
        holder.dataInicio.setText("Início: " + auxilio.get("dataInicio").toString());
        holder.dataFim.setText("Fim: " + auxilio.get("dataFim").toString());

        // Obter a inicial do título e mostrar na imagem circular
        String inicial = titulo.substring(0, 1).toUpperCase();
        holder.imagemInicial.setImageDrawable(getCircularImage(holder.itemView.getContext(), inicial));
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

    // Método para criar a imagem circular com a inicial do título
    private Drawable getCircularImage(Context context, String text) {
        // Criar um bitmap para desenhar a imagem circular
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Definir a cor de fundo do círculo
        Paint paintBg = new Paint();
        paintBg.setColor(Color.BLUE);  // Cor do fundo do círculo
        paintBg.setAntiAlias(true);
        canvas.drawCircle(50, 50, 50, paintBg);

        // Desenhar a inicial do título
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(40);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);
        canvas.drawText(text, 50, 65, paintText);

        return new BitmapDrawable(context.getResources(), bitmap);
    }
}
