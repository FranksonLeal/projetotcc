package com.example.educapoio.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.educapoio.R;
import android.util.Log;


public class notificacaoFragment extends Fragment {

    public static final String NOTIFICATION_PREFS = "notifications";
    private LinearLayout notificationContainer;

    public notificacaoFragment() {
        // Construtor vazio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate o layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_notificacao, container, false);


        // Certifique-se de que o título foi encontrado corretamente
        TextView titulo = view.findViewById(R.id.textView5);
        if (titulo != null) {
            titulo.setVisibility(View.VISIBLE);
        }

        // Garantir que o título está visível
        titulo.setVisibility(View.VISIBLE); // Força a visibilidade

        // Obtenha o título e verifique se ele foi inflado corretamente
        if (titulo != null) {
            Log.d("NotificacaoFragment", "Título inflado corretamente.");
        } else {
            Log.e("NotificacaoFragment", "Erro ao inflar o título.");
        }

        // Obtenha o LinearLayout onde as notificações serão exibidas
        notificationContainer = view.findViewById(R.id.notificationContainer);

        // Atualiza as notificações dinâmicas
        atualizarNotificacoes(notificationContainer);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Atualizar notificações novamente quando o fragmento for exibido
        if (notificationContainer != null) {
            atualizarNotificacoes(notificationContainer);
        }
    }

    private void atualizarNotificacoes(LinearLayout notificationLayout) {
        // Limpar o layout antes de adicionar novas notificações
        notificationLayout.removeAllViews();

        // Carregar notificações armazenadas no SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        String notifications = prefs.getString("notifications_list", "Sem notificações");

        // Se não houver notificações, exibir uma mensagem padrão
        if (notifications.equals("") || notifications.equals("Sem notificações")) {
            TextView noNotificationText = new TextView(getActivity());
            noNotificationText.setText("Sem notificações");
            noNotificationText.setTextSize(18);
            noNotificationText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            noNotificationText.setPadding(45, 20, 45, 20);
            notificationLayout.addView(noNotificationText);
        } else {
            // Dividir as notificações usando o delimitador "|||"
            String[] notificationArray = notifications.split("\\|\\|\\|");

            for (String notification : notificationArray) {
                notification = notification.trim(); // Remover espaços

                if (!notification.isEmpty()) { // Verifique se não está vazio
                    // Criar layout para a notificação
                    LinearLayout notificationItem = new LinearLayout(getActivity());
                    notificationItem.setOrientation(LinearLayout.VERTICAL);

                    // Configurar a mensagem da notificação
                    TextView notificationTextView = new TextView(getActivity());
                    notificationTextView.setText(notification);
                    notificationTextView.setTextSize(20);
                    notificationTextView.setPadding(45, 20, 45, 20);

                    // Adicionar a notificação ao layout
                    notificationItem.addView(notificationTextView);
                    notificationLayout.addView(notificationItem);

                    // Adicionar barra horizontal entre notificações
                    if (notificationLayout.getChildCount() > 0) {
                        View divider = new View(getActivity());
                        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                        divider.setBackgroundColor(getResources().getColor(R.color.black));
                        notificationLayout.addView(divider);
                    }
                }
            }
        }
    }

}
