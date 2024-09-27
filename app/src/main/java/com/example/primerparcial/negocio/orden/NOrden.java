package com.example.primerparcial.negocio.orden;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.orden.DOrden;
import com.example.primerparcial.negocio.detalleOrden.NDetalleOrden;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NOrden {

    private DOrden dOrden;
    private NDetalleOrden nDetalleOrden;

    // Constructor
    public NOrden(Context context) {
        dOrden = new DOrden(context);
        nDetalleOrden = new NDetalleOrden(context);
    }

    // Método para registrar una nueva orden
    public void registrarOrden(String fecha, String estado, double total, int idCliente, int idRepartidor) {
        dOrden.insertarOrden(fecha, estado, total, idCliente, idRepartidor);
    }

    // Método para obtener todas las órdenes
    public List<Map<String, String>> obtenerOrdenes() {
        List<Map<String, String>> ordenes = new ArrayList<>();
        Cursor cursor = dOrden.obtenerOrdenes();
        if (cursor.moveToFirst()) {
            do {
                DOrden orden = new DOrden();
                orden.cargarDesdeCursor(cursor);

                Map<String, String> ordenMap = new HashMap<>();
                ordenMap.put("id", String.valueOf(orden.getId()));
                ordenMap.put("fecha", orden.getFecha());
                ordenMap.put("estado", orden.getEstado());
                ordenMap.put("total", String.valueOf(orden.getTotal()));
                ordenMap.put("idCliente", String.valueOf(orden.getIdCliente()));
                ordenMap.put("idRepartidor", String.valueOf(orden.getIdRepartidor()));

                ordenes.add(ordenMap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ordenes;
    }

    // Método para actualizar una orden
    public void actualizarOrden(int id, String fecha, String estado, double total, int idCliente, int idRepartidor) {
        dOrden.actualizarOrden(id, fecha, estado, total, idCliente, idRepartidor);
    }

    // Método para eliminar una orden
    public void eliminarOrden(int id) {
        // Primero eliminar los detalles de la orden
        nDetalleOrden.eliminarDetallesPorOrden(id);

        // Luego eliminar la orden
        dOrden.eliminarOrden(id);
    }

    public double obtenerTotalOrden(int idOrden) {
        return dOrden.obtenerTotalOrden(idOrden);
    }

    // Método para actualizar el estado y asignar un repartidor a la orden
    public void actualizarEstadoYRepartidor(int idOrden, String nuevoEstado, int idRepartidor) {
        if (!nuevoEstado.equals("Enviado") && !nuevoEstado.equals("Completado")) {
            throw new IllegalArgumentException("Estado no válido. Debe ser 'Enviado' o 'Completado'.");
        }

        dOrden.actualizarEstadoYRepartidor(idOrden, nuevoEstado, idRepartidor);
    }

    // Método para obtener los datos del cliente a partir de una orden
    public Map<String, String> obtenerDatosCliente(int idOrden) {
        // Llamamos al método en DOrden para obtener los datos del cliente
        return dOrden.obtenerDatosCliente(idOrden);
    }

    public Map<String, String> obtenerDatosOrden(int idOrden) {
        return dOrden.obtenerDatosOrden(idOrden);
    }

}
