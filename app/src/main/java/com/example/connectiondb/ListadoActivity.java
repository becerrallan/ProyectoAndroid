package com.example.connectiondb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conexiondb.DynamicTable;
import com.example.conexiondb.FeedReaderContract;
import com.example.conexiondb.FeedReaderDbHelper;

import java.util.ArrayList;

public class ListadoActivity extends AppCompatActivity {
    private final String[] cabecera = {"Id", "Nombre", "Apellido"};
    private final ArrayList<String[]> datos = new ArrayList<>();
    private FeedReaderDbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        dbHelper = new FeedReaderDbHelper(this);
        TableLayout tbListado = findViewById(R.id.tblistado);
        TextView txtVacio = findViewById(R.id.txtVacio);
        if (txtVacio == null) {
            txtVacio = findViewById(R.id.txtvacio);
        }

        traerDatos();

        if (datos.isEmpty()) {
            if (txtVacio != null) {
                txtVacio.setVisibility(View.VISIBLE);
            }
            if (tbListado != null) {
                tbListado.setVisibility(View.GONE);
            }
        } else {
            if (txtVacio != null) {
                txtVacio.setVisibility(View.GONE);
            }
            if (tbListado != null) {
                tbListado.setVisibility(View.VISIBLE);
                DynamicTable creaTabla = new DynamicTable(tbListado, this);
                creaTabla.setCabecera(cabecera);
                creaTabla.setDatos(datos);
                creaTabla.crearCabecera();
                creaTabla.crearFila();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }

    private void traerDatos() {
        datos.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.column1,
                FeedReaderContract.FeedEntry.column2
        };
        String sortOrder = FeedReaderContract.FeedEntry.column2 + " ASC";

        try (Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.nameTable,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        )) {
            while (cursor.moveToNext()) {
                String id = String.valueOf(cursor.getLong(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID)));
                String nombre = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column1));
                String apellido = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column2));
                datos.add(new String[]{id, nombre, apellido});
            }
        }
    }
}
