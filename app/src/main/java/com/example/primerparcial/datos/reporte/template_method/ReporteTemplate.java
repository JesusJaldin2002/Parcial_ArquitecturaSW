package com.example.primerparcial.datos.reporte.template_method;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public abstract class ReporteTemplate {

    // Método plantilla que define el esqueleto del algoritmo
    public final Cursor generarReporte(SQLiteDatabase db, String fechaInicio, String fechaFin, Integer idCliente) {
        // Paso 1: Validación común de fechas
        validarFechas(fechaInicio, fechaFin);
        // Paso 2: Construcción de consulta base
        String queryBase = construirConsultaBase(idCliente);
        // Paso 3: Aplicar ordenamiento específico
        String queryFinal = aplicarOrdenamiento(queryBase);
        // Paso 4: Obtener parámetros (común)
        String[] parametros = construirParametros(fechaInicio, fechaFin, idCliente);
        // Paso 5: Ejecutar consulta
        return db.rawQuery(queryFinal, parametros);
    }

    private String[] construirParametros(String fechaInicio, String fechaFin, Integer idCliente) {
        if (idCliente != null) {
            return new String[]{fechaInicio, fechaFin, String.valueOf(idCliente)};
        }
        return new String[]{fechaInicio, fechaFin};
    }

    // Métodos comunes
    private void validarFechas(String fechaInicio, String fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            throw new IllegalArgumentException("Las fechas no pueden estar vacías");
        }

        try {
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);

            if (inicio.isAfter(fin)) {
                throw new IllegalArgumentException("La " +
                        "fecha de inicio no puede ser mayor que la fecha de fin");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("El " +
                    "formato de las fechas es inválido. Use el formato 'yyyy-MM-dd'.");
        }
    }

    // Métodos abstractos
    protected abstract String construirConsultaBase(Integer idCliente);
    protected abstract String aplicarOrdenamiento(String query);
}