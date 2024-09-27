package com.example.primerparcial.negocio.repartidor;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.repartidor.DRepartidor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NRepartidor {

    private DRepartidor dRepartidor;

    // Constructor
    public NRepartidor(Context context) {
        dRepartidor = new DRepartidor(context);
    }

    // Método para registrar un nuevo repartidor
    public void registrarRepartidor(String nombre, String nroTelefono) {
        dRepartidor.insertarRepartidor(nombre, nroTelefono);
    }

    // Método para obtener todos los repartidores
    public List<Map<String, String>> obtenerRepartidores() {
        List<Map<String, String>> repartidores = new ArrayList<>();
        Cursor cursor = dRepartidor.obtenerRepartidores();
        if (cursor.moveToFirst()) {
            do {
                DRepartidor repartidor = new DRepartidor();
                repartidor.cargarDesdeCursor(cursor);

                Map<String, String> repartidorMap = new HashMap<>();
                repartidorMap.put("id", String.valueOf(repartidor.getId()));
                repartidorMap.put("nombre", repartidor.getNombre());
                repartidorMap.put("nroTelefono", repartidor.getNroTelefono());

                repartidores.add(repartidorMap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return repartidores;
    }

    // Método para actualizar un repartidor
    public void actualizarRepartidor(String id, String nombre, String nroTelefono) {
        dRepartidor.actualizarRepartidor(id, nombre, nroTelefono);
    }

    // Método para eliminar un repartidor
    public void eliminarRepartidor(String id) {
        dRepartidor.eliminarRepartidor(id);
    }

    // Método para obtener el nombre de un repartidor por su ID

}
