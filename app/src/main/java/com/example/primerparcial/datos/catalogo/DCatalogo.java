package com.example.primerparcial.datos.catalogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

public class DCatalogo {

    private DBHelper dbHelper;
    private int id;
    private String nombre;
    private String fecha;
    private String descripcion;

    public DCatalogo() {
    }

    public DCatalogo(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DCatalogo(int id, String nombre, String fecha, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.descripcion = descripcion;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Método para cargar los datos de un catálogo a partir de un Cursor
    public void cargarDesdeCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        this.fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
        this.descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
    }

    // Método para insertar un catálogo
    public void insertarCatalogo(String nombre, String fecha, String descripcion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("fecha", fecha);
        values.put("descripcion", descripcion);
        db.insert("catalogos", null, values);
        db.close();
    }

    // Método para obtener todos los catálogos
    public Cursor obtenerCatalogos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT id, nombre, fecha, descripcion FROM catalogos", null);
    }

    // Método para actualizar un catálogo
    public void actualizarCatalogo(String id, String nombre, String fecha, String descripcion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("fecha", fecha);
        values.put("descripcion", descripcion);

        // Actualizamos el catálogo donde el ID coincida
        String[] args = { id };
        db.update("catalogos", values, "id=?", args);
        db.close();
    }

    // Método para eliminar un catálogo
    public void eliminarCatalogo(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("catalogos", "id=?", new String[]{id});
        db.close();
    }
}
