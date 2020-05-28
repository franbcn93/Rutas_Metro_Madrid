package com.example.rutas_metro_madrid;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Lineas implements Parcelable {
    public String nombre;
    public Location[] estaciones;
    public int origenRuta;
    public int finalRuta;
    public double datosParadaOrigen;
    public double datosParadaFinal;


    public void distancias(Location origen, Location destino){
        datosParadaOrigen=origen.distanceTo(estaciones[0]);
        datosParadaFinal=destino.distanceTo(estaciones[0]);
        for (int i = 1; i < estaciones.length; i++) {
            if (origen.distanceTo(estaciones[i])<datosParadaOrigen){
                origenRuta=i;
                datosParadaOrigen=origen.distanceTo(estaciones[i]);
            }
            if (destino.distanceTo(estaciones[i])<datosParadaFinal){
                finalRuta=i;
                datosParadaFinal=origen.distanceTo(estaciones[i]);
            }
        }
        
    }

    public double sumaDistMetros(){

        return datosParadaOrigen+datosParadaFinal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(nombre);
        parcel.writeTypedArray(estaciones,0);
        parcel.writeInt(origenRuta);
        parcel.writeInt(finalRuta);
        parcel.writeDouble(datosParadaOrigen);
        parcel.writeDouble(datosParadaFinal);
    }

    public static final Parcelable.Creator<Lineas> CREATOR=new Parcelable.Creator<Lineas>(){
        public Lineas createFromParcel(Parcel paquete){

            return new Lineas(paquete);

        }
        public Lineas[] newArray(int tamagno){
            return new Lineas[tamagno];
        }
    };

    public Lineas(){

    }

    private Lineas(Parcel parcel){

        nombre=parcel.readString();
        estaciones=parcel.createTypedArray(Location.CREATOR);
        origenRuta=parcel.readInt();
        finalRuta=parcel.readInt();
        datosParadaOrigen=parcel.readDouble();
        datosParadaFinal=parcel.readDouble();

    }
}
