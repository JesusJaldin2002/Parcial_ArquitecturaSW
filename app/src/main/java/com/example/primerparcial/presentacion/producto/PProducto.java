package com.example.primerparcial.presentacion.producto;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.primerparcial.MainActivity;
import com.example.primerparcial.R;
import com.example.primerparcial.negocio.categoria.NCategoria;
import com.example.primerparcial.negocio.producto.NProducto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PProducto extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private NProducto nProducto;
    private NCategoria nCategoria;
    private EditText etNombreProducto, etDescripcionProducto, etPrecioProducto, etStockProducto;
    private Spinner spinnerCategoria;
    private ImageView imgProducto;
    private String imagenSeleccionada;
    private int idCategoriaSeleccionada;
    private View gestionarProductoView;
    private View listarProductosView;

    // Variables para el modal de edición
    private Map<String, String> productoEnEdicion;
    private Dialog dialogEnEdicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar ambos layouts
        gestionarProductoView = getLayoutInflater().inflate(R.layout.activity_gestionar_producto, null);
        listarProductosView = getLayoutInflater().inflate(R.layout.lista_productos, null);

        // Inicialmente, mostrar la vista de gestionar productos
        setContentView(gestionarProductoView);

        nProducto = new NProducto(this);
        nCategoria = new NCategoria(this);

        etNombreProducto = findViewById(R.id.etNombreProducto);
        etDescripcionProducto = findViewById(R.id.etDescripcionProducto);
        etPrecioProducto = findViewById(R.id.etPrecioProducto);
        etStockProducto = findViewById(R.id.etStockProducto);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        imgProducto = findViewById(R.id.imgProducto);

        // Cargar las categorías en el Spinner usando ArrayAdapter
        cargarCategorias();

        imgProducto.setOnClickListener(v -> {
            if (checkPermission()) {
                abrirGaleria();
            } else {
                requestPermission();
            }
        });

        Button btnRegistrar = findViewById(R.id.btnRegistrarProducto);
        btnRegistrar.setOnClickListener(v -> registrarProducto());

        Button btnListarProductos = findViewById(R.id.btnListarProductos);
        btnListarProductos.setOnClickListener(v -> listarProductos());

        Button btnVolver = findViewById(R.id.btnCancelarProducto);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(PProducto.this, MainActivity.class);
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
// Método para abrir la galería y seleccionar una imagen
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  // Añadir este flag para obtener el permiso
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Obtener el Bitmap desde el InputStream
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // Asignar la imagen al ImageView
                imgProducto.setImageBitmap(bitmap);
                // Si estamos editando un producto, actualizamos la imagen en el modal
                if (dialogEnEdicion != null && productoEnEdicion != null) {
                    ImageView imgProductoEditar = dialogEnEdicion.findViewById(R.id.imgProductoEditar);
                    imgProductoEditar.setImageBitmap(bitmap);
                    // Actualizar el path de la imagen seleccionada
                    imagenSeleccionada = imageUri.toString();
                } else {
                    imagenSeleccionada = imageUri.toString();  // Actualizar la imagen seleccionada para la creación
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para registrar un producto
    private void registrarProducto() {
        String nombre = etNombreProducto.getText().toString();
        String descripcion = etDescripcionProducto.getText().toString();
        String precio = etPrecioProducto.getText().toString();
        String stock = etStockProducto.getText().toString();

        if (!nombre.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty() && !stock.isEmpty() && imagenSeleccionada != null) {
            nProducto.registrarProducto(nombre, descripcion, Double.parseDouble(precio), imagenSeleccionada, Integer.parseInt(stock), idCategoriaSeleccionada);
            Toast.makeText(this, "Producto registrado", Toast.LENGTH_SHORT).show();

            // Limpiar los campos
            etNombreProducto.setText("");
            etDescripcionProducto.setText("");
            etPrecioProducto.setText("");
            etStockProducto.setText("");
            imgProducto.setImageResource(R.drawable.ic_add_photo);
            imagenSeleccionada = null;

        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para cargar las categorías en el Spinner usando ArrayAdapter
    private void cargarCategorias() {
        List<Map<String, String>> categorias = nCategoria.obtenerCategorias();
        List<String> nombresCategorias = new ArrayList<>();
        for (Map<String, String> categoria : categorias) {
            nombresCategorias.add(categoria.get("nombre"));
        }

        // Usar un ArrayAdapter para mostrar las categorías en el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

        // Configurar la selección del Spinner
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idCategoriaSeleccionada = Integer.parseInt(Objects.requireNonNull(categorias.get(position).get("id")));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    // Método para listar productos agrupados por categorías
    private void listarProductos() {
        setContentView(listarProductosView);

        LinearLayout listaCategoriasLayout = findViewById(R.id.listaCategoriasLayout);
        listaCategoriasLayout.removeAllViews();

        // Obtener productos agrupados por categorías
        Map<String, List<Map<String, String>>> productosPorCategoria = nProducto.obtenerProductosPorCategoria();

        for (String categoria : productosPorCategoria.keySet()) {
            // Crear un TextView para el nombre de la categoría
            TextView categoriaTextView = new TextView(this);
            categoriaTextView.setText(categoria);
            categoriaTextView.setTextSize(18f);
            categoriaTextView.setTextColor(getResources().getColor(R.color.black));
            categoriaTextView.setTypeface(null, Typeface.BOLD);
            categoriaTextView.setPadding(0, 16, 0, 8);

            // Agregar la categoría al layout
            listaCategoriasLayout.addView(categoriaTextView);

            // Obtener los productos de la categoría actual
            List<Map<String, String>> productos = productosPorCategoria.get(categoria);

            for (Map<String, String> producto : productos) {
                // Inflar la vista del producto
                View productoView = getLayoutInflater().inflate(R.layout.producto_item, null);

                TextView idTextView = productoView.findViewById(R.id.tvIdProducto);
                TextView nombreTextView = productoView.findViewById(R.id.tvNombreProducto);
                TextView descripcionTextView = productoView.findViewById(R.id.tvDescripcionProducto);
                TextView stockTextView = productoView.findViewById(R.id.tvStockProducto);
                ImageView productoImageView = productoView.findViewById(R.id.imgProducto);
                TextView precioTextView = productoView.findViewById(R.id.tvPrecioProducto);

                // Asignar valores a las vistas del producto
                idTextView.setText("ID: " + producto.get("id"));
                nombreTextView.setText("Nombre: " + producto.get("nombre"));
                descripcionTextView.setText("Descripción: " + producto.get("descripcion"));
                stockTextView.setText("Stock: " + producto.get("stock"));
                precioTextView.setText("Precio: Bs " + producto.get("precio"));
                // Manejar la imagen del producto
                try {
                    Uri imageUri = Uri.parse(producto.get("imagenPath"));
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    productoImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Botón Editar
                Button btnEditarProducto = productoView.findViewById(R.id.btnEditarProducto);
                btnEditarProducto.setOnClickListener(v -> mostrarModalEditarProducto(producto));

                // Botón Eliminar
                Button btnEliminarProducto = productoView.findViewById(R.id.btnEliminarProducto);
                btnEliminarProducto.setOnClickListener(v -> eliminarProductoConConfirmacion(producto.get("id")));

                // Botón Ver Stock
                Button btnVerStockProducto = productoView.findViewById(R.id.btnVerStockProducto);
                btnVerStockProducto.setOnClickListener(v -> mostrarModalEditarStock(producto));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 0, 0, 32);
                productoView.setLayoutParams(layoutParams);

                // Agregar cada producto debajo de su respectiva categoría
                listaCategoriasLayout.addView(productoView);
            }

            // Agregar un separador entre categorías
            View separator = new View(this);
            LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
            );
            separatorParams.setMargins(0, 16, 0, 16);
            separator.setLayoutParams(separatorParams);
            separator.setBackgroundColor(getResources().getColor(R.color.secondary_color));

            listaCategoriasLayout.addView(separator);
        }

        // Botón para volver a la pantalla de gestionar productos
        Button btnVolver = listarProductosView.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> setContentView(gestionarProductoView));
    }


    // Código de editar producto
    private void mostrarModalEditarProducto(Map<String, String> producto) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_producto);

        // Inicializar las vistas del modal
        EditText etNombreEditar = dialog.findViewById(R.id.etNombreProductoEditar);
        EditText etDescripcionEditar = dialog.findViewById(R.id.etDescripcionProductoEditar);
        EditText etPrecioEditar = dialog.findViewById(R.id.etPrecioProductoEditar);
        Spinner spinnerCategoriaEditar = dialog.findViewById(R.id.spinnerCategoriaEditar);
        ImageView imgProductoEditar = dialog.findViewById(R.id.imgProductoEditar);

        // Cargar los datos actuales del producto
        etNombreEditar.setText(producto.get("nombre"));
        etDescripcionEditar.setText(producto.get("descripcion"));
        etPrecioEditar.setText(producto.get("precio"));

        // Manejar la imagen del producto
        try {
            Uri imageUri = Uri.parse(producto.get("imagenPath"));
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            imgProductoEditar.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reemplazar la imagen si se hace clic en ella
        imgProductoEditar.setOnClickListener(v -> abrirGaleriaParaEditar(producto, dialog));

        // Cargar las categorías en el Spinner de edición
        List<Map<String, String>> categorias = nCategoria.obtenerCategorias();
        List<String> nombresCategorias = new ArrayList<>();
        List<Integer> idsCategorias = new ArrayList<>(); // Para almacenar los IDs de las categorías

        for (Map<String, String> categoria : categorias) {
            nombresCategorias.add(categoria.get("nombre"));
            idsCategorias.add(Integer.parseInt(Objects.requireNonNull(categoria.get("id"))));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoriaEditar.setAdapter(adapter);

        String categoriaActual = producto.get("categoria");
        if (categoriaActual != null) {
            int spinnerPosition = adapter.getPosition(categoriaActual);
            spinnerCategoriaEditar.setSelection(spinnerPosition);
        }

        // Almacenar la categoría seleccionada
        final int[] idCategoriaSeleccionada = {idsCategorias.get(spinnerCategoriaEditar.getSelectedItemPosition())};

        spinnerCategoriaEditar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                idCategoriaSeleccionada[0] = idsCategorias.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No se selecciona nada
            }
        });

        // Botón Cancelar
        Button btnCancelarEditar = dialog.findViewById(R.id.btnCancelarEditarProducto);
        btnCancelarEditar.setOnClickListener(v -> dialog.dismiss());

        // Botón Guardar
        Button btnGuardarEditar = dialog.findViewById(R.id.btnGuardarEditarProducto);
        btnGuardarEditar.setOnClickListener(v -> {
            String nuevoNombre = etNombreEditar.getText().toString();
            String nuevaDescripcion = etDescripcionEditar.getText().toString();
            String nuevoPrecioStr = etPrecioEditar.getText().toString();

            // Verificar si la imagen fue cambiada
            String nuevoImagenPath = imagenSeleccionada != null ? imagenSeleccionada : producto.get("imagenPath");

            if (!nuevoNombre.isEmpty() && !nuevaDescripcion.isEmpty() && !nuevoPrecioStr.isEmpty()) {
                int idProducto = Integer.parseInt(producto.get("id"));
                double nuevoPrecio = Double.parseDouble(nuevoPrecioStr);

                // Usamos el ID de la categoría seleccionada
                nProducto.actualizarProducto(idProducto, nuevoNombre, nuevaDescripcion, nuevoPrecio, nuevoImagenPath, Integer.parseInt(producto.get("stock")), idCategoriaSeleccionada[0]);
                Toast.makeText(PProducto.this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                listarProductos();
            } else {
                Toast.makeText(PProducto.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    // Método para abrir galería para editar
    private void abrirGaleriaParaEditar(Map<String, String> producto, Dialog dialog) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

        // Guardar la referencia del producto que se está editando
        productoEnEdicion = producto;
        dialogEnEdicion = dialog;
    }

    // Método para eliminar producto con confirmación
    private void eliminarProductoConConfirmacion(String productoId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_eliminar);

        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        tvTitulo.setText("Eliminar Producto");
        tvMensaje.setText("¿Estás seguro de que quieres eliminar este producto?");

        Button btnSi = dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(v -> {
            nProducto.eliminarProducto(Integer.parseInt(productoId));
            Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
            listarProductos();
            dialog.dismiss();
        });

        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarModalEditarStock(Map<String, String> producto) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_stock);

        // Inicializar las vistas del modal
        EditText etNuevoStock = dialog.findViewById(R.id.etNuevoStock);
        Button btnGuardarStock = dialog.findViewById(R.id.btnGuardarStock);
        Button btnCancelarStock = dialog.findViewById(R.id.btnCancelarStock);

        // Mostrar el stock actual en el campo
        etNuevoStock.setText(producto.get("stock"));

        // Botón Guardar
        btnGuardarStock.setOnClickListener(v -> {
            String nuevoStockStr = etNuevoStock.getText().toString();
            if (!nuevoStockStr.isEmpty()) {
                int nuevoStock = Integer.parseInt(nuevoStockStr);
                int idProducto = Integer.parseInt(producto.get("id"));

                // Actualizar el stock en la base de datos
                nProducto.actualizarStock(idProducto, nuevoStock);
                Toast.makeText(PProducto.this, "Stock actualizado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                listarProductos(); // Refrescar la lista de productos
            } else {
                Toast.makeText(PProducto.this, "Por favor, ingrese un valor para el stock", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Cancelar
        btnCancelarStock.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        // Ajustar el tamaño del modal
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}
