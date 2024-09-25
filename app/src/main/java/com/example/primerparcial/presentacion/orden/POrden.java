package com.example.primerparcial.presentacion.orden;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerparcial.MainActivity;
import com.example.primerparcial.R;
import com.example.primerparcial.negocio.cliente.NCliente;
import com.example.primerparcial.negocio.orden.NOrden;
import com.example.primerparcial.negocio.repartidor.NRepartidor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class POrden extends AppCompatActivity {

    private NOrden nOrden;
    private NCliente nCliente;
    private NRepartidor nRepartidor;

    private EditText etFechaOrden, etEstadoOrden, etTotalOrden;
    private Spinner spinnerCliente, spinnerRepartidor;
    private int idClienteSeleccionado, idRepartidorSeleccionado;
    private View gestionarOrdenView;
    private View listarOrdenesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar ambos layouts
        gestionarOrdenView = getLayoutInflater().inflate(R.layout.activity_gestionar_orden, null);
        listarOrdenesView = getLayoutInflater().inflate(R.layout.lista_ordenes, null);

        // Inicialmente, mostrar la vista de gestionar órdenes
        setContentView(gestionarOrdenView);

        nOrden = new NOrden(this);
        nCliente = new NCliente(this);
        nRepartidor = new NRepartidor(this);

        etFechaOrden = findViewById(R.id.etFechaOrden);
        etEstadoOrden = findViewById(R.id.etEstadoOrden);
        etTotalOrden = findViewById(R.id.etTotalOrden);
        spinnerCliente = findViewById(R.id.spinnerCliente);
        spinnerRepartidor = findViewById(R.id.spinnerRepartidor);

        // Cargar los datos en los Spinners
        cargarClientes();
        cargarRepartidores();

        Button btnRegistrar = findViewById(R.id.btnRegistrarOrden);
        btnRegistrar.setOnClickListener(v -> registrarOrden());

        Button btnListarOrdenes = findViewById(R.id.btnListarOrdenes);
        btnListarOrdenes.setOnClickListener(v -> listarOrdenes());

        Button btnVolver = findViewById(R.id.btnCancelarOrden);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(POrden.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Método para registrar una nueva orden
    private void registrarOrden() {
        String fecha = etFechaOrden.getText().toString();
        String estado = etEstadoOrden.getText().toString();
        String total = etTotalOrden.getText().toString();

        if (!fecha.isEmpty() && !estado.isEmpty()) {
            // Verificar si el campo total está vacío y asignar 0 si es el caso
            double totalOrden = total.isEmpty() ? 0.0 : Double.parseDouble(total);

            if (isValidDate(fecha)) {
                nOrden.registrarOrden(fecha, estado, totalOrden, idClienteSeleccionado, idRepartidorSeleccionado);
                Toast.makeText(this, "Orden registrada", Toast.LENGTH_SHORT).show();

                // Limpiar los campos después de registrar
                etFechaOrden.setText("");
                etEstadoOrden.setText("");
                etTotalOrden.setText("0");  // Restablecer el valor total a 0
            } else {
                Toast.makeText(this, "Por favor, ingrese una fecha válida en el formato dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Método para cargar los clientes en el Spinner
    private void cargarClientes() {
        List<Map<String, String>> clientes = nCliente.obtenerClientes();
        List<String> nombresClientes = new ArrayList<>();
        for (Map<String, String> cliente : clientes) {
            nombresClientes.add(cliente.get("nombre"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresClientes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(adapter);

        // Manejar la selección del Spinner
        spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idClienteSeleccionado = Integer.parseInt(Objects.requireNonNull(clientes.get(position).get("id")));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    // Método para cargar los repartidores en el Spinner
    private void cargarRepartidores() {
        List<Map<String, String>> repartidores = nRepartidor.obtenerRepartidores();
        List<String> nombresRepartidores = new ArrayList<>();
        for (Map<String, String> repartidor : repartidores) {
            nombresRepartidores.add(repartidor.get("nombre"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresRepartidores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepartidor.setAdapter(adapter);

        // Manejar la selección del Spinner
        spinnerRepartidor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idRepartidorSeleccionado = Integer.parseInt(Objects.requireNonNull(repartidores.get(position).get("id")));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    // Método para listar órdenes
    private void listarOrdenes() {
        setContentView(listarOrdenesView);

        LinearLayout listaOrdenesLayout = findViewById(R.id.listaOrdenesLayout);
        listaOrdenesLayout.removeAllViews();

        // Obtener las órdenes desde la capa de negocio
        List<Map<String, String>> ordenes = nOrden.obtenerOrdenes();

        for (Map<String, String> orden : ordenes) {
            View ordenView = getLayoutInflater().inflate(R.layout.item_orden, null);

            // Asignar los valores a las vistas
            TextView idTextView = ordenView.findViewById(R.id.tvIdOrden);
            TextView fechaTextView = ordenView.findViewById(R.id.tvFechaOrden);
            TextView estadoTextView = ordenView.findViewById(R.id.tvEstadoOrden);
            TextView totalTextView = ordenView.findViewById(R.id.tvTotalOrden);
            TextView clienteTextView = ordenView.findViewById(R.id.tvClienteOrden);

            // Asignar valores obtenidos de la base de datos o el objeto orden
            idTextView.setText("ID: " + orden.get("id"));
            fechaTextView.setText("Fecha: " + orden.get("fecha"));
            estadoTextView.setText("Estado: " + orden.get("estado"));
            totalTextView.setText("Total: Bs " + orden.get("total"));

            // Obtener el nombre del cliente usando el idCliente
            int idCliente = Integer.parseInt(orden.get("idCliente"));
            String nombreCliente = nCliente.obtenerNombreClientePorId(idCliente);
            clienteTextView.setText("Cliente: " + nombreCliente);

            // Manejar los botones adicionales para editar, eliminar, etc.
            manejarBotonesAdicionales(ordenView, orden);

            // Agregar la vista de orden a la lista
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 32);
            ordenView.setLayoutParams(layoutParams);

            listaOrdenesLayout.addView(ordenView);
        }

        // Botón para volver a la pantalla de gestionar órdenes
        Button btnVolver = listarOrdenesView.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> setContentView(gestionarOrdenView));
    }

    // Método para mostrar el modal de edición de una orden
    private void mostrarModalEditarOrden(Map<String, String> orden) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_orden);

        // Inicializar las vistas del modal
        EditText etFechaEditar = dialog.findViewById(R.id.etFechaOrdenEditar);
        EditText etEstadoEditar = dialog.findViewById(R.id.etEstadoOrdenEditar);
        EditText etTotalEditar = dialog.findViewById(R.id.etTotalOrdenEditar);
        Spinner spinnerClienteEditar = dialog.findViewById(R.id.spinnerClienteEditar);
        Spinner spinnerRepartidorEditar = dialog.findViewById(R.id.spinnerRepartidorEditar);

        // Cargar los datos actuales de la orden en el modal
        etFechaEditar.setText(orden.get("fecha"));
        etEstadoEditar.setText(orden.get("estado"));
        etTotalEditar.setText(orden.get("total"));

        // Cargar clientes y repartidores en los Spinners
        cargarClientesEditar(spinnerClienteEditar, orden.get("idCliente"));
        cargarRepartidoresEditar(spinnerRepartidorEditar, orden.get("idRepartidor"));

        // Botón Cancelar
        Button btnCancelarEditar = dialog.findViewById(R.id.btnCancelarOrdenEditar);
        btnCancelarEditar.setOnClickListener(v -> dialog.dismiss());

        // Botón Guardar
        Button btnGuardarEditar = dialog.findViewById(R.id.btnGuardarOrdenEditar);
        btnGuardarEditar.setOnClickListener(v -> {
            String nuevaFecha = etFechaEditar.getText().toString();
            String nuevoEstado = etEstadoEditar.getText().toString();
            String nuevoTotalStr = etTotalEditar.getText().toString();

            if (!nuevaFecha.isEmpty() && !nuevoEstado.isEmpty() && !nuevoTotalStr.isEmpty()) {
                double nuevoTotal = Double.parseDouble(nuevoTotalStr);
                int idOrden = Integer.parseInt(orden.get("id"));

                // Llamar a la capa de negocio para actualizar la orden
                nOrden.actualizarOrden(idOrden, nuevaFecha, nuevoEstado, nuevoTotal, idClienteSeleccionado, idRepartidorSeleccionado);
                Toast.makeText(POrden.this, "Orden actualizada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                listarOrdenes(); // Refrescar la lista de órdenes
            } else {
                Toast.makeText(POrden.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        // Aquí ajustamos el tamaño del modal
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    // Método para cargar clientes en el spinner de edición
    private void cargarClientesEditar(Spinner spinner, String idClienteActual) {
        List<Map<String, String>> clientes = nCliente.obtenerClientes();
        List<String> nombresClientes = new ArrayList<>();
        List<Integer> idsClientes = new ArrayList<>();

        for (Map<String, String> cliente : clientes) {
            nombresClientes.add(cliente.get("nombre"));
            idsClientes.add(Integer.parseInt(cliente.get("id")));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresClientes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Seleccionar el cliente actual en el Spinner
        int clientePosition = idsClientes.indexOf(Integer.parseInt(idClienteActual));
        if (clientePosition >= 0) {
            spinner.setSelection(clientePosition);
        }

        // Almacenar la nueva selección del cliente
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idClienteSeleccionado = idsClientes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    // Método para cargar repartidores en el spinner de edición
    private void cargarRepartidoresEditar(Spinner spinner, String idRepartidorActual) {
        List<Map<String, String>> repartidores = nRepartidor.obtenerRepartidores();
        List<String> nombresRepartidores = new ArrayList<>();
        List<Integer> idsRepartidores = new ArrayList<>();

        for (Map<String, String> repartidor : repartidores) {
            nombresRepartidores.add(repartidor.get("nombre"));
            idsRepartidores.add(Integer.parseInt(repartidor.get("id")));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresRepartidores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Seleccionar el repartidor actual en el Spinner
        int repartidorPosition = idsRepartidores.indexOf(Integer.parseInt(idRepartidorActual));
        if (repartidorPosition >= 0) {
            spinner.setSelection(repartidorPosition);
        }

        // Almacenar la nueva selección del repartidor
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idRepartidorSeleccionado = idsRepartidores.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    // Método para manejar la eliminación de una orden
    private void eliminarOrdenConConfirmacion(String idOrden) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_eliminar);

        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        tvTitulo.setText("Eliminar Orden");
        tvMensaje.setText("¿Estás seguro de que quieres eliminar esta orden?");

        Button btnSi = dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(v -> {
            nOrden.eliminarOrden(Integer.parseInt(idOrden));
            Toast.makeText(this, "Orden eliminada", Toast.LENGTH_SHORT).show();
            listarOrdenes();
            dialog.dismiss();
        });

        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Método para manejar el botón de agregar productos a la orden
    private void agregarProductoAOrden(Map<String, String> orden) {
        // Aquí puedes implementar la lógica para agregar productos a una orden
        // Podrías abrir un modal o una nueva pantalla para seleccionar productos y agregarlos a la orden
        Toast.makeText(this, "Función de agregar productos no implementada aún", Toast.LENGTH_SHORT).show();
    }

    // Método para ver detalles de la orden
    private void verDetallesOrden(Map<String, String> orden) {
        // Aquí puedes implementar la lógica para ver los detalles de la orden
        Toast.makeText(this, "Detalles de la orden: " + orden.get("id"), Toast.LENGTH_SHORT).show();
    }

    // Método para crear los botones adicionales y asignarles funcionalidad
    private void manejarBotonesAdicionales(View ordenView, Map<String, String> orden) {
        Button btnEditarOrden = ordenView.findViewById(R.id.btnEditarOrden);
        Button btnEliminarOrden = ordenView.findViewById(R.id.btnEliminarOrden);
        Button btnVerDetallesOrden = ordenView.findViewById(R.id.btnVerDetallesOrden);
        Button btnAgregarProducto = ordenView.findViewById(R.id.btnAnadirProductoOrden); // Puedes cambiar el nombre a btnAgregarProducto si es más claro

        // Asignar acciones a los botones
        btnEditarOrden.setOnClickListener(v -> mostrarModalEditarOrden(orden));
        btnEliminarOrden.setOnClickListener(v -> eliminarOrdenConConfirmacion(orden.get("id")));
        btnVerDetallesOrden.setOnClickListener(v -> verDetallesOrden(orden));
        btnAgregarProducto.setOnClickListener(v -> agregarProductoAOrden(orden));
    }

}
