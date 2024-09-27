package com.example.primerparcial.negocio.catalogo;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.catalogo.DCatalogo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NCatalogo {

    private DCatalogo dCatalogo;

    public NCatalogo(Context context) {
        dCatalogo = new DCatalogo(context);
    }

    // Método para insertar un nuevo catálogo
    public void insertarCatalogo(String nombre, String fecha, String descripcion) {
        dCatalogo.insertarCatalogo(nombre, fecha, descripcion);
    }

    // Método para actualizar un catálogo existente
    public void actualizarCatalogo(String id, String nombre, String fecha, String descripcion) {
        dCatalogo.actualizarCatalogo(id, nombre, fecha, descripcion);
    }

    // Método para eliminar un catálogo por su ID
    public void eliminarCatalogo(String id) {
        dCatalogo.eliminarCatalogo(id);
    }

    // Método para obtener todos los catálogos en una lista
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

    // Método para obtener un catálogo por su ID
    public Map<String, String> obtenerCatalogoPorId(String id) {
        Cursor cursor = dCatalogo.obtenerCatalogos();
        Map<String, String> catalogo = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndexOrThrow("id")).equals(id)) {
                    catalogo = new HashMap<>();
                    catalogo.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")));
                    catalogo.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                    catalogo.put("fecha", cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                    catalogo.put("descripcion", cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                    break;
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        return catalogo;
    }

    public Map<String, String> obtenerDatosCatalogo(int idCatalogo) {
        return dCatalogo.obtenerDatosCatalogo(idCatalogo);
    }
}
