package com.example.primerparcial.presentacion.detalleOrden.strategy;

import android.content.Context;
import android.net.Uri;

import com.example.primerparcial.presentacion.detalleOrden.strategy.DocumentStrategy;

import java.util.List;
import java.util.Map;

public class DocumentContext {
    private DocumentStrategy strategy;
    private Context context;

    public DocumentContext(Context context) {
        this.context = context;
    }

    public void setStrategy(DocumentStrategy strategy) {
        this.strategy = strategy;
    }

    // Cambia el método para aceptar parámetros desglosados
    public void executeStrategy(int orderId, Map<String, String> orderData,
                                Map<String, String> clientData, List<Map<String, String>> orderDetails) {
        // Llama a la estrategia usando los datos desglosados
        Uri documentUri = strategy.generateDocument(orderId, orderData, clientData, orderDetails);
        String phoneNumber = clientData.get("nroTelefono");
        strategy.shareDocument(documentUri, phoneNumber);
    }
}