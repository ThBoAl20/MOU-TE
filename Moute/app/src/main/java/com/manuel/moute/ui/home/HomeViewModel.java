package com.manuel.moute.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel conservado de la plantilla de navegación de Android Studio.
 * Expone un texto fijo de ejemplo; HomeFragment no lo observa en el flujo actual
 * ni lo utiliza para gestionar tarjetas, sesión o el estado del mapa.
 */
public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    /** Inicializa el único valor de muestra del modelo. */
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    /** Expone el texto como LiveData sin permitir modificarlo desde el consumidor. */
    public LiveData<String> getText() {
        return mText;
    }
}
