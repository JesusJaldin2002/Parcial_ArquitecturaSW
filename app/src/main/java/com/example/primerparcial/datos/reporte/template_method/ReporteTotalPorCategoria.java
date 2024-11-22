package com.example.primerparcial.datos.reporte.template_method;

public class ReporteTotalPorCategoria extends ReporteTemplate {

    @Override
    protected String construirConsultaBase(Integer idCliente) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT cat.nombre AS categoria_nombre, ")
                .append("SUM(do.cantidad * do.precio) AS total_sum, ")
                .append("SUM(do.cantidad) AS total_productos ")
                .append("FROM ordenes o ")
                .append("INNER JOIN detalleOrden do ON o.id = do.idOrden ")
                .append("INNER JOIN productos p ON do.idProducto = p.id ")
                .append("INNER JOIN categorias cat ON p.idCategoria = cat.id ")
                .append("WHERE (substr(o.fecha, 7, 4) || '-' || substr(o.fecha, 4, 2) || '-' || substr(o.fecha, 1, 2)) ")
                .append("BETWEEN ? AND ? ");

        if (idCliente != null) {
            query.append("AND o.idCliente = ? ");
        }

        query.append("GROUP BY cat.id");
        return query.toString();
    }

    @Override
    protected String aplicarOrdenamiento(String query) {
        return query + " ORDER BY total_sum DESC";
    }
}
