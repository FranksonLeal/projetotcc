package com.example.educapoio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Map;

import static com.example.educapoio.fragments.notificacaoFragment.NOTIFICATION_PREFS;

public class PrazoAuxilioWorker extends Worker {

    private FirebaseFirestore db;

    public PrazoAuxilioWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        db = FirebaseFirestore.getInstance(); // Inicializar o Firestore
    }

    @NonNull

    @Override
    public Result doWork() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        String lastNotificationDate = prefs.getString("last_notification_date", "");

        // Data atual formatada como "dd/MM/yyyy"
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        if (todayDate.equals(lastNotificationDate)) {
            // Já enviou notificações hoje, não precisa enviar de novo
            Log.d("PrazoAuxilioWorker", "Notificações já enviadas hoje. Ignorando...");
            return Result.success();
        }

        // Atualiza a data para hoje, pois vamos enviar notificações agora
        prefs.edit().putString("last_notification_date", todayDate).apply();

        return buscarAuxilios();
    }


    private Result buscarAuxilios() {
        try {
            db.collection("auxilios")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Map<String, Object> auxilio = document.getData();
                                verificarPrazo(auxilio);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("PrazoAuxilioWorker", "Erro ao buscar auxílios: ", e);
                        }
                    });
            // Aguarda a conclusão da operação assíncrona
            Thread.sleep(3000); // Aguardar um tempo para garantir a finalização
        } catch (Exception e) {
            Log.e("PrazoAuxilioWorker", "Erro no worker: ", e);
            return Result.failure();
        }
        return Result.success();
    }

    private void verificarPrazo(Map<String, Object> auxilio) {
        // Extrai as informações do auxílio
        String titulo = (String) auxilio.get("titulo");
        String dataFim = (String) auxilio.get("dataFim"); // Formato esperado: "DD/MM/YYYY"

        // Converte a data de fim para LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataFimAuxilio = LocalDate.parse(dataFim, formatter);
        LocalDate dataAtual = LocalDate.now();

        // Calcula os dias restantes até o vencimento
        long diasRestantes = dataAtual.until(dataFimAuxilio).getDays();

        // Define a mensagem dependendo da situação do prazo
        String mensagem;
        if (diasRestantes > 0) {
            mensagem = "A oportunidade \"" + titulo + "\" expira em " + diasRestantes + " dias. Não perca!";
        } else if (diasRestantes == 0) {
            mensagem = "A oportunidade \"" + titulo + "\" expira hoje! Aproveite antes que seja tarde!";
        } else {
            mensagem = "A oportunidade \"" + titulo + "\" já expirou. Fique de olho nas próximas!";
        }

        // Formata a data e hora atuais
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        mensagem += "\n\nData: " + currentDateTime;

        // Salva a notificação
        salvarNotificacao(titulo, mensagem);
    }

    private void salvarNotificacao(String titulo, String mensagem) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        String existingNotifications = prefs.getString("notifications_list", "");

        // Cria um ID único para a notificação
        String uniqueId = String.valueOf(System.currentTimeMillis());

        // Adiciona a nova notificação
        String updatedNotifications = existingNotifications.isEmpty() ? uniqueId + "|" + mensagem : existingNotifications + "|||" + uniqueId + "|" + mensagem;
        prefs.edit().putString("notifications_list", updatedNotifications).apply();

        Log.d("PrazoAuxilioWorker", "Notificação salva: " + mensagem);

        // Envia broadcast para atualizar a badge automaticamente
        Intent intent = new Intent("NOTICIA_ATUALIZADA");
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

}
