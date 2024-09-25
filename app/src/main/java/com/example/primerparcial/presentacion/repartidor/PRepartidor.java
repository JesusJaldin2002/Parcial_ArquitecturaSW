package com.example.primerparcial.presentacion.repartidor;

import android.app.Dialog;
import android.content.Intent;
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

import com.example.primerparcial.MainActivity;
import com.example.primerparcial.R;
import com.example.primerparcial.negocio.repartidor.NRepartidor;

import java.util.List;
import java.util.Map;

public class PRepartidor extends AppCompatActivity {

    private NRepartidor nRepartidor;
    private EditText etNombreRepartidor, etNroTelefonoRepartidor;
    private View gestionarRepartidorView;
    private View listarRepartidoresView;

    // Variables para guardar repartidor y diálogo que están siendo editados
    private Map<String, String> repartidorEnEdicion;
    private Dialog dialogEnEdicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar ambos layouts
        gestionarRepartidorView = getLayoutInflater().inflate(R.layout.activity_gestionar_repartidor, null);
        listarRepartidoresView = getLayoutInflater().inflate(R.layout.lista_repartidores, null);

        // Inicialmente, mostrar la vista de gestionar repartidores
        setContentView(gestionarRepartidorView);

        nRepartidor = new NRepartidor(this);

        etNombreRepartidor = findViewById(R.id.etNombreRepartidor);
        etNroTelefonoRepartidor = findViewById(R.id.etNroTelefonoRepartidor);

        Button btnRegistrar = findViewById(R.id.btnRegistrarRepartidor);
        btnRegistrar.setOnClickListener(v -> registrarRepartidor());

        Button btnListarRepartidores = findViewById(R.id.btnListarRepartidores);
        btnListarRepartidores.setOnClickListener(v -> listarRepartidores());

        Button btnVolver = findViewById(R.id.btnCancelarRepartidor);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(PRepartidor.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Método para registrar un repartidor
    private void registrarRepartidor() {
        String nombre = etNombreRepartidor.getText().toString();
        String nroTelefono = etNroTelefonoRepartidor.getText().toString();

        if (!nombre.isEmpty() && !nroTelefono.isEmpty()) {
            nRepartidor.registrarRepartidor(nombre, nroTelefono);
            Toast.makeText(this, "Repartidor registrado", Toast.LENGTH_SHORT).show();

            // Limpiar los campos después de registrar
            etNombreRepartidor.setText("");
            etNroTelefonoRepartidor.setText("");

        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarRepartidores() {
        setContentView(listarRepartidoresView);

        LinearLayout listaRepartidoresLayout = findViewById(R.id.listaRepartidoresLayout);
        listaRepartidoresLayout.removeAllViews();

        // Obtenemos la lista de repartidores desde la capa de negocio
        List<Map<String, String>> repartidores = nRepartidor.obtenerRepartidores();

        for (Map<String, String> repartidor : repartidores) {
            View repartidorView = getLayoutInflater().inflate(R.layout.item_repartidor, null);

            // Asigna los valores a las vistas
            TextView idTextView = repartidorView.findViewById(R.id.tvIdRepartidor);
            TextView nombreTextView = repartidorView.findViewById(R.id.tvNombreRepartidor);
            TextView telefonoTextView = repartidorView.findViewById(R.id.tvNroTelefonoRepartidor);

            idTextView.setText("ID: " + repartidor.get("id"));
            nombreTextView.setText("Nombre: " + repartidor.get("nombre"));
            telefonoTextView.setText("Teléfono: " + repartidor.get("nroTelefono"));

            // Botón Editar Repartidor
            Button btnEditarRepartidor = repartidorView.findViewById(R.id.btnEditarRepartidor);
            btnEditarRepartidor.setOnClickListener(v -> mostrarModalEditarRepartidor(repartidor));

            // Botón Eliminar Repartidor
            Button btnEliminarRepartidor = repartidorView.findViewById(R.id.btnEliminarRepartidor);
            btnEliminarRepartidor.setOnClickListener(v -> {
                // Mostrar confirmación antes de eliminar
                eliminarRepartidorConConfirmacion(repartidor.get("id"));
            });

            // Agregar la vista del repartidor a la lista
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 32);
            repartidorView.setLayoutParams(layoutParams);

            listaRepartidoresLayout.addView(repartidorView);
        }

        Button btnVolver = listarRepartidoresView.findViewById(R.id.btnVolverRepartidor);
        btnVolver.setOnClickListener(v -> setContentView(gestionarRepartidorView));
    }

    // Método para mostrar confirmación de eliminación
    private void eliminarRepartidorConConfirmacion(String repartidorId) {
        // Crear el diálogo manualmente
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_eliminar);

        // Configurar el título y el mensaje del diálogo
        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        tvTitulo.setText("Eliminar Repartidor");
        tvMensaje.setText("¿Estás seguro de que quieres eliminar este repartidor?");

        // Botón Sí para confirmar la eliminación
        Button btnSi = dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(v -> {
            nRepartidor.eliminarRepartidor(repartidorId);
            Toast.makeText(this, "Repartidor eliminado", Toast.LENGTH_SHORT).show();
            listarRepartidores();
            dialog.dismiss();
        });

        // Botón No para cancelar
        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Método para mostrar el modal de editar repartidor
    private void mostrarModalEditarRepartidor(Map<String, String> repartidor) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_repartidor);

        // Inicializar las vistas del modal
        EditText etNombreEditar = dialog.findViewById(R.id.etNombreRepartidorEditar);
        EditText etNroTelefonoEditar = dialog.findViewById(R.id.etNroTelefonoRepartidorEditar);

        etNombreEditar.setText(repartidor.get("nombre"));
        etNroTelefonoEditar.setText(repartidor.get("nroTelefono"));

        // Botón Cancelar
        Button btnCancelarEditar = dialog.findViewById(R.id.btnCancelarRepartidorEditar);
        btnCancelarEditar.setOnClickListener(v -> dialog.dismiss());

        // Botón Guardar
        Button btnGuardarEditar = dialog.findViewById(R.id.btnGuardarRepartidorEditar);
        btnGuardarEditar.setOnClickListener(v -> {
            String nuevoNombre = etNombreEditar.getText().toString();
            String nuevoTelefono = etNroTelefonoEditar.getText().toString();

            if (!nuevoNombre.isEmpty() && !nuevoTelefono.isEmpty()) {
                // Llamar a la capa de negocio para actualizar
                nRepartidor.actualizarRepartidor(repartidor.get("id"), nuevoNombre, nuevoTelefono);
                Toast.makeText(PRepartidor.this, "Repartidor actualizado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                listarRepartidores(); // Refrescar la lista
            } else {
                Toast.makeText(PRepartidor.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        // Ajustar el tamaño del modal
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}
