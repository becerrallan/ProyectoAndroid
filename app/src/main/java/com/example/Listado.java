package com.example;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.conexiondb.DynamicTable;
import com.example.conexiondb.FeeReaderContract;
import com.example.conexiondb.FeeReaderDbHelper;
import com.example.testclase1.R;

import java.util.ArrayList;
public class Listado extends AppCompatActivity {
    private final String[] cabecera = {"id", "Nombre", "Apellido"};
    private final ArrayList<String[]> datos = new ArrayList<>();

    private FeeReaderDbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
        View listado = findViewById(R.id.listado);
        int paddingLeft = listado.getPaddingLeft();
        int paddingTop = listado.getPaddingTop();
        int paddingRight = listado.getPaddingRight();
        int paddingBottom = listado.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(listado, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            view.setPadding(paddingLeft + insets.left, paddingTop + insets.top,
                    paddingRight + insets.right, paddingBottom + insets.bottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(listado);
        dbHelper = new FeeReaderDbHelper(this);
        TableLayout tbListado = findViewById(R.id.tblistado);
        TextView txtVacio = findViewById(R.id.txtVacio);
        traerDatos();
        if(datos.isEmpty()){
            txtVacio.setVisibility(View.VISIBLE);
        }else {
            DynamicTable creaTable = new DynamicTable(tbListado, this);
            creaTable.setCabecera(cabecera);
            creaTable.setDatos(datos);
            creaTable.crearCabecera();
            creaTable.crearFila();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }


    private void traerDatos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projecView = {
                BaseColumns._ID,
                FeeReaderContract.FeedEntry.column1,
                FeeReaderContract.FeedEntry.column2
        };
        String sortOrder = FeeReaderContract.FeedEntry.column2 + " ASC";
        try(Cursor cursor = db.query(
                FeeReaderContract.FeedEntry.nameTable,
                projecView,
                null,
            null,
                null,
                null,
                sortOrder
        )) {
            while(cursor.moveToNext()){
                String[] fila = new String[3];
                fila[0] = String.valueOf(cursor.getLong(
                        cursor.getColumnIndexOrThrow(FeeReaderContract.FeedEntry._ID)
                ));
                fila[1] = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeeReaderContract.FeedEntry.column1)
                );
                fila[2] = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeeReaderContract.FeedEntry.column2)
                );
                datos.add(fila);
            }
        }
    }

    public void regresar(View view){
        finish();
    }


}
