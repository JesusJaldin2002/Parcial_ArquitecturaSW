package com.example.primerparcial.datos.reporte.template_method;

public class ReporteTotalOrdenes extends ReporteTemplate {

    @Override
    protected String construirConsultaBase(Integer idCliente) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT o.id AS orden_id, ")
                .append("o.total AS orden_total, ")
                .append("o.fecha AS fecha_orden, ")
                .append("GROUP_CONCAT(p.nombre || ' (Cantidad: ' || d.cantidad || ', Precio: ' || d.precio || ')', '; ') AS productos_detalles ")
                .append("FROM ordenes o ")
                .append("INNER JOIN detalleOrden d ON o.id = d.idOrden ")
                .append("INNER JOIN productos p ON d.idProducto = p.id ")
                .append("WHERE (substr(o.fecha, 7, 4) || '-' || substr(o.fecha, 4, 2) || '-' || substr(o.fecha, 1, 2)) ")
                .append("BETWEEN ? AND ? ");

        if (idCliente != null) {
            query.append("AND o.idCliente = ? ");
        }

        query.append("GROUP BY o.id, o.total, o.fecha");
        return query.toString();
    }

    @Override
    protected String aplicarOrdenamiento(String query) {
        return query + " ORDER BY (substr(fecha_orden, 7, 4) || '-' || " +
                "substr(fecha_orden, 4, 2) || '-' || substr(fecha_orden, 1, 2)) DESC";
    }
}

