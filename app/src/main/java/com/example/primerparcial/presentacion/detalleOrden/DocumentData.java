package com.example.primerparcial.presentacion.detalleOrden;

import java.util.List;
import java.util.Map;

public class DocumentData {
    private int orderId;
    private Map<String, String> orderData;
    private Map<String, String> clientData;
    private List<Map<String, String>> orderDetails;

    public DocumentData(int orderId, Map<String, String> orderData,
                        Map<String, String> clientData,
                        List<Map<String, String>> orderDetails) {
        this.orderId = orderId;
        this.orderData = orderData;
        this.clientData = clientData;
        this.orderDetails = orderDetails;
    }

    // Getters
    public int getOrderId() { return orderId; }
    public Map<String, String> getOrderData() { return orderData; }
    public Map<String, String> getClientData() { return clientData; }
    public List<Map<String, String>> getOrderDetails() { return orderDetails; }
}