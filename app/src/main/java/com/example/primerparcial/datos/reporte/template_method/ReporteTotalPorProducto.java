package com.example.primerparcial.datos.reporte.template_method;

public class ReporteTotalPorProducto extends ReporteTemplate {

    @Override
    protected String construirConsultaBase(Integer idCliente) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT p.nombre AS producto_nombre, ")
                .append("SUM(do.cantidad * do.precio) AS total_sum, ") // Total ingresos por producto
                .append("SUM(do.cantidad) AS unidades_vendidas ") // Total unidades vendidas
                .append("FROM ordenes o ")
                .append("INNER JOIN detalleOrden do ON o.id = do.idOrden ")
                .append("INNER JOIN productos p ON do.idProducto = p.id ")
                .append("WHERE (substr(o.fecha, 7, 4) || '-' || substr(o.fecha, 4, 2) || '-' || substr(o.fecha, 1, 2)) ")
                .append("BETWEEN ? AND ? ");

        if (idCliente != null) {
            query.append("AND o.idCliente = ? ");
        }

        query.append("GROUP BY p.id");
        return query.toString();
    }

    @Override
    protected String aplicarOrdenamiento(String query) {
        // Ordenar por ingresos generados, luego por cantidad de unidades vendidas, y finalmente alfabéticamente
        return query + " ORDER BY total_sum DESC, unidades_vendidas DESC, producto_nombre ASC";
    }
}