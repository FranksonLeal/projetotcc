package com.example.educapoio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.educapoio.databinding.ActivityAdministradorBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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

    private String[] cursos = {"Selecione um curso:","Sistemas de Informação", "Engenharia de Software", "Engenharia de Produção",
            "Matemática e física", "Pedagogia", "Química e biologia", "Farmácia", "Engenharia sanitária", "Agronomia"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnVisualizarDados.setOnClickListener(v -> {
            Intent intent = new Intent(Administrador.this, visualizarDados.class);
            startActivity(intent);
        });


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Criando o ArrayAdapter para os cursos
        ArrayAdapter<String> cursosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cursos);
        cursosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCursos.setAdapter(cursosAdapter);  // Setando o adapter no Spinner

        // Inicializa o comportamento do CheckBox
        binding.checkboxPublicoGeral.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.textCursos.setVisibility(View.GONE);
                binding.spinnerCursos.setVisibility(View.GONE);  // Tornar o Spinner visível
            } else {
                binding.textCursos.setVisibility(View.VISIBLE);
                binding.spinnerCursos.setVisibility(View.VISIBLE);  // Tornar o Spinner visível
            }
        });

        binding.buttonRemoveImage.setOnClickListener(v -> {
            imageUri = null;
            binding.imagePreview.setImageDrawable(null);
            binding.imagePreviewContainer.setVisibility(View.GONE);
        });


        // DatePicker para dataInicio
        binding.editEmail.setOnClickListener(v -> showDatePickerDialog(binding.editEmail));

        // DatePicker para dataFim
        binding.editSenha.setOnClickListener(v -> showDatePickerDialog(binding.editSenha));

        binding.editDataPubli.setOnClickListener(v -> showDatePickerDialog(binding.editDataPubli));


        // Selecionar imagem
        binding.buttonSelectImage.setOnClickListener(v -> openFileChooser());

        binding.buttonCadastrarNoticia.setOnClickListener(v -> {
            String titulo = binding.editNome.getText().toString().trim();
            String dataInicio = binding.editEmail.getText().toString().trim();
            String dataFim = binding.editSenha.getText().toString().trim();
            String url = binding.editUrl.getText().toString().trim();

            // Se o título, data de início e data de fim não estiverem vazios, faça o upload da imagem
            if (!titulo.isEmpty() && !dataInicio.isEmpty() && !dataFim.isEmpty()) {
                uploadImage(titulo, dataInicio, dataFim, url); // Chama a função para upload
            } else {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            }
        });


        binding.buttonCadastrarOutras.setOnClickListener(v -> {
            String tituloOutras = binding.textTituloOutras.getText().toString().trim();
            String dataPublicacao = binding.editDataPubli.getText().toString().trim();

            if (!tituloOutras.isEmpty() && !dataPublicacao.isEmpty()) {
                cadastrarOutrasNoticias(tituloOutras, dataPublicacao);
            } else {
                Toast.makeText(this, "Preencha todos os campos de outras notícias", Toast.LENGTH_SHORT).show();
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
            imageUri = data.getData(); // já deve existir essa linha
            binding.imagePreview.setImageURI(imageUri);
            binding.imagePreviewContainer.setVisibility(View.VISIBLE);

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

    private String getCursoSelecionado() {
        int selectedPosition = binding.spinnerCursos.getSelectedItemPosition();
        if (selectedPosition > 0) {
            return cursos[selectedPosition]; // Retorna o nome do curso selecionado
        }
        return "";
    }


    private void cadastrarOutrasNoticias(String titulo, String dataPublicacao) {
        String id = db.collection("noticias").document().getId();

        String url = binding.editUrlNoti.getText().toString().trim();

        Map<String, Object> noticia = new HashMap<>();
        noticia.put("id", id);
        noticia.put("titulo", titulo);
        noticia.put("dataPublicacao", dataPublicacao);

        // Só adiciona a URL se ela não estiver vazia
        if (!url.isEmpty()) {
            noticia.put("url", url);
        }

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("noticias").document(id)
                .set(noticia)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    mostrarSnackbarSucesso("Notícia cadastrada com sucesso!");
                    limparCamposNoticias();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Erro ao cadastrar notícia", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Erro ao cadastrar notícia: ", e);
                });

    }

    private void limparCamposNoticias() {
        binding.textTituloOutras.setText("");
        binding.editDataPubli.setText("");
        binding.editUrlNoti.setText(""); // Limpar URL também
    }





    private void addAuxilioToFirestore(String titulo, String dataInicio, String dataFim, String url, String imagemUrl) {
        String id = db.collection("auxilios").document().getId();

        boolean isPublicoGeral = binding.checkboxPublicoGeral.isChecked();
        Map<String, Object> auxilio = new HashMap<>();
        auxilio.put("titulo", titulo);
        auxilio.put("dataInicio", dataInicio);
        auxilio.put("dataFim", dataFim);
        auxilio.put("url", url);
        auxilio.put("imagemUrl", imagemUrl);
        auxilio.put("status", "aberto");
        auxilio.put("publicoGeral", isPublicoGeral);

        if (!isPublicoGeral) {
            String cursoSelecionado = getCursoSelecionado();
            if (!cursoSelecionado.isEmpty()) {
                List<String> cursosList = new ArrayList<>();
                cursosList.add(cursoSelecionado);
                auxilio.put("cursos", cursosList); // Salva como lista
            } else {
                Toast.makeText(Administrador.this, "Selecione um curso!", Toast.LENGTH_SHORT).show();
                return; // Impede o cadastro sem curso selecionado
            }
        }


        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("auxilios").document(id).set(auxilio)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    mostrarSnackbarSucesso("Auxílio cadastrado com sucesso");
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Administrador.this, "Erro ao cadastrar auxílio", Toast.LENGTH_SHORT).show();
                });

    }

    private void mostrarSnackbarSucesso(String mensagem) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensagem, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#4CAF50")); // Verde

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
        int bottomMarginPx = (int) (64 * getResources().getDisplayMetrics().density);
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMarginPx);
        snackbarView.setLayoutParams(params);

        int snackbarTextId = getResources().getIdentifier("snackbar_text", "id", "com.google.android.material");
        TextView textView = snackbarView.findViewById(snackbarTextId);
        if (textView != null) {
            textView.setTextColor(Color.WHITE);
        }

        snackbar.show();
    }



    private String getCursosSelecionados() {
        // Obtém o índice do curso selecionado no Spinner
        int selectedPosition = binding.spinnerCursos.getSelectedItemPosition();
        // Verifica se o curso não for "Nenhum curso selecionado"
        if (selectedPosition > 0) {
            return cursos[selectedPosition];
        }
        return ""; // Caso nenhum curso seja selecionado
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
                com.example.educapoio.PrazoAuxilioWorker.class, 1, TimeUnit.DAYS) // Executa uma vez por dia
                .build();

        // Agendar o Worker
        WorkManager.getInstance(this).enqueue(prazoAuxilioRequest);
    }
}