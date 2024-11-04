package com.example.educapoio;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class TransitionUtil {

    // Método para iniciar uma atividade com animação de transição
    public static void startActivityWithAnimation(AppCompatActivity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // Método para finalizar uma atividade com animação de transição
    public static void finishWithAnimation(AppCompatActivity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
