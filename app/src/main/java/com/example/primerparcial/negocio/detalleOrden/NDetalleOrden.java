package com.example.primerparcial.negocio.detalleOrden;

import android.content.Context;

import com.example.primerparcial.datos.detalleOrden.DDetalleOrden;
import com.example.primerparcial.datos.orden.DOrden;
import com.example.primerparcial.datos.producto.DProducto;

public class NDetalleOrden {

    private DDetalleOrden dDetalleOrden;
    private DOrden dOrden;
    private DProducto dProducto;

    // Constructor que inicializa la capa de datos
    public NDetalleOrden(Context context) {
        dDetalleOrden = new DDetalleOrden(context);
        dOrden = new DOrden(context);
        dProducto = new DProducto(context); // Inicializar la capa de datos de productos para actualizar el stock
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
}
