package com.manuel.moute.ui.Register;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.manuel.moute.R;
import com.manuel.moute.adapter.TarjetaAdapter;
import com.manuel.moute.models.Tarjeta;
import com.manuel.moute.models.Usuario;

import java.util.ArrayList;
import java.util.Map;


/**
 * Primer paso del registro: recoge identidad, correo y credenciales del nuevo usuario.
 *
 * <p>El perfil viaja a {@link Register2Fragment} como Parcelable en {@code userA}.
 * La contraseña se pasa por separado en {@code registrationPassword}: no forma parte
 * del modelo Usuario ni de los datos que se guardan en Realtime Database. Este paso
 * solo valida el formulario; la cuenta de Firebase se crea en Register3Fragment.</p>
 */
public class RegisterFragment extends Fragment {
    private View root;
    //------Variables de Firebase-----
    private FirebaseDatabase database;
    private DatabaseReference refUser;
    private FirebaseAuth auth;
    // VARIABLES RECYCLERVIEW
    private TarjetaAdapter adapter;
    private Map<String, Tarjeta> mapUser;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Usuario> listaUsuarios;
    private int resource = R.layout.tarjeta_turista;

    private TextView lblDoLogin, lblPrivacyPolitic, lblNext;
    private TextInputLayout txtNombre, txtApellidos, txtEmail, txtTelefono, txtDni, txtFechaNac, txtPassword;
    private Button btnDoReg, btnNext;
    private LinearLayout linearLayout;

    String nombre, apellidos, dni, email, password, fechaNac, telefono;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_register, container, false);
        txtNombre = (TextInputLayout) root.findViewById(R.id.txtNombreReg);
        txtApellidos = (TextInputLayout) root.findViewById(R.id.txtApellidosReg);
        lblPrivacyPolitic = root.findViewById(R.id.lblReadPrivacyPolitic);
        linearLayout = root.findViewById(R.id.privacyContainer);
        lblNext = root.findViewById(R.id.lblTitle1);
        txtEmail = (TextInputLayout)root.findViewById(R.id.txtEmailReg);
        txtDni = (TextInputLayout)root.findViewById(R.id.txtDniReg);
        txtPassword = (TextInputLayout) root.findViewById(R.id.txtPasswordReg);
        lblDoLogin = root.findViewById(R.id.lblLinkDoLogin);


        listaUsuarios = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        btnNext = root.findViewById(R.id.btnNext1);
        refUser = FirebaseDatabase.getInstance().getReference("Usuarios");

        lblDoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.loginFragment);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = txtNombre.getEditText().getText().toString().trim();
                apellidos = txtApellidos.getEditText().getText().toString().trim();
                dni = txtDni.getEditText().getText().toString().trim();
                email = txtEmail.getEditText().getText().toString().trim();
                password = txtPassword.getEditText().getText().toString();
                // El operador | evalúa todas las validaciones para mostrar todos los errores.
                if (!nameUser() | !apellidosUser() | !emailUser()
                         | !dniUser() | !passwordUser()){
                    return;
                }
                // Mantener las credenciales transitorias fuera del objeto de perfil persistible.
                Usuario usuario = new Usuario(nombre, apellidos, email, dni);
                Bundle bundle = new Bundle();
                bundle.putParcelable("userA", usuario);
                bundle.putString("registrationPassword", password);
                Navigation.findNavController(v).navigate(R.id.nav_register2, bundle);
            }
        });

        return root;
    }

    // MÉTODOS PARA CONTROLAR QUE LOS DATOS DEL USUARIO ESTAN TODOS PUESTOS
    /** Comprueba el nombre y muestra el error junto a su propio campo. */
    public boolean nameUser(){
        String nombre = txtNombre.getEditText().getText().toString().trim();
        if (nombre.isEmpty()){
            txtNombre.setError("El campo no puede estar vacio");
            return  false;
        }
        else {
            txtNombre.setError(null);
            return true;
        }
    }

    /** Exige apellidos; no realiza validación de identidad oficial. */
    public boolean apellidosUser(){
        String nombre = txtApellidos.getEditText().getText().toString().trim();
        if (nombre.isEmpty()){
            txtApellidos.setError("El campo no puede estar vaci");
            return  false;
        }
        else {
            txtApellidos.setError(null);
            return true;
        }
    }

    /** Valida el formato local del correo; no comprueba si ya existe una cuenta. */
    public boolean emailUser(){
        String email = txtEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()){
            txtEmail.setError("El campo no puede estar vacio");
            return  false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("El email no es correcto");
            return false;
        }
        else {
            txtEmail.setError(null);
            return true;
        }

    }

    /** Aplica la longitud mínima del formulario antes de enviar la contraseña a Firebase Auth. */
    public boolean passwordUser(){
        String password = txtPassword.getEditText().getText().toString();
        if (password.isEmpty()){
            txtPassword.setError("El campo no puede estar vacio");
            return  false;
        }
        else if (password.length() < 6){
            txtPassword.setError("La contraseña de ser >= 6 caracteres");
            return false;
        }
        else {
            txtPassword.setError(null);
            return true;
        }
    }

    /** Exige un documento no vacío; no valida letras de control de DNI o NIE. */
    public boolean dniUser(){
        String nombre = txtDni.getEditText().getText().toString().trim();
        if (nombre.isEmpty()){
            txtDni.setError("El campo no puede estar vacio");
            return  false;
        }
        else {
            txtDni.setError(null);
            return true;
        }
    }

}
