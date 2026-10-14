package com.manuel.moute.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Datos de una tarjeta del catálogo o de una contratación de demostración.
 *
 * <p>Los fragmentos completan titular, modalidad, precio, viajes y fechas durante
 * la contratación. Los getters/setters y el constructor vacío permiten mapear
 * datos de Firebase; Parcelable permite pasar el modelo en un Bundle entre
 * pantallas. Un objeto de este modelo no acredita un pago real.</p>
 */
public class Tarjeta implements Parcelable {
    // Datos del titular; para invitados se usan datos neutrales, no una cuenta Firebase.
    private String nombre;
    private String apellidos;
    private String dni;
    // Identificador de demostración; no es el número de una tarjeta bancaria.
    private String numCard;
    private float precio;
    private int cantidadViajes;
    // tipo: personal/no personal; titulo: producto o bono de transporte elegido.
    private String tipo;
    private String titulo;
    private String fechaContrato;
    private boolean estado;
    /** Necesario para la deserialización de Firebase y la reconstrucción local. */
    public Tarjeta() {
    }

    /**
     * Constructor heredado parcial: actualmente solo asigna nombre, apellidos y
     * DNI. El argumento numCard no se asigna aquí; los flujos activos usan setters
     * o el constructor completo para establecer el identificador.
     */
    public Tarjeta(String nombre, String apellidos, String dni, String numCard) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.titulo = titulo;
        this.precio = precio;
    }

    /** Inicializa una selección de tarjeta; fecha y estado se completan después. */
    public Tarjeta(String nombre, String apellidos, String dni, String numCard, float precio, int cantidadViajes, String tipo, String titulo) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.numCard = numCard;
        this.precio = precio;
        this.cantidadViajes = cantidadViajes;
        this.tipo = tipo;
        this.titulo = titulo;
    }



    /** Lee exactamente el mismo orden y los mismos tipos que writeToParcel. */
    protected Tarjeta(Parcel in) {
        nombre = in.readString();
        apellidos = in.readString();
        dni = in.readString();
        numCard = in.readString();
        precio = in.readFloat();
        cantidadViajes = in.readInt();
        tipo = in.readString();
        titulo = in.readString();
        fechaContrato = in.readString();
        estado = in.readByte() != 0;
    }

    /** Fábrica que Android utiliza para recuperar la tarjeta de un Bundle. */
    public static final Creator<Tarjeta> CREATOR = new Creator<Tarjeta>() {
        @Override
        public Tarjeta createFromParcel(Parcel in) {
            return new Tarjeta(in);
        }

        @Override
        public Tarjeta[] newArray(int size) {
            return new Tarjeta[size];
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

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getCantidadViajes() {
        return cantidadViajes;
    }

    public void setCantidadViajes(int cantidadViajes) {
        this.cantidadViajes = cantidadViajes;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFechaContrato() {
        return fechaContrato;
    }

    public void setFechaContrato(String fechaContrato) {
        this.fechaContrato = fechaContrato;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Serializa todos los campos. El booleano se escribe como byte para conservar
     * compatibilidad con el Android mínimo del proyecto; no cambiar solo la lectura.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(apellidos);
        dest.writeString(dni);
        dest.writeString(numCard);
        dest.writeFloat(precio);
        dest.writeInt(cantidadViajes);
        dest.writeString(tipo);
        dest.writeString(titulo);
        dest.writeString(fechaContrato);
        dest.writeByte((byte) (estado ? 1 : 0));


    }
}
