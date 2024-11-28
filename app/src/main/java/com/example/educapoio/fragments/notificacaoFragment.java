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
        notificationLayout.removeAllViews();
        SharedPreferences prefs = requireActivity().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        String notifications = prefs.getString("notifications_list", "");

        if (notifications.isEmpty()) {
            addNoNotificationMessage(notificationLayout);
        } else {
            String[] notificationArray = notifications.split("\\|\\|\\|");
            boolean isEmpty = true;

            for (String notification : notificationArray) {
                notification = notification.trim();
                if (!notification.isEmpty()) {
                    isEmpty = false;
                    addNotificationItem(notificationLayout, notification);
                }
            }

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
        if (notification.contains("|")) {
            String[] parts = notification.split("\\|");

            if (parts.length >= 2) {
                String uniqueId = parts[0];
                String message = parts[1];

                LinearLayout horizontalLayout = new LinearLayout(getContext());
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                horizontalLayout.setPadding(16, 16, 16, 16);

                TextView notificationView = new TextView(getContext());
                notificationView.setText(message);
                notificationView.setTextColor(Color.parseColor("#626364"));
                notificationView.setTextSize(18);

                notificationView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

                ImageButton deleteButton = new ImageButton(getContext());
                deleteButton.setImageResource(R.drawable.ic_exclusao);
                deleteButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

                View divider = new View(getContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
                divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.barra_divisoria));

                deleteButton.setOnClickListener(v -> {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setPadding(40, 40, 40, 40);

                    GradientDrawable backgroundDrawable = new GradientDrawable();
                    backgroundDrawable.setColor(Color.WHITE);
                    backgroundDrawable.setCornerRadius(20f);
                    layout.setBackground(backgroundDrawable);

                    TextView messageText = new TextView(getContext());
                    messageText.setText("Você tem certeza que deseja excluir esta notificação?");
                    messageText.setTextSize(18);
                    messageText.setTextColor(Color.BLACK);
                    messageText.setPadding(0, 0, 0, 40);

                    Button confirmButton = new Button(getContext());
                    confirmButton.setText("Sim");
                    confirmButton.setBackgroundColor(Color.BLACK);
                    confirmButton.setTextColor(Color.WHITE);
                    confirmButton.setPadding(40, 20, 40, 20);

                    confirmButton.setOnClickListener(v1 -> {
                        notificationLayout.removeView(horizontalLayout);
                        notificationLayout.removeView(divider);
                        deleteNotification(uniqueId);
                        bottomSheetDialog.dismiss();

                        if (notificationLayout.getChildCount() == 0) {
                            addNoNotificationMessage(notificationLayout);
                        }
                    });

                    Button cancelButton = new Button(getContext());
                    cancelButton.setText("Não");
                    cancelButton.setBackgroundColor(Color.GRAY);
                    cancelButton.setTextColor(Color.WHITE);
                    cancelButton.setPadding(40, 20, 40, 20);

                    cancelButton.setOnClickListener(v12 -> bottomSheetDialog.dismiss());

                    layout.addView(messageText);
                    layout.addView(confirmButton);
                    layout.addView(cancelButton);
                    bottomSheetDialog.setContentView(layout);
                    bottomSheetDialog.show();
                });

                horizontalLayout.addView(notificationView);
                horizontalLayout.addView(deleteButton);
                notificationLayout.addView(horizontalLayout);
                notificationLayout.addView(divider);
            }
        }
    }
    private void deleteNotification(String uniqueId) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        String notifications = prefs.getString("notifications_list", "");

        String[] notificationArray = notifications.split("\\|\\|\\|");
        StringBuilder updatedNotifications = new StringBuilder();

        for (String notification : notificationArray) {
            if (!notification.startsWith(uniqueId + "|")) {
                updatedNotifications.append(notification).append("|||");
            }
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("notifications_list", updatedNotifications.toString());
        editor.apply();

        // Atualizar a lista de notificações
        atualizarNotificacoes(notificationContainer);
    }


    private void deleteAllNotifications() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.toast_custom_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView dialogMessage = bottomSheetView.findViewById(R.id.dialog_message);
        dialogMessage.setText("Você tem certeza que deseja excluir todas as notificações?");

        Button buttonConfirm = bottomSheetView.findViewById(R.id.button_open_url);
        buttonConfirm.setText("Sim");
        buttonConfirm.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("notifications_list", "");
            editor.apply();

            // Atualizar a lista de notificações após a exclusão
            atualizarNotificacoes(notificationContainer);

            Log.d("NotificacaoFragment", "Todas as notificações foram excluídas.");
            bottomSheetDialog.dismiss();
        });

        Button buttonCancel = bottomSheetView.findViewById(R.id.button_cancel);
        buttonCancel.setText("Não");
        buttonCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

}
