package com.manuel.moute.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Datos de perfil asociados a una cuenta de Firebase Authentication.
 *
 * <p>Se guardan bajo Usuarios/{uid}/Datos y se pasan entre los pasos de registro
 * mediante Parcelable. Incluyen información personal y no deben considerarse
 * públicos. Este modelo no contiene contraseñas: las credenciales corresponden
 * a Firebase Authentication, no al perfil de Realtime Database.</p>
 */
public class Usuario implements Parcelable {
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String dni;
    private String fechaNac;
    private String genero;
    private String fotoPerfil;
    private ArrayList<Tarjeta> listaTarjetas;

    /** Constructor vacío para que Firebase reconstruya el perfil desde el JSON. */
    public Usuario() {
    }

    /** Los pasos posteriores del registro completan teléfono, nacimiento y género. */
    public Usuario(String nombre, String apellido, String correo, String dni) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.dni = dni;
    }

    /** Lee el perfil y la lista tipada de tarjetas en el orden de writeToParcel. */
    protected Usuario(Parcel in) {
        nombre = in.readString();
        apellido = in.readString();
        correo = in.readString();
        telefono = in.readString();
        dni = in.readString();
        fechaNac = in.readString();
        genero = in.readString();
        fotoPerfil = in.readString();
        listaTarjetas = in.createTypedArrayList(Tarjeta.CREATOR);
    }

    /** Permite recuperar «usuario» de los argumentos de navegación. */
    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getFechaNac() { return fechaNac; }
    public void setFechaNac(String fechaNac) { this.fechaNac = fechaNac; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public ArrayList<Tarjeta> getListaTarjetas() { return listaTarjetas; }
    public void setListaTarjetas(ArrayList<Tarjeta> listaTarjetas) { this.listaTarjetas = listaTarjetas; }

    @Override
    public int describeContents() {
        return 0;
    }

    /** Serializa el perfil, sin credenciales, para enviarlo entre fragmentos. */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(apellido);
        dest.writeString(correo);
        dest.writeString(telefono);
        dest.writeString(dni);
        dest.writeString(fechaNac);
        dest.writeString(genero);
        dest.writeString(fotoPerfil);
        dest.writeTypedList(listaTarjetas);
    }
}
