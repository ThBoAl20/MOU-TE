package com.manuel.moute.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Tarjeta ya contratada tal como la consumen «Mis tarjetas» y su detalle.
 *
 * <p>A diferencia de Tarjeta, este modelo incluye fechas de activación/caducidad
 * y usa los nombres heredados cantidad y estado1. Esos nombres forman parte del
 * esquema leído de Firebase. Las tarjetas locales se convierten a este modelo
 * para compartir la misma lista; el origen local se pasa aparte en el Bundle.</p>
 */
public class TarjetaUser implements Parcelable {
    private String nombre;
    private String apellidos;
    private String dni;
    private String numCard;
    private String titulo;
    private String tipo;
    private int cantidad;
    private float precio;
    private boolean estado1;
    private String fechaContrato;
    private String fechaCaducidad;
    private String fechaActivacion;


    /** Constructor vacío requerido por el mapeo automático de Firebase. */
    public TarjetaUser() {
    }

    /** Inicializa únicamente los datos de titular e identificación. */
    public TarjetaUser(String nombre, String apellidos, String dni, String numCard) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.numCard = numCard;
    }

    /** Constructor parcial; modalidad, cantidad, estado y fechas se completan aparte. */
    public TarjetaUser(String nombre, String apellidos, String dni, String numCard, String titulo, float precio) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.numCard = numCard;
        this.titulo = titulo;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    /** Construye una tarjeta con todos los datos que se muestran en el detalle. */
    public TarjetaUser(String nombre, String apellidos, String dni, String numCard, String titulo, String tipo, int cantidad, float precio, boolean estado1, String fechaContrato, String fechaCaducidad, String fechaActivacion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.numCard = numCard;
        this.titulo = titulo;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.estado1 = estado1;
        this.fechaContrato = fechaContrato;
        this.fechaCaducidad = fechaCaducidad;
        this.fechaActivacion = fechaActivacion;
    }

    /** El orden debe coincidir con writeToParcel para no intercambiar campos. */
    protected TarjetaUser(Parcel in) {
        nombre = in.readString();
        apellidos = in.readString();
        dni = in.readString();
        numCard = in.readString();
        titulo = in.readString();
        tipo = in.readString();
        cantidad = in.readInt();
        precio = in.readFloat();
        estado1 = in.readByte() != 0;
        fechaContrato = in.readString();
        fechaCaducidad = in.readString();
        fechaActivacion = in.readString();

    }

    /** Recupera el modelo que el adaptador envía al fragmento de detalle. */
    public static final Creator<TarjetaUser> CREATOR = new Creator<TarjetaUser>() {
        @Override
        public TarjetaUser createFromParcel(Parcel in) {
            return new TarjetaUser(in);
        }

        @Override
        public TarjetaUser[] newArray(int size) {
            return new TarjetaUser[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNumCard() {
        return numCard;
    }

    public void setNumCard(String numCard) {
        this.numCard = numCard;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    /** Sobrecarga heredada: devuelve el estado almacenado, no utiliza el argumento. */
    public Boolean isEstado1(boolean activacion) {
        return estado1;
    }

    /** Getter sin argumentos utilizado para leer el estado del modelo. */
    public boolean isEstado1() {
        return estado1;
    }

    public void setEstado1(Boolean estado1) {
        this.estado1 = estado1;
    }

    public String getFechaContrato() {
        return fechaContrato;
    }

    public void setFechaContrato(String fechaContrato) {
        this.fechaContrato = fechaContrato;
    }

    public String getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(String fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getFechaActivacion() {
        return fechaActivacion;
    }

    public void setFechaActivacion(String fechaActivacion) {
        this.fechaActivacion = fechaActivacion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /** Conserva todo el estado al navegar, incluido el booleano codificado como byte. */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(apellidos);
        dest.writeString(dni);
        dest.writeString(numCard);
        dest.writeString(titulo);
        dest.writeString(tipo);
        dest.writeInt(cantidad);
        dest.writeFloat(precio);
        dest.writeByte((byte) (estado1 ? 1 : 0));
        dest.writeString(fechaContrato);
        dest.writeString(fechaCaducidad);
        dest.writeString(fechaActivacion);
    }
}
