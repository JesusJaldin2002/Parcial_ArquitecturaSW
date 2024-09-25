package com.example.primerparcial.presentacion.posicion;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerparcial.R;
import com.example.primerparcial.negocio.posicion.NPosicion;

import java.util.List;
import java.util.Map;

public class PPosicion extends AppCompatActivity {

    private NPosicion nPosicion;  // Capa de negocio
    private EditText etNombreUbicacion, etReferenciaUbicacion, etLinkGoogleMaps;
    private int idCliente;  // Para almacenar el ID del cliente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener el ID del cliente desde el intent
        idCliente = getIntent().getIntExtra("idCliente", -1);
        String action = getIntent().getStringExtra("action");
        String nombreCliente = getIntent().getStringExtra("nombreCliente");

        // Verificar que el ID del cliente sea válido
        if (idCliente == -1) {
            Toast.makeText(this, "Error: Cliente no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        nPosicion = new NPosicion(this);

        if ("listar".equals(action)) {
            setContentView(R.layout.gestionar_ubicaciones);
            TextView tvNombreCliente = findViewById(R.id.tvNombreCliente);
            tvNombreCliente.setText(nombreCliente);
            listarUbicaciones();

            // Botón Volver
            Button btnVolver = findViewById(R.id.btnVolverAtras);
            btnVolver.setOnClickListener(v -> {
                finish();
            });
        } else {
            setContentView(R.layout.registrar_ubicacion);

            // Inicializar los campos del layout
            etNombreUbicacion = findViewById(R.id.etNombreUbicacion);
            etReferenciaUbicacion = findViewById(R.id.etReferenciaUbicacion);
            etLinkGoogleMaps = findViewById(R.id.etLinkGoogleMaps);

            // Botón Guardar
            Button btnGuardar = findViewById(R.id.btnGuardarUbicacion);
            btnGuardar.setOnClickListener(v -> registrarUbicacion());

            // Botón Volver
            Button btnVolver = findViewById(R.id.btnVolverUbicacion);
            btnVolver.setOnClickListener(v -> {
                finish();
            });
        }
    }


    // Método para registrar una nueva ubicación
    private void registrarUbicacion() {
        String nombre = etNombreUbicacion.getText().toString().trim();
        String referencia = etReferenciaUbicacion.getText().toString().trim();
        String linkGoogleMaps = etLinkGoogleMaps.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || referencia.isEmpty() || linkGoogleMaps.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamar a la capa de negocio para registrar la posición
        nPosicion.registrarPosicion(nombre, linkGoogleMaps, referencia, idCliente);

        // Mostrar mensaje de éxito
        Toast.makeText(this, "Ubicación registrada con éxito", Toast.LENGTH_SHORT).show();

        // Limpiar los campos después de registrar
        limpiarCampos();
    }

    private void listarUbicaciones() {
        LinearLayout listaUbicacionesLayout = findViewById(R.id.listaUbicacionesLayout);
        listaUbicacionesLayout.removeAllViews();

        // Obtener las ubicaciones desde la capa de negocio
        List<Map<String, String>> ubicaciones = nPosicion.obtenerUbicacionesPorCliente(idCliente);

        for (Map<String, String> ubicacion : ubicaciones) {
            View ubicacionView = getLayoutInflater().inflate(R.layout.item_ubicacion, null);

            // Asignar los valores de la ubicación
            TextView idTextView = ubicacionView.findViewById(R.id.tvIdUbicacion);
            TextView nombreTextView = ubicacionView.findViewById(R.id.tvNombreUbicacion);
            TextView referenciaTextView = ubicacionView.findViewById(R.id.tvReferenciaUbicacion);
            TextView urlTextView = ubicacionView.findViewById(R.id.tvUrlUbicacion);

            idTextView.setText("ID: " + ubicacion.get("id"));
            nombreTextView.setText("Nombre: " + ubicacion.get("nombre"));
            referenciaTextView.setText("Referencia: " + ubicacion.get("referencia"));
            urlTextView.setText("URL: " + ubicacion.get("urlMapa"));

            // Botón Editar Ubicación
            Button btnEditarUbicacion = ubicacionView.findViewById(R.id.btnEditarUbicacion);
            btnEditarUbicacion.setOnClickListener(v -> {
                mostrarModalEditarUbicacion(ubicacion);
            });

            // Botón Eliminar Ubicación
            Button btnEliminarUbicacion = ubicacionView.findViewById(R.id.btnEliminarUbicacion);
            btnEliminarUbicacion.setOnClickListener(v -> {
                eliminarUbicacionConConfirmacion(ubicacion.get("id"));
            });

            listaUbicacionesLayout.addView(ubicacionView);
        }
    }

    // Método para limpiar los campos
    private void limpiarCampos() {
        etNombreUbicacion.setText("");
        etReferenciaUbicacion.setText("");
        etLinkGoogleMaps.setText("");
    }

    private void mostrarModalEditarUbicacion(Map<String, String> ubicacion) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_ubicacion);

        // Inicializar las vistas del modal
        EditText etNombreEditar = dialog.findViewById(R.id.etNombreEditar);
        EditText etReferenciaEditar = dialog.findViewById(R.id.etReferenciaEditar);
        EditText etUrlEditar = dialog.findViewById(R.id.etUrlEditar);

        // Asignar los valores actuales de la ubicación a los campos de texto
        etNombreEditar.setText(ubicacion.get("nombre"));
        etReferenciaEditar.setText(ubicacion.get("referencia"));
        etUrlEditar.setText(ubicacion.get("urlMapa"));

        // Botón Cancelar
        Button btnCancelarEditar = dialog.findViewById(R.id.btnCancelarEditar);
        btnCancelarEditar.setOnClickListener(v -> dialog.dismiss());

        // Botón Guardar
        Button btnGuardarEditar = dialog.findViewById(R.id.btnGuardarEditar);
        btnGuardarEditar.setOnClickListener(v -> {
            String nuevoNombre = etNombreEditar.getText().toString().trim();
            String nuevaReferencia = etReferenciaEditar.getText().toString().trim();
            String nuevoUrl = etUrlEditar.getText().toString().trim();

            if (!nuevoNombre.isEmpty() && !nuevaReferencia.isEmpty() && !nuevoUrl.isEmpty()) {
                // Llamar a la capa de negocio para actualizar la ubicación
                nPosicion.actualizarPosicion(ubicacion.get("id"), nuevoNombre, nuevoUrl, nuevaReferencia, idCliente);
                Toast.makeText(PPosicion.this, "Ubicación actualizada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                listarUbicaciones();
            } else {
                Toast.makeText(PPosicion.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        // Aquí ajustamos el tamaño del modal
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void eliminarUbicacionConConfirmacion(String ubicacionId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_eliminar);

        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        tvTitulo.setText("Eliminar Ubicación");
        tvMensaje.setText("¿Estás seguro de que quieres eliminar esta ubicación?");

        Button btnSi = dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(v -> {
            nPosicion.eliminarPosicion(ubicacionId);
            Toast.makeText(this, "Ubicación eliminada", Toast.LENGTH_SHORT).show();
            listarUbicaciones();  // Refrescar la lista de ubicaciones
            dialog.dismiss();
        });

        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
