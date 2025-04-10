package com.example.primerparcial.datos.cliente;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

import java.util.HashMap;
import java.util.Map;

public class DCliente {

    private DBHelper dbHelper;
    private int id;
    private String nombre;
    private String nroTelefono;
    private String imagenPath;

    public DCliente() {
    }

    public DCliente(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DCliente(int id, String nombre, String nroTelefono, String imagenPath) {
        this.id = id;
        this.nombre = nombre;
        this.nroTelefono = nroTelefono;
        this.imagenPath = imagenPath;
    }

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

    public String getImagenPath() {
        return imagenPath;
    }

    public void setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
    }

    public void cargarDesdeCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        this.nroTelefono = cursor.getString(cursor.getColumnIndexOrThrow("nroTelefono"));
        this.imagenPath = cursor.getString(cursor.getColumnIndexOrThrow("imagenPath"));
    }

    // Método para insertar un cliente
    public void insertarCliente(String nombre, String nroTelefono, String imagenPath) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("nroTelefono", nroTelefono);
        values.put("imagenPath", imagenPath);  // Guardamos solo la ruta de la imagen
        db.insert("clientes", null, values);
        db.close();
    }

    // Método para obtener todos los clientes de la base de datos
    public Cursor obtenerClientes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT id, nombre, nroTelefono, imagenPath FROM clientes", null);
    }

    // Método para actualizar un cliente
    public void actualizarCliente(String id, String nombre, String nroTelefono, String imagenPath) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("nroTelefono", nroTelefono);
        values.put("imagenPath", imagenPath);

        // Actualizamos el cliente donde el ID coincida
        String[] args = { id };
        db.update("clientes", values, "id=?", args);
        db.close();
    }

    // Método para eliminar un cliente
    public void eliminarCliente(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("clientes", "id=?", new String[]{id});
        db.close();
    }

    // Método para obtener el nombre del cliente por ID
    public String obtenerNombreClientePorId(int idCliente) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String nombreCliente = null;

        Cursor cursor = db.rawQuery("SELECT nombre FROM clientes WHERE id = ?", new String[]{String.valueOf(idCliente)});
        if (cursor.moveToFirst()) {
            nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        }
        cursor.close();
        db.close();

        return nombreCliente;
    }

    public Map<String, String> obtenerUbicacionCliente(int idCliente) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, String> ubicacion = null;

        String query = "SELECT nombre, urlMapa, referencia FROM posiciones WHERE idCliente = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idCliente)});

        if (cursor != null && cursor.moveToFirst()) {
            ubicacion = new HashMap<>();
            ubicacion.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            ubicacion.put("urlMapa", cursor.getString(cursor.getColumnIndexOrThrow("urlMapa")));
            ubicacion.put("referencia", cursor.getString(cursor.getColumnIndexOrThrow("referencia")));
            cursor.close();
        }
        db.close();
        return ubicacion;
    }


}
