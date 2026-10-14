package com.manuel.moute.Loandings;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.manuel.moute.R;

/**
 * Diálogo genérico de espera para las pantallas de la aplicación.
 * Solo controla la presentación de loading_layout: no realiza trabajo en segundo
 * plano ni comprueba resultados de Firebase. El llamador gestiona su duración.
 */
public class LoandigDialog {
    Activity activity;
    AlertDialog alertDialog;

    /** Usa la Activity actual como contexto de la ventana. */
    public LoandigDialog(Activity activity) {
        this.activity = activity;
    }

    /** Crea y muestra un diálogo no cancelable por el usuario. */
    public void starLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_layout, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    /** Debe llamarse tras starLoading para cerrar la ventana ya creada. */
    public void dissmisDialog(){
        alertDialog.dismiss();
    }
}
