package com.example.educapoio;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

import com.google.firebase.FirebaseApp;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inicialização do Firebase
        FirebaseApp.initializeApp(this);
        AndroidThreeTen.init(this); // Inicializa a biblioteca
    }
}



