package com.example.primerparcial.negocio.producto;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.producto.DProducto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NProducto {

    private DProducto dProducto;

    // Constructor
    public NProducto(Context context) {
        dProducto = new DProducto(context);
    }

    // Método para registrar un nuevo producto
    public void registrarProducto(String nombre, String descripcion, double precio, String imagenPath, int stock, int idCategoria) {
        dProducto.insertarProducto(nombre, descripcion, precio, imagenPath, stock, idCategoria);
    }

    // Método para obtener todos los productos desde la capa de negocio
    public List<Map<String, String>> obtenerProductos() {
        List<Map<String, String>> productos = new ArrayList<>();
        Cursor cursor = dProducto.obtenerProductos();
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> productoMap = new HashMap<>();
                productoMap.put("id", String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id"))));
                productoMap.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                productoMap.put("descripcion", cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                productoMap.put("precio", String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))));
                productoMap.put("imagenPath", cursor.getString(cursor.getColumnIndexOrThrow("imagenPath")));
                productoMap.put("stock", String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("stock"))));
                productoMap.put("idCategoria", String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("idCategoria"))));

                productos.add(productoMap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    // Método para actualizar un producto
    public void actualizarProducto(int id, String nombre, String descripcion, double precio, String imagenPath, int stock, int idCategoria) {
        dProducto.actualizarProducto(id, nombre, descripcion, precio, imagenPath, stock, idCategoria);
    }

    // Método para eliminar un producto
    public void eliminarProducto(int id) {
        dProducto.eliminarProducto(id);
    }

    // Método para obtener productos agrupados por categoría
    public Map<String, List<Map<String, String>>> obtenerProductosPorCategoria() {
        Map<String, List<Map<String, String>>> productosPorCategoria = new HashMap<>();
        Cursor cursor = dProducto.obtenerProductosConCategorias();

        if (cursor.moveToFirst()) {
            do {
                // Crear un Map para almacenar los datos del producto
                Map<String, String> producto = new HashMap<>();
                producto.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")));
                producto.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                producto.put("descripcion", cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                producto.put("precio", cursor.getString(cursor.getColumnIndexOrThrow("precio")));
                producto.put("imagenPath", cursor.getString(cursor.getColumnIndexOrThrow("imagenPath")));
                producto.put("stock", cursor.getString(cursor.getColumnIndexOrThrow("stock")));

                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));

                if (!productosPorCategoria.containsKey(categoria)) {
                    productosPorCategoria.put(categoria, new ArrayList<>());
                }
                productosPorCategoria.get(categoria).add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productosPorCategoria;
    }

    public void actualizarStock(int idProducto, int nuevoStock) {
        dProducto.actualizarStock(idProducto, nuevoStock);
    }

    public int obtenerStockProducto(int idProducto) {
        return dProducto.obtenerStockProducto(idProducto);
    }
}
