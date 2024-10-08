
package com.example.educapoio;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.FirebaseApp;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.concurrent.TimeUnit;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        // Inicialização do Firebase
        FirebaseApp.initializeApp(this);
        // Iniciar o Worker para notificações periódicas
        iniciarNotificacoesPeriodicas();
        AndroidThreeTen.init(this); // Inicializa a biblioteca
        criarCanalNotificacao(); // Cria o canal de notificação
    }
    private void iniciarNotificacoesPeriodicas() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(com.example.educapoio.PrazoAuxilioWorker.class, 1, TimeUnit.HOURS) // Aumentado para uma hora
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("notifications_list", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
        Log.d("MainActivity", "Worker de notificações agendado."); // Log adicionado
    }


    private void criarCanalNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default_channel", "Notificações", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}



