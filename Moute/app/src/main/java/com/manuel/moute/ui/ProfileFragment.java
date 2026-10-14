package com.manuel.moute.ui;


import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.manuel.moute.MainActivity;
import com.manuel.moute.R;
import com.manuel.moute.models.Usuario;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.android.volley.VolleyLog.TAG;


/**
 * Muestra el perfil de la cuenta y permite capturar una foto con la aplicación de cámara.
 *
 * <p>Los datos personales se leen de Usuarios/{uid} en Realtime Database. La foto
 * se sube a Firebase Storage y su URL se asocia al usuario de Firebase Auth. El
 * botón de actualización delega en MainActivity, no guarda estos campos directamente.</p>
 *
 * <p>La pantalla presupone sesión activa. El campo de contraseña queda vacío:
 * Firebase Auth no devuelve la contraseña actual y no debe almacenarse en Usuario.</p>
 */
public class ProfileFragment extends Fragment {

    private View root;
    private DatabaseReference userReference;
    private Map<String, Usuario> mapUser;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private TextInputLayout txtNombre, txtTelefono, txtEmail, txtDni, txtGenero, txtfechaNac, txtPassword;
    private Button btnUpdateProfile;
    private ArrayList<Usuario> listaUsers;
    MainActivity mainActivity;
    private ImageView imagenProfile;
    private Uri file;
    private StorageReference imgStorage;
    private StorageTask mUploadTask;
    private static final int PICK_IMG_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        final FirebaseUser user = auth.getCurrentUser();

        userReference = FirebaseDatabase.getInstance().getReference("Usuarios");

        txtNombre = root.findViewById(R.id.txtNameUserProfile);
        txtTelefono =  root.findViewById(R.id.txtPhoneUserProfile);
        txtEmail = root.findViewById(R.id.txtEmailUserProfile);
        txtDni = root.findViewById(R.id.txtDniUserProfile);
        txtGenero = root.findViewById(R.id.txtGeneroUserProfile);
        txtfechaNac = root.findViewById(R.id.txtFechaNacUserProfile);
        txtPassword = root.findViewById(R.id.txtPassUserProfile);
        imagenProfile = root.findViewById(R.id.imgUserPerfil);
        btnUpdateProfile = root.findViewById(R.id.btnUpdateUser);
        listaUsers = new ArrayList<>();
        imgStorage = FirebaseStorage.getInstance().getReference("FotosDePerfil");

        imagenProfile.setImageResource(R.drawable.miperfil);

        getImagenProfile();
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.updateUser();
            }
        });

        if (auth.getCurrentUser() != null){
            final FirebaseUser u;
            u = auth.getCurrentUser();
            // Escucha continua del perfil. La lista de esta pantalla aún no se reinicia entre eventos.
            userReference.child(u.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator <Map<String, Usuario>> gti = new GenericTypeIndicator<Map<String, Usuario>>(){};
                    mapUser = dataSnapshot.getValue(gti);
                    if (mapUser != null){
                        listaUsers.addAll(mapUser.values());
                    }
                    for (int i = 0; i < listaUsers.size(); i++) {
                        if (u.getEmail().equalsIgnoreCase(listaUsers.get(i).getCorreo())){
                            txtNombre.getEditText().setText(listaUsers.get(i).getNombre()+" "+listaUsers.get(i).getApellido());
                            txtTelefono.getEditText().setText(listaUsers.get(i).getTelefono());
                            txtEmail.getEditText().setText(listaUsers.get(i).getCorreo());
                            txtDni.getEditText().setText(listaUsers.get(i).getDni());
                            txtGenero.getEditText().setText(listaUsers.get(i).getGenero());
                            txtfechaNac.getEditText().setText(listaUsers.get(i).getFechaNac());
                            // Firebase Authentication no permite ni debe exponer la contraseña actual.
                            txtPassword.getEditText().setText("");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        imagenProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleImageClick();
            }
        });

        return root;
    }

    /** Abre una cámara disponible; el retorno utilizado es su miniatura, no una foto a resolución completa. */
    public void handleImageClick(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent, PICK_IMG_REQUEST);
        }
    }

    /** Presenta la miniatura devuelta por la cámara y comienza su subida asíncrona. */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQUEST){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imagenProfile.setImageBitmap(bitmap);
                    handleUpload(bitmap);
                    Usuario usuario = new Usuario();
                    usuario.setFotoPerfil(String.valueOf(bitmap));
                    break;

            }
        }
    }

    /**
     * Codifica la miniatura como JPEG al 80 % y reemplaza fotosPerfil/{uid}.jpeg.
     * El callback de éxito obtiene después la URL de descarga para actualizar Auth.
     */
    public void handleUpload(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        String uid = auth.getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("fotosPerfil").child(uid+".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getCause());
            }
        });

    }

    /** Continúa el flujo de subida con la URL que Firebase Storage asigna al archivo. */
    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: "+ uri);
                setImageUser(uri);
            }
        });
    }
    /** Vincula la URL de Storage al perfil de Auth; no persiste el bitmap en Realtime Database. */
    public void setImageUser(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Imagen de perfil actualizada", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Imagen de perfil fállida", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** Limpia campos visibles del formulario; no elimina ni modifica el perfil remoto. */
    public void restarUserProfileData(){
        txtNombre.getEditText().setText(null);
        txtTelefono.getEditText().setText(null);
        txtEmail.getEditText().setText(null);
        txtDni.getEditText().setText(null);
        txtfechaNac.getEditText().setText(null);
        txtPassword.getEditText().setText(null);
    }

    /** Carga la foto de Auth con Picasso; requiere que la sesión siga activa. */
    public void getImagenProfile(){
        FirebaseUser user;
        user = auth.getCurrentUser();
        if (user.getPhotoUrl() != null){
            Picasso.with(getActivity()).load(user.getPhotoUrl()).into(imagenProfile);
        }
    }

    /** Conecta las acciones del perfil con MainActivity, su actividad anfitriona. */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }




}
