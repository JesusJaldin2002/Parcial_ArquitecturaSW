package com.example.primerparcial.datos.reporte.template_method;

public class ReporteTotalOrdenes extends ReporteTemplate {

    @Override
    protected String construirQuery(Integer idCliente) {
        String baseQuery = "SELECT o.id as orden_id, o.total as orden_total, o.fecha as fecha_orden, " +
                "GROUP_CONCAT(p.nombre || ' (Cantidad: ' || d.cantidad || ', Precio: ' || d.precio || ')', '; ') as productos_detalles " +
                "FROM ordenes o " +
                "INNER JOIN detalleOrden d ON o.id = d.idOrden " +
                "INNER JOIN productos p ON d.idProducto = p.id " +
                "WHERE (substr(o.fecha, 7, 4) || '-' || substr(o.fecha, 4, 2) || '-' || substr(o.fecha, 1, 2)) " +
                "BETWEEN ? AND ? ";

        // Agregar condición opcional para idCliente si no es null
        if (idCliente != null) {
            baseQuery += "AND o.idCliente = ? ";
        }

        baseQuery += "GROUP BY o.id, o.total, o.fecha " +
                "ORDER BY (substr(o.fecha, 7, 4) || '-' || substr(o.fecha, 4, 2) || '-' || substr(o.fecha, 1, 2)) ASC";

        return baseQuery;
    }

    @Override
    protected String[] obtenerParametros(String fechaInicio, String fechaFin, Integer idCliente) {
        if (idCliente != null) {
            return new String[]{fechaInicio, fechaFin, String.valueOf(idCliente)};
        } else {
            return new String[]{fechaInicio, fechaFin};
        }
    }
}
