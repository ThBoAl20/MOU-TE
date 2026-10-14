package com.manuel.moute.ui.Register;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.manuel.moute.R;
import com.manuel.moute.models.Usuario;


/**
 * Segundo paso del registro: añade fecha de nacimiento y género al perfil.
 *
 * <p>Recibe {@code userA} y {@code registrationPassword} del primer paso, y los entrega
 * al tercero como {@code userB} y la misma clave de contraseña. No crea cuentas ni
 * escribe datos remotos; el perfil aún es un borrador del formulario.</p>
 */
public class Register2Fragment extends Fragment {

    private View root;
    private Button btnNext;
    private TextView lblDologin, lblText, lblTitle;
    private DatePicker fechaNac;
    private String nombre, apellidos, email, dni, fechaNacimiento, password, lblGenero;
    private TextInputLayout rgButton;
    private RadioButton rbMan, rbwoman, rbNone;
    private RadioGroup rgGenere;

    private Usuario usuario;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_register2, container, false);
        btnNext = root.findViewById(R.id.btnNext2);
        lblTitle = root.findViewById(R.id.lblTitle2);
        lblDologin = root.findViewById(R.id.lblLinkDoLogin2);
        lblText = root.findViewById(R.id.lblTexto2);
        rbMan = root.findViewById(R.id.rbMan);
        rbwoman = root.findViewById(R.id.rbWoman);
        rbNone = root.findViewById(R.id.rbNone);
        rgGenere = root.findViewById(R.id.selecGenere);
        fechaNac = root.findViewById(R.id.dtFechaNac);

        // El fragmento depende de Navigation; una entrada sin el paso anterior no puede avanzar.
        Bundle arguments = getArguments();
        if (arguments != null) {
            usuario = arguments.getParcelable("userA");
            password = arguments.getString("registrationPassword");
        }
        if (usuario == null || password == null || password.length() < 6) {
            btnNext.setEnabled(false);
            Toast.makeText(getContext(), "Faltan datos del registro. Vuelve al primer paso.", Toast.LENGTH_LONG).show();
            return root;
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectGender()){
                    return;
                }
                // DatePicker numera meses desde cero; el texto de fecha muestra meses de 1 a 12.
                int getMonthOfYear = fechaNac.getMonth() +1;
                fechaNacimiento = fechaNac.getDayOfMonth()+ "/" + getMonthOfYear +"/"+fechaNac.getYear();
                usuario.setFechaNac(fechaNacimiento);
                usuario.setGenero(lblGenero);
                Bundle bundle = new Bundle();
                bundle.putParcelable("userB", usuario);
                bundle.putString("registrationPassword", password);
                Navigation.findNavController(v).navigate(R.id.nav_register3, bundle);
            }
        });
        return root;
    }

    /**
     * Boceto alternativo de escucha del calendario, actualmente no invocado.
     * El botón Siguiente obtiene directamente la fecha seleccionada.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void selectDate(){
        fechaNac.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fechaNacimiento = dayOfMonth +"/"+ monthOfYear+"/"+year;
                System.out.println(fechaNacimiento);
            }
        });


    }

    /** Obliga a seleccionar una opción y conserva su etiqueta para el perfil. */
    public boolean selectGender(){
        String genero = "";

        if (rbMan.isChecked() == false && rbwoman.isChecked() == false && rbNone.isChecked() == false){
            Toast.makeText(getActivity(), "Seleciona género", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (rbMan.isChecked() == true){
                genero  = "Hombre";

            } else if (rbwoman.isChecked() == true){
                genero = "Mujer";
            }else if (rbNone.isChecked() == true){
                genero = "No especificado";
            }
        }
        lblGenero = genero;
        return true;
    }

}
