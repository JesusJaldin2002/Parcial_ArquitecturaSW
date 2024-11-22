package com.example.primerparcial.presentacion.orden.strategy;

import android.content.Context;
import android.net.Uri;

import java.util.List;
import java.util.Map;

public class DocumentContext {
    // Relación de agregación: `DocumentContext` contiene una referencia a una instancia de `DocumentStrategy`.
    // `DocumentContext` utiliza esta estrategia, pero no controla su ciclo de vida.
    private DocumentStrategy strategy;

    // Contexto de la aplicación (necesario para interactuar con componentes de Android).
    private Context context;

    // Constructor: Se pasa el contexto de la aplicación, lo que refuerza la independencia de la clase.
    public DocumentContext(Context context) {
        this.context = context;
    }

    // Método para inyectar una estrategia concreta desde fuera.
    // Aquí se realiza la agregación: la instancia de `DocumentStrategy` es proporcionada por otra clase.
    public void setStrategy(DocumentStrategy strategy) {
        this.strategy = strategy;
    }

    // Método que ejecuta la estrategia actual configurada.
    public void executeStrategy(
            int orderId,
            Map<String, String> orderData,
            Map<String, String> clientData,
            List<Map<String, String>> orderDetails) {
        // Se delega la generación del documento a la estrategia actual.
        Uri documentUri = strategy.generateDocument(orderId, orderData, clientData, orderDetails);

        // Se delega el envío del documento a la estrategia actual.
        String phoneNumber = clientData.get("nroTelefono");
        strategy.shareDocument(documentUri, phoneNumber);
    }
}
