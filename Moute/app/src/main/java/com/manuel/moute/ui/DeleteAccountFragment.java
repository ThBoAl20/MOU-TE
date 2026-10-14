package com.manuel.moute.ui;

import android.content.Context;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.manuel.moute.MainActivity;
import com.manuel.moute.R;

/**
 * Solicita reautenticación antes de eliminar la cuenta de Firebase Authentication.
 *
 * <p>Esta pantalla requiere una sesión activa. También solicita borrar el nodo
 * Usuarios/{uid}, pero ambos borrados son operaciones independientes: no hay una
 * transacción o reversión conjunta si falla una de ellas.</p>
 */
public class DeleteAccountFragment extends Fragment {
    private View root;

    private TextInputLayout txtEmail, txtPassword;
    private Button btnDelete;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String password, email;
    private MainActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_delete_account, container, false);
        txtEmail = root.findViewById(R.id.txtEmailDelete);
        txtPassword = root.findViewById(R.id.txtPasswordDelete);
        btnDelete = root.findViewById(R.id.btnDeleteAccount);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        txtEmail.getEditText().setText(user.getEmail());
        email = txtEmail.getEditText().getText().toString();
        password =  txtPassword.getEditText().getText().toString();

        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid());


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!validarPassword()){
                    return;
                }
                AuthCredential credential = EmailAuthProvider.getCredential(email,
                        txtPassword.getEditText().getText().toString());
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            deleteAccount(v);
                            // No espera al resultado de user.delete(): la base puede borrarse antes que Auth.
                            reference.removeValue();
                        }else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });

        return root;
    }

    /** Actualiza sesión y navegación únicamente cuando Auth confirma que eliminó la cuenta. */
    public void deleteAccount(final View v){
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getActivity(), "Se ha eliminado el usuario correctamente", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    mainActivity.restarNavHeader();
                    mainActivity.logOutCondition();
                    Navigation.findNavController(v).navigate(R.id.nav_home);

                }
                else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /** Exige la contraseña para poder solicitar credenciales de reautenticación. */
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

    /** Conserva el anfitrión encargado de retirar los datos de cuenta de la cabecera. */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
}
