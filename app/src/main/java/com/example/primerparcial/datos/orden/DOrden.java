package com.example.primerparcial.datos.orden;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

import java.util.HashMap;
import java.util.Map;

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

    // Métodos relacionados con detalleOrden

    public void insertarDetalleOrden(int cantidad, double precio, int idOrden, int idProducto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cantidad", cantidad);
        values.put("precio", precio);
        values.put("idOrden", idOrden);
        values.put("idProducto", idProducto);
        db.insertWithOnConflict("detalleOrden", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void eliminarDetalleOrden(int idOrden, int idProducto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("detalleOrden", "idOrden = ? AND idProducto = ?", new String[]{String.valueOf(idOrden), String.valueOf(idProducto)});
        db.close();
    }

    public boolean productoYaEnOrden(int idOrden, int idProducto) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM detalleOrden WHERE idOrden = ? AND idProducto = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idOrden), String.valueOf(idProducto)});

        boolean existe = false;
        if (cursor.moveToFirst()) {
            existe = cursor.getInt(0) > 0;
        }
        cursor.close();
        db.close();

        return existe;
    }

    public Cursor obtenerDetallesPorOrden(int idOrden) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM detalleOrden WHERE idOrden = ?";
        return db.rawQuery(query, new String[]{String.valueOf(idOrden)});
    }

    public void actualizarCantidad(int idOrden, int idProducto, int nuevaCantidad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cantidad", nuevaCantidad);
        db.update("detalleOrden", values, "idOrden = ? AND idProducto = ?", new String[]{String.valueOf(idOrden), String.valueOf(idProducto)});
        db.close();
    }

    public void eliminarDetallesPorOrden(int idOrden) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("detalleOrden", "idOrden=?", new String[]{String.valueOf(idOrden)});
        db.close();
    }

    // Métodos originales de DOrden
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

    public void cargarDesdeCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
        this.estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
        this.total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        this.idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("idCliente"));
        this.idRepartidor = cursor.getInt(cursor.getColumnIndexOrThrow("idRepartidor"));
    }

    public Cursor obtenerOrdenes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM ordenes", null);
    }

    public void actualizarOrden(int id, String fecha, String estado, double total, int idCliente, int idRepartidor) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fecha", fecha);
        values.put("estado", estado);
        values.put("total", total);
        values.put("idCliente", idCliente);
        values.put("idRepartidor", idRepartidor);

        String[] args = {String.valueOf(id)};
        db.update("ordenes", values, "id=?", args);
        db.close();
    }

    public void eliminarOrden(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("ordenes", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void actualizarTotalOrden(int idOrden, double totalDetalle, boolean isAdding) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT total FROM ordenes WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idOrden)});
        double totalActual = 0.0;
        if (cursor.moveToFirst()) {
            totalActual = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();

        double nuevoTotal = isAdding ? totalActual + totalDetalle : totalActual - totalDetalle;
        ContentValues values = new ContentValues();
        values.put("total", nuevoTotal);
        db.update("ordenes", values, "id = ?", new String[]{String.valueOf(idOrden)});
        db.close();
    }

    public double obtenerTotalOrden(int idOrden) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT total FROM ordenes WHERE id = ?", new String[]{String.valueOf(idOrden)});

        double total = 0.0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        db.close();

        return total;
    }

    public void actualizarEstadoYRepartidor(int idOrden, String nuevoEstado, int idRepartidor) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("estado", nuevoEstado);
        valores.put("idRepartidor", idRepartidor);

        db.update("ordenes", valores, "id=?", new String[]{String.valueOf(idOrden)});
        db.close();
    }

    public Map<String, String> obtenerDatosCliente(int idOrden) {
        Map<String, String> datosCliente = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT c.nombre, c.nroTelefono, c.imagenPath FROM ordenes o JOIN clientes c ON o.idCliente = c.id WHERE o.id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idOrden)});
        if (cursor.moveToFirst()) {
            datosCliente.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            datosCliente.put("nroTelefono", cursor.getString(cursor.getColumnIndexOrThrow("nroTelefono")));
            datosCliente.put("imagenPath", cursor.getString(cursor.getColumnIndexOrThrow("imagenPath")));
        }
        cursor.close();
        db.close();
        return datosCliente;
    }

    public Map<String, String> obtenerDatosOrden(int idOrden) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT idCliente, fecha, total FROM ordenes WHERE id = ?", new String[]{String.valueOf(idOrden)});
        Map<String, String> datosOrden = new HashMap<>();
        if (cursor.moveToFirst()) {
            datosOrden.put("idCliente", cursor.getString(cursor.getColumnIndexOrThrow("idCliente")));
            datosOrden.put("fecha", cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
            datosOrden.put("total", cursor.getString(cursor.getColumnIndexOrThrow("total")));
        }
        cursor.close();
        return datosOrden;
    }
}
