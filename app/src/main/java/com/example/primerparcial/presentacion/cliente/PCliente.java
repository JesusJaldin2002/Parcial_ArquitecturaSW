package com.example.primerparcial.presentacion.cliente;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.primerparcial.MainActivity;
import com.example.primerparcial.R;
import com.example.primerparcial.negocio.cliente.NCliente;
import com.example.primerparcial.presentacion.posicion.PPosicion;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PCliente extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private NCliente nCliente;
    private EditText etNombre, etNroTelefono;
    private ImageView imgCliente;
    private Uri imageUri;
    private String imagenSeleccionada;
    private View gestionarClienteView;
    private View listarClientesView;

    // Variables para guardar cliente y diálogo que están siendo editados
    private Map<String, String> clienteEnEdicion;
    private Dialog dialogEnEdicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar ambos layouts
        gestionarClienteView = getLayoutInflater().inflate(R.layout.activity_gestionar_cliente, null);
        listarClientesView = getLayoutInflater().inflate(R.layout.activity_lista_clientes, null);

        // Inicialmente, mostrar la vista de gestionar clientes
        setContentView(gestionarClienteView);

        nCliente = new NCliente(this);

        etNombre = findViewById(R.id.etNombre);
        etNroTelefono = findViewById(R.id.etNroTelefono);
        imgCliente = findViewById(R.id.imgCliente);

        imgCliente.setOnClickListener(v -> {
            if (checkPermission()) {
                abrirGaleria();
            } else {
                requestPermission();
            }
        });

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(v -> registrarCliente());

        Button btnListarClientes = findViewById(R.id.btnListarClientes);
        btnListarClientes.setOnClickListener(v -> listarClientes());

        Button btnVolver = findViewById(R.id.btnCancelar);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(PCliente.this, MainActivity.class);
            startActivity(intent);
        });
    }


    // Verificar si el permiso de lectura del almacenamiento está otorgado
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // Solicitar permiso de lectura del almacenamiento externo
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    // Manejo de la respuesta del permiso solicitado
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para abrir la galería y seleccionar una imagen
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Método para abrir la galería desde el modal de editar cliente
    private void abrirGaleriaParaEditar(Map<String, String> cliente, Dialog dialog) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

        // Guardar la referencia del cliente que se está editando
        clienteEnEdicion = cliente;
        dialogEnEdicion = dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                if (clienteEnEdicion != null && dialogEnEdicion != null) {
                    // Actualizar la imagen en el modal
                    ImageView imgClienteEditar = dialogEnEdicion.findViewById(R.id.imgClienteEditar);
                    imgClienteEditar.setImageBitmap(bitmap);

                    // Guardar la nueva ruta de la imagen en el cliente
                    clienteEnEdicion.put("imagenPath", imageUri.toString());
                } else {
                    // Si no estamos editando un cliente, usamos la imagen en el registro normal
                    imgCliente.setImageBitmap(bitmap);
                    imagenSeleccionada = imageUri.toString(); // Guardamos la URI de la imagen
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para registrar un cliente
    private void registrarCliente() {
        String nombre = etNombre.getText().toString();
        String nroTelefono = etNroTelefono.getText().toString();

        if (!nombre.isEmpty() && !nroTelefono.isEmpty() && imagenSeleccionada != null) {
            nCliente.registrarCliente(nombre, nroTelefono, imagenSeleccionada);
            Toast.makeText(this, "Cliente registrado", Toast.LENGTH_SHORT).show();

            // Limpiar los campos después de registrar
            etNombre.setText("");
            etNroTelefono.setText("");
            imgCliente.setImageResource(R.drawable.ic_add_photo);
            imagenSeleccionada = null;

        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarClientes() {
        setContentView(listarClientesView);

        LinearLayout listaClientesLayout = findViewById(R.id.listaClientesLayout);
        listaClientesLayout.removeAllViews();

        // Obtenemos la lista de clientes desde la capa de negocio
        List<Map<String, String>> clientes = nCliente.obtenerClientes();

        for (Map<String, String> cliente : clientes) {
            View clienteView = getLayoutInflater().inflate(R.layout.cliente_item, null);

            // Asigna los valores a las vistas
            TextView idTextView = clienteView.findViewById(R.id.tvIdCliente);
            TextView nombreTextView = clienteView.findViewById(R.id.tvNombreCliente);
            TextView telefonoTextView = clienteView.findViewById(R.id.tvTelefonoCliente);
            ImageView clienteImageView = clienteView.findViewById(R.id.imgCliente);

            idTextView.setText("ID: " + cliente.get("id"));
            nombreTextView.setText("Nombre: " + cliente.get("nombre"));
            telefonoTextView.setText("Teléfono: " + cliente.get("nroTelefono"));

            // Manejo de imagen
            try {
                Uri imageUri = Uri.parse(cliente.get("imagenPath"));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                clienteImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Botón Editar Cliente
            Button btnEditarCliente = clienteView.findViewById(R.id.btnEditarCliente);
            btnEditarCliente.setOnClickListener(v -> mostrarModalEditarCliente(cliente));

            // Botón Eliminar Cliente
            Button btnEliminarCliente = clienteView.findViewById(R.id.btnEliminarCliente);
            btnEliminarCliente.setOnClickListener(v -> {
                // Mostrar confirmación antes de eliminar
                eliminarClienteConConfirmacion(cliente.get("id"));
            });

            // Botón para ver las ubicaciones
            Button btnVerUbicaciones = clienteView.findViewById(R.id.btnVerUbicacion);
            btnVerUbicaciones.setOnClickListener(v -> {
                Intent intent = new Intent(PCliente.this, PPosicion.class);
                intent.putExtra("idCliente", Integer.parseInt(Objects.requireNonNull(cliente.get("id"))));
                intent.putExtra("action", "listar");  // Indicar que debe listar ubicaciones
                intent.putExtra("nombreCliente", cliente.get("nombre"));
                startActivity(intent);
            });

            // En el método listarClientes de PCliente.java, modifica el OnClickListener del botón "Agregar Ubicación"
            Button btnAgregarUbicacion = clienteView.findViewById(R.id.btnAgregarUbicacion);
            btnAgregarUbicacion.setOnClickListener(v -> {
                Intent intent = new Intent(PCliente.this, PPosicion.class);
                intent.putExtra("idCliente", Integer.parseInt(Objects.requireNonNull(cliente.get("id"))));
                intent.putExtra("action", "registrar");  // Indicar que debe registrar ubicación
                startActivity(intent);
            });

            // Agregar la vista de cliente a la lista
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 32);
            clienteView.setLayoutParams(layoutParams);

            listaClientesLayout.addView(clienteView);
        }

        Button btnVolver = listarClientesView.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> setContentView(gestionarClienteView));
    }

    // Método para mostrar confirmación de eliminación
    private void eliminarClienteConConfirmacion(String clienteId) {
        // Crear el diálogo manualmente
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_eliminar);

        // Configurar el título y el mensaje del diálogo
        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        tvTitulo.setText("Eliminar Cliente");
        tvMensaje.setText("¿Estás seguro de que quieres eliminar este cliente?");

        // Botón Sí para confirmar la eliminación
        Button btnSi = dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(v -> {
            nCliente.eliminarCliente(clienteId);
            Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show();
            listarClientes();
            dialog.dismiss();
        });

        // Botón No para cancelar
        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void mostrarModalEditarCliente(Map<String, String> cliente) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_cliente);

        // Inicializar las vistas del modal
        EditText etNombreEditar = dialog.findViewById(R.id.etNombreEditar);
        EditText etNroTelefonoEditar = dialog.findViewById(R.id.etNroTelefonoEditar);
        ImageView imgClienteEditar = dialog.findViewById(R.id.imgClienteEditar);

        etNombreEditar.setText(cliente.get("nombre"));
        etNroTelefonoEditar.setText(cliente.get("nroTelefono"));

        // Manejar la imagen
        try {
            Uri imageUri = Uri.parse(cliente.get("imagenPath"));
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            imgClienteEditar.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reemplazar la imagen si se hace clic en ella
        imgClienteEditar.setOnClickListener(v -> abrirGaleriaParaEditar(cliente, dialog));

        // Botón Cancelar
        Button btnCancelarEditar = dialog.findViewById(R.id.btnCancelarEditar);
        btnCancelarEditar.setOnClickListener(v -> dialog.dismiss());

        // Botón Guardar
        Button btnGuardarEditar = dialog.findViewById(R.id.btnGuardarEditar);
        btnGuardarEditar.setOnClickListener(v -> {
            String nuevoNombre = etNombreEditar.getText().toString();
            String nuevoTelefono = etNroTelefonoEditar.getText().toString();

            if (!nuevoNombre.isEmpty() && !nuevoTelefono.isEmpty()) {
                // Llamar a la capa de negocio para actualizar
                nCliente.actualizarCliente(cliente.get("id"), nuevoNombre, nuevoTelefono, cliente.get("imagenPath"));
                Toast.makeText(PCliente.this, "Cliente actualizado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                listarClientes(); // Refrescar la lista
            } else {
                Toast.makeText(PCliente.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        // Aquí ajustamos el tamaño del modal
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

}
