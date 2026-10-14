package com.manuel.moute;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Pantalla de entrada con logo y eslogan animados, antes del contenedor principal.
 * La espera es exclusivamente visual: no descarga datos ni valida la sesión de
 * Firebase. MainActivity y sus fragmentos gestionan esas tareas posteriormente.
 */
public class SplashScreen extends AppCompatActivity {

    // Duración de la presentación en milisegundos.
    private static int TIME = 2500;
    private TextView texto;
    private ImageView logo;
    Animation topAnimation, bottomAnimation;

    /** Muestra el layout a pantalla completa y prepara el paso a MainActivity. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        // Animaciones declaradas en res/anim; cada una se asigna a un elemento.
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Elementos de la ventana
        logo = findViewById(R.id.imgLogo);
        texto = findViewById(R.id.lblSloganLogo);
        logo.setAnimation(bottomAnimation);
        texto.setAnimation(topAnimation);
        // Cierra esta actividad al avanzar para que Atrás no regrese al splash.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();

//                // Boceto antiguo de transición compartida; no se ejecuta actualmente.
//                Pair[] pairs = new Pair[2];
//                pairs[0] = new Pair<View, String>(logo, "logo_img");
//                pairs[1] = new Pair<View, String>(slogan, "slogan_text");
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,pairs);
//                }
            }
        }, TIME);
    }
}
