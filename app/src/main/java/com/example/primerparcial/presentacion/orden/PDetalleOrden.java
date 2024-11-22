package com.example.primerparcial.presentacion.orden;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerparcial.R;
import com.example.primerparcial.negocio.orden.NOrden;
import com.example.primerparcial.negocio.producto.NProducto;
import com.example.primerparcial.presentacion.orden.strategy.DocumentContext;
import com.example.primerparcial.presentacion.orden.strategy.DocumentStrategy;
import com.example.primerparcial.presentacion.orden.strategy.JPGStrategy;
import com.example.primerparcial.presentacion.orden.strategy.MessageStrategy;
import com.example.primerparcial.presentacion.orden.strategy.PDFStrategy;
import com.example.primerparcial.presentacion.orden.strategy.XLSXStrategy;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PDetalleOrden extends AppCompatActivity {

    private NOrden nOrden;
    private NProducto nProducto;
    private LinearLayout listaProductosLayout;
    private TextView tvMontoTotal;
    private int idOrdenSeleccionada;
    private DocumentContext documentContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_orden);

        nOrden = new NOrden(this);
        nProducto = new NProducto(this);
        documentContext = new DocumentContext(this);

        // Recibir el id de la orden
        Intent intent = getIntent();
        idOrdenSeleccionada = intent.getIntExtra("idOrden", -1);

        if (idOrdenSeleccionada != -1) {
            mostrarDetalleOrden();
        }

        Button btnGenerarPDF = findViewById(R.id.btnGenerarPdf);
        btnGenerarPDF.setOnClickListener(v ->
                ejecutarEstrategia(new PDFStrategy(this)));

        Button btnGenerarJPG = findViewById(R.id.btnGenerarJpg);
        btnGenerarJPG.setOnClickListener(v ->
                ejecutarEstrategia(new JPGStrategy(this)));

        Button btnGenerarExcel = findViewById(R.id.btnGenerarExcel);
        btnGenerarExcel.setOnClickListener(v ->
                ejecutarEstrategia(new XLSXStrategy(this)));

        Button btnGenerarMensaje = findViewById(R.id.btnGenerarMensaje);
        btnGenerarMensaje.setOnClickListener(v ->
                ejecutarEstrategia(new MessageStrategy(this)));

        Button btnVolverAtras = findViewById(R.id.btnVolverAtras);
        btnVolverAtras.setOnClickListener(v ->
                finish());
    }

    private void ejecutarEstrategia(DocumentStrategy strategy) {
        documentContext.setStrategy(strategy);

        // Obtén los datos
        int orderId = idOrdenSeleccionada;
        Map<String, String> orderData =
                nOrden.obtenerDatosOrden(idOrdenSeleccionada);
        Map<String, String> clientData =
                nOrden.obtenerDatosCliente(idOrdenSeleccionada);
        List<Map<String, String>> orderDetails =
                nOrden.obtenerDetallesPorOrden(idOrdenSeleccionada);

        // Llama a documentContext con datos
        documentContext.executeStrategy(orderId, orderData, clientData, orderDetails);
    }

    // Método para mostrar los detalles de la orden
    @SuppressLint("SetTextI18n")
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

        List<Map<String, String>> detalles = nOrden.obtenerDetallesPorOrden(idOrdenSeleccionada);

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
                nOrden.actualizarCantidadDetalleOrden(
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
            nOrden.eliminarDetalleOrden(idOrden, idProducto, cantidad, totalDetalle);
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
}
