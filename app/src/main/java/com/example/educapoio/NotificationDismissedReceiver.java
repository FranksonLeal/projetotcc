package com.example.educapoio;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationDismissedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Pega o ID da notificação da Intent
        int notificationId = intent.getIntExtra("notification_id", -1);

        if (notificationId != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId); // Cancela a notificação
            Log.d(TAG, "Notificação com ID " + notificationId + " foi cancelada.");
        }
    }
}
