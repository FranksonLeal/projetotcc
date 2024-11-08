package com.example.educapoio.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.Button;

import com.example.educapoio.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

            boolean isEmpty = true;
            for (String notification : notificationArray) {
                notification = notification.trim(); // Remove espaços
                if (!notification.isEmpty()) {
                    isEmpty = false;
                    addNotificationItem(notificationLayout, notification);
                }
            }

            // Se todas as notificações foram removidas
            if (isEmpty) {
                addNoNotificationMessage(notificationLayout);
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
                notificationView.setText("Mensagem: " + message);
                notificationView.setTextColor(Color.parseColor("#626364"));
                notificationView.setTextSize(18); // Define o tamanho da fonte
                notificationView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1)); // Para ocupar o espaço

                // ImageButton para deletar
                ImageButton deleteButton = new ImageButton(getContext());
                deleteButton.setImageResource(R.drawable.ic_exclusao); // Ícone de deletar
                deleteButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent)); // Remove fundo do botão

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cria o BottomSheetDialog
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

                        // Infla um layout simples programaticamente
                        LinearLayout layout = new LinearLayout(getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setPadding(40, 40, 40, 40); // Ajuste o padding conforme necessário

                        // Cria um GradientDrawable para bordas arredondadas
                        GradientDrawable backgroundDrawable = new GradientDrawable();
                        backgroundDrawable.setColor(Color.WHITE); // Cor de fundo
                        backgroundDrawable.setCornerRadius(20f); // Raio dos cantos
                        layout.setBackground(backgroundDrawable); // Aplica o fundo ao layout

                        // Adiciona o texto da mensagem
                        TextView messageText = new TextView(getContext());
                        messageText.setText("Você tem certeza que deseja excluir esta notificação?");
                        messageText.setTextSize(18); // Tamanho do texto
                        messageText.setTextColor(Color.BLACK); // Cor do texto
                        messageText.setPadding(0, 0, 0, 40); // Padding inferior

                        // Adiciona o botão de confirmação
                        Button confirmButton = new Button(getContext());
                        confirmButton.setText("Sim");
                        confirmButton.setBackgroundColor(Color.BLACK); // Cor de fundo do botão
                        confirmButton.setTextColor(Color.WHITE); // Cor do texto do botão
                        confirmButton.setPadding(40, 20, 40, 20); // Padding do botão

                        // Ação do botão de confirmação
                        confirmButton.setOnClickListener(v1 -> {
                            // Remover a notificação da interface
                            notificationLayout.removeView(horizontalLayout);
                            Log.d("NotificacaoFragment", "Notificação removida: " + message);
                            deleteNotification(uniqueId); // Chama a função para deletar a notificação
                            bottomSheetDialog.dismiss(); // Fecha o dialog após a ação
                        });

                        // Adiciona o botão de cancelamento
                        Button cancelButton = new Button(getContext());
                        cancelButton.setText("Não");
                        cancelButton.setBackgroundColor(Color.GRAY); // Cor de fundo do botão
                        cancelButton.setTextColor(Color.WHITE); // Cor do texto do botão
                        cancelButton.setPadding(40, 20, 40, 20); // Padding do botão

                        // Ação do botão de cancelamento
                        cancelButton.setOnClickListener(v12 -> bottomSheetDialog.dismiss()); // Apenas fecha o dialog

                        // Adiciona os componentes ao layout
                        layout.addView(messageText);
                        layout.addView(confirmButton);
                        layout.addView(cancelButton);

                        // Define o layout inflado no BottomSheetDialog
                        bottomSheetDialog.setContentView(layout);

                        // Exibe o BottomSheetDialog
                        bottomSheetDialog.show();
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
            notificationView.setTextColor(Color.parseColor("#626364"));
            notificationView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1)); // Para ocupar o espaço

            // ImageButton para deletar
            ImageButton deleteButton = new ImageButton(getContext());
            deleteButton.setImageResource(R.drawable.ic_exclusao); // Ícone de deletar
            deleteButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent)); // Remove fundo do botão

            deleteButton.setOnClickListener(v -> {
                // Cria o BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

                // Infla um layout simples programaticamente
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(40, 40, 40, 40); // Ajuste o padding conforme necessário

                // Cria um GradientDrawable para bordas arredondadas
                GradientDrawable backgroundDrawable = new GradientDrawable();
                backgroundDrawable.setColor(Color.WHITE); // Cor de fundo
                backgroundDrawable.setCornerRadius(20f); // Raio dos cantos
                layout.setBackground(backgroundDrawable); // Aplica o fundo ao layout

                // Adiciona o texto da mensagem
                TextView messageText = new TextView(getContext());
                messageText.setText("Você tem certeza que deseja excluir esta notificação?");
                messageText.setTextSize(18); // Tamanho do texto
                messageText.setTextColor(Color.BLACK); // Cor do texto
                messageText.setPadding(0, 0, 0, 40); // Padding inferior

                // Adiciona o botão de confirmação
                Button confirmButton = new Button(getContext());
                confirmButton.setText("Sim");
                confirmButton.setBackgroundColor(Color.BLACK); // Cor de fundo do botão
                confirmButton.setTextColor(Color.WHITE); // Cor do texto do botão
                confirmButton.setPadding(40, 20, 40, 20); // Padding do botão

                // Ação do botão de confirmação
                confirmButton.setOnClickListener(v1 -> {
                    notificationLayout.removeView(horizontalLayout);
                    Log.d("NotificacaoFragment", "Notificação motivacional removida: " + message);
                    deleteNotification(message); // Chama a função para deletar a notificação
                    bottomSheetDialog.dismiss(); // Fecha o dialog após a ação
                });

                // Adiciona o botão de cancelamento
                Button cancelButton = new Button(getContext());
                cancelButton.setText("Não");
                cancelButton.setBackgroundColor(Color.GRAY); // Cor de fundo do botão
                cancelButton.setTextColor(Color.WHITE); // Cor do texto do botão
                cancelButton.setPadding(40, 20, 40, 20); // Padding do botão

                // Ação do botão de cancelamento
                cancelButton.setOnClickListener(v12 -> bottomSheetDialog.dismiss()); // Apenas fecha o dialog

                // Adiciona os componentes ao layout
                layout.addView(messageText);
                layout.addView(confirmButton);
                layout.addView(cancelButton);

                // Define o layout inflado no BottomSheetDialog
                bottomSheetDialog.setContentView(layout);

                // Exibe o BottomSheetDialog
                bottomSheetDialog.show();
            });

            // Adiciona o TextView e o botão ao layout horizontal
            horizontalLayout.addView(notificationView);
            horizontalLayout.addView(deleteButton);

            // Adiciona o layout horizontal ao layout principal
            notificationLayout.addView(horizontalLayout);
        }
    }

    private void deleteNotification(String uniqueId) {
        // Função para deletar a notificação, atualize conforme necessário
        SharedPreferences prefs = requireActivity().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        String notifications = prefs.getString("notifications_list", "");

        // Divida as notificações em um array
        String[] notificationArray = notifications.split("\\|\\|\\|");
        StringBuilder updatedNotifications = new StringBuilder();

        for (String notification : notificationArray) {
            if (!notification.startsWith(uniqueId + "|")) {
                // Adiciona notificações que não são a que está sendo removida
                updatedNotifications.append(notification).append("|||");
            }
        }

        // Atualiza o SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("notifications_list", updatedNotifications.toString());
        editor.apply();
    }

    private void deleteAllNotifications() {
        // Infla o layout do BottomSheetDialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.toast_custom_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Configura a mensagem de confirmação
        TextView dialogMessage = bottomSheetView.findViewById(R.id.dialog_message);
        dialogMessage.setText("Você tem certeza que deseja excluir todas as notificações?");

        // Botão para confirmar a exclusão
        Button buttonConfirm = bottomSheetView.findViewById(R.id.button_open_url);
        buttonConfirm.setText("Sim");
        buttonConfirm.setOnClickListener(v -> {
            // Ação para deletar todas as notificações
            SharedPreferences prefs = requireActivity().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("notifications_list", "Sem notificações");
            editor.apply();
            atualizarNotificacoes(notificationContainer);
            Log.d("NotificacaoFragment", "Todas as notificações foram excluídas.");

            // Fecha o dialog após a ação
            bottomSheetDialog.dismiss();
        });

        // Botão para cancelar a exclusão
        Button buttonCancel = bottomSheetView.findViewById(R.id.button_cancel);
        buttonCancel.setText("Não");
        buttonCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Exibe o BottomSheetDialog
        bottomSheetDialog.show();
    }

}
