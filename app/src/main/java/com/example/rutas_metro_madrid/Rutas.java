package com.example.rutas_metro_madrid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Rutas extends AppCompatActivity {

    public String linea;
    public Location[] paradas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas);

        Intent miIntento=this.getIntent();
        String linea=miIntento.getStringExtra("LINEAS");
        ((TextView)findViewById(R.id.linea)).setText(linea);
        Bundle miBundle=getIntent().getExtras();
        Parcelable[] datos=miBundle.getParcelableArray("PARADAS");
        Location[] ruta= Arrays.copyOf(datos,datos.length,Location[].class);
        LinearLayout rutaContenedor=(LinearLayout)findViewById(R.id.pantalla);
        LayoutInflater inflador=(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        pintaEtapa(inflador,R.drawable.esfera_inicio_fin, ruta[0].getProvider(),rutaContenedor);
        String texto=getString(R.string.a_pie)+ " "+(int)(ruta[0].distanceTo(ruta[1]));
        texto+=" " + getString(R.string.metros);
        pintaEtapa(inflador,R.drawable.caminando,texto,rutaContenedor);
        for (int i = 1; i < ruta.length-1; i++) {
            pintaEtapa(inflador,R.drawable.esfera_etapa, ruta[i].getProvider(), rutaContenedor);
        }
        if (ruta.length>2){
            texto=getString(R.string.a_pie)+" "+(int)(ruta[ruta.length-2].distanceTo((ruta[ruta.length-1])));
            texto+=" "+getString(R.string.metros);
            pintaEtapa(inflador,R.drawable.caminando, texto,rutaContenedor);
        }
        pintaEtapa(inflador,R.drawable.esfera_inicio_fin,ruta[ruta.length-1].getProvider(),rutaContenedor);
    }
    private  void pintaEtapa(LayoutInflater inflador, int imagen, String texto, LinearLayout contenedor){
        LinearLayout distanciaEstaciones=(LinearLayout)inflador.inflate(R.layout.distancia_estaciones,null);
        ((ImageView)distanciaEstaciones.findViewById(R.id.icono)).setImageResource(imagen);
        ((TextView)distanciaEstaciones.findViewById(R.id.texto)).setText(texto);
        contenedor.addView(distanciaEstaciones);
    }
    public void mapa(View vista){
        Bundle miBundle=getIntent().getExtras();
        Intent miIntento=new Intent(this,MapsActivity.class);
        miIntento.putExtras(miBundle);
        startActivity(miIntento);

    }
    public void mejorRuta(Location origen, Location destino, Lineas[] lasLineas){
        Lineas mejorLinea=null;
        for (Lineas linea: lasLineas) {
            linea.distancias(origen, destino);
            if (mejorLinea==null || linea.sumaDistMetros()<mejorLinea.sumaDistMetros()){
                mejorLinea=linea;
            }
        }
        if(mejorLinea==null || origen.distanceTo(destino)<mejorLinea.sumaDistMetros()){
            linea=null;
            paradas=new Location[2];
            paradas[0]=origen;
            paradas[1]=destino;
            return;
        }
        paradas=new Location[mejorLinea.finalRuta-mejorLinea.origenRuta+2];
        paradas[0]=origen;
        paradas[paradas.length-1]=destino;
        for (int i = 1; i < paradas.length-2; i++) {
            paradas[i]=mejorLinea.estaciones[i+mejorLinea.origenRuta-1];
        }
    }
}
