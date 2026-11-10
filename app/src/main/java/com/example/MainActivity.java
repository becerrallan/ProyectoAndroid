package com.example;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conexiondb.FeeReaderContract;
import com.example.conexiondb.FeeReaderDbHelper;
import com.example.testclase1.R;

public class MainActivity extends AppCompatActivity {
    private EditText txtId;
    private EditText txtNombre;
    private EditText txtApellido;
    private FeeReaderDbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtId = findViewById(R.id.etId);
        txtNombre = findViewById(R.id.etNombre);
        txtApellido = findViewById(R.id.etApellido);
        dbHelper = new FeeReaderDbHelper(this);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    public void listar(View view){
        startActivity(new Intent(this, Listado.class));
    }

    public void guardar(View view){
        String nombre = txtNombre.getText().toString().trim();
        String apellido = txtApellido.getText().toString().trim();
        if (nombre.isEmpty() || apellido.isEmpty()){
            mostrarMensaje("Ingrese nombre o apellido");
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeeReaderContract.FeedEntry.column1, nombre);
        values.put(FeeReaderContract.FeedEntry.column2, apellido);
        long newRowId = db.insert(FeeReaderContract.FeedEntry.)
    }

    public void buscar(View vista){
        String id = leerId();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeeReaderContract.FeedEntry.column1,
                FeeReaderContract.FeedEntry.column2
        };
        String selection = FeeReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};
        try (Cursor cursor= db.query(
                FeeReaderContract.FeedEntry.nameTable,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        )){
            if (cursor.moveToFirst()){
                txtNombre.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(FeeReaderContract.FeedEntry.column1)));
                txtApellido.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(FeeReaderContract.FeedEntry.column2)
                ));
            } else{
                txtNombre.setText("");
                txtApellido.setText("");
                mostrarMensaje("No existe un registro con Id ");
            }
        }
    }

    public void actualizar(View vista) {
        String id = leerId();
        if (id == null) return;
        String nombre = txtNombre.getText().toString().trim();
        String apellido = txtApellido.getText().toString().trim();
        if (nombre.isEmpty() || apellido.isEmpty()) {
            mostrarMensaje("Ingresa nombre y apellido");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeeReaderContract.FeedEntry.column1, nombre);
        values.put(FeeReaderContract.FeedEntry.column2, apellido);
        String selection = FeeReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};

        int count = db.update(FeeReaderContract.FeedEntry.nameTable, values, selection, selectionArgs);
        if (count > 0) {
            mostrarMensaje("Se actualizó el registro con clave: " + id);
        } else {
            mostrarMensaje("No existe un registro con clave: " + id);
        }
    }
}
