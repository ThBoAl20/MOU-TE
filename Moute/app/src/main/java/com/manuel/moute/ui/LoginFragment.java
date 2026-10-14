package com.manuel.moute.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manuel.moute.MainActivity;
import com.manuel.moute.R;
import com.manuel.moute.models.Usuario;

import java.util.Map;

/**
 * Inicio de sesión por correo y contraseña mediante Firebase Authentication.
 *
 * <p>Los campos se envían a Auth, no a Realtime Database. Al completar el acceso
 * actualiza la cabecera y el menú del anfitrión y regresa a la pantalla anterior.
 * También enlaza los flujos de registro y recuperación de contraseña.</p>
 */
public class LoginFragment extends Fragment {

    private View root;
    private FirebaseAuth auth;
    private TextInputLayout txtEmail, txtPassword;
    private TextView lblDoRegister, txtSlogan;
    private Button btnDoLogin, btnForgotPass;
    private DatabaseReference reference;
    private FirebaseUser currentUser;
    private Map<String, Usuario> mapUser;
    private ImageView img;

    private TextInputLayout emailPair, passwordPair;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_login, container, false);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        txtSlogan = root.findViewById(R.id.txtWelcome);
        txtEmail = root.findViewById(R.id.txtEmailLogin);
        txtPassword = root.findViewById(R.id.txtPasswordLogin);
        btnDoLogin = root.findViewById(R.id.btnDoLogin);
        lblDoRegister = root.findViewById(R.id.lblLinkDoRegister);
        btnForgotPass = root.findViewById(R.id.bntForgotPass);
        img = root.findViewById(R.id.img_logo_login);
        lblDoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_register);
            }
        });
        btnDoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String email, password;
                email = txtEmail.getEditText().getText().toString();
                password = txtPassword.getEditText().getText().toString();
                if (!validarEmail() | !validarPassword()){
                    return;
                }
                doLogin(email, password, v);

            }
        });

        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_forgotPass);
            }
        });

        return root;
    }

    /** Vuelve atrás solo si Firebase sigue teniendo una sesión autenticada. */
    public void updateUI(FirebaseUser user, View v){
        user = auth.getCurrentUser();
        if (user != null){
            mainActivity.onBackPressed();
        }
    }

    /** Inicia la petición asíncrona de Auth; los errores se muestran sin navegar. */
    public void doLogin(String email, String password, final View v){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = auth.getCurrentUser();
                    //Llamo al metodo del MainActivity para actualizar la inforamcion del nav_header
                    mainActivity.UpdateNavHeader();
                    //Llamo al metodo del MainActivity para actualizar los elementos del menu
                    mainActivity.logOutCondition();
                    mainActivity.progresbarLogin();
                    // Retardo visual del prototipo, iniciado después del éxito de Firebase Auth.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Has iniciado sesión correctamente", Toast.LENGTH_SHORT).show();
                            updateUI(user, v);
                        }
                    },5000);

                }
                else {
                    Toast.makeText(getActivity(), "Error ! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /** Valida únicamente que el correo no esté vacío; Firebase comprueba las credenciales. */
    public boolean validarEmail(){
        String email = txtEmail.getEditText().getText().toString();

        if (email.isEmpty()){
            txtEmail.setError("Introduce tu dirección de correo electrónico");
            return false;
        } else {
            txtEmail.setError(null);
            txtEmail.setErrorEnabled(false);
            return true;
        }
    }

    /** No persiste la contraseña; comprueba que el formulario tenga un valor antes del acceso. */
    public boolean validarPassword(){
        String email = txtPassword.getEditText().getText().toString();
        if (email.isEmpty()){
            txtPassword.setError("Introduce tu contraseña");
            return false;
        } else {
            txtPassword.setError(null);
            txtPassword.setErrorEnabled(false);
            return true;
        }
    }

    /** Vincula la pantalla a MainActivity para refrescar la información de sesión. */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
}
