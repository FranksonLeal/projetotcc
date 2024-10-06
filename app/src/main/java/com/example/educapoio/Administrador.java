package com.example.educapoio;

import static com.example.educapoio.fragments.notificacaoFragment.NOTIFICATION_PREFS;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.educapoio.databinding.ActivityAdministradorBinding;
import com.example.educapoio.fragments.notificacaoFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import android.app.DatePickerDialog;
import java.util.concurrent.TimeUnit;

public class Administrador extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityAdministradorBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri imageUri; // Para armazenar o URI da imagem

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // DatePicker for dataInicio
        binding.editEmail.setOnClickListener(v -> showDatePickerDialog(binding.editEmail));

        // DatePicker for dataFim
        binding.editSenha.setOnClickListener(v -> showDatePickerDialog(binding.editSenha));

        // Selecionar imagem
        binding.buttonSelectImage.setOnClickListener(v -> openFileChooser());

        binding.btnCriarConta.setOnClickListener(v -> {
            String titulo = binding.editNome.getText().toString().trim();
            String dataInicio = binding.editEmail.getText().toString().trim();
            String dataFim = binding.editSenha.getText().toString().trim();
            String url = binding.editUrl.getText().toString().trim();

            if (!titulo.isEmpty() && !dataInicio.isEmpty() && !dataFim.isEmpty()) {
                uploadImage(titulo, dataInicio, dataFim, url); // Chama a função para upload
            } else {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
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

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.imageViewAid.setVisibility(View.VISIBLE);
            binding.imageViewAid.setImageURI(imageUri);
        }
    }

    private void uploadImage(String titulo, String dataInicio, String dataFim, String url) {
        if (imageUri != null) {
            // Caso o usuário tenha selecionado uma imagem
            StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            addAuxilioToFirestore(titulo, dataInicio, dataFim, url, uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Administrador.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Se não houver imagem, gera a URL com a inicial do título
            String imagemUrl = getCircularImageUri(titulo);
            addAuxilioToFirestore(titulo, dataInicio, dataFim, url, imagemUrl);
        }
    }

    private String getCircularImageUri(String titulo) {
        // Retorna uma "URL" representando a inicial do título
        return "https://dummyimage.com/130x130/ffffff/000000.png&text=" + titulo.charAt(0); // Placeholder URL
    }

    private void addAuxilioToFirestore(String titulo, String dataInicio, String dataFim, String url, String imagemUrl) {
        String id = db.collection("auxilios").document().getId();

        // Dados para adicionar
        Map<String, Object> auxilio = new HashMap<>();
        auxilio.put("titulo", titulo);
        auxilio.put("dataInicio", dataInicio);
        auxilio.put("dataFim", dataFim);
        auxilio.put("url", url);
        auxilio.put("imagemUrl", imagemUrl); // URL da imagem (ou inicial do título)
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
}
