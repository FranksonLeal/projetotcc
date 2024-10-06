package com.example.educapoio;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.Tasks;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
        try {
            verificarDataFim();
            return Result.success();
        } catch (ExecutionException e) {
            Log.e(TAG, "Erro ao executar o trabalho: ", e);
            return Result.retry(); // Pode tentar novamente
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Define a flag de interrupção
            Log.e(TAG, "O trabalho foi interrompido: ", e);
            return Result.failure(); // Falha se o trabalho for interrompido
        } catch (Exception e) {
            Log.e(TAG, "Erro inesperado: ", e);
            return Result.failure(); // Falha em caso de qualquer outra exceção
        }
    }

    private void enviarNotificacaoMotivacional() {
        enviarNotificacao("Motivação do Dia!", "Não desista! Cada dia é uma nova oportunidade.");
    }

    private void verificarDataFim() throws ExecutionException, InterruptedException {
        // Aguarda a conclusão da tarefa
        List<DocumentSnapshot> documentos = Tasks.await(db.collection("auxilios").get()).getDocuments();

        for (DocumentSnapshot document : documentos) {
            String dataFimString = document.getString("dataFim");
            Log.d(TAG, "Verificando data de fim: " + dataFimString);

            if (isValidDate(dataFimString)) {
                LocalDateTime dataFim = LocalDateTime.parse(dataFimString, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                LocalDateTime agora = LocalDateTime.now(ZoneId.systemDefault());

                long diasRestantes = Duration.between(agora, dataFim).toDays();

                if (diasRestantes > 0) {
                    enviarNotificacao("Lembrete!", "O auxílio " + document.getString("titulo") + " termina em " + diasRestantes + " dias.");
                    salvarNotificacao("Lembrete!", "O auxílio " + document.getString("titulo") + " termina em " + diasRestantes + " dias.", diasRestantes);
                } else if (diasRestantes == 0) {
                    enviarNotificacao("Lembrete!", "O auxílio " + document.getString("titulo") + " termina hoje.");
                    salvarNotificacao("Lembrete!", "O auxílio " + document.getString("titulo") + " termina hoje.", 0);
                } else if (diasRestantes < 0) {
                    // O prazo expirou, mover para "fechado"
                    atualizarStatusAuxilio(document.getId(), "fechado");
                    enviarNotificacao("Poxa, oportunidade Encerrada", "O " + document.getString("titulo") + " foi encerrado.");
                    salvarNotificacao("Poxa, oportunidade Encerrada", "O " + document.getString("titulo") + " foi encerrado.", 0);
                }
            }
        }
        // Enviar notificação motivacional diariamente
        enviarNotificacaoMotivacional();
    }

    private void atualizarStatusAuxilio(String auxilioId, String novoStatus) {
        // Atualiza o status do auxílio para "fechado"
        db.collection("auxilios").document(auxilioId).update("status", novoStatus)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Status do auxílio atualizado com sucesso"))
                .addOnFailureListener(e -> Log.e(TAG, "Erro ao atualizar status do auxílio: " + e));
    }

    private boolean isValidDate(String date) {
        try {
            LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void enviarNotificacao(String titulo, String mensagem) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "default_channel"; // Canal de notificação

        // Intent para abrir a MainActivity quando a notificação for clicada
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Criação da notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void salvarNotificacao(String titulo, String mensagem, long diasRestantes) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(notificacaoFragment.NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        String notifications = prefs.getString("notifications_list", "");

        String uniqueId = String.valueOf(System.currentTimeMillis()); // ID único para a notificação

        // Adiciona a nova notificação com um delimitador "|||"
        String updatedNotifications = notifications.isEmpty() ? uniqueId + "|" + titulo + ": " + mensagem : notifications + "|||" + uniqueId + "|" + titulo + ": " + mensagem;
        prefs.edit().putString("notifications_list", updatedNotifications).apply();
    }
}
