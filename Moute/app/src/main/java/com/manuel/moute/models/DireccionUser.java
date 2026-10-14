package com.manuel.moute.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.manuel.moute.SplashScreen;

/**
 * Dirección facilitada en la solicitud de envío de una tarjeta física de prueba.
 * Es un contenedor de datos personales, no un servicio de envío real. Los nombres
 * heredados de sus propiedades se conservan porque se usan al guardar en Firebase.
 */
public class DireccionUser implements Parcelable {
    private String direcionCompleta;
    private String info;
    private String codigo_postal;
    private String provincia;
    private String Ciudad;

    /** Constructor vacío para la deserialización del modelo. */
    public DireccionUser() {
    }

    /** CP corresponde al código postal; info añade detalles de la dirección. */
    public DireccionUser(String direcionCompleta, String info, String CP, String provincia, String ciudad) {
        this.direcionCompleta = direcionCompleta;
        this.info = info;
        this.codigo_postal = CP;
        this.provincia = provincia;
        Ciudad = ciudad;
    }


    /** Lee los campos en el mismo orden que writeToParcel. */
    protected DireccionUser(Parcel in) {
        direcionCompleta = in.readString();
        info = in.readString();
        codigo_postal = in.readString();
        provincia = in.readString();
        Ciudad = in.readString();
    }

    public String getDirecionCompleta() {
        return direcionCompleta;
    }

    public void setDirecionCompleta(String direcionCompleta) {
        this.direcionCompleta = direcionCompleta;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCodigo_postal() {
        return codigo_postal;
    }

    public void setCodigo_postal(String CP) {
        this.codigo_postal = CP;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCiudad() {
        return Ciudad;
    }

    public void setCiudad(String ciudad) {
        Ciudad = ciudad;
    }

    /** Fábrica requerida por Android para enviar direcciones dentro de un Bundle. */
    public static final Creator<DireccionUser> CREATOR = new Creator<DireccionUser>() {
        @Override
        public DireccionUser createFromParcel(Parcel in) {
            return new DireccionUser(in);
        }

        @Override
        public DireccionUser[] newArray(int size) {
            return new DireccionUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /** Conserva los cinco campos de dirección al pasar el modelo entre pantallas. */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(direcionCompleta);
        dest.writeString(info);
        dest.writeString(codigo_postal);
        dest.writeString(provincia);
        dest.writeString(Ciudad);
    }
}
