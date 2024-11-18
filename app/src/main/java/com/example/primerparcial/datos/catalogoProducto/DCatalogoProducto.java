package com.example.primerparcial.datos.catalogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;

public class DCatalogoProducto {

    private DBHelper dbHelper;
    private String nota;
    private int idCatalogo;
    private int idProducto;

    public DCatalogoProducto() {
    }

    public DCatalogoProducto(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DCatalogoProducto(String nota, int idCatalogo, int idProducto) {
        this.nota = nota;
        this.idCatalogo = idCatalogo;
        this.idProducto = idProducto;
    }

    // Getters y Setters
    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public int getIdCatalogo() {
        return idCatalogo;
    }

    public void setIdCatalogo(int idCatalogo) {
        this.idCatalogo = idCatalogo;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    // Método para cargar los datos desde un cursor
    public void cargarDesdeCursor(Cursor cursor) {
        this.nota = cursor.getString(cursor.getColumnIndexOrThrow("nota"));
        this.idCatalogo = cursor.getInt(cursor.getColumnIndexOrThrow("idCatalogo"));
        this.idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("idProducto"));
    }

    // Método para insertar una relación entre un catálogo y un producto
    public void insertarCatalogoProducto(int idCatalogo, int idProducto, String nota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idCatalogo", idCatalogo);
        values.put("idProducto", idProducto);
        values.put("nota", nota);
        db.insert("catalogoProducto", null, values);
        db.close();
    }

    // Método para obtener todos los productos de un catálogo
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

    // Método para eliminar un producto del catálogo
    public void eliminarProductoCatalogo(int idCatalogo, int idProducto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("catalogoProducto", "idCatalogo=? AND idProducto=?", new String[]{String.valueOf(idCatalogo), String.valueOf(idProducto)});
        db.close();
    }

    // Método para verificar si un producto ya existe en el catálogo
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

    // Método en la capa de datos para actualizar la nota del producto en un catálogo específico
    public void actualizarNotaProducto(int idCatalogo, int idProducto, String nuevaNota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nota", nuevaNota);

        // Actualizar la nota usando las claves compuestas
        db.update("catalogoProducto", values, "idCatalogo = ? AND idProducto = ?", new String[]{String.valueOf(idCatalogo), String.valueOf(idProducto)});
        db.close();
    }
}
