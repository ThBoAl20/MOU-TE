package com.manuel.moute.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.manuel.moute.MainActivity;
import com.manuel.moute.R;
import com.manuel.moute.models.DireccionUser;
import com.manuel.moute.models.PayPalConfig;
import com.manuel.moute.models.Tarjeta;
import com.manuel.moute.models.GuestCardStore;
import com.manuel.moute.models.TarjetaUser;
import com.manuel.moute.models.Usuario;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Detalle de una tarjeta recibida en el argumento Parcelable {@code cardUser}.
 *
 * <p>El argumento {@code guestCard} decide el origen. Las tarjetas invitadas se
 * consultan y modifican mediante GuestCardStore y no ofrecen envío ni recarga.
 * Para tarjetas de cuenta se conserva el flujo histórico Firebase/PayPal SANDBOX.</p>
 *
 * <p>Algunas operaciones remotas son demostraciones incompletas: el diálogo de
 * eliminación no borra el registro y el resultado de recarga no modifica saldo.
 * Los mensajes de envío no equivalen a una integración con logística real.</p>
 */
public class VerTarjetaFragment extends Fragment {

    private View root;
    private TextView lblNombre, lblApellido, lblDni, lblNumCard, lblTitulo, txtPrecio, txtTitulo,
            dateActivacion, dateContrato, lblEstado;
    private Button btnEmilinar, btnRecargar, btnDetalles, btnActivar, btnPedirCard;
    private LinearLayout linearLayout;
    private FrameLayout layout;
    private MainActivity mainActivity;

    private FirebaseUser user;
    private FirebaseAuth auth;
    private DatabaseReference reference, refDir;
    private boolean activacion;
    private float precioTotal = 0.0f;
    private float precioCard = 20.0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_ver_tarjeta, container, false);

        lblNombre = root.findViewById(R.id.txtNombreVerCard);
        lblApellido = root.findViewById(R.id.txtApellidosVerCard);
        lblDni = root.findViewById(R.id.txtDniVerdCard);
        lblNumCard = root.findViewById(R.id.txtNumVerCard);
        lblTitulo = root.findViewById(R.id.txtTituloVerCard);
        dateActivacion = root.findViewById(R.id.lblFechaActivacion);
        dateContrato = root.findViewById(R.id.fchContrato);
        lblEstado = root.findViewById(R.id.lblEstadCard);
        txtTitulo = root.findViewById(R.id.lblTituloVerCard);
        txtPrecio = root.findViewById(R.id.lblPrecioVerCard);
        btnDetalles = root.findViewById(R.id.btnDetalles);
        btnEmilinar = root.findViewById(R.id.btnDeleteCard);
        btnRecargar = root.findViewById(R.id.btnRecargarCard);
        btnActivar = root.findViewById(R.id.btnActivar);
        btnPedirCard = root.findViewById(R.id.btnPedirCard);
        layout = root.findViewById(R.id.fgVer);
        auth = FirebaseAuth.getInstance();
        linearLayout = root.findViewById(R.id.addDirLayout);
        reference = FirebaseDatabase.getInstance().getReference("Usuarios");

        user = auth.getCurrentUser();


        Bundle arguments = getArguments();
        final TarjetaUser t = arguments == null ? null : (TarjetaUser) arguments.getParcelable("cardUser");
        if (t == null) {
            Toast.makeText(getContext(), "No se ha podido cargar la tarjeta", Toast.LENGTH_SHORT).show();
            return root;
        }
        lblNombre.setText(t.getNombre());
        lblApellido.setText(t.getApellidos());
        lblDni.setText(t.getDni());
        lblNumCard.setText(t.getNumCard());
        dateContrato.setText(t.getFechaContrato());
        lblTitulo.setText(t.getTitulo());
        txtTitulo.setText(t.getTitulo());
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        txtPrecio.setText(numberFormat.format(t.getPrecio()));

        // El origen importa incluso con una sesión abierta: una tarjeta local sigue siendo local.
        if (arguments.getBoolean("guestCard", user == null)) {
            setupGuestCard(t);
            return root;
        }
        if (user == null) {
            Toast.makeText(getContext(), "Esta tarjeta pertenece a una cuenta", Toast.LENGTH_SHORT).show();
            return root;
        }
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);

        // Referencia del registro de esta tarjeta en la cuenta, usada para la dirección de envío.
        refDir = reference.child(user.getUid()).child("listaTarjetas").child(t.getNumCard());


        btnRecargar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                payPalPay();
            }
        });

        btnEmilinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCard();
            }
        });
        btnDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.VISIBLE);
            }
        });
        btnPedirCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                bottonSheetDialog();

            }
        });
        
        btnActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.progresbar();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Tarjeta activada", Toast.LENGTH_SHORT).show();
                        if (activacion == true){
                            lblEstado.setText("Activada");
                        }
                        else {
                            lblEstado.setText("desactivada");
                        }

                        user = auth.getCurrentUser();

                        t.setEstado1(activacion);
                        Date date = new Date();
                        dateActivacion.setText(String.valueOf(date));
                        String fechaAc = dateActivacion.getText().toString();
                        DatabaseReference refCard = reference.child(user.getUid()).child("listaTarjetas");
                        // Limitación heredada: la activación usa el título, no la clave numCard del registro.
                        refCard.child(t.getTitulo()).child("Activado").setValue(activacion);
                        refCard.child(t.getTitulo()).child("Fecha de activación").setValue(fechaAc);
                    }
                }, 6000);
            }
        });
        return root;
    }

    /**
     * Relee la tarjeta local por identificador y configura acciones exclusivamente locales.
     * La eliminación exige confirmación y la interfaz solo avanza si la persistencia tiene éxito.
     */
    private void setupGuestCard(final TarjetaUser displayedCard) {
        final GuestCardStore store = new GuestCardStore(requireContext());
        Tarjeta selected = null;
        for (Tarjeta card : store.getCards()) {
            if (displayedCard.getNumCard().equals(card.getNumCard())) {
                selected = card;
                break;
            }
        }
        if (selected == null) {
            Toast.makeText(getContext(), "La tarjeta ya no está en este dispositivo", Toast.LENGTH_SHORT).show();
            return;
        }
        final Tarjeta localCard = selected;
        txtTitulo.setText(displayedCard.getTitulo() + " · Invitado");
        lblEstado.setText(localCard.isEstado() ? "Activada" : "Desactivada");
        btnPedirCard.setVisibility(View.GONE);
        btnRecargar.setVisibility(View.GONE);
        btnActivar.setEnabled(!localCard.isEstado());
        btnDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.VISIBLE);
            }
        });
        btnActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localCard.setEstado(true);
                if (store.save(localCard)) {
                    lblEstado.setText("Activada");
                    btnActivar.setEnabled(false);
                } else {
                    localCard.setEstado(false);
                    Toast.makeText(getContext(), "No se ha podido guardar el cambio", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnEmilinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Eliminar tarjeta")
                        .setMessage("¿Eliminar esta tarjeta de demostración de este dispositivo?")
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (store.remove(localCard.getNumCard())) {
                                    Navigation.findNavController(root).navigate(R.id.nav_tarjetas);
                                } else {
                                    Toast.makeText(getContext(), "No se ha podido eliminar la tarjeta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
            }
        });
    }

    /**
     * Diálogo de eliminación del flujo de cuenta original.
     * Actualmente solo cierra el diálogo y muestra un aviso: no llama a removeValue.
     */
    private void deleteCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.delete_card, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        builder.create().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnCancelar = view.findViewById(R.id.btnCancel);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Ha eliminado la tarjeta", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Recoge una dirección y escribe su texto en direccionDeEnvio de la tarjeta remota.
     * La confirmación tras seis segundos es una simulación; no espera el resultado de
     * la escritura ni crea un pedido en un servicio externo de envío.
     */
    public void bottonSheetDialog(){
        final BottomSheetDialog bottom = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(getActivity()).
                inflate(R.layout.add_direcction, linearLayout);
        Button btnAddDir = view.findViewById(R.id.btnAddDir);
        //Incializamos las variables a su elemento
        final TextView nameUser = view.findViewById(R.id.lblNombreUserDir);
        final TextInputLayout calle = view.findViewById(R.id.txtCalleDir);
        final TextInputLayout inf = view.findViewById(R.id.txtInfoDir);
        final TextInputLayout provincia = view.findViewById(R.id.txtProvinciaDir);
        final TextInputLayout cPostal = view.findViewById(R.id.txtCpDir);
        final TextInputLayout ciudad = view.findViewById(R.id.txtCiudadDir);
        ImageView imageView = view.findViewById(R.id.imgAddDir);
        final FirebaseUser user;
        nameUser.setText(lblNombre.getText().toString());
        user = auth.getCurrentUser();
        if (user.getPhotoUrl() != null){
            Picasso.with(getActivity()).load(user.getPhotoUrl()).into(imageView);
        }
        btnAddDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String calle1 = calle.getEditText().getText().toString();
                String inf1 = inf.getEditText().getText().toString();
                String provincia1 = provincia.getEditText().getText().toString();
                String cod_postal = cPostal.getEditText().getText().toString();
                String ciudad1 = ciudad.getEditText().getText().toString();
                DireccionUser direccionUser = new DireccionUser(calle1,inf1, provincia1, cod_postal, ciudad1);
                String direccion = calle1+", "+provincia1+", "+cod_postal+", "+ciudad1+"";

                refDir.child("direccionDeEnvio").setValue(direccion);
                bottom.dismiss();
                mainActivity.progresbar();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Se ha procesado la solicitud de envio\nSu tarjeta llegará entre 4 y 5 días", Toast.LENGTH_LONG).show();
                    }
                },6000);
            }
        });
        bottom.setContentView(view);
        bottom.show();
    }

    /** Obtiene el anfitrión que muestra los indicadores de progreso del flujo de cuenta. */
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    // Ejemplo de recarga en SANDBOX: no se utiliza el entorno de pagos reales.
    private int PAYPAL_REQUEST_CODE = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(PayPalConfig.PAYPAL_CLIENT_ID);


    /** Abre PayPal con el importe fijo del prototipo; no calcula viajes o saldo disponible. */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean payPalPay() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(precioCard),
                "EUR", "MOUTE RIDE", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        return true;
    }
    /** Recibe el retorno del SDK; el bloque RESULT_OK todavía no aplica ninguna recarga. */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            //Compruebo que le pago se haya reahlizado la porceder a contratar tarjeta si el usuario a realizado el pago
            if (resultCode == getActivity().RESULT_OK) {

            }
        }
    }

    /** Detiene el servicio del SDK que utiliza el flujo de cuenta al destruir esta pantalla. */
    @Override
    public void onDestroy() {
        if (getActivity() != null && user != null) {
            getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        }
        super.onDestroy();
    }

}
