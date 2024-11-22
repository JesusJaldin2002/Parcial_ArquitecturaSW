package com.example.primerparcial.negocio.catalogo;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.catalogo.DCatalogo;
import com.example.primerparcial.datos.producto.DProducto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NCatalogo {

    private DCatalogo dCatalogo;
    private DProducto dProducto;

    public NCatalogo(Context context) {
        dCatalogo = new DCatalogo(context);
        dProducto = new DProducto(context);
    }

    // Métodos originales de NCatalogo
    public void insertarCatalogo(String nombre, String fecha, String descripcion) {
        dCatalogo.insertarCatalogo(nombre, fecha, descripcion);
    }

    public void actualizarCatalogo(String id, String nombre, String fecha, String descripcion) {
        dCatalogo.actualizarCatalogo(id, nombre, fecha, descripcion);
    }

    public void eliminarCatalogo(String id) {
        dCatalogo.eliminarCatalogo(id);
    }

    public List<Map<String, String>> obtenerCatalogos() {
        List<Map<String, String>> listaCatalogos = new ArrayList<>();
        Cursor cursor = dCatalogo.obtenerCatalogos();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, String> catalogo = new HashMap<>();
                catalogo.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")));
                catalogo.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                catalogo.put("fecha", cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                catalogo.put("descripcion", cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                listaCatalogos.add(catalogo);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return listaCatalogos;
    }

    public Map<String, String> obtenerDatosCatalogo(int idCatalogo) {
        return dCatalogo.obtenerDatosCatalogo(idCatalogo);
    }

    // Métodos añadidos de NCatalogoProducto
    public void registrarProductoCatalogo(int idCatalogo, int idProducto, String nota) {
        dCatalogo.insertarCatalogoProducto(idCatalogo, idProducto, nota);
    }

    public List<Map<String, String>> obtenerProductosPorCatalogo(int idCatalogo) {
        Cursor cursor = dCatalogo.obtenerProductosPorCatalogo(idCatalogo);
        List<Map<String, String>> productos = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> productoMap = new HashMap<>();
                productoMap.put("idProducto", cursor.getString(cursor.getColumnIndexOrThrow("idProducto")));
                productoMap.put("nota", cursor.getString(cursor.getColumnIndexOrThrow("nota")));
                productoMap.put("idCatalogo", cursor.getString(cursor.getColumnIndexOrThrow("idCatalogo")));

                // Obtener información del producto
                int idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("idProducto"));
                Cursor productoCursor = dProducto.obtenerProductoPorId(idProducto);

                if (productoCursor.moveToFirst()) {
                    productoMap.put("nombre", productoCursor.getString(productoCursor.getColumnIndexOrThrow("nombre")));
                    productoMap.put("descripcion", productoCursor.getString(productoCursor.getColumnIndexOrThrow("descripcion")));
                    productoMap.put("stock", productoCursor.getString(productoCursor.getColumnIndexOrThrow("stock")));
                    productoMap.put("precio", productoCursor.getString(productoCursor.getColumnIndexOrThrow("precio")));
                    productoMap.put("imagenPath", productoCursor.getString(productoCursor.getColumnIndexOrThrow("imagenPath")));
                }
                productoCursor.close();

                productos.add(productoMap);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return productos;
    }

    public void eliminarProductoCatalogo(int idCatalogo, int idProducto) {
        dCatalogo.eliminarProductoCatalogo(idCatalogo, idProducto);
    }

    public Map<String, List<Map<String, String>>> obtenerProductosPorCategoriaCatalogo(int idCatalogo) {
        Map<String, List<Map<String, String>>> productosPorCategoria = new HashMap<>();

        Cursor cursor = dCatalogo.obtenerProductosPorCatalogoConCategorias(idCatalogo);

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> producto = new HashMap<>();
                producto.put("idProducto", cursor.getString(cursor.getColumnIndexOrThrow("id")));
                producto.put("idCatalogo", cursor.getString(cursor.getColumnIndexOrThrow("idCatalogo")));
                producto.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                producto.put("descripcion", cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                producto.put("precio", cursor.getString(cursor.getColumnIndexOrThrow("precio")));
                producto.put("imagenPath", cursor.getString(cursor.getColumnIndexOrThrow("imagenPath")));
                producto.put("stock", cursor.getString(cursor.getColumnIndexOrThrow("stock")));
                producto.put("nota", cursor.getString(cursor.getColumnIndexOrThrow("nota")));

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

    public boolean productoYaEnCatalogo(int idCatalogo, int idProducto) {
        return dCatalogo.productoYaEnCatalogo(idCatalogo, idProducto);
    }

    public void actualizarNotaProducto(int idCatalogo, int idProducto, String nuevaNota) {
        dCatalogo.actualizarNotaProducto(idCatalogo, idProducto, nuevaNota);
    }
}
