package com.example.primerparcial.presentacion.detalleOrden;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerparcial.R;
import com.example.primerparcial.negocio.detalleOrden.NDetalleOrden;
import com.example.primerparcial.negocio.orden.NOrden;
import com.example.primerparcial.negocio.producto.NProducto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class PDetalleOrden extends AppCompatActivity {

    private static final int CREATE_FILE = 1;

    private NDetalleOrden nDetalleOrden;
    private NOrden nOrden;
    private NProducto nProducto;
    private LinearLayout listaProductosLayout;
    private TextView tvMontoTotal;
    private int idOrdenSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_orden);

        nDetalleOrden = new NDetalleOrden(this);
        nOrden = new NOrden(this);
        nProducto = new NProducto(this);

        // Recibir el id de la orden
        Intent intent = getIntent();
        idOrdenSeleccionada = intent.getIntExtra("idOrden", -1);

        if (idOrdenSeleccionada != -1) {
            mostrarDetalleOrden();
        }

        Button btnGenerarPDF = findViewById(R.id.btnGenerarPdf);
        btnGenerarPDF.setOnClickListener(v -> generarPDF());

        Button btnPdfWhatsApp = findViewById(R.id.btnPdfWhatsappCliente);
        btnPdfWhatsApp.setOnClickListener(v -> enviarPDFWhatsApp());

        Button btnVolverAtras = findViewById(R.id.btnVolverAtras);
        btnVolverAtras.setOnClickListener(v -> finish());
    }

    // Método para mostrar los detalles de la orden
    private void mostrarDetalleOrden() {
        TextView tvDetalleOrdenTitulo = findViewById(R.id.tvDetalleOrdenTitulo);
        tvDetalleOrdenTitulo.setText("Detalle de la Orden #" + idOrdenSeleccionada);

        // Mostrar el total de la orden
        double montoTotal = nOrden.obtenerTotalOrden(idOrdenSeleccionada);
        tvMontoTotal = findViewById(R.id.tvMontoTotalOrden);
        tvMontoTotal.setText("Monto Total: " + montoTotal + " Bs");

        // Cargar los productos de la orden
        listaProductosLayout = findViewById(R.id.listaProductosLayout);

        // Limpiar el layout de productos antes de volver a cargar
        listaProductosLayout.removeAllViews();

        List<Map<String, String>> detalles = nDetalleOrden.obtenerDetallesPorOrden(idOrdenSeleccionada);

        for (Map<String, String> detalle : detalles) {
            View itemView = getLayoutInflater().inflate(R.layout.item_detalle_orden, null);

            // Cargar datos en el item
            TextView tvIdProducto = itemView.findViewById(R.id.tvIdProducto);
            TextView tvNombreProducto = itemView.findViewById(R.id.tvNombreProducto);
            TextView tvCantidadProducto = itemView.findViewById(R.id.tvCantidadProducto);
            TextView tvPrecioProducto = itemView.findViewById(R.id.tvPrecioProducto);
            TextView tvMontoProducto = itemView.findViewById(R.id.tvMontoProducto);
            ImageView ivProducto = itemView.findViewById(R.id.ivProducto);
            Button btnEditarProducto = itemView.findViewById(R.id.btnEditarProducto);
            Button btnEliminarProducto = itemView.findViewById(R.id.btnEliminarProducto);

            tvIdProducto.setText("ID: " + detalle.get("idProducto"));
            tvNombreProducto.setText("Nombre: " + detalle.get("nombre"));
            tvCantidadProducto.setText("Cantidad: " + detalle.get("cantidad"));
            tvPrecioProducto.setText("Precio: " + detalle.get("precio") + " Bs");
            tvMontoProducto.setText("Monto: " + detalle.get("monto") + " Bs");

            // Cargar imagen del producto
            String imagenPath = detalle.get("imagenPath");
            if (imagenPath != null) {
                try {
                    Uri imageUri = Uri.parse(imagenPath);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ivProducto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Configurar botón Editar
            btnEditarProducto.setOnClickListener(v -> mostrarModalEditarCantidad(detalle, itemView));

            // Configurar botón Eliminar
            btnEliminarProducto.setOnClickListener(v -> {
                int idProducto = Integer.parseInt(detalle.get("idProducto"));
                int cantidad = Integer.parseInt(detalle.get("cantidad"));
                double totalDetalle = Double.parseDouble(detalle.get("monto"));

                // Desplegar el modal de confirmación
                mostrarConfirmacionEliminarProducto(idOrdenSeleccionada, idProducto, cantidad, totalDetalle, itemView);
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 32);
            itemView.setLayoutParams(layoutParams);

            listaProductosLayout.addView(itemView);
        }
    }

    // Método para mostrar el modal de edición de cantidad
    private void mostrarModalEditarCantidad(Map<String, String> detalleProducto, View itemView) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_editar_cantidad);

        // Inicializar las vistas del modal
        EditText etNuevaCantidad = dialog.findViewById(R.id.etNuevaCantidad);
        Button btnGuardarCantidad = dialog.findViewById(R.id.btnGuardarCantidad);
        Button btnCancelarCantidad = dialog.findViewById(R.id.btnCancelarCantidad);

        // Mostrar la cantidad actual en el campo
        etNuevaCantidad.setText(detalleProducto.get("cantidad"));

        // Botón Guardar
        btnGuardarCantidad.setOnClickListener(v -> {
            String nuevaCantidadStr = etNuevaCantidad.getText().toString();
            if (!nuevaCantidadStr.isEmpty()) {
                int nuevaCantidad = Integer.parseInt(nuevaCantidadStr);
                int idProducto = Integer.parseInt(detalleProducto.get("idProducto"));
                int cantidadActual = Integer.parseInt(detalleProducto.get("cantidad"));
                double precioProducto = Double.parseDouble(detalleProducto.get("precio"));

                // Verificar que la nueva cantidad no sea 0 o menor
                if (nuevaCantidad <= 0) {
                    Toast.makeText(PDetalleOrden.this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Obtener stock disponible del producto
                int stockDisponible = nProducto.obtenerStockProducto(idProducto);
                int diferenciaCantidad = nuevaCantidad - cantidadActual;

                // Si estamos aumentando la cantidad, verificar si el stock es suficiente
                if (diferenciaCantidad > 0 && diferenciaCantidad > stockDisponible) {
                    Toast.makeText(PDetalleOrden.this, "No hay suficiente stock disponible", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Actualizar la cantidad en la base de datos
                nDetalleOrden.actualizarCantidadDetalleOrden(
                        idOrdenSeleccionada,
                        idProducto,
                        nuevaCantidad,
                        cantidadActual,
                        precioProducto
                );

                // Actualizar la vista del item en la lista
                TextView tvCantidadProducto = itemView.findViewById(R.id.tvCantidadProducto);
                TextView tvMontoProducto = itemView.findViewById(R.id.tvMontoProducto);
                double nuevoMonto = nuevaCantidad * precioProducto;
                tvCantidadProducto.setText("Cantidad: " + nuevaCantidad);
                tvMontoProducto.setText("Monto: " + nuevoMonto + " Bs");

                // **Actualizar el Map con la nueva cantidad**
                detalleProducto.put("cantidad", String.valueOf(nuevaCantidad));
                detalleProducto.put("monto", String.valueOf(nuevoMonto));

                // Actualizar el total de la orden
                actualizarMontoTotal();
                mostrarDetalleOrden();

                Toast.makeText(PDetalleOrden.this, "Cantidad actualizada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(PDetalleOrden.this, "Por favor, ingrese un valor para la cantidad", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Cancelar
        btnCancelarCantidad.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        // Ajustar el tamaño del modal
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    // Método para mostrar el modal de confirmación antes de eliminar
    private void mostrarConfirmacionEliminarProducto(int idOrden, int idProducto, int cantidad, double totalDetalle, View itemView) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmar_eliminar);

        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        tvTitulo.setText("Eliminar Producto");
        tvMensaje.setText("¿Estás seguro de que quieres eliminar este producto de la orden?");

        Button btnSi = dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(v -> {
            // Eliminar el detalle de la orden y actualizar la vista
            nDetalleOrden.eliminarDetalleOrden(idOrden, idProducto, cantidad, totalDetalle);
            listaProductosLayout.removeView(itemView);
            actualizarMontoTotal();

            Toast.makeText(this, "Producto eliminado de la orden", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Método para actualizar el monto total de la orden
    private void actualizarMontoTotal() {
        double nuevoMontoTotal = nOrden.obtenerTotalOrden(idOrdenSeleccionada);
        tvMontoTotal.setText("Monto Total: " + nuevoMontoTotal + " Bs");
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

    // Generar el PDF, utilizando el método guardarPDFEnUri
    private void generarPDF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 y superior, usar selector de archivos
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, "Detalle_Orden_" + idOrdenSeleccionada + ".pdf");
            startActivityForResult(intent, CREATE_FILE);
        } else {
            Toast.makeText(this, "Guardar en almacenamiento no soportado en esta versión. Utiliza Android 10 o superior.", Toast.LENGTH_SHORT).show();
        }
    }

    // Guardar PDF en la ubicación seleccionada por el usuario (funciona en Android 10+)
    private void guardarPDFEnUri(Uri uri) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(16);
        titlePaint.setFakeBoldText(true);

        // Página 1 (Tamaño A4: 595 x 842 puntos)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Obtener el Canvas para dibujar
        Canvas canvas = page.getCanvas();

        // Definir márgenes
        int leftMargin = 30;
        int topMargin = 30;

        // Definir posiciones iniciales (teniendo en cuenta los márgenes)
        int startX = leftMargin;
        int startY = topMargin;
        int lineHeight = 20; // Altura de cada línea

        // Obtener los datos de la orden (incluyendo fecha y total)
        Map<String, String> datosOrden = nOrden.obtenerDatosOrden(idOrdenSeleccionada);
        String fechaOrden = datosOrden.get("fecha");
        String totalOrden = datosOrden.get("total");

        // Dibujar encabezado: Orden de compra y datos del cliente
        canvas.drawText("ORDEN DE COMPRA: #" + idOrdenSeleccionada, startX, startY, titlePaint);

        // Mostrar la fecha y total
        startY += lineHeight;
        canvas.drawText("Fecha: " + (fechaOrden != null ? fechaOrden : "No disponible"), startX, startY, paint);
        canvas.drawText("Total: " + (totalOrden != null ? totalOrden + " Bs" : "No disponible"), startX + 350, startY, paint);

        // Obtener los datos del cliente y mostrar
        startY += lineHeight;
        Map<String, String> datosCliente = nOrden.obtenerDatosCliente(idOrdenSeleccionada);
        if (datosCliente != null) {
            canvas.drawText("Cliente: " + datosCliente.get("nombre"), startX, startY, paint);
            startY += lineHeight;
            canvas.drawText("Teléfono: " + datosCliente.get("nroTelefono"), startX, startY, paint);

            // Si hay imagen del cliente
            String imagenPath = datosCliente.get("imagenPath");
            if (imagenPath != null && !imagenPath.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(imagenPath);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                    canvas.drawBitmap(scaledBitmap, startX + 450, startY - 60, paint);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            canvas.drawText("Cliente: Información no disponible", startX, startY, paint);
        }

        // Espacio para la lista de productos
        startY += 2 * lineHeight;
        canvas.drawText("Lista de Productos:", startX, startY, titlePaint);

        // Dibujar encabezados de la tabla
        startY += lineHeight;
        canvas.drawText("ID", startX, startY, paint);
        canvas.drawText("Nombre", startX + 50, startY, paint);
        canvas.drawText("Cantidad", startX + 200, startY, paint);
        canvas.drawText("Precio", startX + 300, startY, paint);
        canvas.drawText("Monto", startX + 400, startY, paint);
        canvas.drawText("Imagen", startX + 500, startY, paint);

        // Línea separadora
        startY += lineHeight;
        canvas.drawLine(startX, startY, 595 - leftMargin, startY, paint);
        startY += lineHeight;

        // Obtener detalles de los productos
        List<Map<String, String>> detalles = nDetalleOrden.obtenerDetallesPorOrden(idOrdenSeleccionada);
        if (detalles != null && !detalles.isEmpty()) {
            for (Map<String, String> detalle : detalles) {
                // Dibujar detalles del producto alineados con los encabezados
                canvas.drawText(detalle.get("idProducto"), startX, startY, paint);
                canvas.drawText(detalle.get("nombre"), startX + 50, startY, paint);
                canvas.drawText(detalle.get("cantidad"), startX + 200, startY, paint);
                canvas.drawText(detalle.get("precio") + " Bs", startX + 300, startY, paint);
                canvas.drawText(detalle.get("monto") + " Bs", startX + 400, startY, paint);

                // Cargar la imagen si está disponible
                String imagenPath = detalle.get("imagenPath");
                if (imagenPath != null && !imagenPath.isEmpty()) {
                    try {
                        Uri imageUri = Uri.parse(imagenPath);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
                        canvas.drawBitmap(scaledBitmap, startX + 500, startY - 25, paint);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Mover el cursor a la siguiente fila
                startY += lineHeight * 2;

                // Si la página está llena, crear una nueva
                if (startY > 800) {
                    pdfDocument.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    startY = topMargin;
                }
            }
        } else {
            canvas.drawText("No hay productos en esta orden.", startX, startY, paint);
        }

        // Terminar página actual
        pdfDocument.finishPage(page);

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

    // Método para abrir el PDF inmediatamente después de guardarlo
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

    // Método para generar el PDF y luego enviarlo por WhatsApp
    private void enviarPDFWhatsApp() {
        // Generar el PDF sin solicitar al usuario una ubicación y luego compartirlo por WhatsApp
        generarPDFParaWhatsApp();
    }

    // Método para generar el PDF y guardarlo temporalmente para enviar por WhatsApp
    private void generarPDFParaWhatsApp() {
        // Crear un archivo temporal para el PDF
        File pdfFile = new File(getExternalFilesDir(null), "Detalle_Orden_" + idOrdenSeleccionada + ".pdf");

        // Obtener la URI del archivo utilizando FileProvider
        Uri pdfUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile);

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(16);
        titlePaint.setFakeBoldText(true);

        // Página 1 (Tamaño A4: 595 x 842 puntos)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Obtener el Canvas para dibujar
        Canvas canvas = page.getCanvas();

        // Definir márgenes
        int leftMargin = 30;
        int topMargin = 30;

        // Definir posiciones iniciales (teniendo en cuenta los márgenes)
        int startX = leftMargin;
        int startY = topMargin;
        int lineHeight = 20; // Altura de cada línea

        // Obtener los datos de la orden (incluyendo fecha y total)
        Map<String, String> datosOrden = nOrden.obtenerDatosOrden(idOrdenSeleccionada);
        String fechaOrden = datosOrden.get("fecha");
        String totalOrden = datosOrden.get("total");

        // Dibujar encabezado: Orden de compra y datos del cliente
        canvas.drawText("ORDEN DE COMPRA: #" + idOrdenSeleccionada, startX, startY, titlePaint);

        // Mostrar la fecha y total
        startY += lineHeight;
        canvas.drawText("Fecha: " + (fechaOrden != null ? fechaOrden : "No disponible"), startX, startY, paint);
        canvas.drawText("Total: " + (totalOrden != null ? totalOrden + " Bs" : "No disponible"), startX + 350, startY, paint);

        // Obtener los datos del cliente y mostrar
        startY += lineHeight;
        Map<String, String> datosCliente = nOrden.obtenerDatosCliente(idOrdenSeleccionada);
        if (datosCliente != null) {
            canvas.drawText("Cliente: " + datosCliente.get("nombre"), startX, startY, paint);
            startY += lineHeight;
            canvas.drawText("Teléfono: " + datosCliente.get("nroTelefono"), startX, startY, paint);

            // Si hay imagen del cliente
            String imagenPath = datosCliente.get("imagenPath");
            if (imagenPath != null && !imagenPath.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(imagenPath);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                    canvas.drawBitmap(scaledBitmap, startX + 450, startY - 60, paint);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            canvas.drawText("Cliente: Información no disponible", startX, startY, paint);
        }

        // Espacio para la lista de productos
        startY += 2 * lineHeight;
        canvas.drawText("Lista de Productos:", startX, startY, titlePaint);

        // Dibujar encabezados de la tabla
        startY += lineHeight;
        canvas.drawText("ID", startX, startY, paint);
        canvas.drawText("Nombre", startX + 50, startY, paint);
        canvas.drawText("Cantidad", startX + 200, startY, paint);
        canvas.drawText("Precio", startX + 300, startY, paint);
        canvas.drawText("Monto", startX + 400, startY, paint);
        canvas.drawText("Imagen", startX + 500, startY, paint);

        // Línea separadora
        startY += lineHeight;
        canvas.drawLine(startX, startY, 595 - leftMargin, startY, paint);
        startY += lineHeight;

        // Obtener detalles de los productos
        List<Map<String, String>> detalles = nDetalleOrden.obtenerDetallesPorOrden(idOrdenSeleccionada);
        if (detalles != null && !detalles.isEmpty()) {
            for (Map<String, String> detalle : detalles) {
                // Dibujar detalles del producto alineados con los encabezados
                canvas.drawText(detalle.get("idProducto"), startX, startY, paint);
                canvas.drawText(detalle.get("nombre"), startX + 50, startY, paint);
                canvas.drawText(detalle.get("cantidad"), startX + 200, startY, paint);
                canvas.drawText(detalle.get("precio") + " Bs", startX + 300, startY, paint);
                canvas.drawText(detalle.get("monto") + " Bs", startX + 400, startY, paint);

                // Cargar la imagen si está disponible
                String imagenPath = detalle.get("imagenPath");
                if (imagenPath != null && !imagenPath.isEmpty()) {
                    try {
                        Uri imageUri = Uri.parse(imagenPath);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
                        canvas.drawBitmap(scaledBitmap, startX + 500, startY - 25, paint);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Mover el cursor a la siguiente fila
                startY += lineHeight * 2;

                // Si la página está llena, crear una nueva
                if (startY > 800) {
                    pdfDocument.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    startY = topMargin;
                }
            }
        } else {
            canvas.drawText("No hay productos en esta orden.", startX, startY, paint);
        }

        // Terminar página actual
        pdfDocument.finishPage(page);

        // Guardar el archivo PDF en el archivo temporal
        try {
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(outputStream);
            outputStream.close();

            Toast.makeText(this, "PDF generado correctamente", Toast.LENGTH_SHORT).show();

            // Después de generar el PDF, compartir por WhatsApp
            enviarPDFWhatsApp(pdfUri, datosCliente.get("nroTelefono"));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
        }

        // Cerrar el documento PDF
        pdfDocument.close();
    }

    private void enviarPDFWhatsApp(Uri pdfUri, String numeroCliente) {

        if (numeroCliente != null && !numeroCliente.isEmpty()) {
            String numeroFormateado = "591" + numeroCliente.replace(" ", "").replace("-", "");

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("application/pdf");
            sendIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            sendIntent.putExtra("jid", numeroFormateado + "@s.whatsapp.net");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            sendIntent.setPackage("com.whatsapp");

            try {
                startActivity(sendIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "WhatsApp no está instalado en este dispositivo", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Número de teléfono del cliente no disponible.", Toast.LENGTH_SHORT).show();
        }
    }
}
