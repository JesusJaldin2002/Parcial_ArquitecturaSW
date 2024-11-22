package com.example.primerparcial.presentacion.orden.strategy;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class XLSXStrategy implements DocumentStrategy {

    private Context context;

    public XLSXStrategy(Context context) {
        this.context = context;
    }

    @Override
    public Uri generateDocument(
            int orderId,
            Map<String, String> orderData,
            Map<String, String> clientData,
            List<Map<String, String>> orderDetails) {

        File excelFile = new File(context.getExternalFilesDir(null), "Detalle_Orden_" + orderId + ".xls");

        try {
            WritableWorkbook workbook = Workbook.createWorkbook(excelFile);
            WritableSheet sheet = workbook.createSheet("Detalle de Orden", 0);

            // Crear encabezado de la orden
            int rowNum = 0;
            sheet.addCell(new Label(0, rowNum++, "Orden de Compra #" + orderId));

            // Espacio
            rowNum++;

            // Escribir datos de la orden
            sheet.addCell(new Label(0, rowNum, "Fecha"));
            sheet.addCell(new Label(1, rowNum++, orderData.getOrDefault("fecha", "No disponible")));

            sheet.addCell(new Label(0, rowNum, "Total"));
            sheet.addCell(new Label(1, rowNum++, orderData.getOrDefault("total", "No disponible") + " Bs"));

            // Espacio adicional
            rowNum++;

            // Datos del cliente
            sheet.addCell(new Label(0, rowNum, "Cliente"));
            sheet.addCell(new Label(1, rowNum++, clientData.getOrDefault("nombre", "No disponible")));

            sheet.addCell(new Label(0, rowNum, "Teléfono"));
            sheet.addCell(new Label(1, rowNum++, clientData.getOrDefault("nroTelefono", "No disponible")));

            // Espacio adicional
            rowNum += 2;

            // Encabezado de productos
            sheet.addCell(new Label(0, rowNum, "ID"));
            sheet.addCell(new Label(1, rowNum, "Nombre"));
            sheet.addCell(new Label(2, rowNum, "Cantidad"));
            sheet.addCell(new Label(3, rowNum, "Precio"));
            sheet.addCell(new Label(4, rowNum++, "Monto"));

            // Detalles de productos
            for (Map<String, String> detalle : orderDetails) {
                sheet.addCell(new Label(0, rowNum, detalle.get("idProducto")));
                sheet.addCell(new Label(1, rowNum, detalle.get("nombre")));
                sheet.addCell(new Label(2, rowNum, detalle.get("cantidad")));
                sheet.addCell(new Label(3, rowNum, detalle.get("precio") + " Bs"));
                sheet.addCell(new Label(4, rowNum, detalle.get("monto") + " Bs"));

                // Agregar imagen si está disponible
                String imagenPath = detalle.get("imagenPath");
                if (imagenPath != null && !imagenPath.isEmpty()) {
                    try {
                        Uri imageUri = Uri.parse(imagenPath);
                        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        // Convertir Bitmap a byte[]
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Insertar imagen en la hoja de cálculo
                        WritableImage image = new WritableImage(5, rowNum, 1, 1, imageBytes);
                        sheet.addImage(image);

                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                rowNum++;
            }

            // Guardar el archivo Excel
            workbook.write();
            workbook.close();

            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", excelFile);

        } catch (IOException | WriteException e) {
            Toast.makeText(context, "Error al generar el archivo Excel", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void shareDocument(Uri documentUri, String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String formattedNumber = "591" + phoneNumber.replace(" ", "").replace("-", "");
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("application/vnd.ms-excel");
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
