package com.manuel.moute.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manuel.moute.R;


/**
 * Solicita a Firebase Authentication un correo de restablecimiento de contraseña.
 * No consulta contraseñas en la base de datos ni cambia la contraseña directamente:
 * el usuario completa el proceso desde el enlace enviado por Firebase.
 */
public class ForgotPasswordFragment extends Fragment {

    private Button btnEnlace, btnDoLogin;
    private TextInputLayout txtEmail;
    private View root;

    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        btnEnlace = root.findViewById(R.id.btnEnlace);
        btnDoLogin = root.findViewById(R.id.bntGoToLogin);
        txtEmail = root.findViewById(R.id.txtEmailForgot);
        auth = FirebaseAuth.getInstance();
        btnEnlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validarEmail()){
                    return;
                }
                if (!txtEmail.getEditText().getText().toString().isEmpty()){
                    // El aviso depende de la respuesta asíncrona del servicio de Auth.
                    auth.sendPasswordResetEmail(txtEmail.getEditText().getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getActivity(), "Gracias. Consulta tu email para obtener un enlace para cambiar la contraseña", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

        btnDoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.loginFragment);
            }
        });

        return root;
    }

    /** Evita enviar un campo vacío; la validación del correo restante la realiza Firebase. */
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
}
