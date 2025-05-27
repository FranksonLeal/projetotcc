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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AuxilioAdapterInscricao extends RecyclerView.Adapter<AuxilioAdapterInscricao.AuxilioViewHolder> {

    private final List<Map<String, Object>> auxilios;
    private final OnItemClickListener onItemClickListener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public AuxilioAdapterInscricao(List<Map<String, Object>> auxilios, OnItemClickListener listener) {
        this.auxilios = auxilios;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public AuxilioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auxilio, parent, false);
        return new AuxilioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuxilioViewHolder holder, int position) {
        Map<String, Object> auxilio = auxilios.get(position);

        String titulo = auxilio.get("titulo") != null ? auxilio.get("titulo").toString() : "Sem Título";
        String dataInicio = auxilio.get("dataInicio") != null ? auxilio.get("dataInicio").toString() : "00/00/0000";
        String dataFim = auxilio.get("dataFim") != null ? auxilio.get("dataFim").toString() : "00/00/0000";
        String url = auxilio.get("url") != null ? auxilio.get("url").toString() : null;
        String imagemUrl = auxilio.get("imagemUrl") != null ? auxilio.get("imagemUrl").toString() : null;

        holder.titulo.setText(titulo);
        holder.dataInicio.setText("Início: " + dataInicio);
        holder.dataFim.setText("Fim: " + dataFim);

        // Carregar imagem ou gerar círculo com inicial
        if (imagemUrl != null && !imagemUrl.isEmpty()) {
            Glide.with(holder.imagemInicial.getContext())
                    .load(imagemUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.imagemInicial);
        } else {
            String inicial = !titulo.isEmpty() ? titulo.substring(0, 1).toUpperCase() : "?";
            holder.imagemInicial.setImageDrawable(getCircularImage(holder.itemView.getContext(), inicial));
        }

        // Botão inscrição
        holder.botaoInscricao.setOnClickListener(v -> {
            if (url != null && !url.isEmpty() && onItemClickListener != null) {
                onItemClickListener.onItemClick(url);
            }
        });

        // Clique no ícone compartilhar
        holder.ic_share.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onShareClick(auxilio);
            }
        });

        // Atualiza o selo status baseado na data
        if (isAuxilioAberto(dataInicio, dataFim)) {
            holder.seloStatus.setText("Aberto");
            holder.seloStatus.setTextColor(Color.parseColor("#4CAF50")); // verde
        } else {
            holder.seloStatus.setText("Encerrado");
            holder.seloStatus.setTextColor(Color.parseColor("#FF0000")); // vermelho
        }
    }

    // Retorna true se o auxílio está ABERTO no dia de hoje (dataInicio <= hoje <= dataFim)
    private boolean isAuxilioAberto(String dataInicioStr, String dataFimStr) {
        try {
            Date dataInicio = sdf.parse(dataInicioStr);
            Date dataFim = sdf.parse(dataFimStr);
            Date hoje = sdf.parse(sdf.format(new Date())); // Zera horário para comparar só datas

            if (dataInicio == null || dataFim == null) return false;

            // Está aberto se: dataInicio <= hoje <= dataFim
            return !hoje.before(dataInicio) && !hoje.after(dataFim);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return auxilios.size();
    }

    public static class AuxilioViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, dataInicio, dataFim, seloStatus;
        ImageView imagemInicial;
        ImageView ic_share;  // Ícone de compartilhar
        Button botaoInscricao;

        public AuxilioViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTituloAuxilio);
            dataInicio = itemView.findViewById(R.id.textDataInicio);
            dataFim = itemView.findViewById(R.id.textDataFim);
            imagemInicial = itemView.findViewById(R.id.imagemAuxilio);
            seloStatus = itemView.findViewById(R.id.seloStatus);
            botaoInscricao = itemView.findViewById(R.id.botaoInscricao);
            ic_share = itemView.findViewById(R.id.ic_share);  // Pega o ícone de compartilhar pelo id
        }
    }

    private Drawable getCircularImage(Context context, String text) {
        int size = 100;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paintBg = new Paint();
        paintBg.setColor(Color.BLUE);
        paintBg.setAntiAlias(true);
        canvas.drawCircle(size / 2, size / 2, size / 2, paintBg);

        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(40);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);
        canvas.drawText(text, size / 2, size / 2 + (paintText.getTextSize() / 3), paintText);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public interface OnItemClickListener {
        void onItemClick(String url);         // Clique no botão inscrição
        void onShareClick(Map<String, Object> auxilio);  // Clique no ícone compartilhar
    }
}
