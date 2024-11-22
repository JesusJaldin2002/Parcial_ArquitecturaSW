package com.example.primerparcial.presentacion.orden.strategy;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class MessageStrategy implements DocumentStrategy {

    private Context context;

    public MessageStrategy(Context context) {
        this.context = context;
    }

    @Override
    public Uri generateDocument(
            int orderId,
            Map<String, String> orderData,
            Map<String, String> clientData,
            List<Map<String, String>> orderDetails) {

        // Generar mensaje de texto basado en los datos de la orden
        StringBuilder messageBuilder = new StringBuilder();

        // Encabezado de la orden
        messageBuilder.append("ORDEN DE COMPRA: #").append(orderId).append("\n\n");

        // Datos de la orden
        messageBuilder.append("Fecha: ").append(orderData.getOrDefault("fecha", "No disponible")).append("\n");
        messageBuilder.append("Total: ").append(orderData.getOrDefault("total", "No disponible")).append(" Bs\n\n");

        // Datos del cliente
        messageBuilder.append("Cliente: ").append(clientData.getOrDefault("nombre", "Información no disponible")).append("\n");
        messageBuilder.append("Teléfono: ").append(clientData.getOrDefault("nroTelefono", "Información no disponible")).append("\n\n");

        // Lista de productos
        messageBuilder.append("Lista de Productos:\n");
        messageBuilder.append("-----------------------------------\n");

        for (Map<String, String> detail : orderDetails) {
            messageBuilder.append("ID: ").append(detail.get("idProducto")).append("\n");
            messageBuilder.append("Nombre: ").append(detail.get("nombre")).append("\n");
            messageBuilder.append("Cantidad: ").append(detail.get("cantidad")).append("\n");
            messageBuilder.append("Precio: ").append(detail.get("precio")).append(" Bs\n");
            messageBuilder.append("Monto: ").append(detail.get("monto")).append(" Bs\n");
            messageBuilder.append("-----------------------------------\n");
        }

        // Guardar el mensaje generado como una propiedad para compartirlo más adelante
        messageContent = messageBuilder.toString();

        // No hay un archivo generado, por lo tanto, devolvemos null
        return null;
    }

    private String messageContent = "";

    @Override
    public void shareDocument(Uri documentUri, String phoneNumber) {
        // Enviar el mensaje generado por WhatsApp
        if (messageContent.isEmpty()) {
            Toast.makeText(context, "No hay contenido para enviar.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String formattedNumber = "591" + phoneNumber.replace(" ", "").replace("-", "");

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, messageContent);
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
