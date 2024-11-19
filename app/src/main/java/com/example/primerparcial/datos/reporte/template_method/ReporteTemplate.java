package com.example.primerparcial.datos.reporte.template_method;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class ReporteTemplate {

    // Método plantilla que define el flujo
    public final Cursor generarReporte(SQLiteDatabase db, String fechaInicio, String fechaFin, Integer idCliente) {
        validarFechas(fechaInicio, fechaFin);
        return db.rawQuery(
                construirQuery(idCliente),
                obtenerParametros(fechaInicio, fechaFin, idCliente)
        );
    }

    // Método para construir la consulta, que puede ser adaptado por las subclases
    protected abstract String construirQuery(Integer idCliente);

    // Método para obtener los parámetros de la consulta
    protected abstract String[] obtenerParametros(String fechaInicio, String fechaFin, Integer idCliente);

    private void validarFechas(String fechaInicio, String fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            throw new IllegalArgumentException("Las fechas no pueden estar vacías");
        }
    }
}
