package com.example.primerparcial.presentacion.catalogo;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.primerparcial.R;
import com.example.primerparcial.negocio.catalogo.NCatalogo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class PCatalogoProducto extends AppCompatActivity {

    private static final int CREATE_FILE = 1;

    private NCatalogo nCatalogo;
    private View listaCatalogoProductoView;
    private int idCatalogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaCatalogoProductoView = getLayoutInflater().inflate(R.layout.activity_catalogo_producto, null);
        setContentView(listaCatalogoProductoView);

        // Obtener el ID del catálogo desde el Intent
        idCatalogo = getIntent().getIntExtra("idCatalogo", -1);

        // Inicializar las clases de negocio
        nCatalogo = new NCatalogo(this);

        // Verificar si el ID del catálogo es válido
        if (idCatalogo != -1) {
            // Cargar los productos del catálogo
            cargarProductosPorCatalogo(idCatalogo);
        } else {
            Toast.makeText(this, "Error al obtener el catálogo", Toast.LENGTH_SHORT).show();
        }

        // Configurar los botones fijos
        Button btnGenerarPDF = findViewById(R.id.btnGenerarPDF);
        Button btnEnviarPDFWhatsapp = findViewById(R.id.btnEnviarPDFWhatsapp);
        Button btnVolverAtras = findViewById(R.id.btnVolverAtras);

        btnGenerarPDF.setOnClickListener(v -> generarPDF(idCatalogo));

        btnEnviarPDFWhatsapp.setOnClickListener(v -> {
            generarPDFYEnviarPorWhatsApp();
        });

        btnVolverAtras.setOnClickListener(v -> finish());
    }

    private void cargarProductosPorCatalogo(int idCatalogo) {
        LinearLayout listaCategoriasLayout = findViewById(R.id.listaCategoriasLayout);
        listaCategoriasLayout.removeAllViews();

        // Obtener productos del catálogo desde la capa de negocio
        Map<String, List<Map<String, String>>> productosPorCategoria = nCatalogo.obtenerProductosPorCategoriaCatalogo(idCatalogo);

        // Recorrer las categorías y sus productos
        for (String categoria : productosPorCategoria.keySet()) {
            // Crear un TextView para el nombre de la categoría
            TextView categoriaTextView = new TextView(this);
            categoriaTextView.setText(categoria);
            categoriaTextView.setTextSize(18f);
            categoriaTextView.setTextColor(getResources().getColor(R.color.black));
            categoriaTextView.setTypeface(null, android.graphics.Typeface.BOLD);
            categoriaTextView.setPadding(0, 16, 0, 8);

            // Agregar la categoría al layout
            listaCategoriasLayout.addView(categoriaTextView);

            // Obtener los productos de la categoría actual
            List<Map<String, String>> productos = productosPorCategoria.get(categoria);

            for (Map<String, String> producto : productos) {
                // Inflar la vista del producto
                View productoView = getLayoutInflater().inflate(R.layout.item_catalogo_producto, null);

                // Inicializar las vistas dentro de item_catalogo_producto
                TextView idTextView = productoView.findViewById(R.id.tvIdProducto);
                TextView nombreTextView = productoView.findViewById(R.id.tvNombreProducto);
                TextView descripcionTextView = productoView.findViewById(R.id.tvDescripcionProducto);
                TextView stockTextView = productoView.findViewById(R.id.tvStockProducto);
                TextView precioTextView = productoView.findViewById(R.id.tvPrecioProducto);
                ImageView productoImageView = productoView.findViewById(R.id.imgProducto);
                TextView notaTextView = productoView.findViewById(R.id.tvNotaProducto);
                String notaProducto = producto.get("nota");

                if (notaProducto != null && !notaProducto.isEmpty()) {
                    notaTextView.setText("Nota: " + notaProducto);
                } else {
                    notaTextView.setVisibility(View.GONE);  // Ocultar la nota si está vacía
                }

                // Asignar valores a las vistas del producto
                idTextView.setText("ID: " + producto.get("idProducto"));
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
                btnEditarProducto.setOnClickListener(v -> mostrarModalEditarNota(producto));

                // Botón Eliminar
                Button btnEliminarProducto = productoView.findViewById(R.id.btnEliminarProducto);
                btnEliminarProducto.setOnClickListener(v -> {
                    // Obtener el ID del producto
                    String idProducto = producto.get("idProducto");

                    // Llamar al método para mostrar el diálogo de confirmación
                    eliminarProductoConConfirmacion(idCatalogo, Integer.parseInt(idProducto));
                });

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
    }

    private void mostrarModalEditarNota(Map<String, String> producto) {
        // Crear el diálogo
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_catalogo_producto);

        // Inicializar las vistas del modal
        EditText etNotaEditar = dialog.findViewById(R.id.etNotaProductoEditar);
        etNotaEditar.setText(producto.get("nota"));

        // Botón Cancelar
        Button btnCancelarEditar = dialog.findViewById(R.id.btnCancelarNotaProducto);
        btnCancelarEditar.setOnClickListener(v -> dialog.dismiss());  // Cerrar el modal si se presiona "Cancelar"

        // Botón Guardar
        Button btnGuardarEditar = dialog.findViewById(R.id.btnGuardarNotaProducto);
        btnGuardarEditar.setOnClickListener(v -> {
            String nuevaNota = etNotaEditar.getText().toString();

            if (!nuevaNota.isEmpty()) {
                // Llamar a la capa de negocio para actualizar la nota del producto
                nCatalogo.actualizarNotaProducto(idCatalogo, Integer.parseInt(Objects.requireNonNull(producto.get("idProducto"))), nuevaNota);

                // Mostrar un mensaje de éxito
                Toast.makeText(PCatalogoProducto.this, "Nota del producto actualizada", Toast.LENGTH_SHORT).show();

                // Cerrar el modal y refrescar la lista
                dialog.dismiss();
                cargarProductosPorCatalogo(idCatalogo);  // Refrescar los productos del catálogo
            } else {
                Toast.makeText(PCatalogoProducto.this, "Por favor, ingrese una nueva nota", Toast.LENGTH_SHORT).show();
            }
        });

        // Mostrar el diálogo
        dialog.show();

        // Ajustar el tamaño del modal
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }


    private void generarPDF(int idCatalogo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 y superior, usar selector de archivos
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, "Catalogo_" + idCatalogo + ".pdf");
            startActivityForResult(intent, CREATE_FILE);
        } else {
            Toast.makeText(this, "Guardar en almacenamiento no soportado en esta versión. Utiliza Android 10 o superior.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                guardarPDFEnUri(uri);
            }
        }
    }

    // Método para guardar el PDF
    private void guardarPDFEnUri(Uri uri) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(20);
        titlePaint.setFakeBoldText(true);

        Paint subTitlePaint = new Paint();
        subTitlePaint.setTextSize(16);
        subTitlePaint.setFakeBoldText(true);

        Paint contentPaint = new Paint();
        contentPaint.setTextSize(14);

        int pageWidth = 595;
        int pageHeight = 842;

        // Definir márgenes
        int leftMargin = 40;
        int topMargin = 50;
        int bottomMargin = 50;

        // Página 1 (Tamaño A4: 595 x 842 puntos)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        final PdfDocument.Page[] page = {pdfDocument.startPage(pageInfo)};

        // Obtener el Canvas para dibujar
        final AtomicReference<Canvas>[] canvas = new AtomicReference[]{new AtomicReference<>(page[0].getCanvas())};

        // Definir posiciones iniciales (teniendo en cuenta los márgenes)
        int startX = leftMargin;
        final int[] startY = {topMargin};
        int lineHeight = 25; // Altura de cada línea

        // Método auxiliar para crear una nueva página cuando se alcance el final
        Runnable crearNuevaPagina = () -> {
            pdfDocument.finishPage(page[0]);
            PdfDocument.PageInfo newPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.getPages().size() + 1).create();
            page[0] = pdfDocument.startPage(newPageInfo);
            canvas[0].set(page[0].getCanvas());
            startY[0] = topMargin; // Reiniciar la posición Y para la nueva página
        };

        // Límite vertical para el contenido (después de este límite, se crea una nueva página)
        int limitY = pageHeight - bottomMargin;

        // Obtener detalles del catálogo
        Map<String, String> datosCatalogo = nCatalogo.obtenerDatosCatalogo(idCatalogo); // Implementa este método para obtener el nombre, fecha y descripción
        String nombreCatalogo = datosCatalogo.get("nombre");
        String fechaCatalogo = datosCatalogo.get("fecha");
        String descripcionCatalogo = datosCatalogo.get("descripcion");

        // Dibujar el nombre del catálogo
        canvas[0].get().drawText("CATÁLOGO: " + nombreCatalogo, startX, startY[0], titlePaint);
        startY[0] += lineHeight;

        // Dibujar fecha del catálogo
        canvas[0].get().drawText("Fecha: " + (fechaCatalogo != null ? fechaCatalogo : "No disponible"), startX, startY[0], contentPaint);
        startY[0] += lineHeight;

        // Dibujar la descripción del catálogo
        canvas[0].get().drawText("Descripción: " + (descripcionCatalogo != null ? descripcionCatalogo : "No disponible"), startX, startY[0], contentPaint);
        startY[0] += lineHeight * 2;

        canvas[0].get().drawText("Lista de Productos", startX, startY[0], subTitlePaint);
        startY[0] += lineHeight;

        // Obtener productos por categoría
        Map<String, List<Map<String, String>>> productosPorCategoria = nCatalogo.obtenerProductosPorCategoriaCatalogo(idCatalogo);

        for (String categoria : productosPorCategoria.keySet()) {
            // Verificar si hay suficiente espacio en la página actual
            if (startY[0] + lineHeight > limitY) {
                crearNuevaPagina.run();
            }

            // Dibujar el nombre de la categoría
            canvas[0].get().drawText(categoria, startX, startY[0], subTitlePaint);
            startY[0] += lineHeight;

            List<Map<String, String>> productos = productosPorCategoria.get(categoria);
            for (Map<String, String> producto : productos) {
                // Verificar si hay suficiente espacio en la página actual antes de agregar el producto
                if (startY[0] + (lineHeight * 5) > limitY) { // Cada producto ocupa aproximadamente 5 líneas
                    crearNuevaPagina.run();
                }

                // Dibujar los detalles de cada producto
                canvas[0].get().drawText("Producto: " + producto.get("nombre"), startX + 20, startY[0], contentPaint);
                startY[0] += lineHeight;
                canvas[0].get().drawText("Descripción: " + producto.get("descripcion"), startX + 20, startY[0], contentPaint);
                startY[0] += lineHeight;
                canvas[0].get().drawText("Precio: Bs " + producto.get("precio"), startX + 20, startY[0], contentPaint);
                startY[0] += lineHeight;
                canvas[0].get().drawText("Stock: " + producto.get("stock"), startX + 20, startY[0], contentPaint);

                // Añadir la nota del producto
                String notaProducto = producto.get("nota");
                if (notaProducto != null && !notaProducto.isEmpty()) {
                    startY[0] += lineHeight;
                    canvas[0].get().drawText("Nota: " + notaProducto, startX + 20, startY[0], contentPaint);
                }

                // Si el producto tiene una imagen, cargarla
                String imagenPath = producto.get("imagenPath");
                if (imagenPath != null && !imagenPath.isEmpty()) {
                    try {
                        Uri imageUri = Uri.parse(imagenPath);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                        canvas[0].get().drawBitmap(scaledBitmap, startX + 400, startY[0] - 60, paint); // Posiciona la imagen
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Añadir espacio entre productos
                startY[0] += lineHeight * 3;
            }

            // Añadir espacio entre categorías
            startY[0] += lineHeight * 2;
        }

        // Terminar la página actual
        pdfDocument.finishPage(page[0]);

        // Guardar el archivo PDF
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
            Toast.makeText(this, "PDF guardado con éxito", Toast.LENGTH_SHORT).show();

            abrirPDF(uri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar PDF", Toast.LENGTH_SHORT).show();
        }

        // Cerrar el documento PDF
        pdfDocument.close();
    }

    private void abrirPDF(Uri pdfUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir el archivo PDF", Toast.LENGTH_SHORT).show();
        }
    }


    private void generarPDFYEnviarPorWhatsApp() {
        // Crear un archivo temporal para el PDF
        File pdfFile = new File(getExternalFilesDir(null), "Catalogo_" + idCatalogo + ".pdf");

        // Obtener la URI del archivo utilizando FileProvider
        Uri pdfUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile);

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(20);
        titlePaint.setFakeBoldText(true);

        Paint subTitlePaint = new Paint();
        subTitlePaint.setTextSize(16);
        subTitlePaint.setFakeBoldText(true);

        Paint contentPaint = new Paint();
        contentPaint.setTextSize(14);

        int pageWidth = 595;
        int pageHeight = 842;

        // Definir márgenes
        int leftMargin = 40;
        int topMargin = 50;
        int bottomMargin = 50;

        // Página 1 (Tamaño A4: 595 x 842 puntos)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        final PdfDocument.Page[] page = {pdfDocument.startPage(pageInfo)};

        // Obtener el Canvas para dibujar
        final AtomicReference<Canvas>[] canvas = new AtomicReference[]{new AtomicReference<>(page[0].getCanvas())};

        // Definir posiciones iniciales (teniendo en cuenta los márgenes)
        int startX = leftMargin;
        final int[] startY = {topMargin};
        int lineHeight = 25; // Altura de cada línea

        // Método auxiliar para crear una nueva página cuando se alcance el final
        Runnable crearNuevaPagina = () -> {
            pdfDocument.finishPage(page[0]);
            PdfDocument.PageInfo newPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.getPages().size() + 1).create();
            page[0] = pdfDocument.startPage(newPageInfo);
            canvas[0].set(page[0].getCanvas());
            startY[0] = topMargin; // Reiniciar la posición Y para la nueva página
        };

        int limitY = pageHeight - bottomMargin;

        Map<String, String> datosCatalogo = nCatalogo.obtenerDatosCatalogo(idCatalogo); // Implementa este método para obtener el nombre, fecha y descripción
        String nombreCatalogo = datosCatalogo.get("nombre");
        String fechaCatalogo = datosCatalogo.get("fecha");
        String descripcionCatalogo = datosCatalogo.get("descripcion");

        // Dibujar el nombre del catálogo
        canvas[0].get().drawText("CATÁLOGO: " + nombreCatalogo, startX, startY[0], titlePaint);
        startY[0] += lineHeight;

        // Dibujar fecha del catálogo
        canvas[0].get().drawText("Fecha: " + (fechaCatalogo != null ? fechaCatalogo : "No disponible"), startX, startY[0], contentPaint);
        startY[0] += lineHeight;

        // Dibujar la descripción del catálogo
        canvas[0].get().drawText("Descripción: " + (descripcionCatalogo != null ? descripcionCatalogo : "No disponible"), startX, startY[0], contentPaint);
        startY[0] += lineHeight * 2;

        canvas[0].get().drawText("Lista de Productos", startX, startY[0], subTitlePaint);
        startY[0] += lineHeight;

        // Obtener productos por categoría
        Map<String, List<Map<String, String>>> productosPorCategoria = nCatalogo.obtenerProductosPorCategoriaCatalogo(idCatalogo);

        for (String categoria : productosPorCategoria.keySet()) {
            // Verificar si hay suficiente espacio en la página actual
            if (startY[0] + lineHeight > limitY) {
                crearNuevaPagina.run();
            }

            // Dibujar el nombre de la categoría
            canvas[0].get().drawText(categoria, startX, startY[0], subTitlePaint);
            startY[0] += lineHeight;

            List<Map<String, String>> productos = productosPorCategoria.get(categoria);
            for (Map<String, String> producto : productos) {
                // Verificar si hay suficiente espacio en la página actual antes de agregar el producto
                if (startY[0] + (lineHeight * 5) > limitY) { // Cada producto ocupa aproximadamente 5 líneas
                    crearNuevaPagina.run();
                }

                // Dibujar los detalles de cada producto
                canvas[0].get().drawText("Producto: " + producto.get("nombre"), startX + 20, startY[0], contentPaint);
                startY[0] += lineHeight;
                canvas[0].get().drawText("Descripción: " + producto.get("descripcion"), startX + 20, startY[0], contentPaint);
                startY[0] += lineHeight;
                canvas[0].get().drawText("Precio: Bs " + producto.get("precio"), startX + 20, startY[0], contentPaint);
                startY[0] += lineHeight;
                canvas[0].get().drawText("Stock: " + producto.get("stock"), startX + 20, startY[0], contentPaint);

                // Añadir la nota del producto
                String notaProducto = producto.get("nota");
                if (notaProducto != null && !notaProducto.isEmpty()) {
                    startY[0] += lineHeight;
                    canvas[0].get().drawText("Nota: " + notaProducto, startX + 20, startY[0], contentPaint);
                }

                // Si el producto tiene una imagen, cargarla
                String imagenPath = producto.get("imagenPath");
                if (imagenPath != null && !imagenPath.isEmpty()) {
                    try {
                        Uri imageUri = Uri.parse(imagenPath);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                        canvas[0].get().drawBitmap(scaledBitmap, startX + 400, startY[0] - 60, paint); // Posiciona la imagen
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Añadir espacio entre productos
                startY[0] += lineHeight * 3;
            }

            // Añadir espacio entre categorías
            startY[0] += lineHeight * 2;
        }

        // Terminar la página actual
        pdfDocument.finishPage(page[0]);

        // Guardar el archivo PDF temporal
        try {
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(outputStream);
            outputStream.close();

            // Después de generar el PDF, compartir por WhatsApp
            compartirPDFPorWhatsApp(pdfUri);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
        }

        // Cerrar el documento PDF
        pdfDocument.close();
    }

    private void compartirPDFPorWhatsApp(Uri pdfUri) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("application/pdf");
        sendIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        sendIntent.setPackage("com.whatsapp");

        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "WhatsApp no está instalado en este dispositivo", Toast.LENGTH_LONG).show();
        }
    }


    private void eliminarProductoConConfirmacion(int idCatalogo, int idProducto) {
        // Crear el diálogo manualmente
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_eliminar);

        // Configurar el título y el mensaje del diálogo
        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        tvTitulo.setText("Eliminar Producto");
        tvMensaje.setText("¿Estás seguro de que quieres eliminar este producto del catálogo?");

        // Botón Sí para confirmar la eliminación
        Button btnSi = dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(v -> {
            // Llamar a la capa de negocio para eliminar el producto del catálogo
            nCatalogo.eliminarProductoCatalogo(idCatalogo, idProducto);

            // Mostrar mensaje de éxito
            Toast.makeText(this, "Producto eliminado del catálogo", Toast.LENGTH_SHORT).show();

            // Refrescar la lista de productos del catálogo
            cargarProductosPorCatalogo(idCatalogo);

            // Cerrar el diálogo
            dialog.dismiss();
        });

        // Botón No para cancelar la acción
        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}
