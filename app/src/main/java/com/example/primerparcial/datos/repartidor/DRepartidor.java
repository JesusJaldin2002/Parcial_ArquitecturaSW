package com.example.primerparcial.datos.repartidor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

public class DRepartidor {

    private DBHelper dbHelper;
    private int id;
    private String nombre;
    private String nroTelefono;

    public DRepartidor() {
    }

    public DRepartidor(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DRepartidor(int id, String nombre, String nroTelefono) {
        this.id = id;
        this.nombre = nombre;
        this.nroTelefono = nroTelefono;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNroTelefono() {
        return nroTelefono;
    }

    public void setNroTelefono(String nroTelefono) {
        this.nroTelefono = nroTelefono;
    }

    // Método para cargar los datos de un repartidor a partir de un Cursor
    public void cargarDesdeCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        this.nroTelefono = cursor.getString(cursor.getColumnIndexOrThrow("nroTelefono"));
    }

    // Método para insertar un repartidor
    public void insertarRepartidor(String nombre, String nroTelefono) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("nroTelefono", nroTelefono);
        db.insert("repartidores", null, values);
        db.close();
    }

    // Método para obtener todos los repartidores
    public Cursor obtenerRepartidores() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT id, nombre, nroTelefono FROM repartidores", null);
    }

    // Método para actualizar un repartidor
    public void actualizarRepartidor(String id, String nombre, String nroTelefono) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("nroTelefono", nroTelefono);

        // Actualizamos el repartidor donde el ID coincida
        String[] args = {id};
        db.update("repartidores", values, "id=?", args);
        db.close();
    }

    // Método para eliminar un repartidor
    public void eliminarRepartidor(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("repartidores", "id=?", new String[]{id});
        db.close();
    }
}
