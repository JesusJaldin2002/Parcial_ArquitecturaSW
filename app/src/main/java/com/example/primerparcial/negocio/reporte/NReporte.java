package com.example.primerparcial.negocio.reporte;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.reporte.DReporte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NReporte {
    private DReporte dReporte;

    public NReporte(Context context) {
        this.dReporte = new DReporte(context);
    }

    // Genera el reporte total de órdenes en un rango de fechas, con detalle de las órdenes, con un filtro opcional de idCliente
    public String generarReporteTotalOrdenes(String fechaInicio, String fechaFin, Integer idCliente, String nombreClienteSeleccionado) {
        StringBuilder resultado = new StringBuilder();
        double totalGlobal = 0;
        int totalOrdenes = 0;

        try {
            // Mostrar información del cliente seleccionado si existe
            if (idCliente != null) {
                resultado.append("Cliente: ").append(nombreClienteSeleccionado).append("\n\n");
            }

            Cursor cursor = dReporte.generarReporteTotalOrdenes(fechaInicio, fechaFin, idCliente);
            if (cursor != null && cursor.moveToFirst()) {
                resultado.append("Reporte de órdenes del ").append(fechaInicio).append(" al ").append(fechaFin).append(":\n\n");
                do {
                    // Obtener los datos de cada orden
                    int ordenId = cursor.getInt(cursor.getColumnIndexOrThrow("orden_id"));
                    double ordenTotal = cursor.getDouble(cursor.getColumnIndexOrThrow("orden_total"));
                    String fechaOrden = cursor.getString(cursor.getColumnIndexOrThrow("fecha_orden"));
                    String productosDetalles = cursor.getString(cursor.getColumnIndexOrThrow("productos_detalles"));

                    // Sumar al total global y contar la orden
                    totalGlobal += ordenTotal;
                    totalOrdenes++;

                    // Mostrar detalles de cada orden
                    resultado.append("Orden ID: ").append(ordenId).append("\n")
                            .append("Fecha: ").append(fechaOrden).append("\n")
                            .append("Total de la orden: Bs ").append(ordenTotal).append("\n")
                            .append("Productos:\n");

                    // Mostrar los productos asociados
                    if (productosDetalles != null) {
                        String[] productosArray = productosDetalles.split("; ");
                        for (String producto : productosArray) {
                            resultado.append("  - ").append(producto).append("\n");
                        }
                    } else {
                        resultado.append("  No hay productos asociados.\n");
                    }

                    resultado.append("\n"); // Separador entre órdenes
                } while (cursor.moveToNext());

                // Mostrar el total global
                resultado.append("====================================\n");
                resultado.append("Total de órdenes: ").append(totalOrdenes).append("\n");
                resultado.append("Suma total de todas las órdenes: Bs ").append(totalGlobal).append("\n");
            } else {
                resultado.append("No se encontraron órdenes en el rango de fechas especificado.\n");
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            resultado.append("Error al generar el reporte de órdenes: ").append(e.getMessage());
        }

        return resultado.toString();
    }

    // Genera el reporte de total por categoría en un rango de fechas, con un filtro opcional de idCliente
    public String generarReporteTotalPorCategoria(String fechaInicio, String fechaFin, Integer idCliente, String nombreClienteSeleccionado) {
        StringBuilder resultado = new StringBuilder();

        // Mostrar información del cliente seleccionado si existe
        if (idCliente != null) {
            resultado.append("Cliente seleccionado: ").append(nombreClienteSeleccionado).append("\n\n");
        }

        resultado.append("Reporte de total por categoría del ").append(fechaInicio).append(" al ").append(fechaFin).append(":\n\n");
        double totalGlobal = 0;

        try {
            Cursor cursor = dReporte.generarReporteTotalPorCategoria(fechaInicio, fechaFin, idCliente);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Obtener los datos de cada categoría
                    String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria_nombre"));
                    double totalSum = cursor.getDouble(cursor.getColumnIndexOrThrow("total_sum"));

                    // Sumar al total global
                    totalGlobal += totalSum;

                    // Mostrar detalles de la categoría
                    resultado.append("Categoría: ").append(categoria).append("\n")
                            .append("Total: Bs ").append(totalSum).append("\n")
                            .append("--------------------------------------------------\n");
                } while (cursor.moveToNext());

                // Mostrar el total global
                resultado.append("\n====================================\n");
                resultado.append("Total acumulado de todas las categorías: Bs ").append(totalGlobal).append("\n");
            } else {
                resultado.append("No se encontraron resultados para las categorías en el rango de fechas especificado.\n");
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            resultado.append("Error al generar el reporte por categoría: ").append(e.getMessage());
        }

        return resultado.toString();
    }


    // Genera el reporte de total por producto en un rango de fechas, con un filtro opcional de idCliente
    public String generarReporteTotalPorProducto(String fechaInicio, String fechaFin, Integer idCliente, String nombreClienteSeleccionado) {
        StringBuilder resultado = new StringBuilder();

        // Mostrar información del cliente seleccionado si existe
        if (idCliente != null) {
            resultado.append("Cliente seleccionado: ").append(nombreClienteSeleccionado).append("\n\n");
        }

        resultado.append("Reporte de total por producto del ").append(fechaInicio).append(" al ").append(fechaFin).append(":\n\n");
        double totalGlobal = 0;

        try {
            Cursor cursor = dReporte.generarReporteTotalPorProducto(fechaInicio, fechaFin, idCliente);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Obtener los datos de cada producto
                    String producto = cursor.getString(cursor.getColumnIndexOrThrow("producto_nombre"));
                    double totalSum = cursor.getDouble(cursor.getColumnIndexOrThrow("total_sum"));

                    // Sumar al total global
                    totalGlobal += totalSum;

                    // Mostrar detalles del producto
                    resultado.append("Producto: ").append(producto).append("\n")
                            .append("Total: Bs ").append(totalSum).append("\n")
                            .append("--------------------------------------------------\n");
                } while (cursor.moveToNext());

                // Mostrar el total global
                resultado.append("\n====================================\n");
                resultado.append("Total acumulado de todos los productos: Bs ").append(totalGlobal).append("\n");
            } else {
                resultado.append("No se encontraron resultados para los productos en el rango de fechas especificado.\n");
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            resultado.append("Error al generar el reporte por producto: ").append(e.getMessage());
        }

        return resultado.toString();
    }


    public List<Map<String, String>> obtenerClientes() {
        List<Map<String, String>> clientes = new ArrayList<>();
        Cursor cursor = dReporte.obtenerClientes();
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> clienteMap = new HashMap<>();
                clienteMap.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")));
                clienteMap.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                clientes.add(clienteMap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return clientes;
    }
}
