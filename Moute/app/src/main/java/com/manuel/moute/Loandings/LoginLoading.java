package com.manuel.moute.Loandings;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.manuel.moute.R;

/**
 * Diálogo visual de espera utilizado por el inicio de sesión.
 * No ejecuta autenticación ni decide si ha terminado: quien lo muestra debe
 * cerrarlo. Los nombres heredados del paquete y los métodos se conservan.
 */
public class LoginLoading {
    Activity activity;
    AlertDialog alertDialog;

    /** La Activity proporciona el contexto visual del diálogo. */
    public LoginLoading(Activity activity) {
        this.activity = activity;
    }
    /** Muestra loading_login e impide que el usuario lo cierre pulsando fuera. */
    public void starLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_login, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }
    
    /** Cierra el diálogo creado por starLoading; se llama después de mostrarlo. */
    public void dissmisDialog() {
        alertDialog.dismiss();
    }
}
