package com.example.educapoio;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.educapoio.fragments.notificacaoFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

public class PrazoAuxilioWorker extends Worker {
    private static final String TAG = "PrazoAuxilioWorker";
    private FirebaseFirestore db;

    public PrazoAuxilioWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        db = FirebaseFirestore.getInstance(); // Inicializa o Firestore
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Worker está sendo executado.");
        verificarDataFim();
        return Result.success();
    }

    private void verificarDataFim() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("auxilios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String dataFimString = document.getString("dataFim");
                    Log.d(TAG, "Verificando data de fim: " + dataFimString);

                    if (isValidDate(dataFimString)) {
                        LocalDateTime dataFim = LocalDateTime.parse(dataFimString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime agora = LocalDateTime.now(ZoneId.systemDefault());

                        long diasRestantes = Duration.between(agora, dataFim).toDays();

                        if (diasRestantes > 0) {
                            enviarNotificacao("Lembrete de Auxílio!", "O auxílio " + document.getString("titulo") + " termina em " + diasRestantes + " dias.");
                        } else if (diasRestantes == 0) {
                            enviarNotificacao("Lembrete de Auxílio!", "O auxílio " + document.getString("titulo") + " termina hoje.");
                        } else if (diasRestantes < 0) {
                            // O prazo expirou, mover para "fechado"
                            atualizarStatusAuxilio(document.getId(), "fechado");
                            enviarNotificacao("Auxílio Encerrado", "O auxílio " + document.getString("titulo") + " foi encerrado.");
                        }
                    }
                }
            } else {
                Log.e(TAG, "Erro ao obter auxílios: " + task.getException());
            }
        });
    }
    private void atualizarStatusAuxilio(String auxilioId, String novoStatus) {
        // Atualiza o status do auxílio para "fechado"
        db.collection("auxilios").document(auxilioId).update("status", novoStatus)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Status do auxílio atualizado para: " + novoStatus))
                .addOnFailureListener(e -> Log.e(TAG, "Erro ao atualizar status do auxílio", e));
    }

    private void enviarNotificacao(String titulo, String mensagem) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "canal_1";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Auxílios", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataHora = agora.format(formatter);

        String mensagemCompleta = mensagem + "\n\nEnviado em: " + dataHora;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_notificacao)
                .setContentTitle(titulo)
                .setContentText(mensagemCompleta)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());

        salvarNotificacao(titulo, mensagemCompleta);
    }

    private void salvarNotificacao(String titulo, String mensagem) {
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(notificacaoFragment.NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String notifications = prefs.getString("notifications_list", "Sem notificações");
        if (notifications.equals("Sem notificações")) {
            notifications = ""; // Se não houver notificações, inicie com uma string vazia
        } else {
            notifications += "|||"; // Adicione o delimitador se já houver notificações
        }
        notifications += titulo + ": " + mensagem; // Adicione a nova notificação

        editor.putString("notifications_list", notifications);
        editor.apply();
    }


    private boolean isValidDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime.parse(date, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
