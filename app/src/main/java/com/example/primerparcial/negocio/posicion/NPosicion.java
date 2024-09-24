package com.example.primerparcial.negocio.posicion;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.posicion.DPosicion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPosicion {

    private DPosicion dPosicion;

    // Constructor
    public NPosicion(Context context) {
        dPosicion = new DPosicion(context);
    }

    // Método para registrar una nueva posición
    public void registrarPosicion(String nombre, String urlMapa, String referencia, int idCliente) {
        dPosicion.insertarPosicion(nombre, urlMapa, referencia, idCliente);
    }

    // Método para obtener todas las posiciones desde la capa de negocio
    // Método para obtener todas las posiciones de un cliente
    public List<Map<String, String>> obtenerUbicacionesPorCliente(int idCliente) {
        List<Map<String, String>> ubicaciones = new ArrayList<>();
        Cursor cursor = dPosicion.obtenerPosicionesPorCliente(idCliente);  // Obtener posiciones por cliente específico
        if (cursor.moveToFirst()) {
            do {
                DPosicion posicion = new DPosicion();
                posicion.cargarDesdeCursor(cursor);

                Map<String, String> posicionMap = new HashMap<>();
                posicionMap.put("id", String.valueOf(posicion.getId()));
                posicionMap.put("nombre", posicion.getNombre());
                posicionMap.put("urlMapa", posicion.getUrlMapa());
                posicionMap.put("referencia", posicion.getReferencia());

                ubicaciones.add(posicionMap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ubicaciones;
    }

    // Método para actualizar una posición
    public void actualizarPosicion(String id, String nombre, String urlMapa, String referencia, int idCliente) {
        dPosicion.actualizarPosicion(id, nombre, urlMapa, referencia, idCliente);  // Método actualizarPosicion debe implementarse en DPosicion
    }

    // Método para eliminar una posición
    public void eliminarPosicion(String id) {
        dPosicion.eliminarPosicion(id);
    }
}
