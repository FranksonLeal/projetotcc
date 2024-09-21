package com.example.educapoio;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializar a biblioteca ThreeTenABP
        AndroidThreeTen.init(this);
    }
}
