package com.example.primerparcial.presentacion.categoria;

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
import com.example.primerparcial.negocio.categoria.NCategoria;

import java.util.List;
import java.util.Map;

public class PCategoria extends AppCompatActivity {

    private NCategoria nCategoria;
    private EditText etNombreCategoria;
    private View gestionarCategoriaView;
    private View listarCategoriasView;

    // Variables para guardar categoría y diálogo que están siendo editados
    private Map<String, String> categoriaEnEdicion;
    private Dialog dialogEnEdicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar ambos layouts
        gestionarCategoriaView = getLayoutInflater().inflate(R.layout.activity_gestionar_categoria, null);
        listarCategoriasView = getLayoutInflater().inflate(R.layout.lista_categorias, null);

        // Inicialmente, mostrar la vista de gestionar categorías
        setContentView(gestionarCategoriaView);

        nCategoria = new NCategoria(this);

        etNombreCategoria = findViewById(R.id.etNombreCategoria);

        Button btnRegistrar = findViewById(R.id.btnRegistrarCategoria);
        btnRegistrar.setOnClickListener(v -> registrarCategoria());

        Button btnListarCategorias = findViewById(R.id.btnListarCategorias);
        btnListarCategorias.setOnClickListener(v -> listarCategorias());

        Button btnVolver = findViewById(R.id.btnCancelarCategoria);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(PCategoria.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Método para registrar una categoría
    private void registrarCategoria() {
        String nombre = etNombreCategoria.getText().toString();

        if (!nombre.isEmpty()) {
            nCategoria.registrarCategoria(nombre);
            Toast.makeText(this, "Categoría registrada", Toast.LENGTH_SHORT).show();

            // Limpiar los campos después de registrar
            etNombreCategoria.setText("");

        } else {
            Toast.makeText(this, "Por favor, complete el campo de nombre", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarCategorias() {
        setContentView(listarCategoriasView);

        LinearLayout listaCategoriasLayout = findViewById(R.id.listaCategoriasLayout);
        listaCategoriasLayout.removeAllViews();

        // Obtenemos la lista de categorías desde la capa de negocio
        List<Map<String, String>> categorias = nCategoria.obtenerCategorias();

        for (Map<String, String> categoria : categorias) {
            View categoriaView = getLayoutInflater().inflate(R.layout.categoria_item, null);

            // Asigna los valores a las vistas
            TextView idTextView = categoriaView.findViewById(R.id.tvIdCategoria);
            TextView nombreTextView = categoriaView.findViewById(R.id.tvNombreCategoria);

            idTextView.setText("ID: " + categoria.get("id"));
            nombreTextView.setText("Nombre: " + categoria.get("nombre"));

            // Botón Editar Categoría
            Button btnEditarCategoria = categoriaView.findViewById(R.id.btnEditarCategoria);
            btnEditarCategoria.setOnClickListener(v -> mostrarModalEditarCategoria(categoria));

            // Botón Eliminar Categoría
            Button btnEliminarCategoria = categoriaView.findViewById(R.id.btnEliminarCategoria);
            btnEliminarCategoria.setOnClickListener(v -> {
                // Mostrar confirmación antes de eliminar
                eliminarCategoriaConConfirmacion(categoria.get("id"));
            });

            // Agregar la vista de categoría a la lista
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 32);
            categoriaView.setLayoutParams(layoutParams);

            listaCategoriasLayout.addView(categoriaView);
        }

        Button btnVolver = listarCategoriasView.findViewById(R.id.btnVolverCategoria);
        btnVolver.setOnClickListener(v -> setContentView(gestionarCategoriaView));
    }

    // Método para mostrar confirmación de eliminación
    private void eliminarCategoriaConConfirmacion(String categoriaId) {
        // Crear el diálogo manualmente
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_eliminar);

        // Configurar el título y el mensaje del diálogo
        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        tvTitulo.setText("Eliminar Categoría");
        tvMensaje.setText("¿Estás seguro de que quieres eliminar esta categoría?");

        // Botón Sí para confirmar la eliminación
        Button btnSi = dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(v -> {
            nCategoria.eliminarCategoria(categoriaId);
            Toast.makeText(this, "Categoría eliminada", Toast.LENGTH_SHORT).show();
            listarCategorias();
            dialog.dismiss();
        });

        // Botón No para cancelar
        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarModalEditarCategoria(Map<String, String> categoria) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_categoria);

        // Inicializar las vistas del modal
        EditText etNombreEditar = dialog.findViewById(R.id.etNombreCategoriaEditar);

        etNombreEditar.setText(categoria.get("nombre"));

        // Botón Cancelar
        Button btnCancelarEditar = dialog.findViewById(R.id.btnCancelarCategoriaEditar);
        btnCancelarEditar.setOnClickListener(v -> dialog.dismiss());

        // Botón Guardar
        Button btnGuardarEditar = dialog.findViewById(R.id.btnGuardarCategoriaEditar);
        btnGuardarEditar.setOnClickListener(v -> {
            String nuevoNombre = etNombreEditar.getText().toString();

            if (!nuevoNombre.isEmpty()) {
                // Llamar a la capa de negocio para actualizar
                nCategoria.actualizarCategoria(categoria.get("id"), nuevoNombre);
                Toast.makeText(PCategoria.this, "Categoría actualizada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                listarCategorias(); // Refrescar la lista
            } else {
                Toast.makeText(PCategoria.this, "Por favor, complete el campo de nombre", Toast.LENGTH_SHORT).show();
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
