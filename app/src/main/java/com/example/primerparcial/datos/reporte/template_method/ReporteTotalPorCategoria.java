package com.example.primerparcial.datos.reporte.template_method;

public class ReporteTotalPorCategoria extends ReporteTemplate {

    @Override
    protected String construirQuery(Integer idCliente) {
        String baseQuery = "SELECT cat.nombre AS categoria_nombre, SUM(do.cantidad * do.precio) AS total_sum " +
                "FROM ordenes o " +
                "INNER JOIN detalleOrden do ON o.id = do.idOrden " +
                "INNER JOIN productos p ON do.idProducto = p.id " +
                "INNER JOIN categorias cat ON p.idCategoria = cat.id " +
                "WHERE (substr(o.fecha, 7, 4) || '-' || substr(o.fecha, 4, 2) || '-' || substr(o.fecha, 1, 2)) " +
                "BETWEEN ? AND ? ";

        // Agregar condición opcional para idCliente
        if (idCliente != null) {
            baseQuery += "AND o.idCliente = ? ";
        }

        baseQuery += "GROUP BY cat.id " +
                "ORDER BY total_sum DESC";

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
