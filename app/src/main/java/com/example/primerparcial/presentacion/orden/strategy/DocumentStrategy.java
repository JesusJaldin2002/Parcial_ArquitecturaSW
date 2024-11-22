package com.example.primerparcial.presentacion.orden.strategy;

import android.net.Uri;
import java.util.List;
import java.util.Map;

public interface DocumentStrategy {
    // Cambia la firma para aceptar parámetros desglosados
    Uri generateDocument(
            int orderId,
            Map<String, String> orderData,
            Map<String, String> clientData,
            List<Map<String, String>> orderDetails);

    void shareDocument(Uri documentUri, String phoneNumber);
}
