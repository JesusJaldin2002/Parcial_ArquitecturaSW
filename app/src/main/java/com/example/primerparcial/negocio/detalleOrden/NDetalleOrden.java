package com.example.primerparcial.negocio.detalleOrden;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.detalleOrden.DDetalleOrden;
import com.example.primerparcial.datos.orden.DOrden;
import com.example.primerparcial.datos.producto.DProducto;
import com.example.primerparcial.negocio.orden.NOrden;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NDetalleOrden {

    private DDetalleOrden dDetalleOrden;
    private DOrden dOrden;
    private DProducto dProducto;

    // Constructor que inicializa la capa de datos
    public NDetalleOrden(Context context) {
        dDetalleOrden = new DDetalleOrden(context);
        dOrden = new DOrden(context);
        dProducto = new DProducto(context);
    }

    // Método para registrar un nuevo detalle de orden y actualizar el total de la orden
    public String registrarDetalleOrden(int cantidad, double precio, int idOrden, int idProducto) {
        // 1. Verificar el stock disponible del producto
        int stockDisponible = dProducto.obtenerStockProducto(idProducto);
        if (cantidad > stockDisponible) {
            return "La cantidad no puede ser mayor al stock disponible.";
        }
        if (cantidad == 0) {
            return "La cantidad no puede ser 0.";
        }

        // 2. Insertar el detalle de la orden
        dDetalleOrden.insertarDetalleOrden(cantidad, precio, idOrden, idProducto);

        // 3. Calcular el monto del detalle (cantidad * precio)
        double totalDetalle = cantidad * precio;

        // 4. Actualizar el total de la orden
        dOrden.actualizarTotalOrden(idOrden, totalDetalle, true);

        // 5. Reducir el stock del producto
        int nuevoStock = stockDisponible - cantidad;
        dProducto.actualizarStock(idProducto, nuevoStock);

        return "Producto añadido a la orden correctamente.";
    }

    // Método para eliminar un detalle de orden y restar del total de la orden, además de restaurar el stock
    public void eliminarDetalleOrden(int idDetalle, int idOrden, int idProducto, int cantidad, double totalDetalle) {
        // 1. Eliminar el detalle de la orden
        dDetalleOrden.eliminarDetalleOrden(idDetalle);

        // 2. Restar el monto del detalle del total de la orden
        dOrden.actualizarTotalOrden(idOrden, totalDetalle, false);

        // 3. Restaurar el stock del producto
        int stockActual = dProducto.obtenerStockProducto(idProducto);
        int nuevoStock = stockActual + cantidad;
        dProducto.actualizarStock(idProducto, nuevoStock);
    }

    public boolean verificarProductoEnOrden(int idOrden, int idProducto) {
        return dDetalleOrden.productoYaEnOrden(idOrden, idProducto);
    }

    // Método para obtener los detalles de una orden y transformarlos a una lista de mapas
    public List<Map<String, String>> obtenerDetallesPorOrden(int idOrden) {
        Cursor cursor = dDetalleOrden.obtenerDetallesPorOrden(idOrden);
        List<Map<String, String>> detalles = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> detalleMap = new HashMap<>();
                detalleMap.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")));
                detalleMap.put("cantidad", cursor.getString(cursor.getColumnIndexOrThrow("cantidad")));
                detalleMap.put("precio", cursor.getString(cursor.getColumnIndexOrThrow("precio")));
                detalleMap.put("idProducto", cursor.getString(cursor.getColumnIndexOrThrow("idProducto")));
                detalleMap.put("idOrden", cursor.getString(cursor.getColumnIndexOrThrow("idOrden")));

                // Obtener información del producto consultando la base de datos
                int idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("idProducto"));
                Cursor productoCursor = dProducto.obtenerProductoPorId(idProducto);

                if (productoCursor.moveToFirst()) {
                    detalleMap.put("nombre", productoCursor.getString(productoCursor.getColumnIndexOrThrow("nombre")));
                    detalleMap.put("imagenPath", productoCursor.getString(productoCursor.getColumnIndexOrThrow("imagenPath")));
                }
                productoCursor.close();

                // Calcular el monto del detalle (cantidad * precio)
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                detalleMap.put("monto", String.valueOf(cantidad * precio));

                detalles.add(detalleMap);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return detalles;
    }

    // Método para actualizar la cantidad de un detalle de la orden y ajustar el total de la orden
    public void actualizarCantidadDetalleOrden(int idDetalle, int idProducto, int idOrden, int nuevaCantidad, int cantidadAnterior, double precio) {
        // Verificar la diferencia entre la cantidad nueva y la anterior
        int diferenciaCantidad = nuevaCantidad - cantidadAnterior;

        // Obtener el stock actual del producto
        int stockDisponible = dProducto.obtenerStockProducto(idProducto);

        // Si la nueva cantidad es mayor a la anterior, estamos aumentando la cantidad
        if (diferenciaCantidad > 0) {
            // Verificar si hay suficiente stock disponible para cubrir el aumento
            if (diferenciaCantidad > stockDisponible) {
                throw new IllegalArgumentException("La cantidad no puede ser mayor al stock disponible");
            }
            // Reducir el stock disponible según la diferencia
            int nuevoStock = stockDisponible - diferenciaCantidad;
            dProducto.actualizarStock(idProducto, nuevoStock);
        } else if (diferenciaCantidad < 0) {
            // Si la cantidad nueva es menor, estamos disminuyendo la cantidad, así que devolvemos al stock
            int nuevoStock = stockDisponible + Math.abs(diferenciaCantidad); // Sumamos la diferencia al stock
            dProducto.actualizarStock(idProducto, nuevoStock);
        }

        // Actualizar la cantidad en la base de datos
        dDetalleOrden.actualizarCantidad(idDetalle, nuevaCantidad);

        // Recalcular el total del detalle (nueva cantidad * precio)
        double nuevoMontoDetalle = nuevaCantidad * precio;
        double montoDetalleAnterior = cantidadAnterior * precio;

        // Actualizar el total de la orden restando el monto anterior y sumando el nuevo
        dOrden.actualizarTotalOrden(idOrden, nuevoMontoDetalle - montoDetalleAnterior, true);
    }

    // Método para eliminar todos los detalles de una orden
    public void eliminarDetallesPorOrden(int idOrden) {
        // Obtener los detalles de la orden
        List<Map<String, String>> detalles = obtenerDetallesPorOrden(idOrden);

        // Para cada detalle, restaurar el stock antes de eliminar el detalle
        for (Map<String, String> detalle : detalles) {
            int idProducto = Integer.parseInt(detalle.get("idProducto"));
            int cantidad = Integer.parseInt(detalle.get("cantidad"));

            // Restaurar el stock del producto
            int stockActual = dProducto.obtenerStockProducto(idProducto);
            int nuevoStock = stockActual + cantidad;
            dProducto.actualizarStock(idProducto, nuevoStock);
        }

        // Ahora eliminar todos los detalles de la base de datos utilizando el método de la capa DDetalleOrden
        dDetalleOrden.eliminarDetallesPorOrden(idOrden);
    }

}
