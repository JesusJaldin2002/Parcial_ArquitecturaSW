package com.example.primerparcial.presentacion.catalogo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerparcial.MainActivity;
import com.example.primerparcial.R;
import com.example.primerparcial.negocio.catalogo.NCatalogo;
import com.example.primerparcial.negocio.catalogoProducto.NCatalogoProducto;
import com.example.primerparcial.negocio.categoria.NCategoria;
import com.example.primerparcial.negocio.producto.NProducto;
import com.example.primerparcial.presentacion.catalogoProducto.PCatalogoProducto;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PCatalogo extends AppCompatActivity {

    private NCatalogo nCatalogo;
    private NCategoria nCategoria;
    private NProducto nProducto;
    private NCatalogoProducto nCatalogoProducto;
    private View gestionarCatalogoView;
    private View listarCatalogosView;
    private View anadirProductoView;

    private EditText etNombreCatalogo, etFechaCatalogo, etDescripcionCatalogo;
    private Spinner spinnerCategoria, spinnerProducto;
    private TextView tvPrecioProducto, tvStockProducto;
    private EditText etNotaProducto;
    private ImageView ivImagenProducto;

    private List<Map<String, String>> productosDisponibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar los layouts
        gestionarCatalogoView = getLayoutInflater().inflate(R.layout.activity_gestionar_catalogo, null);
        listarCatalogosView = getLayoutInflater().inflate(R.layout.lista_catalogo, null);
        anadirProductoView = getLayoutInflater().inflate(R.layout.anadir_producto_catalogo, null);

        // Mostrar la vista de gestionar catálogos inicialmente
        setContentView(gestionarCatalogoView);

        // Inicializar las clases de negocio
        nCatalogo = new NCatalogo(this);
        nCategoria = new NCategoria(this);
        nProducto = new NProducto(this);
        nCatalogoProducto = new NCatalogoProducto(this);

        // Inicializar las vistas del layout de gestionar catálogos
        etNombreCatalogo = findViewById(R.id.etNombreCatalogo);
        etFechaCatalogo = findViewById(R.id.etFechaCatalogo);
        etDescripcionCatalogo = findViewById(R.id.etDescripcionCatalogo);

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

    private void registrarCatalogo() {
        String nombre = etNombreCatalogo.getText().toString();
        String fecha = etFechaCatalogo.getText().toString();
        String descripcion = etDescripcionCatalogo.getText().toString();

        if (!isValidDate(fecha)) {
            Toast.makeText(this, "Por favor, ingrese una fecha válida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nombre.isEmpty() && !fecha.isEmpty() && !descripcion.isEmpty()) {
            nCatalogo.insertarCatalogo(nombre, fecha, descripcion);
            Toast.makeText(this, "Catálogo registrado", Toast.LENGTH_SHORT).show();

            etNombreCatalogo.setText("");
            etFechaCatalogo.setText("");
            etDescripcionCatalogo.setText("");
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    // Mostrar selector de fecha
    private void showDatePickerDialog() {
        final Calendar calendario = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> etFechaCatalogo.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year)),
                calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Validar fecha
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

    // Listar catálogos
    private void listarCatalogos() {
        setContentView(listarCatalogosView);

        LinearLayout listaCatalogosLayout = findViewById(R.id.listaCatalogosLayout);
        listaCatalogosLayout.removeAllViews();

        List<Map<String, String>> catalogos = nCatalogo.obtenerCatalogos();

        for (Map<String, String> catalogo : catalogos) {
            View catalogoView = getLayoutInflater().inflate(R.layout.item_catalogo, null);

            TextView idTextView = catalogoView.findViewById(R.id.tvIdCatalogo);
            TextView nombreTextView = catalogoView.findViewById(R.id.tvNombreCatalogo);
            TextView fechaTextView = catalogoView.findViewById(R.id.tvFechaCatalogo);
            TextView descripcionTextView = catalogoView.findViewById(R.id.tvDescripcionCatalogo);

            idTextView.setText("ID: " + catalogo.get("id"));
            nombreTextView.setText("Nombre: " + catalogo.get("nombre"));
            fechaTextView.setText("Fecha: " + catalogo.get("fecha"));
            descripcionTextView.setText("Descripción: " + catalogo.get("descripcion"));

            Button btnAnadirProductos = catalogoView.findViewById(R.id.btnAnadirProductos);
            btnAnadirProductos.setOnClickListener(v -> mostrarAnadirProductoView(Integer.parseInt(catalogo.get("id"))));

            Button btnVerProductos = catalogoView.findViewById(R.id.btnVerProductos);
            btnVerProductos.setOnClickListener(v -> {
                int idCatalogo = Integer.parseInt(catalogo.get("id"));
                Intent intent = new Intent(PCatalogo.this, PCatalogoProducto.class);
                intent.putExtra("idCatalogo", idCatalogo);  // Pasar el ID del catálogo como extra
                startActivity(intent);  // Iniciar la actividad de PCatalogoProducto
            });

            // Botón para editar el catálogo
            Button btnEditarCatalogo = catalogoView.findViewById(R.id.btnEditarCatalogo);
            btnEditarCatalogo.setOnClickListener(v -> mostrarModalEditarCatalogo(catalogo));

            // Botón para eliminar el catálogo
            Button btnEliminarCatalogo = catalogoView.findViewById(R.id.btnEliminarCatalogo);
            btnEliminarCatalogo.setOnClickListener(v -> eliminarCatalogoConConfirmacion(catalogo.get("id")));


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

    // Mostrar la vista de añadir producto
    private void mostrarAnadirProductoView(int idCatalogo) {
        setContentView(anadirProductoView);

        // Inicializar los elementos de la vista de añadir producto
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerProducto = findViewById(R.id.spinnerProducto);
        tvStockProducto = findViewById(R.id.tvStockProducto);
        tvPrecioProducto = findViewById(R.id.tvPrecioProducto);
        etNotaProducto = findViewById(R.id.etNotaProducto);
        ivImagenProducto = findViewById(R.id.ivImagenProducto);

        // Cargar las categorías en el spinner
        cargarCategorias();

        Button btnAnadirProducto = findViewById(R.id.btnAnadirProducto);
        btnAnadirProducto.setOnClickListener(v -> {
            // Verificar que se haya seleccionado un producto
            if (spinnerProducto.getSelectedItemPosition() == -1) {
                Toast.makeText(this, "Por favor, seleccione un producto", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener el producto seleccionado
            int posicionSeleccionada = spinnerProducto.getSelectedItemPosition();
            Map<String, String> productoSeleccionado = productosDisponibles.get(posicionSeleccionada);
            int idProducto = Integer.parseInt(productoSeleccionado.get("id"));

            // Verificar si el producto ya está en el catálogo
            if (nCatalogoProducto.productoYaEnCatalogo(idCatalogo, idProducto)) {
                Toast.makeText(this, "El producto ya está en el catálogo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener la nota del producto
            String nota = etNotaProducto.getText().toString().trim();

            // Registrar el producto en el catálogo
            nCatalogoProducto.registrarProductoCatalogo(idCatalogo, idProducto, nota);

            // Mostrar mensaje de confirmación
            Toast.makeText(this, "Producto añadido al catálogo", Toast.LENGTH_SHORT).show();

            // Limpiar los campos para permitir agregar otro producto
            etNotaProducto.setText("");
            ivImagenProducto.setImageBitmap(null);
            spinnerCategoria.setSelection(0);
        });

        // Botón para volver atrás
        Button btnVolverAtras = findViewById(R.id.btnVolverAtras);
        btnVolverAtras.setOnClickListener(v -> listarCatalogos());
    }

    // Método para cargar las categorías en el spinner
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
                // No hacer nada
            }
        });
    }

    // Método para cargar los productos según la categoría seleccionada
    private void cargarProductosPorCategoria(String categoriaSeleccionada) {
        Map<String, List<Map<String, String>>> productosPorCategoria = nProducto.obtenerProductosPorCategoria();
        productosDisponibles = productosPorCategoria.get(categoriaSeleccionada);

        // Verificar si la categoría tiene productos disponibles
        if (productosDisponibles == null || productosDisponibles.isEmpty()) {
            Toast.makeText(this, "No hay productos disponibles en esta categoría", Toast.LENGTH_SHORT).show();
            spinnerProducto.setAdapter(null);
            return;
        }

        List<String> productosDisplay = new ArrayList<>();
        for (Map<String, String> producto : productosDisponibles) {
            String nombreProducto = producto.get("nombre");
            String precioProducto = producto.get("precio");
            productosDisplay.add(nombreProducto + " - " + precioProducto + " Bs");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productosDisplay);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducto.setAdapter(adapter);

        // Manejar la selección de producto
        spinnerProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> productoSeleccionado = productosDisponibles.get(position);

                if (productoSeleccionado != null) {
                    tvPrecioProducto.setText("Precio: " + productoSeleccionado.get("precio") + " Bs");
                    tvStockProducto.setText("Stock disponible: " + productoSeleccionado.get("stock"));

                    // Cargar y mostrar la imagen del producto
                    String imagenPath = productoSeleccionado.get("imagenPath");
                    if (imagenPath != null) {
                        try {
                            Uri imageUri = Uri.parse(imagenPath);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            ivImagenProducto.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    private void mostrarModalEditarCatalogo(Map<String, String> catalogo) {
        // Crear el diálogo manualmente
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_catalogo);

        // Inicializar las vistas del modal
        EditText etNombreEditar = dialog.findViewById(R.id.etNombreCatalogoEditar);
        EditText etFechaEditar = dialog.findViewById(R.id.etFechaCatalogoEditar);
        EditText etDescripcionEditar = dialog.findViewById(R.id.etDescripcionCatalogoEditar);

        // Poner los valores actuales del catálogo en los campos
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
            nCatalogo.eliminarCatalogo(catalogoId);  // Eliminar catálogo y productos asociados
            Toast.makeText(this, "Catálogo eliminado", Toast.LENGTH_SHORT).show();
            listarCatalogos(); // Refrescar la lista
            dialog.dismiss();
        });

        // Botón No para cancelar
        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
