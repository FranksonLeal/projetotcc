package com.example.educapoio;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.educapoio.databinding.ActivityInicioBinding;

public class inicio extends AppCompatActivity {
    private ActivityInicioBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInicioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initNavigation();
    }

    @Override
    public void onBackPressed() {
        if (navController.getCurrentDestination().getId() == R.id.configuracao) {
            // Se o usuário estiver na tela de configuração, volte para o fragmento de perfil
            navController.navigate(R.id.perfilFragment);
        } else {
            // Caso contrário, utilize o comportamento padrão de voltar
            super.onBackPressed();
        }
    }

    private void initNavigation() {
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }
}
