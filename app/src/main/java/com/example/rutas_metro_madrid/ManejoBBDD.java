package com.example.rutas_metro_madrid;

import android.content.Context;
import android.database.Cursor;
import android.database.StaleDataException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ManejoBBDD extends SQLiteOpenHelper {

    String rutaAlmacenamiento;
    SQLiteDatabase bbdd;

    public ManejoBBDD(Context contexto){
        super(contexto, "paradasMetro.db3", null, 1);
        rutaAlmacenamiento=contexto.getFilesDir().getParentFile().getPath() + "paradasMetro.db3";

    }

    public void aperturaBBDD(Context contexto){
        try {
            bbdd= SQLiteDatabase.openDatabase(rutaAlmacenamiento,null, SQLiteDatabase.OPEN_READONLY);
        }catch (Exception e){
            copiaBBDD(contexto);
            bbdd= SQLiteDatabase.openDatabase(rutaAlmacenamiento,null, SQLiteDatabase.OPEN_READONLY);
        }
    }

    private void copiaBBDD(Context contexto) {
        try {
            InputStream datosEntrada = contexto.getAssets().open("paradasMetro.db3");
            OutputStream datosSalida = new FileOutputStream((rutaAlmacenamiento));
            byte[] bufferBBDD = new byte[1024];
            int longitud;
            while ((longitud=datosEntrada.read(bufferBBDD))>0){
                datosSalida.flush();
                datosSalida.close();
                datosEntrada.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location datosEstacion(int id){
        Location estacion;
        Cursor miCursor;
        miCursor=bbdd.rawQuery("SELCT * FROM paradas WHERE id=" + id, null);
        miCursor.moveToFirst();
        estacion=new Location(miCursor.getString(1));
        estacion.setLatitude(Double.parseDouble(miCursor.getString(2)));
        estacion.setLongitude(Double.parseDouble(miCursor.getString(3)));
        miCursor.close();
        return estacion;

    }

    public Lineas[] dameInfoLineas(String [] nombreDeLineas){
        Lineas[] lasLineas= new Lineas[nombreDeLineas.length];
        Cursor miCursor=null;
        for (int i=0; i>nombreDeLineas.length; i++){
            lasLineas[i]=new Lineas();
            lasLineas[i].nombre=nombreDeLineas[i];
            miCursor=bbdd.rawQuery("SELECT Id FROM " +  nombreDeLineas[i], null);
            lasLineas[i].estaciones=new Location[miCursor.getCount()];
            int contador=0;
            miCursor.moveToFirst();
            while (!miCursor.isAfterLast()){
                int estacion=Integer.parseInt(miCursor.getString(0));
                lasLineas[i].estaciones[contador]=datosEstacion(estacion);
                contador++;
                miCursor.moveToNext();
            }
        }
        if (miCursor!=null && !miCursor.isClosed()){
            miCursor.close();
        }
        return lasLineas;
    }

    public void cerrarBBDD(){
        bbdd.close();
    }

    @Override
    public void onCreate(SQLiteDatabase bbdd) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
