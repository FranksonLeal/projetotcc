package com.example.educapoio;

import static com.example.educapoio.fragments.notificacaoFragment.NOTIFICATION_PREFS;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.educapoio.databinding.ActivityAdministradorBinding;
import com.example.educapoio.fragments.notificacaoFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import android.app.DatePickerDialog;
import java.util.Calendar;

public class Administrador extends AppCompatActivity {
    private ActivityAdministradorBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // DatePicker for dataInicio
        binding.editEmail.setOnClickListener(v -> showDatePickerDialog(binding.editEmail));

        // DatePicker for dataFim
        binding.editSenha.setOnClickListener(v -> showDatePickerDialog(binding.editSenha));

        binding.btnCriarConta.setOnClickListener(v -> {
            String titulo = binding.editNome.getText().toString().trim();
            String dataInicio = binding.editEmail.getText().toString().trim();
            String dataFim = binding.editSenha.getText().toString().trim();
            String url = binding.editUrl.getText().toString().trim();

            if (!titulo.isEmpty() && !dataInicio.isEmpty() && !dataFim.isEmpty()) {
                adicionarAuxilio(titulo, dataInicio, dataFim, url);
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSair.setOnClickListener(v -> {
            // Desconectar o usuário do Firebase
            FirebaseAuth.getInstance().signOut();

            // Redirecionar para a tela de login
            Intent intent = new Intent(this, login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Finaliza a activity atual para que o usuário não consiga voltar
        });


        // Agendar o Worker para verificar os auxílios uma vez por dia
        agendarVerificacaoAuxilios();
    }

    private void showDatePickerDialog(EditText editText) {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(Administrador.this, (view, yearSelected, monthSelected, daySelected) -> {
            // Format the date as "DD/MM/YYYY"
            String selectedDate = String.format("%02d/%02d/%04d", daySelected, monthSelected + 1, yearSelected);
            editText.setText(selectedDate);
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void agendarVerificacaoAuxilios() {
        PeriodicWorkRequest prazoAuxilioRequest = new PeriodicWorkRequest.Builder(
                PrazoAuxilioWorker.class, 1, TimeUnit.DAYS) // Executa uma vez por dia
                .build();

        // Agendar o Worker
        WorkManager.getInstance(this).enqueue(prazoAuxilioRequest);
    }

    private void adicionarAuxilio(String titulo, String dataInicio, String dataFim, String url) {
        String id = db.collection("auxilios").document().getId();

        // Dados para adicionar
        Map<String, Object> auxilio = new HashMap<>();
        auxilio.put("titulo", titulo);
        auxilio.put("dataInicio", dataInicio);
        auxilio.put("dataFim", dataFim);
        auxilio.put("url", url);
        auxilio.put("status", "aberto");

        // Adiciona o documento no Firestore
        db.collection("auxilios").document(id).set(auxilio)
                .addOnSuccessListener(aVoid -> {
                    String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                    String uniqueId = String.valueOf(System.currentTimeMillis()); // ID único para a notificação
                    String mensagem = "Nova oportunidade " + titulo + " lançada! Aproveite a oportunidade!  \nData: " + currentDateTime;

                    // Carrega as notificações existentes
                    SharedPreferences prefs = getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
                    String existingNotifications = prefs.getString("notifications_list", "");

                    // Adiciona a nova notificação com um delimitador "|||"
                    String updatedNotifications = existingNotifications.isEmpty() ? uniqueId + "|" + mensagem : existingNotifications + "|||" + uniqueId + "|" + mensagem;
                    prefs.edit().putString("notifications_list", updatedNotifications).apply();

                    Toast.makeText(Administrador.this, "Auxílio cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Administrador.this, "Erro ao cadastrar auxílio", Toast.LENGTH_SHORT).show();
                });
    }




    private void salvarNotificacao(String titulo, String mensagem, long diasRestantes) {
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(notificacaoFragment.NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Gerar um ID único (você pode usar um contador ou um UUID)
        String uniqueId = String.valueOf(System.currentTimeMillis()); // ou UUID.randomUUID().toString();

        // Adiciona a nova notificação ao formato: "id|titulo|mensagem"
        String notificationEntry = uniqueId + "|" + titulo + "| " + mensagem + " (faltam " + diasRestantes + " dias)";
        String notifications = prefs.getString("notifications_list", "Sem notificações");
        notifications += (notifications.equals("Sem notificações") ? "" : "|||") + notificationEntry;

        editor.putString("notifications_list", notifications);
        editor.apply();
    }





}
