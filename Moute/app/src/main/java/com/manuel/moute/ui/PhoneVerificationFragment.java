package com.manuel.moute.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manuel.moute.R;


/**
 * Pantalla de verificación telefónica de muestra, limitada al layout.
 * No envía SMS, no recibe códigos ni verifica números con Firebase Auth.
 * El registro actual recoge un teléfono, pero no completa esta verificación.
 */
public class PhoneVerificationFragment extends Fragment {

    public PhoneVerificationFragment() {
        // Constructor público sin argumentos requerido para recreación por Android.
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_verification, container, false);
    }
}
