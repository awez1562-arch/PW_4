package com.example.sqlitelab;

public class Order {
    private int id;
    private String clientName;
    private String orderDate;
    private double cost;
    private String status;

    public Order(int id, String clientName, String orderDate, double cost, String status) {
        this.id = id;
        this.clientName = clientName;
        this.orderDate = orderDate;
        this.cost = cost;
        this.status = status;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Заказ #" + id + " | " + clientName + " | " + orderDate +
                " | " + cost + " руб. | " + status;
    }
}