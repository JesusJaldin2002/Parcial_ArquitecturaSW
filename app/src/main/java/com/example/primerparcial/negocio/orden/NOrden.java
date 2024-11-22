package com.example.primerparcial.negocio.orden;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.orden.DOrden;
import com.example.primerparcial.datos.producto.DProducto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NOrden {

    private DOrden dOrden;
    private DProducto dProducto;

    // Constructor
    public NOrden(Context context) {
        dOrden = new DOrden(context);
        dProducto = new DProducto(context);
    }

    // Métodos relacionados con detalles de la orden
    public String registrarDetalleOrden(int cantidad, double precio, int idOrden, int idProducto) {
        int stockDisponible = dProducto.obtenerStockProducto(idProducto);
        if (cantidad > stockDisponible) {
            return "La cantidad no puede ser mayor al stock disponible.";
        }
        if (cantidad == 0) {
            return "La cantidad no puede ser 0.";
        }

        dOrden.insertarDetalleOrden(cantidad, precio, idOrden, idProducto);
        double totalDetalle = cantidad * precio;
        dOrden.actualizarTotalOrden(idOrden, totalDetalle, true);

        int nuevoStock = stockDisponible - cantidad;
        dProducto.actualizarStock(idProducto, nuevoStock);

        return "Producto añadido a la orden correctamente.";
    }

    public void eliminarDetalleOrden(int idOrden, int idProducto, int cantidad, double totalDetalle) {
        dOrden.eliminarDetalleOrden(idOrden, idProducto);
        dOrden.actualizarTotalOrden(idOrden, totalDetalle, false);

        int stockActual = dProducto.obtenerStockProducto(idProducto);
        int nuevoStock = stockActual + cantidad;
        dProducto.actualizarStock(idProducto, nuevoStock);
    }

    public boolean verificarProductoEnOrden(int idOrden, int idProducto) {
        return dOrden.productoYaEnOrden(idOrden, idProducto);
    }

    public List<Map<String, String>> obtenerDetallesPorOrden(int idOrden) {
        Cursor cursor = dOrden.obtenerDetallesPorOrden(idOrden);
        List<Map<String, String>> detalles = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> detalleMap = new HashMap<>();
                detalleMap.put("cantidad", cursor.getString(cursor.getColumnIndexOrThrow("cantidad")));
                detalleMap.put("precio", cursor.getString(cursor.getColumnIndexOrThrow("precio")));
                detalleMap.put("idProducto", cursor.getString(cursor.getColumnIndexOrThrow("idProducto")));
                detalleMap.put("idOrden", cursor.getString(cursor.getColumnIndexOrThrow("idOrden")));

                int idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("idProducto"));
                Cursor productoCursor = dProducto.obtenerProductoPorId(idProducto);

                if (productoCursor.moveToFirst()) {
                    detalleMap.put("nombre", productoCursor.getString(productoCursor.getColumnIndexOrThrow("nombre")));
                    detalleMap.put("imagenPath", productoCursor.getString(productoCursor.getColumnIndexOrThrow("imagenPath")));
                }
                productoCursor.close();

                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                detalleMap.put("monto", String.valueOf(cantidad * precio));

                detalles.add(detalleMap);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return detalles;
    }

    public void actualizarCantidadDetalleOrden(int idOrden, int idProducto, int nuevaCantidad, int cantidadAnterior, double precio) {
        int diferenciaCantidad = nuevaCantidad - cantidadAnterior;
        int stockDisponible = dProducto.obtenerStockProducto(idProducto);

        if (diferenciaCantidad > 0 && diferenciaCantidad > stockDisponible) {
            throw new IllegalArgumentException("La cantidad no puede ser mayor al stock disponible.");
        }

        int nuevoStock = (diferenciaCantidad > 0)
                ? stockDisponible - diferenciaCantidad
                : stockDisponible + Math.abs(diferenciaCantidad);
        dProducto.actualizarStock(idProducto, nuevoStock);

        dOrden.actualizarCantidad(idOrden, idProducto, nuevaCantidad);

        double nuevoMontoDetalle = nuevaCantidad * precio;
        double montoDetalleAnterior = cantidadAnterior * precio;
        dOrden.actualizarTotalOrden(idOrden, nuevoMontoDetalle - montoDetalleAnterior, true);
    }

    public void eliminarDetallesPorOrden(int idOrden) {
        List<Map<String, String>> detalles = obtenerDetallesPorOrden(idOrden);

        for (Map<String, String> detalle : detalles) {
            int idProducto = Integer.parseInt(detalle.get("idProducto"));
            int cantidad = Integer.parseInt(detalle.get("cantidad"));

            int stockActual = dProducto.obtenerStockProducto(idProducto);
            int nuevoStock = stockActual + cantidad;
            dProducto.actualizarStock(idProducto, nuevoStock);
        }

        dOrden.eliminarDetallesPorOrden(idOrden);
    }

    // Métodos originales de NOrden
    public void registrarOrden(String fecha, String estado, double total, int idCliente, int idRepartidor) {
        dOrden.insertarOrden(fecha, estado, total, idCliente, idRepartidor);
    }

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

    public void actualizarOrden(int id, String fecha, String estado, double total, int idCliente, int idRepartidor) {
        dOrden.actualizarOrden(id, fecha, estado, total, idCliente, idRepartidor);
    }

    public void eliminarOrden(int id) {
        eliminarDetallesPorOrden(id);
        dOrden.eliminarOrden(id);
    }

    public double obtenerTotalOrden(int idOrden) {
        return dOrden.obtenerTotalOrden(idOrden);
    }

    public void actualizarEstadoYRepartidor(int idOrden, String nuevoEstado, int idRepartidor) {
        if (!nuevoEstado.equals("Enviado") && !nuevoEstado.equals("Completado")) {
            throw new IllegalArgumentException("Estado no válido. Debe ser 'Enviado' o 'Completado'.");
        }
        dOrden.actualizarEstadoYRepartidor(idOrden, nuevoEstado, idRepartidor);
    }

    public Map<String, String> obtenerDatosCliente(int idOrden) {
        return dOrden.obtenerDatosCliente(idOrden);
    }

    public Map<String, String> obtenerDatosOrden(int idOrden) {
        return dOrden.obtenerDatosOrden(idOrden);
    }
}
