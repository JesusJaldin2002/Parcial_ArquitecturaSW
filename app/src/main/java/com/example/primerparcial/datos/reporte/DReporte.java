package com.example.primerparcial.datos.reporte;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.primerparcial.datos.DBHelper;
import com.example.primerparcial.datos.reporte.template_method.ReporteTemplate;
import com.example.primerparcial.datos.reporte.template_method.ReporteTotalOrdenes;
import com.example.primerparcial.datos.reporte.template_method.ReporteTotalPorCategoria;
import com.example.primerparcial.datos.reporte.template_method.ReporteTotalPorProducto;

public class DReporte {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DReporte(Context context) {
        this.dbHelper = new DBHelper(context);
        this.db = dbHelper.getReadableDatabase();
    }

    public Cursor generarReporteTotalOrdenes(String fechaInicio, String fechaFin, Integer idCliente) {
        ReporteTemplate reporte = new ReporteTotalOrdenes();
        return reporte.generarReporte(db, fechaInicio, fechaFin, idCliente);
    }

    public Cursor generarReporteTotalPorCategoria(String fechaInicio, String fechaFin, Integer idCliente) {
        ReporteTemplate reporte = new ReporteTotalPorCategoria();
        return reporte.generarReporte(db, fechaInicio, fechaFin, idCliente);
    }

    public Cursor generarReporteTotalPorProducto(String fechaInicio, String fechaFin, Integer idCliente) {
        ReporteTemplate reporte = new ReporteTotalPorProducto();
        return reporte.generarReporte(db, fechaInicio, fechaFin, idCliente);
    }
    public Cursor obtenerClientes() {
        return db.rawQuery("SELECT id, nombre FROM clientes", null);
    }

}
