package com.manuel.moute.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manuel.moute.R;

/**
 * Pantalla de pago de muestra: actualmente solo muestra fragment_pay.xml.
 * El flujo de ejemplo PayPal SANDBOX está en TypeOfCardFragment y
 * VerTarjetaFragment; este fragmento no procesa tarjetas bancarias ni pagos.
 */
public class PayFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pay, container, false);
    }
}
