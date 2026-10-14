package com.manuel.moute;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.manuel.moute.ui.Register.Register3Fragment;
import com.manuel.moute.ui.Register.RegisterFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Casos instrumentados de regresión del formulario de registro.
 *
 * <p>Construyen vistas con el tema de Moute en el hilo principal y comprueban
 * errores locales y el prefijo telefónico. No pulsan el botón de alta, no crean
 * cuentas ni verifican una operación remota de Firebase. Requieren compilar el
 * APK de pruebas y disponer de un dispositivo o emulador para ejecutarse.</p>
 */
@RunWith(AndroidJUnit4.class)
public class RegistrationPhoneTest {
    /** Verifica que el selector esté vinculado al campo y forme números con dos prefijos. */
    @Test
    public void registrationScreenReadsPhoneWithoutCrashing() {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                Register3Fragment fragment = new Register3Fragment();
                View screen = fragment.onCreateView(themedInflater(), null, null);
                CountryCodePicker picker = screen.findViewById(R.id.ccp);
                TextInputLayout phone = screen.findViewById(R.id.txtTelefonoReg3);

                picker.setCountryForPhoneCode(34);
                phone.getEditText().setText("612345678");
                fragment.getNumbre();
                assertEquals("+34612345678", picker.getFullNumberWithPlus());

                picker.setCountryForPhoneCode(44);
                phone.getEditText().setText("7400123456");
                fragment.getNumbre();
                assertEquals("+447400123456", picker.getFullNumberWithPlus());
            }
        });
    }

    /** Impide que la validación de un campo use por error el valor de otro. */
    @Test
    public void registrationValidatesNameAndEmailIndependently() {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                RegisterFragment fragment = new RegisterFragment();
                View screen = fragment.onCreateView(themedInflater(), null, null);
                TextInputLayout name = screen.findViewById(R.id.txtNombreReg);
                TextInputLayout email = screen.findViewById(R.id.txtEmailReg);

                name.getEditText().setText("");
                email.getEditText().setText("test@example.com");
                assertFalse(fragment.nameUser());

                name.getEditText().setText("Prueba");
                email.getEditText().setText("");
                assertFalse(fragment.emailUser());
            }
        });
    }

    /** Los componentes Material necesitan el tema de la aplicación al inflar los layouts. */
    private static LayoutInflater themedInflater() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return LayoutInflater.from(new ContextThemeWrapper(context, R.style.AppTheme));
    }

    /**
     * Ejecuta las interacciones de vista en el hilo de Android y propaga cualquier
     * excepción o aserción al hilo de JUnit para que el caso falle visiblemente.
     */
    private static void runOnMainThread(final Runnable action) {
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    action.run();
                } catch (Throwable error) {
                    failure.set(error);
                }
            }
        });
        if (failure.get() != null) {
            throw new AssertionError(failure.get());
        }
    }
}
