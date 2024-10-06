package com.example.educapoio.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.Button; // Importar para o botão de excluir todas

import com.example.educapoio.R;

public class notificacaoFragment extends Fragment {

    public static final String NOTIFICATION_PREFS = "notifications";
    private LinearLayout notificationContainer;

    public notificacaoFragment() {
        // Construtor vazio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla o layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_notificacao, container, false);

        // Obtém o título e verifica se foi inflado corretamente
        TextView titulo = view.findViewById(R.id.textView5);
        if (titulo != null) {
            titulo.setVisibility(View.VISIBLE);
            Log.d("NotificacaoFragment", "Título inflado corretamente.");
        } else {
            Log.e("NotificacaoFragment", "Erro ao inflar o título.");
        }

        // Obtém o LinearLayout onde as notificações serão exibidas
        notificationContainer = view.findViewById(R.id.notificationContainer);

        // Botão para excluir todas as notificações
        Button deleteAllButton = view.findViewById(R.id.deleteAllButton);
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllNotifications();
            }
        });

        // Atualiza as notificações dinâmicas
        atualizarNotificacoes(notificationContainer);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Atualiza notificações novamente quando o fragmento for exibido
        if (notificationContainer != null) {
            atualizarNotificacoes(notificationContainer);
        }
    }

    private void atualizarNotificacoes(LinearLayout notificationLayout) {
        // Limpa o layout antes de adicionar novas notificações
        notificationLayout.removeAllViews();

        // Carrega notificações armazenadas no SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        String notifications = prefs.getString("notifications_list", "Sem notificações");

        // Se não houver notificações, exibe uma mensagem padrão
        if (notifications.isEmpty() || notifications.equals("Sem notificações")) {
            addNoNotificationMessage(notificationLayout);
        } else {
            // Divide as notificações usando o delimitador "|||"
            String[] notificationArray = notifications.split("\\|\\|\\|");

            for (String notification : notificationArray) {
                notification = notification.trim(); // Remove espaços

                if (!notification.isEmpty()) { // Verifica se não está vazio
                    addNotificationItem(notificationLayout, notification);
                }
            }
        }
    }

    private void addNoNotificationMessage(LinearLayout notificationLayout) {
        TextView noNotificationText = new TextView(requireActivity());
        noNotificationText.setText("Sem notificações");
        noNotificationText.setTextSize(18);
        noNotificationText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        noNotificationText.setPadding(45, 20, 45, 20);
        notificationLayout.addView(noNotificationText);
    }

    private void addNotificationItem(LinearLayout notificationLayout, String notification) {
        // Verifica se a notificação contém o delimitador '|'
        if (notification.contains("|")) {
            String[] parts = notification.split("\\|");

            if (parts.length >= 2) {
                String uniqueId = parts[0];  // ID da notificação
                String message = parts[1];   // Mensagem da notificação

                // Criar um layout horizontal para a notificação e o botão de deletar
                LinearLayout horizontalLayout = new LinearLayout(getContext());
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                horizontalLayout.setPadding(16, 16, 16, 16); // Padding ao redor do layout

                // TextView para a notificação
                TextView notificationView = new TextView(getContext());
                notificationView.setText("ID: " + uniqueId + "\nMensagem: " + message);
                notificationView.setTextSize(18); // Define o tamanho da fonte
                notificationView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1)); // Para ocupar o espaço

                // ImageButton para deletar
                ImageButton deleteButton = new ImageButton(getContext());
                deleteButton.setImageResource(R.drawable.ic_exclusao); // Ícone de deletar
                deleteButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent)); // Remove fundo do botão

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Exibe a confirmação antes de deletar
                        new AlertDialog.Builder(getContext())
                                .setTitle("Confirmação")
                                .setMessage("Você tem certeza que deseja excluir esta notificação?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Remover a notificação da interface
                                        notificationLayout.removeView(horizontalLayout);
                                        Log.d("NotificacaoFragment", "Notificação removida: " + notification);
                                        deleteNotification(uniqueId);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    }
                });

                // Adiciona o TextView e o botão ao layout horizontal
                horizontalLayout.addView(notificationView);
                horizontalLayout.addView(deleteButton);

                // Adiciona o layout horizontal ao layout principal
                notificationLayout.addView(horizontalLayout);

                // Adiciona a barra divisória
                View divider = new View(getContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2)); // Altura da barra divisória
                divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black)); // Cor da barra divisória
                notificationLayout.addView(divider);
            } else {
                Log.e("NotificacaoFragment", "Notificação mal formatada: " + notification);
            }
        } else {
            // Tratamento para notificações motivacionais ou sem ID
            String message = notification;
            Log.e("NotificacaoFragment", "Notificação motivacional ou sem ID: " + message);

            // Criar um layout horizontal para a notificação e o botão de deletar
            LinearLayout horizontalLayout = new LinearLayout(getContext());
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setPadding(16, 16, 16, 16);

            // TextView para a notificação
            TextView notificationView = new TextView(getContext());
            notificationView.setText(message);
            notificationView.setTextSize(18); // Define o tamanho da fonte
            notificationView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            // ImageButton para deletar
            ImageButton deleteButton = new ImageButton(getContext());
            deleteButton.setImageResource(R.drawable.ic_exclusao);
            deleteButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Exibe a confirmação antes de deletar
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirmação")
                            .setMessage("Você tem certeza que deseja excluir esta notificação?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Remover a notificação da interface
                                    notificationLayout.removeView(horizontalLayout);
                                    Log.d("NotificacaoFragment", "Notificação removida: " + message);
                                    deleteNotification(message); // Chama a função para deletar a notificação
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            });

            // Adiciona o TextView e o botão ao layout horizontal
            horizontalLayout.addView(notificationView);
            horizontalLayout.addView(deleteButton);

            // Adiciona o layout horizontal ao layout principal
            notificationLayout.addView(horizontalLayout);

            // Adiciona a barra divisória
            View divider = new View(getContext());
            divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2)); // Altura da barra divisória
            divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
            notificationLayout.addView(divider);
        }
    }

    private void deleteAllNotifications() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmação")
                .setMessage("Você tem certeza que deseja excluir todas as notificações?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Limpa as notificações do SharedPreferences
                        SharedPreferences prefs = requireActivity().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("notifications_list", ""); // Limpa as notificações
                        editor.apply();

                        // Atualiza a interface
                        notificationContainer.removeAllViews();
                        addNoNotificationMessage(notificationContainer);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteNotification(String notificationId) {
        // Remove a notificação do SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Obtém a lista atual de notificações
        String notifications = prefs.getString("notifications_list", "");

        // Se a lista não estiver vazia, remove a notificação específica
        if (!notifications.isEmpty()) {
            String[] notificationArray = notifications.split("\\|\\|\\|");
            StringBuilder newNotifications = new StringBuilder();

            for (String notification : notificationArray) {
                if (!notification.contains(notificationId)) { // Exclui a notificação se o ID coincidir
                    newNotifications.append(notification).append("|||");
                }
            }

            // Salva a nova lista de notificações
            editor.putString("notifications_list", newNotifications.toString());
            editor.apply();
        }
    }
}
