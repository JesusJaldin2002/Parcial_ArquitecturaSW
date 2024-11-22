package com.example.primerparcial.presentacion.orden;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.primerparcial.MainActivity;
import com.example.primerparcial.R;
import com.example.primerparcial.negocio.categoria.NCategoria;
import com.example.primerparcial.negocio.cliente.NCliente;
import com.example.primerparcial.negocio.orden.NOrden;
import com.example.primerparcial.negocio.producto.NProducto;
import com.example.primerparcial.negocio.repartidor.NRepartidor;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class POrden extends AppCompatActivity {

    private NOrden nOrden;
    private NCliente nCliente;
    private NProducto nProducto;
    private NCategoria nCategoria;
    private NRepartidor nRepartidor;

    private EditText etFechaOrden, etCantidad, etPrecio;
    private Spinner spinnerCliente, spinnerCategoria, spinnerProducto, spinnerRepartidor;
    private ImageView ivImagenProducto;
    private int idClienteSeleccionado;
    private View gestionarOrdenView, listarOrdenesView, anadirProductoView, actualizarEstadoView;
    private int idOrdenSeleccionada;
    private List<Map<String, String>> productosDisponibles;
    private List<Map<String, String>> repartidoresDisponibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestionarOrdenView = getLayoutInflater().inflate(R.layout.activity_gestionar_orden, null);
        listarOrdenesView = getLayoutInflater().inflate(R.layout.lista_ordenes, null);
        anadirProductoView = getLayoutInflater().inflate(R.layout.anadir_producto, null);
        actualizarEstadoView = getLayoutInflater().inflate(R.layout.actualizar_estado_orden, null);

        setContentView(gestionarOrdenView);

        nOrden = new NOrden(this);
        nCliente = new NCliente(this);
        nProducto = new NProducto(this);
        nCategoria = new NCategoria(this);
        nRepartidor = new NRepartidor(this);

        etFechaOrden = findViewById(R.id.etFechaOrden);
        etFechaOrden.setOnClickListener(v -> showDatePickerDialog());
        spinnerCliente = findViewById(R.id.spinnerCliente);

        cargarClientes();

        Button btnRegistrar = findViewById(R.id.btnRegistrarOrden);
        btnRegistrar.setOnClickListener(v -> registrarOrden());

        Button btnListarOrdenes = findViewById(R.id.btnListarOrdenes);
        btnListarOrdenes.setOnClickListener(v -> listarOrdenes());

        Button btnVolver = findViewById(R.id.btnCancelarOrden);
        btnVolver.setOnClickListener(v -> startActivity(new Intent(POrden.this, MainActivity.class)));
    }

    // Manejo de las fechas
    private void showDatePickerDialog() {
        final Calendar calendario = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> etFechaOrden.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year)),
                calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Métodos para manejar el registro y la visualización de órdenes
    private void registrarOrden() {
        String fecha = etFechaOrden.getText().toString();
        String estado = "Pendiente";
        double totalOrden = 0.0;

        if (!fecha.isEmpty() && isValidDate(fecha)) {
            nOrden.registrarOrden(fecha, estado, totalOrden, idClienteSeleccionado, -1);
            Toast.makeText(this, "Orden registrada", Toast.LENGTH_SHORT).show();
            etFechaOrden.setText("");
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarOrdenes() {
        setContentView(listarOrdenesView);
        LinearLayout listaOrdenesLayout = findViewById(R.id.listaOrdenesLayout);
        listaOrdenesLayout.removeAllViews();
        List<Map<String, String>> ordenes = nOrden.obtenerOrdenes();

        for (Map<String, String> orden : ordenes) {
            View ordenView = getLayoutInflater().inflate(R.layout.item_orden, null);

            TextView idTextView = ordenView.findViewById(R.id.tvIdOrden);
            TextView fechaTextView = ordenView.findViewById(R.id.tvFechaOrden);
            TextView estadoTextView = ordenView.findViewById(R.id.tvEstadoOrden);
            TextView totalTextView = ordenView.findViewById(R.id.tvTotalOrden);
            TextView clienteTextView = ordenView.findViewById(R.id.tvClienteOrden);

            idTextView.setText("ID: " + orden.get("id"));
            fechaTextView.setText("Fecha: " + orden.get("fecha"));
            String estadoOrden = orden.get("estado");
            estadoTextView.setText("Estado: " + estadoOrden);

            if ("Pendiente".equals(estadoOrden)) {
                estadoTextView.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else if ("Completado".equals(estadoOrden)) {
                estadoTextView.setTextColor(ContextCompat.getColor(this, R.color.green));
            } else if ("Enviado".equals(estadoOrden)) {
                estadoTextView.setTextColor(ContextCompat.getColor(this, R.color.blue));
            } else {
                estadoTextView.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
            totalTextView.setText("Total: Bs " + orden.get("total"));

            int idCliente = Integer.parseInt(orden.get("idCliente"));
            String nombreCliente = nCliente.obtenerNombreClientePorId(idCliente);
            clienteTextView.setText("Cliente: " + nombreCliente);

            manejarBotonesAdicionales(ordenView, orden);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 32);
            ordenView.setLayoutParams(layoutParams);

            listaOrdenesLayout.addView(ordenView);
        }

        Button btnVolver = listarOrdenesView.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> setContentView(gestionarOrdenView));
    }

    // Manejo de los botones en las vistas de orden
    private void manejarBotonesAdicionales(View ordenView, Map<String, String> orden) {
        Button btnEditarOrden = ordenView.findViewById(R.id.btnEditarOrden);
        Button btnEliminarOrden = ordenView.findViewById(R.id.btnEliminarOrden);
        Button btnVerDetallesOrden = ordenView.findViewById(R.id.btnVerDetallesOrden);
        Button btnAgregarProducto = ordenView.findViewById(R.id.btnAnadirProductoOrden);
        Button btnActualizarEstado = ordenView.findViewById(R.id.btnActualizarEstado);

        btnEditarOrden.setOnClickListener(v -> mostrarModalEditarOrden(orden));
        btnEliminarOrden.setOnClickListener(v -> eliminarOrdenConConfirmacion(orden.get("id")));
        btnVerDetallesOrden.setOnClickListener(v -> verDetallesOrden(orden));
        btnAgregarProducto.setOnClickListener(v -> agregarProductoAOrden(orden));
        btnActualizarEstado.setOnClickListener(v -> mostrarActualizarEstadoView(orden.get("id")));
    }

    private void mostrarModalEditarOrden(Map<String, String> orden) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_orden);

        EditText etFechaEditar = dialog.findViewById(R.id.etFechaOrdenEditar);
        Spinner spinnerClienteEditar = dialog.findViewById(R.id.spinnerClienteEditar);

        etFechaEditar.setText(orden.get("fecha"));
        cargarClientesEditar(spinnerClienteEditar, orden.get("idCliente"));

        Button btnGuardarEditar = dialog.findViewById(R.id.btnGuardarOrdenEditar);
        btnGuardarEditar.setOnClickListener(v -> {
            String nuevaFecha = etFechaEditar.getText().toString();
            if (!nuevaFecha.isEmpty() && isValidDate(nuevaFecha)) {
                nOrden.actualizarOrden(Integer.parseInt(orden.get("id")), nuevaFecha, "Pendiente", 0.0, idClienteSeleccionado, Integer.parseInt(orden.get("idRepartidor")));
                Toast.makeText(POrden.this, "Orden actualizada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                listarOrdenes();
            } else {
                Toast.makeText(POrden.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void eliminarOrdenConConfirmacion(String idOrden) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_eliminar);
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

    // Métodos para agregar productos a una orden
    private void mostrarAnadirProductoView(int idOrden) {
        setContentView(anadirProductoView);
        idOrdenSeleccionada = idOrden;

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerProducto = findViewById(R.id.spinnerProducto);
        etCantidad = findViewById(R.id.etCantidad);
        etPrecio = findViewById(R.id.etPrecio);
        ivImagenProducto = findViewById(R.id.ivImagenProducto);

        // Cargar categorías en el spinner
        cargarCategorias();

        Button btnAnadirProducto = findViewById(R.id.btnAnadirProducto);
        btnAnadirProducto.setOnClickListener(v -> {
            String cantidadStr = etCantidad.getText().toString();
            String precioStr = etPrecio.getText().toString();

            if (cantidadStr.isEmpty() || precioStr.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            double precio = Double.parseDouble(precioStr);
            int posicionSeleccionada = spinnerProducto.getSelectedItemPosition();
            Map<String, String> productoSeleccionado = productosDisponibles.get(posicionSeleccionada);
            int idProducto = Integer.parseInt(productoSeleccionado.get("id"));
            int stockDisponible = Integer.parseInt(productoSeleccionado.get("stock"));

            // Validar que la cantidad no sea mayor al stock disponible
            if (cantidad > stockDisponible) {
                Toast.makeText(this, "La cantidad no puede ser mayor al stock disponible", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que la cantidad no sea 0
            if (cantidad == 0) {
                Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar si el producto ya existe en la orden
            if (nOrden.verificarProductoEnOrden(idOrdenSeleccionada, idProducto)) {
                Toast.makeText(this, "El producto ya está en la orden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insertar el detalle de la orden
            nOrden.registrarDetalleOrden(cantidad, precio, idOrdenSeleccionada, idProducto);

            // Reducir el stock del producto
            int nuevoStock = stockDisponible - cantidad;
            nProducto.actualizarStock(idProducto, nuevoStock);

            // Actualizar la lista de productos en el spinner
            cargarProductosPorCategoria(spinnerCategoria.getSelectedItem().toString());

            Toast.makeText(this, "Producto añadido a la orden", Toast.LENGTH_SHORT).show();

            // Limpiar los campos para permitir agregar otro producto
            etCantidad.setText("");
            etPrecio.setText("");
            ivImagenProducto.setImageBitmap(null);
            spinnerCategoria.setSelection(0);
        });


        Button btnVolverAtras = findViewById(R.id.btnVolverAtras);
        btnVolverAtras.setOnClickListener(v -> listarOrdenes());
    }

    private void cargarCategorias() {
        List<Map<String, String>> categorias = nCategoria.obtenerCategorias();
        List<String> nombresCategorias = new ArrayList<>();
        for (Map<String, String> categoria : categorias) {
            nombresCategorias.add(categoria.get("nombre"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarProductosPorCategoria(nombresCategorias.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void cargarProductosPorCategoria(String categoriaSeleccionada) {
        Map<String, List<Map<String, String>>> productosPorCategoria = nProducto.obtenerProductosPorCategoria();
        productosDisponibles = productosPorCategoria.get(categoriaSeleccionada);  // Guardar productos disponibles

        // Crear una lista personalizada con nombre y precio
        List<String> productosDisplay = new ArrayList<>();
        for (Map<String, String> producto : productosDisponibles) {
            String nombreProducto = producto.get("nombre");
            String precioProducto = producto.get("precio");
            productosDisplay.add(nombreProducto + " - " + precioProducto + " Bs");
        }

        // Crear un ArrayAdapter con la lista personalizada
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productosDisplay);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducto.setAdapter(adapter);

        // Manejar selección de producto para mostrar imagen, precio y stock
        spinnerProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> productoSeleccionado = productosDisponibles.get(position);  // Obtener el producto desde la lista

                // Mostrar el precio en el campo de precio
                etPrecio.setText(productoSeleccionado.get("precio"));

                // Mostrar el stock en el TextView
                TextView tvStockProducto = findViewById(R.id.tvStockProducto);
                tvStockProducto.setText("Stock disponible: " + productoSeleccionado.get("stock"));

                // Cargar y mostrar la imagen del producto
                String imagenPath = productoSeleccionado.get("imagenPath");
                try {
                    Uri imageUri = Uri.parse(imagenPath);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ivImagenProducto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    // Otros métodos relacionados con órdenes
    private void agregarProductoAOrden(Map<String, String> orden) {
        mostrarAnadirProductoView(Integer.parseInt(Objects.requireNonNull(orden.get("id"))));
    }

    private void verDetallesOrden(Map<String, String> orden) {
        Intent intent = new Intent(POrden.this, PDetalleOrden.class);
        intent.putExtra("idOrden", Integer.parseInt(orden.get("id")));
        startActivity(intent);
    }

    private void mostrarActualizarEstadoView(String idOrden) {
        setContentView(actualizarEstadoView);
        TextView tvestadoOrdenTitulo = findViewById(R.id.tvEstadoOrden);
        tvestadoOrdenTitulo.setText("Estado de la Orden #" + idOrden);

        spinnerRepartidor = findViewById(R.id.spinnerRepartidor);
        cargarRepartidores();

        // Obtener el botón para actualizar el estado
        Button btnActualizarEstado = findViewById(R.id.btnActualizarEstado);
        btnActualizarEstado.setOnClickListener(v -> actualizarEstadoOrden(idOrden));

        Button btnEnviarUbicacionRepartidor = findViewById(R.id.btnEnviarUbicacionRepartidor);
        btnEnviarUbicacionRepartidor.setOnClickListener(v -> enviarUbicacion(Integer.parseInt(idOrden)));

        // Botón para volver atrás
        Button btnVolverAtras = findViewById(R.id.btnVolverAtras);
        btnVolverAtras.setOnClickListener(v -> listarOrdenes());
    }

    private void actualizarEstadoOrden(String idOrden) {
        // Validar estado seleccionado
        RadioGroup radioGroupEstado = findViewById(R.id.radioGroupEstado);
        int estadoSeleccionadoId = radioGroupEstado.getCheckedRadioButtonId();
        String nuevoEstado = "";

        if (estadoSeleccionadoId == R.id.rbEnviado) {
            nuevoEstado = "Enviado";
        } else if (estadoSeleccionadoId == R.id.rbCompletado) {
            nuevoEstado = "Completado";
        } else {
            Toast.makeText(this, "Por favor, selecciona un estado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar lista de repartidores
        if (repartidoresDisponibles == null || repartidoresDisponibles.isEmpty()) {
            Toast.makeText(this, "No hay repartidores disponibles.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar repartidor seleccionado
        int posicionRepartidorSeleccionado = spinnerRepartidor.getSelectedItemPosition();
        if (posicionRepartidorSeleccionado < 0 || posicionRepartidorSeleccionado >= repartidoresDisponibles.size()) {
            Toast.makeText(this, "Por favor, selecciona un repartidor válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener repartidor seleccionado
        Map<String, String> repartidorSeleccionado = repartidoresDisponibles.get(posicionRepartidorSeleccionado);
        int idRepartidorSeleccionado = Integer.parseInt(repartidorSeleccionado.get("id"));

        // Actualizar estado y repartidor
        nOrden.actualizarEstadoYRepartidor(Integer.parseInt(idOrden), nuevoEstado, idRepartidorSeleccionado);

        Toast.makeText(this, "Estado de la orden actualizado", Toast.LENGTH_SHORT).show();

        // Volver a la lista de órdenes
        listarOrdenes();
    }

    private void cargarRepartidores() {
        repartidoresDisponibles = nRepartidor.obtenerRepartidores();

        if (repartidoresDisponibles == null || repartidoresDisponibles.isEmpty()) {
            Toast.makeText(this, "No hay repartidores disponibles.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> nombresRepartidores = new ArrayList<>();
        for (Map<String, String> repartidor : repartidoresDisponibles) {
            String nombreRepartidor = repartidor.get("nombre");
            String telefonoRepartidor = repartidor.get("nroTelefono");
            nombresRepartidores.add(nombreRepartidor + " - " + telefonoRepartidor);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresRepartidores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepartidor.setAdapter(adapter);
    }

    // Cargar clientes en el spinner
    private void cargarClientes() {
        List<Map<String, String>> clientes = nCliente.obtenerClientes();
        List<String> nombresClientes = new ArrayList<>();
        for (Map<String, String> cliente : clientes) {
            nombresClientes.add(cliente.get("nombre"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresClientes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(adapter);

        spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idClienteSeleccionado = Integer.parseInt(Objects.requireNonNull(clientes.get(position).get("id")));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

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

        int clientePosition = idsClientes.indexOf(Integer.parseInt(idClienteActual));
        if (clientePosition >= 0) {
            spinner.setSelection(clientePosition);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idClienteSeleccionado = idsClientes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void enviarUbicacion(int idOrden) {
        // Obtener los detalles de la orden, incluyendo el idCliente
        Map<String, String> datosOrden = nOrden.obtenerDatosOrden(idOrden); // Asegúrate que este método obtiene la orden completa
        String idCliente = datosOrden.get("idCliente");
        Log.d("idCliente", idCliente);

        if (idCliente != null && !idCliente.isEmpty()) {
            // Obtener los datos del cliente (nombre y teléfono del cliente)
            String datosCliente = nCliente.obtenerNombreClientePorId(Integer.parseInt(idCliente));

            // Obtener la ubicación del cliente (nombre de la ubicación, urlMapa y referencia)
            Map<String, String> ubicacionCliente = nCliente.obtenerUbicacionCliente(Integer.parseInt(idCliente));
            Log.d("ubicacionCliente", String.valueOf(ubicacionCliente));

            if (ubicacionCliente != null) {
                String nombreUbicacion = ubicacionCliente.get("nombre");  // Este es el nombre de la ubicación, no del cliente
                String referencia = ubicacionCliente.get("referencia");
                String urlMapa = ubicacionCliente.get("urlMapa");

                // Obtener el número del repartidor desde la vista
                int posicionRepartidorSeleccionado = spinnerRepartidor.getSelectedItemPosition();
                Map<String, String> repartidorSeleccionado = repartidoresDisponibles.get(posicionRepartidorSeleccionado);
                String numeroRepartidor = repartidorSeleccionado.get("nroTelefono");

                if (numeroRepartidor != null && !numeroRepartidor.isEmpty()) {
                    // Formatear el mensaje correctamente
                    String mensajeUbicacion = "Cliente: " + datosCliente + "\n" +  // Usamos el nombre del cliente, no de la ubicación
                            "Referencia: " + referencia + "\n" +
                            "Ubicación: " + urlMapa;

                    // Enviar el mensaje por WhatsApp
                    enviarUbicacionWhatsApp(mensajeUbicacion, numeroRepartidor);
                } else {
                    Toast.makeText(this, "Número del repartidor no disponible.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No se encontró la ubicación del cliente.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se encontró el cliente para esta orden.", Toast.LENGTH_SHORT).show();
        }
    }



    private void enviarUbicacionWhatsApp(String mensaje, String numeroRepartidor) {
        if (numeroRepartidor != null && !numeroRepartidor.isEmpty()) {
            String numeroFormateado = "591" + numeroRepartidor.replace(" ", "").replace("-", "");

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
            sendIntent.putExtra("jid", numeroFormateado + "@s.whatsapp.net");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            sendIntent.setPackage("com.whatsapp");

            try {
                startActivity(sendIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "WhatsApp no está instalado en este dispositivo", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Número de teléfono del repartidor no disponible.", Toast.LENGTH_SHORT).show();
        }
    }


}
