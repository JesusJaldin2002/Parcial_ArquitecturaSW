package com.example.primerparcial.negocio.categoria;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.categoria.DCategoria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NCategoria {

    private DCategoria dCategoria;

    // Constructor
    public NCategoria(Context context) {
        dCategoria = new DCategoria(context);
    }

    // Método para registrar una nueva categoría
    public void registrarCategoria(String nombre) {
        dCategoria.insertarCategoria(nombre);
    }

    // Método para obtener todas las categorías
    public List<Map<String, String>> obtenerCategorias() {
        List<Map<String, String>> categorias = new ArrayList<>();
        Cursor cursor = dCategoria.obtenerCategorias();
        if (cursor.moveToFirst()) {
            do {
                DCategoria categoria = new DCategoria();
                categoria.cargarDesdeCursor(cursor);

                Map<String, String> categoriaMap = new HashMap<>();
                categoriaMap.put("id", String.valueOf(categoria.getId()));
                categoriaMap.put("nombre", categoria.getNombre());

                categorias.add(categoriaMap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categorias;
    }

    // Método para actualizar una categoría
    public void actualizarCategoria(String id, String nombre) {
        dCategoria.actualizarCategoria(id, nombre);
    }

    // Método para eliminar una categoría
    public void eliminarCategoria(String id) {
        dCategoria.eliminarCategoria(id);
    }
}
