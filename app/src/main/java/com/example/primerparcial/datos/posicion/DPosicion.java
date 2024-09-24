package com.example.primerparcial.datos.posicion;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import com.example.primerparcial.datos.DBHelper;

public class DPosicion {

    private DBHelper dbHelper;
    private int id;
    private String nombre;
    private String urlMapa;
    private String referencia;
    private int idCliente;

    public DPosicion() {
    }

    public DPosicion(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DPosicion(int id, String nombre, String urlMapa, String referencia, int idCliente) {
        this.id = id;
        this.nombre = nombre;
        this.urlMapa = urlMapa;
        this.referencia = referencia;
        this.idCliente = idCliente;
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

    public String getUrlMapa() {
        return urlMapa;
    }

    public void setUrlMapa(String urlMapa) {
        this.urlMapa = urlMapa;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    // Método para cargar los datos de una posición a partir de un Cursor
    public void cargarDesdeCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        this.urlMapa = cursor.getString(cursor.getColumnIndexOrThrow("urlMapa"));
        this.referencia = cursor.getString(cursor.getColumnIndexOrThrow("referencia"));
        this.idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("idCliente"));
    }

    // Método para insertar una posición
    public void insertarPosicion(String nombre, String urlMapa, String referencia, int idCliente) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("urlMapa", urlMapa);
        values.put("referencia", referencia);
        values.put("idCliente", idCliente);  // Relación con la tabla clientes
        db.insert("posiciones", null, values);
        db.close();
    }

    // Método para obtener las posiciones filtradas por cliente
    public Cursor obtenerPosicionesPorCliente(int idCliente) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] args = {String.valueOf(idCliente)};
        return db.rawQuery("SELECT id, nombre, urlMapa, referencia, idCliente FROM posiciones WHERE idCliente=?", args);
    }

    // Método para actualizar una posición
    public void actualizarPosicion(String id, String nombre, String urlMapa, String referencia, int idCliente) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("urlMapa", urlMapa);
        values.put("referencia", referencia);
        values.put("idCliente", idCliente);  // Relación con la tabla clientes

        // Actualizamos la posición donde el ID coincida
        String[] args = { id };
        db.update("posiciones", values, "id=?", args);
        db.close();
    }

    // Método para eliminar una posición
    public void eliminarPosicion(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("posiciones", "id=?", new String[]{id});
        db.close();
    }
}
