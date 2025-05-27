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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuxilioAdapter extends RecyclerView.Adapter<AuxilioAdapter.AuxilioViewHolder> {

    private List<Map<String, Object>> auxilios;
    private OnItemClickListener onItemClickListener;

    // Construtor atualizado para garantir que 'auxilios' nunca seja nulo
    public AuxilioAdapter(List<Map<String, Object>> auxilios, OnItemClickListener listener) {
        this.auxilios = auxilios != null ? auxilios : new ArrayList<>();  // Evita NullPointerException
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

        String titulo = auxilio.get("titulo") != null ? auxilio.get("titulo").toString() : "Título indisponível";
        String dataInicio = auxilio.get("dataInicio") != null ? auxilio.get("dataInicio").toString() : "Data de início não disponível";
        String dataFim = auxilio.get("dataFim") != null ? auxilio.get("dataFim").toString() : "Data de fim não disponível";
        String url = auxilio.get("url") != null ? auxilio.get("url").toString() : "";

        // ADICIONE ESSA LINHA:
        String imagemUrl = auxilio.get("imagem") != null ? auxilio.get("imagem").toString() : "";

        // Atualiza os TextViews e imagem
        holder.titulo.setText(titulo);
        holder.dataInicio.setText("Início: " + dataInicio);
        holder.dataFim.setText("Fim: " + dataFim);

        if (imagemUrl != null && !imagemUrl.isEmpty()) {
            Glide.with(holder.imagemInicial.getContext())
                    .load(imagemUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.imagemInicial);
        } else {
            String inicial = titulo.substring(0, 1).toUpperCase();
            holder.imagemInicial.setImageDrawable(getCircularImage(holder.itemView.getContext(), inicial));
        }

        // Clique para abrir URL
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(url);
            }
        });

        // Clique para compartilhar
        holder.buttonCompartilhar.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                String textoParaCompartilhar = "Auxílio: " + titulo +
                        "\nInício: " + dataInicio +
                        "\nFim: " + dataFim +
                        (url.isEmpty() ? "" : "\nSaiba mais: " + url);
                onItemClickListener.onShareClick(textoParaCompartilhar);
            }
        });
    }

    @Override
    public int getItemCount() {
        return auxilios.size();
    }

    public static class AuxilioViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, dataInicio, dataFim;
        ImageView imagemInicial, buttonCompartilhar;

        public AuxilioViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTituloAuxilio);
            dataInicio = itemView.findViewById(R.id.textDataInicio);
            dataFim = itemView.findViewById(R.id.textDataFim);
            imagemInicial = itemView.findViewById(R.id.imagemAuxilio);
            buttonCompartilhar = itemView.findViewById(R.id.buttonCompartilhar);  // Aqui
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
        void onItemClick(String url);
        void onShareClick(String textoParaCompartilhar);

    }


    // Método para atualizar a lista de auxílios no adapter
    // Método para atualizar a lista de auxílios e notificar o adapter
    // Método para atualizar a lista de auxílios e notificar o adapter
    public void updateAuxilios(List<Map<String, Object>> newAuxilios) {
        this.auxilios.clear();  // Limpa a lista existente
        this.auxilios.addAll(newAuxilios);  // Adiciona os novos auxílios
        notifyDataSetChanged();  // Notifica o adapter sobre a mudança
    }

    private String gerarTextoCompartilhamento(Map<String, Object> auxilio) {
        String titulo = (String) auxilio.get("titulo");
        String dataInicio = (String) auxilio.get("dataInicio");
        String dataFim = (String) auxilio.get("dataFim");
        String url = (String) auxilio.get("url");

        String texto = "🚀 *Oportunidade Acadêmica Disponível!*\n\n"
                + "📌 *Título:* " + titulo + "\n"
                + "🗓️ *Período:* " + dataInicio + " até " + dataFim + "\n";

        if (url != null && !url.isEmpty()) {
            texto += "🔗 *Acesse mais informações:* " + url + "\n";
        }

        texto += "\n💡 *Compartilhado via EducNews* - Fique sempre por dentro das melhores oportunidades acadêmicas!";
        return texto;
    }




}
