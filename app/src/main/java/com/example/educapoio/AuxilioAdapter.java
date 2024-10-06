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

import com.bumptech.glide.Glide;

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

        // Recupera os dados do auxílio
        String titulo = auxilio.get("titulo").toString();
        String dataInicio = auxilio.get("dataInicio").toString();
        String dataFim = auxilio.get("dataFim").toString();
        String url = auxilio.get("url").toString();  // Supondo que exista uma URL no objeto auxilio
        String imagemUrl = auxilio.get("imagemUrl") != null ? auxilio.get("imagemUrl").toString() : null; // Verifica se há URL de imagem

        // Define os textos nos TextViews correspondentes
        holder.titulo.setText(titulo);
        holder.dataInicio.setText("Início: " + dataInicio);
        holder.dataFim.setText("Fim: " + dataFim);

        // Verifica se existe uma URL de imagem
        if (imagemUrl != null && !imagemUrl.isEmpty()) {
            // Se houver uma URL de imagem válida, carrega a imagem do auxílio
            Glide.with(holder.imagemInicial.getContext())
                    .load(imagemUrl)
                    .placeholder(R.drawable.placeholder_image)  // Imagem temporária enquanto carrega
                    .error(R.drawable.error_image)              // Imagem padrão em caso de erro
                    .into(holder.imagemInicial);
        } else {
            // Caso não haja imagem, gera uma imagem circular com a inicial do título
            String inicial = titulo.substring(0, 1).toUpperCase();
            holder.imagemInicial.setImageDrawable(getCircularImage(holder.itemView.getContext(), inicial));
        }

        // Configura o clique no item do RecyclerView
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(url);
            }
        });
    }



    // Método para carregar a imagem usando Glide ou outra biblioteca de carregamento de imagem
    private void carregarImagem(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder_image) // Imagem de carregamento
                .error(R.drawable.error_image) // Imagem em caso de erro
                .into(imageView);
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
            imagemInicial = itemView.findViewById(R.id.imagemAuxilio);
        }
    }

    // Método para gerar imagem circular com a inicial do título
    private Drawable getCircularImage(Context context, String text) {
        int size = 100;  // Tamanho do círculo
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Criação da cor de fundo (círculo)
        Paint paintBg = new Paint();
        paintBg.setColor(Color.BLUE);  // Define a cor azul, mas pode ser personalizada
        paintBg.setAntiAlias(true);  // Suaviza as bordas do círculo
        canvas.drawCircle(size / 2, size / 2, size / 2, paintBg);

        // Criação da letra no centro do círculo
        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);  // Cor da letra branca
        paintText.setTextSize(40);  // Tamanho do texto
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);  // Suaviza as bordas da letra

        // Posição da letra (ajustada verticalmente para ficar centralizada)
        canvas.drawText(text, size / 2, size / 2 + (paintText.getTextSize() / 3), paintText);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    // Interface para lidar com cliques em itens do RecyclerView
    public interface OnItemClickListener {
        void onItemClick(String url);  // Lida com a URL quando o item é clicado
    }
}
