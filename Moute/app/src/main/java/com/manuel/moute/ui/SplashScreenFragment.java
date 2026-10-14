package com.manuel.moute.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.manuel.moute.R;


/**
 * Boceto de pantalla inicial conservado como Fragment.
 * Solo infla su layout: el temporizador tiene un callback vacío y no realiza
 * ninguna navegación. No sustituye la actividad SplashScreen de la aplicación.
 */
public class SplashScreenFragment extends Fragment {

    Animation topAnimation, bottonAnimation;
    public static int SPLASH_TIMER = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getWindow()

        // Punto de extensión histórico; no espera una carga real ni cambia de pantalla.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, SPLASH_TIMER);
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }
}
