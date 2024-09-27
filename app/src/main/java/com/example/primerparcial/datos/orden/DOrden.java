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

    // Método para actualizar el total de la orden
    public void actualizarTotalOrden(int idOrden, double totalDetalle, boolean isAdding) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Obtener el total actual de la orden
        String query = "SELECT total FROM ordenes WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idOrden)});
        double totalActual = 0.0;
        if (cursor.moveToFirst()) {
            totalActual = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();

        // Calcular el nuevo total
        double nuevoTotal;
        if (isAdding) {
            nuevoTotal = totalActual + totalDetalle;
        } else {
            nuevoTotal = totalActual - totalDetalle;
        }

        // Actualizar el total de la orden
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

    // Método para actualizar el estado de una orden y asignar un repartidor
    public void actualizarEstadoYRepartidor(int idOrden, String nuevoEstado, int idRepartidor) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("estado", nuevoEstado);       // Actualiza el estado de la orden
        valores.put("idRepartidor", idRepartidor); // Asigna el repartidor

        db.update("ordenes", valores, "id=?", new String[]{String.valueOf(idOrden)});
        db.close();
    }

    // Funcion para obtener los datos del cliente a partir del idCliente de la orden
    public Map<String, String> obtenerDatosCliente(int idOrden) {
        Map<String, String> datosCliente = new HashMap<>();

        // Realizamos la consulta SQL
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT c.nombre, c.nroTelefono, c.imagenPath " +
                "FROM ordenes o " +
                "JOIN clientes c ON o.idCliente = c.id " +
                "WHERE o.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idOrden)});

        // Verificamos si tenemos datos y los extraemos
        if (cursor.moveToFirst()) {
            datosCliente.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            datosCliente.put("nroTelefono", cursor.getString(cursor.getColumnIndexOrThrow("nroTelefono")));
            datosCliente.put("imagenPath", cursor.getString(cursor.getColumnIndexOrThrow("imagenPath")));  // si tienes una imagen de cliente
        }

        // Cerramos el cursor y la base de datos
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
