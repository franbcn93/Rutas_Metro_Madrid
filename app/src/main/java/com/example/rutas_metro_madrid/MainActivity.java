package com.example.rutas_metro_madrid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    public final String[] LINEAS = {"Linea2", "Linea3", "Linea6"};
    Lineas[] lineas;
    private ProgressBar barraProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barraProgreso=findViewById(R.id.barraProgreso);
        barraProgreso.setVisibility(View.VISIBLE);
        Sincroniza comienzo=new Sincroniza();
        comienzo.execute();
    }

    private class Sincroniza extends AsyncTask<String, Integer,String>{

        @Override
        protected String doInBackground(String... strings) {
            ManejoBBDD bbdd = new ManejoBBDD(getApplicationContext());
            try {
                bbdd.aperturaBBDD(getApplicationContext());
                lineas=bbdd.dameInfoLineas(LINEAS);
                bbdd.cerrarBBDD();

            }catch (Exception e){
                finish();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... valores){
            barraProgreso.setProgress(valores[0], true);
        }
        
        protected void onPostExecute(String resultado){

            comenzar();
        }

    }

    private void comenzar() {
        Bundle miBundle=new Bundle();
        miBundle.putParcelableArray("LINEAS",lineas);
        Intent miIntent=new Intent(this, Buscador.class);
    }

}
