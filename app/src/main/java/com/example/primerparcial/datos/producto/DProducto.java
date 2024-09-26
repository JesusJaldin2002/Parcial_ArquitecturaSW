package com.example.primerparcial.datos.producto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

public class DProducto {

    private DBHelper dbHelper;
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String imagenPath;
    private int stock;
    private int idCategoria;

    public DProducto() {
    }

    public DProducto(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DProducto(int id, String nombre, String descripcion, double precio, String imagenPath, int stock, int idCategoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenPath = imagenPath;
        this.stock = stock;
        this.idCategoria = idCategoria;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagenPath() {
        return imagenPath;
    }

    public void setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    // Método para cargar los datos de un producto a partir de un Cursor
    public void cargarDesdeCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        this.descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
        this.precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
        this.imagenPath = cursor.getString(cursor.getColumnIndexOrThrow("imagenPath"));
        this.stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"));
        this.idCategoria = cursor.getInt(cursor.getColumnIndexOrThrow("idCategoria"));
    }

    // Método para insertar un producto
    public void insertarProducto(String nombre, String descripcion, double precio, String imagenPath, int stock, int idCategoria) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("descripcion", descripcion);
        values.put("precio", precio);
        values.put("imagenPath", imagenPath);
        values.put("stock", stock);
        values.put("idCategoria", idCategoria);  // Relación con la tabla categorias
        db.insert("productos", null, values);
        db.close();
    }

    // Método para obtener todos los productos
    public Cursor obtenerProductos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT id, nombre, descripcion, precio, imagenPath, stock, idCategoria FROM productos", null);
    }

    // Método para actualizar un producto
    public void actualizarProducto(int id, String nombre, String descripcion, double precio, String imagenPath, int stock, int idCategoria) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("descripcion", descripcion);
        values.put("precio", precio);
        values.put("imagenPath", imagenPath);
        values.put("stock", stock);
        values.put("idCategoria", idCategoria);  // Relación con la tabla categorias

        // Actualizamos el producto donde el ID coincida
        String[] args = {String.valueOf(id)};
        db.update("productos", values, "id=?", args);
        db.close();
    }

    // Método para eliminar un producto
    public void eliminarProducto(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("productos", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Método para obtener productos junto con sus categorías
    public Cursor obtenerProductosConCategorias() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT p.id, p.nombre, p.descripcion, p.precio, p.imagenPath, p.stock, c.nombre as categoria " +
                "FROM productos p " +
                "JOIN categorias c ON p.idCategoria = c.id";
        return db.rawQuery(query, null);
    }

    public void actualizarStock(int idProducto, int nuevoStock) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stock", nuevoStock);

        // Actualizar el stock donde el id del producto coincida
        db.update("productos", values, "id = ?", new String[]{String.valueOf(idProducto)});
        db.close();
    }

    public int obtenerStockProducto(int idProducto) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT stock FROM productos WHERE id = ?", new String[]{String.valueOf(idProducto)});

        if (cursor.moveToFirst()) {
            int stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"));
            cursor.close();
            return stock;
        }
        cursor.close();
        return 0; // Retornar 0 si no se encuentra el producto o no tiene stock
    }
}
