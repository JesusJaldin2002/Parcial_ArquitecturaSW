package com.example.primerparcial.negocio.catalogoProducto;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.catalogo.DCatalogoProducto;
import com.example.primerparcial.datos.producto.DProducto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NCatalogoProducto {

    private DCatalogoProducto dCatalogoProducto;
    private DProducto dProducto;

    // Constructor
    public NCatalogoProducto(Context context) {
        dCatalogoProducto = new DCatalogoProducto(context);
        dProducto = new DProducto(context);
    }

    // Método para registrar un producto en un catálogo
    public void registrarProductoCatalogo(int idCatalogo, int idProducto, String nota) {
        dCatalogoProducto.insertarCatalogoProducto(idCatalogo, idProducto, nota);
    }

    // Método para obtener productos asociados a un catálogo
    public List<Map<String, String>> obtenerProductosPorCatalogo(int idCatalogo) {
        Cursor cursor = dCatalogoProducto.obtenerProductosPorCatalogo(idCatalogo);
        List<Map<String, String>> productos = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> productoMap = new HashMap<>();
                productoMap.put("id", cursor.getString(cursor.getColumnIndexOrThrow("idProducto")));
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

    // Método para eliminar un producto de un catálogo
    public void eliminarProductoCatalogo(int idCatalogo, int idProducto) {
        dCatalogoProducto.eliminarProductoCatalogo(idCatalogo, idProducto);
    }

    public Map<String, List<Map<String, String>>> obtenerProductosPorCategoriaCatalogo(int idCatalogo) {
        Map<String, List<Map<String, String>>> productosPorCategoria = new HashMap<>();

        // Obtener los productos de un catálogo específico desde la capa de datos
        Cursor cursor = dCatalogoProducto.obtenerProductosPorCatalogoConCategorias(idCatalogo);

        if (cursor.moveToFirst()) {
            do {
                // Crear un Map para almacenar los datos del producto
                Map<String, String> producto = new HashMap<>();
                producto.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")));  // ID del producto
                producto.put("idCatalogoProducto", cursor.getString(cursor.getColumnIndexOrThrow("idCatalogoProducto")));  // ID de la tabla catalogoProducto
                producto.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                producto.put("descripcion", cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                producto.put("precio", cursor.getString(cursor.getColumnIndexOrThrow("precio")));
                producto.put("imagenPath", cursor.getString(cursor.getColumnIndexOrThrow("imagenPath")));
                producto.put("stock", cursor.getString(cursor.getColumnIndexOrThrow("stock")));
                producto.put("nota", cursor.getString(cursor.getColumnIndexOrThrow("nota")));
                producto.put("idCatalogo", cursor.getString(cursor.getColumnIndexOrThrow("idCatalogo")));

                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));

                // Agrupar los productos bajo su respectiva categoría
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
        return dCatalogoProducto.productoYaEnCatalogo(idCatalogo, idProducto);
    }

    // Método para actualizar la nota del producto en la capa de negocio (NCatalogoProducto)
    public void actualizarNotaProducto(String idCatalogoProducto, String nuevaNota) {
        dCatalogoProducto.actualizarNotaProducto(idCatalogoProducto, nuevaNota);  // Asegurarse de que el método reciba el idCatalogoProducto
    }

    // Metodo para obtener los datos de un catalogo

}
