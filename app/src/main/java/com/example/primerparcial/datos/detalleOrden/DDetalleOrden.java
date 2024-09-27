package com.example.primerparcial.datos.detalleOrden;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

public class DDetalleOrden {

    private DBHelper dbHelper;
    private int id;
    private int cantidad;
    private double precio;
    private int idOrden;
    private int idProducto;

    public DDetalleOrden() {
    }

    public DDetalleOrden(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DDetalleOrden(int id, int cantidad, double precio, int idOrden, int idProducto) {
        this.id = id;
        this.cantidad = cantidad;
        this.precio = precio;
        this.idOrden = idOrden;
        this.idProducto = idProducto;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
        db.insert("detalleOrden", null, values);
        db.close();
    }

    // Método para eliminar un detalle de orden
    public void eliminarDetalleOrden(int idDetalle) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Eliminar el detalle de orden basado en el ID
        db.delete("detalleOrden", "id = ?", new String[]{String.valueOf(idDetalle)});

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

    public void actualizarCantidad(int idDetalle, int nuevaCantidad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cantidad", nuevaCantidad);

        db.update("detalleOrden", values, "id = ?", new String[]{String.valueOf(idDetalle)});
        db.close();
    }

    public int obtenerIdProductoPorDetalle(int idDetalle) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int idProducto = -1;

        // Consulta para obtener el idProducto según el id del detalle
        Cursor cursor = db.rawQuery("SELECT idProducto FROM detalleOrden WHERE id = ?", new String[]{String.valueOf(idDetalle)});

        if (cursor.moveToFirst()) {
            idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("idProducto"));
        }

        cursor.close();
        db.close();

        return idProducto;
    }

    public void eliminarDetallesPorOrden(int idOrden) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("detalleOrden", "idOrden=?", new String[]{String.valueOf(idOrden)});
        db.close();
    }


}
