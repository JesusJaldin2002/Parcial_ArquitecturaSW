package com.example.primerparcial.presentacion.detalleOrden.strategy;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PDFStrategy implements DocumentStrategy {

    private Context context;

    public PDFStrategy(Context context) {
        this.context = context;
    }

    @Override
    public Uri generateDocument(int orderId, Map<String, String> orderData,
                                Map<String, String> clientData, List<Map<String, String>> orderDetails) {
        File pdfFile = new File(context.getExternalFilesDir(null), "Detalle_Orden_" + orderId + ".pdf");
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(16);
        titlePaint.setFakeBoldText(true);
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Definir márgenes y posiciones iniciales
        int leftMargin = 30;
        int topMargin = 30;
        int startX = leftMargin;
        int startY = topMargin;
        int lineHeight = 20;

        // Dibujar encabezado
        canvas.drawText("ORDEN DE COMPRA: #" + orderId, startX, startY, titlePaint);
        startY += lineHeight;
        canvas.drawText("Fecha: " + orderData.getOrDefault("fecha", "No disponible"), startX, startY, paint);
        canvas.drawText("Total: " + orderData.getOrDefault("total", "No disponible") + " Bs", startX + 350, startY, paint);
        startY += lineHeight;

        // Datos del cliente
        canvas.drawText("Cliente: " + clientData.getOrDefault("nombre", "Información no disponible"), startX, startY, paint);
        startY += lineHeight;
        canvas.drawText("Teléfono: " + clientData.getOrDefault("nroTelefono", "Información no disponible"), startX, startY, paint);

        // Cargar la imagen del cliente si está disponible
        String clienteImagenPath = clientData.get("imagenPath");
        if (clienteImagenPath != null && !clienteImagenPath.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(clienteImagenPath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                canvas.drawBitmap(scaledBitmap, startX + 450, startY - 60, paint);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Espacio para la lista de productos
        startY += 2 * lineHeight;
        canvas.drawText("Lista de Productos:", startX, startY, titlePaint);
        startY += lineHeight;

        Paint headerBackgroundPaint = new Paint();
        headerBackgroundPaint.setColor(Color.LTGRAY); // Color de fondo del encabezado

        Paint headerTextPaint = new Paint();
        headerTextPaint.setTextSize(14);
        headerTextPaint.setFakeBoldText(true);
        // Dibujar fondo del encabezado
        canvas.drawRect(startX, startY, startX + 550, startY + lineHeight, headerBackgroundPaint);

        // Dibujar texto del encabezado
        startY += lineHeight - 5; // Ajustar ligeramente la posición vertical del texto
        canvas.drawText("ID", startX, startY, headerTextPaint);
        canvas.drawText("Nombre", startX + 50, startY, headerTextPaint);
        canvas.drawText("Cantidad", startX + 150, startY, headerTextPaint);
        canvas.drawText("Precio", startX + 250, startY, headerTextPaint);
        canvas.drawText("Monto", startX + 350, startY, headerTextPaint);
        canvas.drawText("Imagen", startX + 450, startY, headerTextPaint);

        // Añadir una separación adicional
        startY += lineHeight * 1.5;

        // Detalles de productos
        for (Map<String, String> detail : orderDetails) {
            // Dibujar detalles del producto
            canvas.drawText(detail.get("idProducto"), startX, startY, paint);
            canvas.drawText(detail.get("nombre"), startX + 50, startY, paint);
            canvas.drawText(detail.get("cantidad"), startX + 150, startY, paint);
            canvas.drawText(detail.get("precio") + " Bs", startX + 250, startY, paint);
            canvas.drawText(detail.get("monto") + " Bs", startX + 350, startY, paint);

            // Cargar la imagen del producto si está disponible
            String imagenPath = detail.get("imagenPath");
            if (imagenPath != null && !imagenPath.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(imagenPath);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 40, 40, false); // Ajustar el tamaño de la imagen
                    int imageYPosition = startY - 20; // Centrar verticalmente la imagen respecto al texto
                    canvas.drawBitmap(scaledBitmap, startX + 450, imageYPosition, paint);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Dibujar borde inferior de la fila
            int rowBottomY = startY + lineHeight;
            canvas.drawLine(startX, rowBottomY, startX + 550, rowBottomY, borderPaint);

            // Mover el cursor a la siguiente fila
            startY += lineHeight * 3; // Ajuste para dar más espacio vertical a las imágenes

            // Crear nueva página si es necesario
            if (startY > 800) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                startY = topMargin;
            }
        }

        // Terminar página actual y guardar el documento
        pdfDocument.finishPage(page);
        try {
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
            pdfDocument.close();
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", pdfFile);
        } catch (IOException e) {
            Toast.makeText(context, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        pdfDocument.close();
        return null;
    }

    @Override
    public void shareDocument(Uri documentUri, String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String formattedNumber = "591" + phoneNumber.replace(" ", "").replace("-", "");
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("application/pdf");
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
