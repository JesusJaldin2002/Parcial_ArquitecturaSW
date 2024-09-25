package com.example.primerparcial.datos.orden;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

public class DOrden {

    private DBHelper dbHelper;
    private int id;
    private String fecha;
    private String estado;
    private double total;
    private int idCliente;
    private int idRepartidor;

    public DOrden() {
    }

    public DOrden(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DOrden(int id, String fecha, String estado, double total, int idCliente, int idRepartidor) {
        this.id = id;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
        this.idCliente = idCliente;
        this.idRepartidor = idRepartidor;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(int idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    // Método para cargar los datos de una orden a partir de un Cursor
    public void cargarDesdeCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
        this.estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
        this.total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        this.idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("idCliente"));
        this.idRepartidor = cursor.getInt(cursor.getColumnIndexOrThrow("idRepartidor"));
    }

    // Método para insertar una orden
    public void insertarOrden(String fecha, String estado, double total, int idCliente, int idRepartidor) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fecha", fecha);
        values.put("estado", estado);
        values.put("total", total);
        values.put("idCliente", idCliente);
        values.put("idRepartidor", idRepartidor);
        db.insert("ordenes", null, values);
        db.close();
    }

    // Método para obtener todas las órdenes
    public Cursor obtenerOrdenes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM ordenes", null);
    }

    // Método para actualizar una orden
    public void actualizarOrden(int id, String fecha, String estado, double total, int idCliente, int idRepartidor) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fecha", fecha);
        values.put("estado", estado);
        values.put("total", total);
        values.put("idCliente", idCliente);
        values.put("idRepartidor", idRepartidor);

        String[] args = { String.valueOf(id) };
        db.update("ordenes", values, "id=?", args);
        db.close();
    }

    // Método para eliminar una orden
    public void eliminarOrden(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("ordenes", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
