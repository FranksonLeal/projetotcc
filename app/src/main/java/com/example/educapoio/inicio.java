package com.example.educapoio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.educapoio.databinding.ActivityInicioBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class inicio extends AppCompatActivity {
    private ActivityInicioBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;

    private static final String NOTIFICATION_PREFS = "notifications"; // deve ser o mesmo nome usado para salvar notificações
    private static final String NOTIFICATIONS_LIST_KEY = "notifications_list";

    // BroadcastReceiver para ouvir atualizações de notícias
    private final BroadcastReceiver noticiaAtualizadaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Atualiza a badge sempre que receber o broadcast
            atualizarBadgeNotificacoes();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInicioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initNavigation();

        atualizarBadgeNotificacoes();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Registrar o receiver para receber notificações de atualização
        LocalBroadcastManager.getInstance(this).registerReceiver(
                noticiaAtualizadaReceiver,
                new IntentFilter("NOTICIA_ATUALIZADA")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Desregistrar receiver para evitar leaks
        LocalBroadcastManager.getInstance(this).unregisterReceiver(noticiaAtualizadaReceiver);
    }

    @Override
    public void onBackPressed() {
        if (navController.getCurrentDestination().getId() == R.id.configuracao) {
            navController.navigate(R.id.perfilFragment);
        } else {
            super.onBackPressed();
        }
    }

    private void initNavigation() {
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    private void atualizarBadgeNotificacoes() {
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;

        // Recupera as notificações salvas no SharedPreferences
        SharedPreferences prefs = getSharedPreferences(NOTIFICATION_PREFS, MODE_PRIVATE);
        String notificacoes = prefs.getString(NOTIFICATIONS_LIST_KEY, "");

        int numeroDeNotificacoes = contarNotificacoes(notificacoes);

        // Obtém ou cria o badge para o item de notificações (coloque o id correto do item no menu)
        int idMenuNotificacao = R.id.menu_notificacao; // seu id do menu de notificações

        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(idMenuNotificacao);

        if (numeroDeNotificacoes > 0) {
            badge.setVisible(true);
            badge.setNumber(numeroDeNotificacoes);
        } else {
            badge.clearNumber();
            badge.setVisible(false);
        }
    }

    // Método para contar as notificações na string (baseado no seu separador "|||")
    private int contarNotificacoes(String notificacoes) {
        if (notificacoes == null || notificacoes.isEmpty()) {
            return 0;
        }
        // Cada notificação está separada por "|||"
        String[] lista = notificacoes.split("\\|\\|\\|");
        return lista.length;
    }
}
