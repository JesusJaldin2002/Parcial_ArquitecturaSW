package com.example.primerparcial.datos.categoria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

public class DCategoria {

    private DBHelper dbHelper;
    private int id;
    private String nombre;

    public DCategoria() {
    }

    public DCategoria(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DCategoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    // Método para cargar los datos de una categoría a partir de un Cursor
    public void cargarDesdeCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
    }

    // Método para insertar una categoría
    public void insertarCategoria(String nombre) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        db.insert("categorias", null, values);
        db.close();
    }

    // Método para obtener todas las categorías
    public Cursor obtenerCategorias() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT id, nombre FROM categorias", null);
    }

    // Método para actualizar una categoría
    public void actualizarCategoria(String id, String nombre) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);

        // Actualizamos la categoría donde el ID coincida
        String[] args = { id };
        db.update("categorias", values, "id=?", args);
        db.close();
    }

    // Método para eliminar una categoría
    public void eliminarCategoria(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("categorias", "id=?", new String[]{id});
        db.close();
    }
}
