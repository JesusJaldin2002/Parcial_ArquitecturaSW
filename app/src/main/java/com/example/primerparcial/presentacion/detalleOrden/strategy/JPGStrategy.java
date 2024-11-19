package com.example.primerparcial.presentacion.detalleOrden.strategy;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JPGStrategy implements DocumentStrategy {

    private Context context;

    public JPGStrategy(Context context) {
        this.context = context;
    }

    @Override
    public Uri generateDocument(int orderId, Map<String, String> orderData,
                                Map<String, String> clientData, List<Map<String, String>> orderDetails) {
        List<Uri> imageUris = new ArrayList<>();
        int pageCount = 1;
        File jpgFile;
        int orderIndex = 0; // Para mantener el seguimiento del índice de detalles del pedido

        do {
            // Crear un nuevo archivo de imagen para cada página
            jpgFile = new File(context.getExternalFilesDir(null), "Detalle_Orden_" + orderId + "_page_" + pageCount + ".jpg");

            // Crear un nuevo bitmap y configurar el canvas
            Bitmap bitmap = Bitmap.createBitmap(800, 1200, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE); // Fondo blanco

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(24);

            Paint titlePaint = new Paint();
            titlePaint.setTextSize(26);
            titlePaint.setFakeBoldText(true);
            titlePaint.setColor(Color.BLACK);

            Paint borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setColor(Color.BLACK);
            borderPaint.setStrokeWidth(2);

            int leftMargin = 50;
            int topMargin = 50;
            int startX = leftMargin;
            int startY = topMargin;
            int lineHeight = 40;

            // Dibujar encabezado
            canvas.drawText("ORDEN DE COMPRA: #" + orderId, startX, startY, titlePaint);
            startY += lineHeight;
            canvas.drawText("Fecha: " + orderData.getOrDefault("fecha", "No disponible"), startX, startY, paint);
            canvas.drawText("Total: " + orderData.getOrDefault("total", "No disponible") + " Bs", startX + 400, startY, paint);
            startY += lineHeight;

            // Dibujar datos del cliente
            canvas.drawText("Cliente: " + clientData.getOrDefault("nombre", "Información no disponible"), startX, startY, paint);
            startY += lineHeight;
            canvas.drawText("Teléfono: " + clientData.getOrDefault("nroTelefono", "Información no disponible"), startX, startY, paint);

            // Cargar la imagen del cliente si está disponible
            String clienteImagenPath = clientData.get("imagenPath");
            if (clienteImagenPath != null && !clienteImagenPath.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(clienteImagenPath);
                    Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapImage, 100, 100, false);
                    canvas.drawBitmap(scaledBitmap, startX + 550, startY - 80, paint);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Espacio para la lista de productos
            startY += 2 * lineHeight;
            canvas.drawText("Lista de Productos:", startX, startY, titlePaint);
            startY += lineHeight;

            // Dibujar encabezados de tabla
            canvas.drawText("ID", startX, startY, paint);
            canvas.drawText("Nombre", startX + 100, startY, paint);
            canvas.drawText("Cantidad", startX + 300, startY, paint);
            canvas.drawText("Precio", startX + 450, startY, paint);
            canvas.drawText("Monto", startX + 600, startY, paint);

            // Añadir una separación adicional
            startY += lineHeight * 2;

            // Dibujar detalles de productos
            int itemsPerPage = 0; // Para manejar el número de elementos por página

            while (orderIndex < orderDetails.size()) {
                Map<String, String> detail = orderDetails.get(orderIndex);

                // Dibujar detalles del producto
                canvas.drawText(detail.get("idProducto"), startX, startY, paint);
                canvas.drawText(detail.get("nombre"), startX + 100, startY, paint);
                canvas.drawText(detail.get("cantidad"), startX + 300, startY, paint);
                canvas.drawText(detail.get("precio") + " Bs", startX + 450, startY, paint);
                canvas.drawText(detail.get("monto") + " Bs", startX + 600, startY, paint);

                // Cargar la imagen del producto si está disponible
                String imagenPath = detail.get("imagenPath");
                if (imagenPath != null && !imagenPath.isEmpty()) {
                    try {
                        Uri imageUri = Uri.parse(imagenPath);
                        Bitmap bitmapProduct = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                        Bitmap scaledBitmapProduct = Bitmap.createScaledBitmap(bitmapProduct, 40, 40, false); // Ajustar el tamaño de la imagen
                        int imageYPosition = startY - 30; // Centrar verticalmente la imagen respecto al texto
                        canvas.drawBitmap(scaledBitmapProduct, startX + 700, imageYPosition, paint);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Dibujar borde inferior de la fila
                int rowBottomY = startY + lineHeight;
                canvas.drawLine(startX, rowBottomY, startX + 750, rowBottomY, borderPaint);

                // Mover el cursor a la siguiente fila
                startY += lineHeight * 3;
                itemsPerPage++;
                orderIndex++;

                // Verificar si el contenido excede la imagen
                if (startY > 1150 || itemsPerPage >= 20) { // Si supera el límite de la imagen o el máximo de ítems
                    break;
                }
            }

            // Guardar la página actual
            try {
                FileOutputStream outputStream = new FileOutputStream(jpgFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                imageUris.add(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", jpgFile));
            } catch (IOException e) {
                Toast.makeText(context, "Error al generar el JPG", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            pageCount++;
        } while (orderIndex < orderDetails.size());

        return imageUris.isEmpty() ? null : imageUris.get(0); // Devuelve la primera imagen si hay varias
    }

    @Override
    public void shareDocument(Uri documentUri, String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String formattedNumber = "591" + phoneNumber.replace(" ", "").replace("-", "");
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("image/jpeg");
            sendIntent.putExtra(Intent.EXTRA_STREAM, documentUri);
            sendIntent.putExtra("jid", formattedNumber + "@s.whatsapp.net");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendIntent.setPackage("com.whatsapp");

            try {
                context.startActivity(sendIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "WhatsApp no está instalado en este dispositivo", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Número de teléfono del cliente no disponible.", Toast.LENGTH_SHORT).show();
        }
    }
}
