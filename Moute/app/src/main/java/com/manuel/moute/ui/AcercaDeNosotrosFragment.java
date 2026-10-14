package com.manuel.moute.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manuel.moute.R;

/**
 * Pantalla informativa de presentación del proyecto.
 * Su contenido se define en fragment_acerca_de_nosotros.xml; no consulta Firebase
 * ni altera la sesión o las tarjetas.
 */
public class AcercaDeNosotrosFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_acerca_de_nosotros, container, false);
    }
}
