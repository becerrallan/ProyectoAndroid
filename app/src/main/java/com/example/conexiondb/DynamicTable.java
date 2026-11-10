package com.example.conexiondb;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class DynamicTable {
    private static final int COLOR_CABEZERA = Color.parseColor("1A2142");
    private static final int COLOR_FILA_PAR = Color.parseColor("FFFFF");
    private static final int COLOR_FILA_IMPAR = Color.parseColor("F8F285");

    private final TableLayout table;
    private final Context context;
    private String[] cabecera;
    private ArrayList<String[]> datos;

    private TableRow fila;
    private TextView celda;

    public DynamicTable(TableLayout table, Context context) {
        this.table = table;
        this.context = context;
    }

    public void setCabecera(String[] cabecera) {
        this.cabecera = cabecera;
    }

    public void setDatos(ArrayList<String[]> datos) {
        this.datos = datos;
    }

    public void nuevaFila() {
        this.fila = new TableRow(context);
    }

    public void nuevaCelda() {
        celda = new TextView(context);
        celda.setGravity(Gravity.CENTER);
        celda.setPadding(24,16,24,16);
        celda.setTextSize(14);
    }

    public void crearCabecera(){
        nuevaFila();
        for(String titulo: cabecera){
            nuevaCelda();
            celda.setText(titulo);
            celda.setTextSize(18);
            celda.setTypeface(null, Typeface.BOLD);
            celda.setTextColor(Color.WHITE);
            celda.setBackgroundColor(COLOR_CABEZERA);
            fila.addView(celda, parametrosCelda());
        }
        table.addView(fila);
    }

    public void crearFila(){
        int numeroFila = 0;
        for(String[] datoFila: datos){
            nuevaFila();
            int colorFondo = (numeroFila % 2 == 0 ) ? COLOR_FILA_PAR : COLOR_FILA_IMPAR;
            for(String dato: datoFila){
                nuevaCelda();
                celda.setText(dato);
                celda.setTextSize(16);
                celda.setTextColor(Color.BLACK);
                celda.setBackgroundColor(colorFondo);
                fila.addView(celda, parametrosCelda());
            }
            table.addView(fila);
            numeroFila++;
        }
    }

    private TableRow.LayoutParams parametrosCelda() {
        TableRow.LayoutParams parametros = new TableRow.LayoutParams();
        parametros.setMargins(1,1,1,1);
        parametros.weight = 1;
        return parametros;
    }
}

