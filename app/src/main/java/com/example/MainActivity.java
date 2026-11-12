package com.example;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        long newRowId = db.insert(FeeReaderContract.FeedEntry.nameTable, null, values);
        if (newRowId == -1){
            mostrarMensaje("No se guardo el registro");
        }else {
            txtId.setText(String.valueOf(newRowId));
            mostrarMensaje("Se guardo con exito");
        }
    }

    public void buscar(View view){
        String id = leerId();
        if (id == null || id.isEmpty()){return;}
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeeReaderContract.FeedEntry.column1,
                FeeReaderContract.FeedEntry.column2
        };
        String selection = FeeReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};
        try(Cursor cursor = db.query(
                FeeReaderContract.FeedEntry.nameTable,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()){
                txtNombre.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(FeeReaderContract.FeedEntry.column1)
                ));
                txtApellido.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(FeeReaderContract.FeedEntry.column2)
                ));
            }else{
                txtNombre.setText("");
                txtApellido.setText("");
                mostrarMensaje("No existe un registro con ese id");
            }
        }
    }

    public void actualizar(View view) {
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

    public void eliminar(View view){
        String id = leerId();
        if (id == null) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = FeeReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};
        int deletedRows = db.delete(FeeReaderContract.FeedEntry.nameTable, selection, selectionArgs);
        if (deletedRows > 0) {
            limpiarCampos();
        }
        mostrarMensaje("Se eliminó " + deletedRows + " registro(s)");
    }

    private void limpiarCampos(){
        txtId.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
    }

    private String leerId(){
        String id = txtId.getText().toString().trim();
        if(id.isEmpty()){
            mostrarMensaje("Ingrese el ID del registro");
            return null;
        }
        return id;
    }

    private void mostrarMensaje(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

}
