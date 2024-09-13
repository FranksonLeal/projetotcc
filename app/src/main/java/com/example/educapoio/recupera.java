package com.example.educapoio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.educapoio.databinding.ActivityRecuperaBinding;
import com.google.firebase.auth.FirebaseAuth;

public class recupera extends AppCompatActivity {

    private ActivityRecuperaBinding binding;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperaBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btnRecuperaConta.setOnClickListener(v -> validaDados());

        Button btnVoltarLogin = findViewById(R.id.btnVoltarLogin);
        btnVoltarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ao clicar no botão, iniciar a atividade Splash3
                startActivity(new Intent(recupera.this, login.class));
                // Finalizar a atividade atual para não retornar a ela ao pressionar o botão "voltar"
            }
        });
    }

    private void validaDados() {
        String email = binding.editEmail.getText().toString().trim();

        if (!email.isEmpty()) {
            binding.progressBar.setVisibility(View.VISIBLE);

            recuperaContaFirebase(email);
        } else {
            Toast.makeText(this, "Informe seu email", Toast.LENGTH_SHORT).show();
        }
    }
    private void recuperaContaFirebase(String email){
        mAuth.sendPasswordResetEmail(
                email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Verifique o email que foi enviado para você.", Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(this, "Ocorreu algum erro!", Toast.LENGTH_SHORT).show();
            }
            binding.progressBar.setVisibility(View.GONE);

        });



    }
}