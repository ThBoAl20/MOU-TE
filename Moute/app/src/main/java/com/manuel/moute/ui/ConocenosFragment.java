package com.manuel.moute.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manuel.moute.R;

/**
 * Pantalla estática Conócenos cuyo contenido está en fragment_conocenos.xml.
 * No tiene operaciones de red ni lógica de contratación.
 */
public class ConocenosFragment extends Fragment {

    public ConocenosFragment() {
        // Constructor público sin argumentos para que Android pueda recrear el fragmento.
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conocenos, container, false);
    }
}
