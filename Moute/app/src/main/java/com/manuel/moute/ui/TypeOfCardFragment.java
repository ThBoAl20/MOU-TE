package com.manuel.moute.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.manuel.moute.R;
import com.manuel.moute.models.CardNumberGenerator;
import com.manuel.moute.models.GuestCardStore;
import com.manuel.moute.models.PayPalConfig;
import com.manuel.moute.models.Tarjeta;
import com.manuel.moute.models.Usuario;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;


/**
 * Configura el título y precio de una tarjeta elegida en el catálogo de contratación.
 *
 * <p>Recibe una Tarjeta Parcelable en el argumento {@code tarjeta}. El formulario
 * completa esa misma instancia con identificador, titular y producto seleccionado.
 * Sin sesión confirma una demostración local mediante GuestCardStore; con sesión
 * conserva el ejemplo original de pago PayPal SANDBOX y guardado en Firebase.</p>
 *
 * <p>Los importes son valores fijos del prototipo. Este cliente no implementa cobros
 * reales, validación de precios en servidor ni verificación remota de pagos.</p>
 */
public class TypeOfCardFragment extends Fragment {
    private View root;
    //Variables Firebase
    private DatabaseReference userReference;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Map<String, Usuario> mapUser;
    private ArrayList<Usuario> listaUsuarios;
    //Variables Adapter
    private ArrayList<Tarjeta> tarjetaUsers;
    private NumberFormat numberFormat;


    private TextView txtNombre, txtApellido, txtDni, txtNumCard, txtTitulo;
    private Button btnContratar;

    private String numCard, tituloFinal;
    private float precioTotal = 0.0f;
    private Random random;
    private Tarjeta tarjeta;
    private Spinner spinner;
    private LinearLayout linearLayout, linearLayoutNum;
    private CheckBox chBonoOro, chAbTrasporte, chMouteJove, chBonoBus, chBnTransBordo, chBnTranBordoAB, chViajesSinLimites;
    private TextView lblPrecioKind, tituloCard, descripCard, valCard;
    private FrameLayout frameLayout;

    private int resource = R.layout.fragment_type_of_card;
    private DatabaseReference referenceCard;
    private boolean pagoRalizado = false;
    private Calendar calendar;

    //VARIABLES RADIOBUTTON, GROUP
    private RadioGroup rgViajes, rgBonoTrasbordo, rgBonoTrasbordoAB;
    private RadioButton rbViajes1, rbViajes2, rbViajes3;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_type_of_card, container, false);
        txtNombre = root.findViewById(R.id.txtNombreCardUser1);
        txtApellido = root.findViewById(R.id.txtApellidoCardUser1);
        txtDni = root.findViewById(R.id.txtDniCardUser1);
        txtNumCard = root.findViewById(R.id.txtNumCardUser1);
        txtTitulo = root.findViewById(R.id.lblTipoCardUser1);
        spinner = root.findViewById(R.id.spinner1);
        linearLayout = root.findViewById(R.id.tipoLinearLayout);
        linearLayoutNum = root.findViewById(R.id.numLinearLayout);
        frameLayout = root.findViewById(R.id.lblFragment);
        btnContratar = root.findViewById(R.id.btnContratar);
        // VARIBLES PARA SELECCIONAR EL TITULO DE LA TARJETA;
        chBonoOro = root.findViewById(R.id.chBonoOro);
        chAbTrasporte = root.findViewById(R.id.chAbTranporte);
        chMouteJove = root.findViewById(R.id.chMouteJove);

        //INCIALIZAMOS LAS VARIABLES CHECKBOXS
        chBonoBus = root.findViewById(R.id.chBonoBus);
        chBnTransBordo = root.findViewById(R.id.chBonoTransbordo);
        chBnTranBordoAB = root.findViewById(R.id.chBonoTrasporteAB);
        chViajesSinLimites = root.findViewById(R.id.chViajesSinLimites);
        lblPrecioKind = root.findViewById(R.id.lblPrecioKind);
        //VARIABLES FRAMELAYOUT CARD
        tituloCard = root.findViewById(R.id.tituloCard);
        descripCard = root.findViewById(R.id.descripcionCard);
        valCard = root.findViewById(R.id.validesCard);


        tarjetaUsers = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        // El formato usa la región del dispositivo; el ejemplo de PayPal fija EUR más abajo.
        numberFormat = NumberFormat.getCurrencyInstance();
        random = new Random();
        listaUsuarios = new ArrayList<>();
        //INTENT PARA INCICAR EL PAYACTIVITY DE PAYPAL
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        // Un invitado no inicia el servicio de pagos: su contratación solo se guarda localmente.
        if (user != null) {
            getActivity().startService(intent);
        }

        // Recogemos los datos enviados desde ContratarFragment antes de usar la pantalla.
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey("tarjeta")) {
            Toast.makeText(getActivity(), "No se ha podido cargar la tarjeta", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(root).navigate(R.id.contratarFragment);
            return root;
        }
        tarjeta = arguments.getParcelable("tarjeta");
        if (tarjeta == null) {
            btnContratar.setEnabled(false);
            return root;
        }

        //Llamo a los métodos y le paso los paramatemos que haya elegido el usuario
        userData();
        numCardRandom();
        txtNumCard.setText(numCard);
        txtTitulo.setText(tarjeta.getTitulo());
        //MÉTODO COMPROBACIÓN DE SPINNER
        tipoDeTarjeta();
        /**
         * AQUI LLAMO A LOS MÉTODOS PARA CONFIGURAR EL TÍTULO DE LA TARJETA
         * 1.  LLAMO A LOS METODOS DE LAS TARJETAS NO PERSONALIZADAS
         * 2. LLAMO A LOS METODOS DE LAS TARJETAS PERSONALIZADAS
         */
        BonoOro();
        AbonoTrasporte();
        MouteJove();
        BonoBus();
        BonoTransbordo();
        BonoTransbordoAB();
        viajesSinLimite();

        numberFormat.format(precioTotal);
        // Decide el almacenamiento según la sesión actual, no la que había al abrir la pantalla.
        btnContratar.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                if (auth.getCurrentUser() == null) {
                    confirmGuestCard();
                } else {
                    payPalPay();
                }

            }
        });

        return root;
    }



    /** Configura el selector de categorías y muestra el grupo de títulos correspondiente. */
    public void tipoDeTarjeta() {
        String[] opciones = {"Seleciona una opción", "Tarjeta MOUTE personalizadas", "Tarjeta MOUTE no personalizadas"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_custom, opciones);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resetSelection();
                String selecionar = spinner.getSelectedItem().toString();
                float precioTotal = 0.0f;
                float precioBus = 1.5F;
                float precioMetro = 1.5F;
                float precioTec = 2.5F;
                switch (selecionar) {
                    case "Tarjeta MOUTE personalizadas":
                        linearLayout.setVisibility(View.VISIBLE);
                        linearLayoutNum.setVisibility(View.GONE);
                        break;
                    case "Tarjeta MOUTE no personalizadas":
                        linearLayoutNum.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /** Evita conservar el precio o las opciones de la categoría elegida anteriormente. */
    private void resetSelection() {
        for (CheckBox option : new CheckBox[]{chBonoOro, chAbTrasporte, chMouteJove,
                chBonoBus, chBnTransBordo, chBnTranBordoAB, chViajesSinLimites}) {
            option.setChecked(false);
            option.setEnabled(true);
            option.setVisibility(View.VISIBLE);
        }
        precioTotal = 0;
        tituloFinal = "";
        tituloCard.setText("");
        descripCard.setText("");
        valCard.setText("");
        lblPrecioKind.setText(numberFormat.format(0));
        btnContratar.setEnabled(false);
        frameLayout.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        linearLayoutNum.setVisibility(View.GONE);
    }

    /** Explica que no hay cobro y solicita confirmación antes de crear la tarjeta local. */
    private void confirmGuestCard() {
        if (tituloFinal == null || tituloFinal.isEmpty() || precioTotal <= 0) {
            Toast.makeText(getContext(), "Selecciona un tipo de tarjeta", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Contratar como invitado")
                .setMessage("Esta contratación es una demostración. La tarjeta se guarda en este dispositivo y no se realiza ningún cobro.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Contratar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveGuestCard();
                    }
                }).show();
    }

    /**
     * Persiste una tarjeta de demostración, activa y sin identidad de una cuenta remota.
     * El identificador se regenera si ya está en el almacén local; la navegación solo
     * ocurre cuando GuestCardStore informa de que el guardado terminó correctamente.
     */
    private void saveGuestCard() {
        if (!isAdded()) {
            return;
        }
        GuestCardStore store = new GuestCardStore(requireContext());
        while (store.contains(tarjeta.getNumCard())) {
            numCardRandom();
        }
        tarjeta.setNombre("Invitado");
        tarjeta.setApellidos("");
        tarjeta.setDni("");
        tarjeta.setTipo(spinner.getSelectedItemPosition() == 1 ? "Personalizada" : "No personalizada");
        Calendar date = Calendar.getInstance();
        tarjeta.setFechaContrato(date.get(Calendar.DAY_OF_MONTH) + "/"
                + (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.YEAR));
        tarjeta.setEstado(true);
        if (!store.save(tarjeta)) {
            Toast.makeText(getContext(), "No se ha podido guardar la tarjeta", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(getContext(), "Tarjeta guardada en este dispositivo", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(root).navigate(R.id.nav_tarjetas);
    }


    // Configuración exclusiva de demostración. SANDBOX no representa un cobro real.
    private int PAYPAL_REQUEST_CODE = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    /** Abre la pantalla del SDK de PayPal con el importe del formulario en euros. */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean payPalPay() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(precioTotal),
                "EUR", "MOUTE RIDE", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        return true;
    }

    /**
     * Recibe el resultado del ejemplo PayPal y guarda la tarjeta bajo la cuenta actual.
     * RESULT_OK es aquí una señal del SDK cliente, no una verificación de pago en servidor.
     * La escritura remota tampoco espera un callback de confirmación antes de mostrar éxito.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            //Compruebo que le pago se haya reahlizado la porceder a contratar tarjeta si el usuario a realizado el pago
            // Demostración: PayPal se utiliza como muestra del flujo de pago.
            if (resultCode == getActivity().RESULT_OK) {
                user = auth.getCurrentUser();
                if (user == null) {
                    confirmGuestCard();
                    return;
                }
                userReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid());
                referenceCard = userReference.child("listaTarjetas").child(tarjeta.getNumCard());
                referenceCard.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<ArrayList<Tarjeta>> gti =
                                new GenericTypeIndicator<ArrayList<Tarjeta>>() {
                                };
                        tarjetaUsers = dataSnapshot.getValue(gti);
                        if (tarjetaUsers == null) {
                        }
                        tarjetaUsers = new ArrayList<>();
                        calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH)+1;
                        int year = calendar.get(Calendar.YEAR);

                        String sDate = day + "/" + month + "/" + year;
                        boolean actiavacion = true;
                        tarjeta.setFechaContrato(sDate);
                        tarjeta.setEstado(actiavacion);
                        // Esquema compartido con MisTarjetas y el panel: listaTarjetas/{numCard}.
                        referenceCard.setValue(tarjeta);

                        Toast.makeText(getActivity(), "Has contratado la tarjeta " +tarjeta.getTitulo()+" correctamente", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("t", tarjeta);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {
                Toast.makeText(getActivity(), "No se ha realizado el pago", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Detiene el servicio de demostración cuando se destruye esta pantalla con sesión. */
    @Override
    public void onDestroy() {
        if (getActivity() != null && user != null) {
            getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        }
        super.onDestroy();
    }

    /**
     * MÉTODOS PARA SELECIIONAL EL TITULO DE LAS TARJETAS PERSONALIZADAS
     */
    /** Selección personalizada Bono Oro: excluye los demás títulos y actualiza la vista previa. */
    public void BonoOro() {
        final float precioBus = 9.30F;
        chBonoOro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chBonoOro.isChecked()) {
                    chMouteJove.setEnabled(false);
                    chAbTrasporte.setEnabled(false);
                    String getData = chBonoOro.getText().toString();
                    numberFormat.format(precioBus);
                    precioTotal += precioBus;
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    frameLayout.setVisibility(View.VISIBLE);
                    //Habilito el Boton contratar que por defecto esta desahabilitado
                    btnContratar.setEnabled(true);
                    tituloFinal = "BONO ORO";
                    int aBt = R.string.tituloBO;
                    tituloCard.setText(aBt);
                    int aBd = R.string.descripcionBO;
                    descripCard.setText(aBd);
                    int aBv = R.string.validesBO;
                    valCard.setText(aBv);
                    tarjeta.setTitulo(tituloFinal);
                    tarjeta.setPrecio(precioTotal);
                } else if (!chBonoOro.isChecked()) {
                    chMouteJove.setEnabled(true);
                    chAbTrasporte.setEnabled(true);
                    precioTotal -= precioBus;
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    frameLayout.setVisibility(View.GONE);
                    //Vuelvo deshabilitar el boton contratar cuando el checkbox esta deshabilitado
                    btnContratar.setEnabled(false);
                    tituloFinal = "";
                    tituloCard.setText("");
                    valCard.setText("");
                    descripCard.setText("");
                    tarjeta.setTitulo(tituloFinal);
                }
            }
        });
    }

    /** Selección personalizada Abono Transporte con precio fijo del prototipo. */
    public void AbonoTrasporte() {
        final float precioAB = 1.00f;
        chAbTrasporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chAbTrasporte.isChecked()) {
                    chMouteJove.setEnabled(false);
                    chBonoOro.setEnabled(false);
                    //Habilito el Boton contratar que por defecto esta desahabilitado
                    btnContratar.setEnabled(true);
                    precioTotal += precioAB;
                    numberFormat.format(precioTotal);
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    int aBTt = R.string.tituloABT;
                    tituloCard.setText(aBTt);
                    int aBTd = R.string.descripcionABT;
                    descripCard.setText(aBTd);
                    int aBT = R.string.validesABT;
                    valCard.setText(aBT);
                    frameLayout.setVisibility(View.VISIBLE);
                    tituloFinal = "ABONO TRANSPORTE";

                    tarjeta.setTitulo(tituloFinal);
                    tarjeta.setPrecio(precioTotal);
                } else if (!chAbTrasporte.isChecked()) {
                    chMouteJove.setEnabled(true);
                    chBonoOro.setEnabled(true);
                    precioTotal -= precioAB;
                    numberFormat.format(precioTotal);
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    frameLayout.setVisibility(View.GONE);
                    //Vuelvo deshabilitar el boton contratar cuando el checkbox esta deshabilitado
                    btnContratar.setEnabled(false);
                    tituloFinal = "";
                    tituloCard.setText("");
                    valCard.setText("");
                    descripCard.setText("");
                    tarjeta.setTitulo(tituloFinal);


                }
            }
        });
    }

    /** Selección personalizada Moute Jove, mutuamente excluyente con Oro y Abono Transporte. */
    public void MouteJove() {
        final float precioMj = 20.00f;
        chMouteJove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chMouteJove.isChecked()) {
                    chAbTrasporte.setEnabled(false);
                    chBonoOro.setEnabled(false);
                    precioTotal += precioMj;
                    //Habilito el Boton contratar que por defecto esta desahabilitado
                    btnContratar.setEnabled(true);
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    int aMjt = R.string.tituloJove;
                    tituloCard.setText(aMjt);
                    int atD = R.string.descripcionJove;
                    descripCard.setText(atD);
                    int aMjd = R.string.validesJove;
                    valCard.setText(aMjd);
                    frameLayout.setVisibility(View.VISIBLE);
                    tituloFinal = "MOUTE JOVE";
                    tarjeta.setTitulo(tituloFinal);
                    tarjeta.setPrecio(precioTotal);
                } else if (!chMouteJove.isChecked()) {
                    chAbTrasporte.setEnabled(true);
                    chBonoOro.setEnabled(true);
                    precioTotal -= precioMj;
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    frameLayout.setVisibility(View.GONE);
                    //Vuelvo deshabilitar el boton contratar cuando el checkbox esta deshabilitado
                    btnContratar.setEnabled(false);
                    tituloFinal = "";
                    tituloCard.setText("");
                    valCard.setText("");
                    descripCard.setText("");
                    tarjeta.setTitulo(tituloFinal);
                    tarjeta.setPrecio(precioTotal);
                }
                numberFormat.format(precioTotal);
            }
        });

    }

    /**
     * MÉTODOS PARA SELECIIONAL EL TITULO DE LAS TARJETAS NO PERSONALIZADAS
     */
    /** Selección no personalizada Bono Bús; oculta alternativas mientras está marcada. */
    public void BonoBus() {
        final float precioBonoBus = 8.5F;
        chBonoBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chBonoBus.isChecked()) {
                    //CÓDIGO PAR HACER INVISIBLES LOS ELEMENTOS NO SELECCIONADOS
                    chBnTransBordo.setVisibility(View.GONE);
                    chBnTranBordoAB.setVisibility(View.GONE);
                    chViajesSinLimites.setVisibility(View.GONE);
                    //CÓDIGO PARA DESHABILITAR LOS DEMAS CHECHBOX
                    chBnTransBordo.setEnabled(false);
                    chBnTranBordoAB.setEnabled(false);
                    chViajesSinLimites.setEnabled(false);
                    //Habilito el Boton contratar que por defecto esta desahabilitado
                    btnContratar.setEnabled(true);

                    numberFormat.format(precioBonoBus);
                    precioTotal += precioBonoBus;
                    btnContratar.setEnabled(true);
                    numberFormat.format(precioTotal);
                    tituloCard.setText("BONO BÚS");
                    descripCard.setText("");
                    valCard.setText("");
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    frameLayout.setVisibility(View.VISIBLE);
                    tituloFinal = "BONO BÚS";
                    tarjeta.setTitulo(tituloFinal);
                    tarjeta.setPrecio(precioTotal);
                } else if (!chBonoBus.isChecked()) {
                    btnContratar.setEnabled(false);
                    chBnTranBordoAB.setEnabled(true);
                    chBnTransBordo.setEnabled(true);
                    chViajesSinLimites.setEnabled(true);
                    precioTotal -= precioBonoBus;
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    frameLayout.setVisibility(View.GONE);
                    //Vuelvo deshabilitar el boton contratar cuando el checkbox esta deshabilitado
                    btnContratar.setEnabled(false);

                    //CÓDIGO PAR HACER VISIBLE LOS ELEMENTOS NO SELECCIONADOS
                    chBnTransBordo.setVisibility(View.VISIBLE);
                    chBnTranBordoAB.setVisibility(View.VISIBLE);
                    chViajesSinLimites.setVisibility(View.VISIBLE);
                    tituloFinal = "";
                    tarjeta.setTitulo(tituloFinal);
                }
            }
        });

    }

    /** Selección no personalizada Transbordo con su precio fijo y estado de contratación. */
    public void BonoTransbordo() {
        final float precioMetro = 9.00f;
        chBnTransBordo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chBnTransBordo.isChecked()) {
                    //CÓDIGO PAR HACER INVISIBLES LOS ELEMENTOS NO SELECCIONADOS
                    chBonoBus.setVisibility(View.GONE);
                    chBnTranBordoAB.setVisibility(View.GONE);
                    chViajesSinLimites.setVisibility(View.GONE);
                    //Habilito el Boton contratar que por defecto esta desahabilitado
                    btnContratar.setEnabled(true);
                    //CÓDIGO PARA DESHABILITAR LOS DEMAS CHECHBOX
                    chBonoBus.setEnabled(false);
                    chBnTranBordoAB.setEnabled(false);
                    chViajesSinLimites.setEnabled(false);
                    String getData = chAbTrasporte.getText().toString();
                    precioTotal += precioMetro;
                    numberFormat.format(precioTotal);
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    tituloCard.setText("TRANSBORDO");
                    descripCard.setText("");
                    valCard.setText("");
                    frameLayout.setVisibility(View.VISIBLE);
                    tituloFinal = "TRANSBORDO";
                    tarjeta.setTitulo(tituloFinal);
                    tarjeta.setPrecio(precioTotal);
                } else if (!chBnTransBordo.isChecked()) {
                    chBonoBus.setEnabled(true);
                    chBnTranBordoAB.setEnabled(true);
                    chViajesSinLimites.setEnabled(true);
                    precioTotal -= precioMetro;
                    numberFormat.format(precioTotal);
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    frameLayout.setVisibility(View.GONE);
                    //Vuelvo deshabilitar el boton contratar cuando el checkbox esta deshabilitado
                    btnContratar.setEnabled(false);
                    //CÓDIGO PAR HACER VISIBLE LOS ELEMENTOS NO SELECCIONADOS
                    chBonoBus.setVisibility(View.VISIBLE);
                    chBnTranBordoAB.setVisibility(View.VISIBLE);
                    chViajesSinLimites.setVisibility(View.VISIBLE);
                    tituloFinal = "";
                    tarjeta.setTitulo(tituloFinal);
                }
            }
        });

    }

    /** Selección Transbordo AB; la etiqueta persistida usa la abreviatura histórica TRANSB ZONA AB. */
    public void BonoTransbordoAB() {
        final float precioTec = 15.50f;
        numberFormat.format(precioTec);
        chBnTranBordoAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chBnTranBordoAB.isChecked()) {
                    //CÓDIGO PAR HACER INVISIBLES LOS ELEMENTOS NO SELECCIONADOS
                    chBonoBus.setVisibility(View.GONE);
                    chBnTransBordo.setVisibility(View.GONE);
                    chViajesSinLimites.setVisibility(View.GONE);
                    //CÓDIGO PARA DESHABILITAR LOS DEMAS CHECHBOX
                    chBonoBus.setEnabled(false);
                    chBnTransBordo.setEnabled(false);
                    chViajesSinLimites.setEnabled(false);
                    //Habilito el Boton contratar que por defecto esta desahabilitado
                    btnContratar.setEnabled(true);
                    precioTotal += precioTec;
                    numberFormat.format(precioTotal);
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    tituloCard.setText("TRANSBORDO ZONA AB");
                    descripCard.setText("");
                    valCard.setText("");
                    frameLayout.setVisibility(View.VISIBLE);
                    tituloFinal = "TRANSB ZONA AB";
                    tarjeta.setTitulo(tituloFinal);
                    tarjeta.setPrecio(precioTotal);
                } else if (!chBnTranBordoAB.isChecked()) {

                    chBonoBus.setEnabled(true);
                    chBnTransBordo.setEnabled(true);
                    chViajesSinLimites.setEnabled(true);
                    //Vuelvo deshabilitar el boton contratar cuando el checkbox esta deshabilitado
                    btnContratar.setEnabled(false);
                    precioTotal -= precioTec;
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    frameLayout.setVisibility(View.GONE);
                    //CÓDIGO PAR HACER VISIBLE LOS ELEMENTOS NO SELECCIONADOS
                    chBonoBus.setVisibility(View.VISIBLE);
                    chBnTransBordo.setVisibility(View.VISIBLE);
                    chViajesSinLimites.setVisibility(View.VISIBLE);
                    tituloFinal = "";
                    tarjeta.setTitulo(tituloFinal);

                }
            }
        });
    }

    /**
     * Maneja la casilla Viajes sin límites. Limitación heredada: la vista previa y
     * el modelo usan la etiqueta VIAJES PERSONALIZADOS, distinta del nombre de la casilla.
     */
    private void viajesSinLimite() {
        final float precioTec = 9.70f;
        chViajesSinLimites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chViajesSinLimites.isChecked()) {
                    //CÓDIGO PAR HACER INVISIBLES LOS ELEMENTOS NO SELECCIONADOS
                    chBonoBus.setVisibility(View.GONE);
                    chBnTransBordo.setVisibility(View.GONE);
                    chBnTranBordoAB.setVisibility(View.GONE);
                    //CÓDIGO PARA DESHABILITAR LOS DEMAS CHECHBOX
                    chBonoBus.setEnabled(false);
                    chBnTransBordo.setEnabled(false);
                    chBnTranBordoAB.setEnabled(false);
                    //String getData = chMouteJove.getText().toString();
                    precioTotal += precioTec;
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    //Habilito el Boton contratar que por defecto esta desahabilitado
                    btnContratar.setEnabled(true);

                    tituloCard.setText("VIAJES PERSONALIZADOS");
                    descripCard.setText("");
                    valCard.setText("");
                    frameLayout.setVisibility(View.VISIBLE);
                    tituloFinal = "VIAJES PERSONALIZADOS";
                    tarjeta.setTitulo(tituloFinal);
                    tarjeta.setPrecio(precioTotal);
                } else if (!chViajesSinLimites.isChecked()) {
                    chBonoBus.setEnabled(true);
                    chBnTransBordo.setEnabled(true);
                    chBnTranBordoAB.setEnabled(true);
                    //Vuelvo deshabilitar el boton contratar cuando el checkbox esta deshabilitado
                    btnContratar.setEnabled(false);
                    precioTotal -= precioTec;
                    lblPrecioKind.setText(String.valueOf(precioTotal));
                    frameLayout.setVisibility(View.GONE);
                    //CÓDIGO PAR HACER VISIBLE LOS ELEMENTOS NO SELECCIONADOS
                    chBonoBus.setVisibility(View.VISIBLE);
                    chBnTransBordo.setVisibility(View.VISIBLE);
                    chBnTranBordoAB.setVisibility(View.VISIBLE);
                    chViajesSinLimites.setVisibility(View.VISIBLE);
                    tituloFinal = "";
                    tarjeta.setTitulo(tituloFinal);

                }
            }
        });
    }

    /** Genera un identificador del prototipo; no es un número bancario ni un token de pago. */
    public void numCardRandom() {
        numCard = CardNumberGenerator.generate(random);
        tarjeta.setNumCard(numCard);
    }

    /**
     * Carga el titular de la sesión desde Usuarios/{uid}, o muestra un titular invitado.
     * La lectura remota es continua; sus errores no se presentan en la interfaz actual.
     */
    private void userData() {
        if (user != null) {
            final FirebaseUser u;
            u = auth.getCurrentUser();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");
            reference.child(u.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<Map<String, Usuario>> gti = new GenericTypeIndicator<Map<String, Usuario>>() {
                    };
                    mapUser = dataSnapshot.getValue(gti);
                    listaUsuarios.clear();
                    if (mapUser != null) {
                        listaUsuarios.addAll(mapUser.values());
                    }
                    for (int i = 0; i < listaUsuarios.size(); i++) {
                        if (u.getEmail().equalsIgnoreCase(listaUsuarios.get(i).getCorreo())) {
                            txtNombre.setText(listaUsuarios.get(i).getNombre());
                            txtApellido.setText(listaUsuarios.get(i).getApellido());
                            txtDni.setText(listaUsuarios.get(i).getDni());
                            tarjeta.setNombre(txtNombre.getText().toString());
                            tarjeta.setApellidos(txtApellido.getText().toString());
                            tarjeta.setDni(txtDni.getText().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            txtNombre.setText("Invitado");
            txtApellido.setText("");
            txtDni.setText("");
        }
    }

}
