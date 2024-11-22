package com.example.primerparcial.datos.catalogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

import java.util.HashMap;
import java.util.Map;

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

    // Métodos originales de DCatalogo
    public void cargarDesdeCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        this.fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
        this.descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
    }

    public void insertarCatalogo(String nombre, String fecha, String descripcion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("fecha", fecha);
        values.put("descripcion", descripcion);
        db.insert("catalogos", null, values);
        db.close();
    }

    public Cursor obtenerCatalogos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT id, nombre, fecha, descripcion FROM catalogos", null);
    }

    public void actualizarCatalogo(String id, String nombre, String fecha, String descripcion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("fecha", fecha);
        values.put("descripcion", descripcion);
        db.update("catalogos", values, "id=?", new String[]{id});
        db.close();
    }

    public void eliminarCatalogo(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("catalogoProducto", "idCatalogo=?", new String[]{id}); // Eliminar productos asociados
        db.delete("catalogos", "id=?", new String[]{id}); // Eliminar catálogo
        db.close();
    }

    public Map<String, String> obtenerDatosCatalogo(int idCatalogo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT nombre, fecha, descripcion FROM catalogos WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idCatalogo)});

        Map<String, String> catalogoData = new HashMap<>();
        if (cursor.moveToFirst()) {
            catalogoData.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            catalogoData.put("fecha", cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
            catalogoData.put("descripcion", cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
        }

        cursor.close();
        db.close();
        return catalogoData;
    }

    // Métodos añadidos de DCatalogoProducto
    public void insertarCatalogoProducto(int idCatalogo, int idProducto, String nota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idCatalogo", idCatalogo);
        values.put("idProducto", idProducto);
        values.put("nota", nota);
        db.insert("catalogoProducto", null, values);
        db.close();
    }

    public Cursor obtenerProductosPorCatalogo(int idCatalogo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM catalogoProducto WHERE idCatalogo = ?";
        return db.rawQuery(query, new String[]{String.valueOf(idCatalogo)});
    }

    public Cursor obtenerProductosPorCatalogoConCategorias(int idCatalogo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT cp.idCatalogo, cp.idProducto, p.id, p.nombre, p.descripcion, p.precio, p.imagenPath, p.stock, c.nombre AS categoria, cp.nota " +
                "FROM productos p " +
                "JOIN categorias c ON p.idCategoria = c.id " +
                "JOIN catalogoProducto cp ON p.id = cp.idProducto " +
                "WHERE cp.idCatalogo = ?";
        return db.rawQuery(query, new String[]{String.valueOf(idCatalogo)});
    }

    public void eliminarProductoCatalogo(int idCatalogo, int idProducto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("catalogoProducto", "idCatalogo=? AND idProducto=?", new String[]{String.valueOf(idCatalogo), String.valueOf(idProducto)});
        db.close();
    }

    public boolean productoYaEnCatalogo(int idCatalogo, int idProducto) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM catalogoProducto WHERE idCatalogo = ? AND idProducto = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idCatalogo), String.valueOf(idProducto)});

        boolean existe = false;
        if (cursor.moveToFirst()) {
            existe = cursor.getInt(0) > 0;
        }
        cursor.close();
        db.close();

        return existe;
    }

    public void actualizarNotaProducto(int idCatalogo, int idProducto, String nuevaNota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nota", nuevaNota);
        db.update("catalogoProducto", values, "idCatalogo = ? AND idProducto = ?", new String[]{String.valueOf(idCatalogo), String.valueOf(idProducto)});
        db.close();
    }
}
