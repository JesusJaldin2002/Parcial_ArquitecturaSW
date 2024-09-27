package com.example.primerparcial.negocio.cliente;

import android.content.Context;
import android.database.Cursor;

import com.example.primerparcial.datos.cliente.DCliente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NCliente {

    private DCliente dCliente;

    public NCliente(Context context) {
        dCliente = new DCliente(context);
    }

    // Método para registrar un cliente
    public void registrarCliente(String nombre, String nroTelefono, String imagenPath) {
        dCliente.insertarCliente(nombre, nroTelefono, imagenPath);
    }

    // Método para actualizar un cliente
    public void actualizarCliente(String id, String nombre, String nroTelefono, String imagenPath) {
        dCliente.actualizarCliente(id, nombre, nroTelefono, imagenPath);
    }

    // Método para obtener todos los clientes desde la capa de negocio
    public List<Map<String, String>> obtenerClientes() {
        List<Map<String, String>> clientes = new ArrayList<>();
        Cursor cursor = dCliente.obtenerClientes();
        if (cursor.moveToFirst()) {
            do {
                DCliente cliente = new DCliente();
                cliente.cargarDesdeCursor(cursor);

                Map<String, String> clienteMap = new HashMap<>();
                clienteMap.put("id", String.valueOf(cliente.getId()));
                clienteMap.put("nombre", cliente.getNombre());
                clienteMap.put("nroTelefono", cliente.getNroTelefono());
                clienteMap.put("imagenPath", cliente.getImagenPath());

                clientes.add(clienteMap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return clientes;
    }

    // Método para eliminar un cliente
    public void eliminarCliente(String id) {
        dCliente.eliminarCliente(id);
    }

    // Método para obtener el nombre del cliente por su ID
    public String obtenerNombreClientePorId(int idCliente) {
        return dCliente.obtenerNombreClientePorId(idCliente);
    }

    public Map<String, String> obtenerUbicacionCliente(int idCliente) {
        return dCliente.obtenerUbicacionCliente(idCliente);
    }
}
