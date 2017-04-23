package ru.paviannik.pavitranslator;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;


public class MyTranslatorApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

}
