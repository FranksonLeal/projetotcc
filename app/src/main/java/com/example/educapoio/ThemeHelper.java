package com.example.educapoio;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ThemeHelper {

    // Versão antiga — mantém para compatibilidade
    public static void aplicarModoEscuro(Context context, View rootView) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

        int color = isDarkMode ?
                ContextCompat.getColor(context, R.color.colorPrimaryDark) :
                ContextCompat.getColor(context, R.color.colorPrimaryLight);

        if (rootView != null) {
            rootView.setBackgroundColor(color);
        }
    }

    // Nova versão — permite também aplicar no BottomNavigationView
    public static void aplicarModoEscuro(Context context, View rootView, BottomNavigationView bottomNavigationView) {
        aplicarModoEscuro(context, rootView); // reutiliza a lógica original

        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

        int color = isDarkMode ?
                ContextCompat.getColor(context, R.color.colorPrimaryDark) :
                ContextCompat.getColor(context, R.color.colorPrimaryLight);

        if (bottomNavigationView != null) {
            bottomNavigationView.setBackgroundColor(color);
        }
    }
}
