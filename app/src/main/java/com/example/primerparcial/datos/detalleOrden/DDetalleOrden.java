package com.example.primerparcial.datos.detalleOrden;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

public class DDetalleOrden {

    private DBHelper dbHelper;
    private int cantidad;
    private double precio;
    private int idOrden;
    private int idProducto;

    public DDetalleOrden() {
    }

    public DDetalleOrden(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DDetalleOrden(int cantidad, double precio, int idOrden, int idProducto) {
        this.cantidad = cantidad;
        this.precio = precio;
        this.idOrden = idOrden;
        this.idProducto = idProducto;
    }

    // Getters and Setters
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    // Método para insertar un detalle de orden
    public void insertarDetalleOrden(int cantidad, double precio, int idOrden, int idProducto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cantidad", cantidad);
        values.put("precio", precio);
        values.put("idOrden", idOrden);
        values.put("idProducto", idProducto);
        db.insertWithOnConflict("detalleOrden", null, values, SQLiteDatabase.CONFLICT_REPLACE); // Reemplaza si ya existe la combinación
        db.close();
    }

    // Método para eliminar un detalle de orden
    public void eliminarDetalleOrden(int idOrden, int idProducto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("detalleOrden", "idOrden = ? AND idProducto = ?", new String[]{String.valueOf(idOrden), String.valueOf(idProducto)});
        db.close();
    }

    // Método para verificar si un producto ya existe en el detalle de la orden
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

    // Método para obtener los detalles de una orden específica
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
}
