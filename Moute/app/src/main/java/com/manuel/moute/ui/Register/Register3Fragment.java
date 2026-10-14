package com.manuel.moute.ui.Register;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.manuel.moute.MainActivity;
import com.manuel.moute.R;
import com.manuel.moute.models.Usuario;

import java.util.ArrayList;

/**
 * Último paso del registro: recoge teléfono y aceptación de condiciones y crea la cuenta.
 *
 * <p>Consume {@code userB} y {@code registrationPassword} de Register2Fragment.
 * Firebase Authentication recibe correo y contraseña; el perfil sin contraseña se
 * guarda en {@code Usuarios/{uid}/Datos} de Realtime Database.</p>
 *
 * <p>Limitación del prototipo: el mensaje final y la navegación usan un temporizador
 * independiente de la respuesta de Firebase. No constituyen una confirmación fiable
 * de creación de cuenta o de persistencia del perfil.</p>
 */
public class Register3Fragment extends Fragment {
    private View root;
    //------Variables de Firebase-----
    private FirebaseDatabase database;
    private DatabaseReference refUser;
    private FirebaseAuth auth;

    private ArrayList<Usuario> listaUsuarios;

    private TextInputLayout txtTelefono;
    private TextView lblPrivacyPolitic, login;
    private String nombre, apellidos, telefono, email, dni, fechaNacimiento, password, genero;
    private Button btnDoReg;
    private CheckBox ch1, ch2;
    private CountryCodePicker ccp;
    private LinearLayout linearLayout;
    private Usuario usuario;
    private MainActivity mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_register3, container, false);
        lblPrivacyPolitic = root.findViewById(R.id.lblReadPrivacyPolitic);
        login = root.findViewById(R.id.lblLinkDoLogin2);
        txtTelefono = root.findViewById(R.id.txtTelefonoReg3);
        ccp = root.findViewById(R.id.ccp);
        // Vincula el número local al selector para obtener luego prefijo y número completos.
        ccp.registerCarrierNumberEditText(txtTelefono.getEditText());
        btnDoReg = root.findViewById(R.id.btnDoLogin3);
        linearLayout = root.findViewById(R.id.privacyContainer);
        ch1 = root.findViewById(R.id.chcUseConditions);
        ch2 = root.findViewById(R.id.chcUseReadCondition);

        auth = FirebaseAuth.getInstance();
        refUser = FirebaseDatabase.getInstance().getReference("Usuarios");
        listaUsuarios = new ArrayList<>();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.loginFragment);
            }
        });
        lblPrivacyPolitic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottonSheetDialog();
            }
        });

        btnDoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Este paso asume los argumentos del paso 2; no admite entrada aislada al registro.
                usuario = getArguments().getParcelable("userB");
                nombre = usuario.getNombre();
                apellidos = usuario.getApellido();
                email = usuario.getCorreo();
                fechaNacimiento = usuario.getFechaNac();
                password = getArguments().getString("registrationPassword");
                genero = usuario.getGenero();
                dni = usuario.getDni();
                if (!phoneUser() | !privacyPolitic()){
                    return;
                }
                getNumbre();
                usuario.setTelefono(telefono);
                doRegister(email, password);
                // Pendiente: este retardo muestra éxito incluso si falla la petición asíncrona.
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Se ha realizado el registo correctamete", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.nav_home);                    }
                },6000);
            }
        });
        return root;
    }


    /** Muestra las condiciones; cerrar el panel no marca las casillas de aceptación. */
    public void bottonSheetDialog(){
        final BottomSheetDialog bottom = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(getActivity()).
                inflate(R.layout.condiciones_uso, linearLayout);
        Button btnAcept = view.findViewById(R.id.btnAcept);
        btnAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom.dismiss();
            }
        });
        bottom.setContentView(view);
        bottom.show();
    }

    /**
     * Crea la cuenta en Firebase Auth y, si responde con éxito, inicia el guardado del perfil.
     * La escritura de Datos no tiene aquí un callback que confirme o gestione su resultado.
     */
    public void doRegister(String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getActivity(), "Usuario creado", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();
                            //Llamo al metodo del MainActivity para actualizar la inforamcion del nav_header
                            mainActivity.UpdateNavHeader();
                            //Llamo al metodo del MainActivity para actualizar los elementos del menu
                            mainActivity.logOutCondition();
                            // Se almacena solo Usuario: la contraseña no se serializa en la base.
                            refUser.child(user.getUid()).child("Datos").setValue(usuario);
                            mainActivity.progresbar();
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(getActivity(), "No se ha creado el usuario."+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    /**
     * Intenta actualizar el nombre de la sesión actual, no el Usuario de Realtime Database.
     * La implementación consulta auth y no utiliza el parámetro recibido como fuente de verdad.
     */
    public void updateUI(FirebaseUser user){

        String nombreUsuario = nombre;
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().
                setDisplayName(nombreUsuario).build();
        user = auth.getCurrentUser();
        if (user != null){
            user.updateProfile(profileChangeRequest);
        }
    }

    /** Comprueba que haya número; no verifica por SMS su propiedad ni su validez completa. */
    public boolean phoneUser(){
        String nombre = txtTelefono.getEditText().getText().toString();

        if (nombre.isEmpty()){
            txtTelefono.setError("El campo no puede estar vacio");
            return  false;
        }
        else {
            txtTelefono.setError(null);
            return true;
        }
    }

    /** Exige ambas casillas antes de solicitar la creación de cuenta. */
    public boolean privacyPolitic(){
        if (!ch1.isChecked() ){
            Toast.makeText(getActivity(), "Tienes que aceptar los términos y condiciones de uso", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!ch2.isChecked()){
            Toast.makeText(getActivity(), "Tienes que aceptar los términos y condiciones de uso", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }


    }

    /** Obtiene el teléfono con prefijo internacional a partir del campo asociado al selector. */
    public void getNumbre(){
        telefono = ccp.getFullNumberWithPlus();

    }

    /** La pantalla usa MainActivity como anfitrión para actualizar menú y cabecera. */
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }


}
