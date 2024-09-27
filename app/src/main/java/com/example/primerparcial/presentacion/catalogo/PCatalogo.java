package com.example.primerparcial.presentacion.catalogo;

import android.app.DatePickerDialog;
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
import com.example.primerparcial.negocio.catalogo.NCatalogo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PCatalogo extends AppCompatActivity {

    private NCatalogo nCatalogo;
    private EditText etNombreCatalogo, etFechaCatalogo, etDescripcionCatalogo;
    private View gestionarCatalogoView;
    private View listarCatalogosView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar ambos layouts
        gestionarCatalogoView = getLayoutInflater().inflate(R.layout.activity_gestionar_catalogo, null);
        listarCatalogosView = getLayoutInflater().inflate(R.layout.lista_catalogo, null);

        // Inicialmente, mostrar la vista de gestionar catálogos
        setContentView(gestionarCatalogoView);

        nCatalogo = new NCatalogo(this);

        etNombreCatalogo = findViewById(R.id.etNombreCatalogo);
        etFechaCatalogo = findViewById(R.id.etFechaCatalogo);
        etDescripcionCatalogo = findViewById(R.id.etDescripcionCatalogo);

        // Mostrar el selector de fecha al hacer clic en el campo de fecha
        etFechaCatalogo.setOnClickListener(v -> showDatePickerDialog());

        Button btnRegistrar = findViewById(R.id.btnRegistrarCatalogo);
        btnRegistrar.setOnClickListener(v -> registrarCatalogo());

        Button btnListarCatalogos = findViewById(R.id.btnListarCatalogos);
        btnListarCatalogos.setOnClickListener(v -> listarCatalogos());

        Button btnVolver = findViewById(R.id.btnCancelarCatalogo);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(PCatalogo.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Método para registrar un catálogo
    private void registrarCatalogo() {
        String nombre = etNombreCatalogo.getText().toString();
        String fecha = etFechaCatalogo.getText().toString();
        String descripcion = etDescripcionCatalogo.getText().toString();

        // Validar si la fecha es válida
        if (!isValidDate(fecha)) {
            Toast.makeText(this, "Por favor, ingrese una fecha válida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nombre.isEmpty() && !fecha.isEmpty() && !descripcion.isEmpty()) {
            nCatalogo.insertarCatalogo(nombre, fecha, descripcion);
            Toast.makeText(this, "Catálogo registrado", Toast.LENGTH_SHORT).show();

            // Limpiar los campos después de registrar
            etNombreCatalogo.setText("");
            etFechaCatalogo.setText("");
            etDescripcionCatalogo.setText("");
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para mostrar el selector de fecha
    private void showDatePickerDialog() {
        final Calendar calendario = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> etFechaCatalogo.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year)),
                calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Validar si la fecha ingresada es válida
    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Método para listar catálogos
    private void listarCatalogos() {
        setContentView(listarCatalogosView);

        LinearLayout listaCatalogosLayout = findViewById(R.id.listaCatalogosLayout);
        listaCatalogosLayout.removeAllViews();

        // Obtenemos la lista de catálogos desde la capa de negocio
        List<Map<String, String>> catalogos = nCatalogo.obtenerCatalogos();

        for (Map<String, String> catalogo : catalogos) {
            View catalogoView = getLayoutInflater().inflate(R.layout.item_catalogo, null);

            // Asignar los valores a las vistas
            TextView idTextView = catalogoView.findViewById(R.id.tvIdCatalogo);
            TextView nombreTextView = catalogoView.findViewById(R.id.tvNombreCatalogo);
            TextView fechaTextView = catalogoView.findViewById(R.id.tvFechaCatalogo);
            TextView descripcionTextView = catalogoView.findViewById(R.id.tvDescripcionCatalogo);

            idTextView.setText("ID: " + catalogo.get("id"));
            nombreTextView.setText("Nombre: " + catalogo.get("nombre"));
            fechaTextView.setText("Fecha: " + catalogo.get("fecha"));
            descripcionTextView.setText("Descripción: " + catalogo.get("descripcion"));

            // Botón Editar Catálogo
            Button btnEditarCatalogo = catalogoView.findViewById(R.id.btnEditarCatalogo);
            btnEditarCatalogo.setOnClickListener(v -> mostrarModalEditarCatalogo(catalogo));

            // Botón Eliminar Catálogo
            Button btnEliminarCatalogo = catalogoView.findViewById(R.id.btnEliminarCatalogo);
            btnEliminarCatalogo.setOnClickListener(v -> {
                // Mostrar confirmación antes de eliminar
                eliminarCatalogoConConfirmacion(catalogo.get("id"));
            });

            // Botón Ver Productos
            Button btnVerProductos = catalogoView.findViewById(R.id.btnVerProductos);
            btnVerProductos.setOnClickListener(v -> verProductosCatalogo(catalogo.get("id")));

            // Botón Añadir Productos
            Button btnAnadirProductos = catalogoView.findViewById(R.id.btnAnadirProductos);
            btnAnadirProductos.setOnClickListener(v -> anadirProductoCatalogo(catalogo.get("id")));

            // Agregar la vista del catálogo a la lista
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 32);
            catalogoView.setLayoutParams(layoutParams);

            listaCatalogosLayout.addView(catalogoView);
        }

        Button btnVolver = listarCatalogosView.findViewById(R.id.btnVolverCatalogo);
        btnVolver.setOnClickListener(v -> setContentView(gestionarCatalogoView));
    }

    // Método para mostrar confirmación de eliminación
    private void eliminarCatalogoConConfirmacion(String catalogoId) {
        // Crear el diálogo manualmente
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_eliminar);

        // Configurar el título y el mensaje del diálogo
        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        tvTitulo.setText("Eliminar Catálogo");
        tvMensaje.setText("¿Estás seguro de que quieres eliminar este catálogo?");

        // Botón Sí para confirmar la eliminación
        Button btnSi = dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(v -> {
            nCatalogo.eliminarCatalogo(catalogoId);
            Toast.makeText(this, "Catálogo eliminado", Toast.LENGTH_SHORT).show();
            listarCatalogos();
            dialog.dismiss();
        });

        // Botón No para cancelar
        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarModalEditarCatalogo(Map<String, String> catalogo) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_catalogo);

        // Inicializar las vistas del modal
        EditText etNombreEditar = dialog.findViewById(R.id.etNombreCatalogoEditar);
        EditText etFechaEditar = dialog.findViewById(R.id.etFechaCatalogoEditar);
        EditText etDescripcionEditar = dialog.findViewById(R.id.etDescripcionCatalogoEditar);

        etNombreEditar.setText(catalogo.get("nombre"));
        etFechaEditar.setText(catalogo.get("fecha"));
        etDescripcionEditar.setText(catalogo.get("descripcion"));

        // Botón Cancelar
        Button btnCancelarEditar = dialog.findViewById(R.id.btnCancelarCatalogoEditar);
        btnCancelarEditar.setOnClickListener(v -> dialog.dismiss());

        // Botón Guardar
        Button btnGuardarEditar = dialog.findViewById(R.id.btnGuardarCatalogoEditar);
        btnGuardarEditar.setOnClickListener(v -> {
            String nuevoNombre = etNombreEditar.getText().toString();
            String nuevaFecha = etFechaEditar.getText().toString();
            String nuevaDescripcion = etDescripcionEditar.getText().toString();

            if (!isValidDate(nuevaFecha)) {
                Toast.makeText(this, "Por favor, ingrese una fecha válida", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!nuevoNombre.isEmpty() && !nuevaFecha.isEmpty() && !nuevaDescripcion.isEmpty()) {
                // Llamar a la capa de negocio para actualizar
                nCatalogo.actualizarCatalogo(catalogo.get("id"), nuevoNombre, nuevaFecha, nuevaDescripcion);
                Toast.makeText(PCatalogo.this, "Catálogo actualizado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                listarCatalogos(); // Refrescar la lista
            } else {
                Toast.makeText(PCatalogo.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        // Ajustar el tamaño del modal
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    // Método para ver los productos asociados al catálogo
    private void verProductosCatalogo(String catalogoId) {
        // Implementación para ver productos del catálogo
        Toast.makeText(this, "Funcionalidad de ver productos no implementada aún", Toast.LENGTH_SHORT).show();
    }

    // Método para añadir productos al catálogo
    private void anadirProductoCatalogo(String catalogoId) {
        // Implementación para añadir productos al catálogo
        Toast.makeText(this, "Funcionalidad de añadir productos no implementada aún", Toast.LENGTH_SHORT).show();
    }
}
