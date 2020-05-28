package com.example.rutas_metro_madrid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Buscador extends AppCompatActivity {

    Lineas[] lineas;
    Rutas ruta;
    private ProgressBar barraProgreso;
    EditText origen, destino;
    Button enviar;
    String direccionOrigen, direccionDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);
    }
    public void onResume() {

        super.onResume();
        origen=findViewById(R.id.origen);
        origen.setText("");
        destino=findViewById(R.id.destino);
        destino.setText("");
        enviar=findViewById(R.id.enviar);
        enviar.setAlpha(1);
        enviar.setEnabled(true);

        if (lineas==null){
            Bundle miBundle=getIntent().getExtras();
            Parcelable[] datos=miBundle.getParcelableArray("LINEAS");
            lineas= Arrays.copyOf(datos, datos.length, Lineas[].class);
        }
    ruta=new Rutas();

    }

    public void leeDirecciones(View Vista){
        enviar.setAlpha(0);
        enviar.setEnabled(false);
        direccionOrigen=origen.getText().toString();
        direccionDestino=destino.getText().toString();
        InputMethodManager intoduce= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        intoduce.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        barraProgreso=new ProgressBar(this);
        EjetutaSegundoPlano tarea=new EjetutaSegundoPlano();
        tarea.execute();
    }
    private class EjetutaSegundoPlano extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            Location puntoOrigen;
            Location puntoDestino;
            Context contexto=getApplicationContext();
            ConnectivityManager miManager=(ConnectivityManager)contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo estadoRed=miManager.getActiveNetworkInfo();
            if(estadoRed==null || estadoRed.isConnected() || !estadoRed.isAvailable()){
                return getString(R.string.error_iconexion);
            }
            try {
                puntoOrigen=OptimizacionBusqueda.busca(direccionOrigen);
                if (puntoOrigen==null) return getString(R.string.error_destino);
                puntoDestino=OptimizacionBusqueda.busca(direccionDestino);
                if(puntoDestino==null) return getString(R.string.error_destino);

            }catch (Exception e) {
                return getString((R.string.error_red));
            }
            ruta.mejorRuta(puntoOrigen, puntoDestino, lineas);
            return null;
        }
        protected void onPostExecute(String resultado){
            barraProgreso=null;
            enviar.setAlpha(1);
            enviar.setEnabled(true);
            muestraRuta();
        }


    }
    private void muestraRuta() {

    }

    public void onBackPressed(){
        if(barraProgreso!=null) barraProgreso=null;
        moveTaskToBack(true);

    }
}
