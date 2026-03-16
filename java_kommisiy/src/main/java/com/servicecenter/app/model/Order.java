package com.servicecenter.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {

    private Long id;
    private String customerName;
    private String deviceModel;
    private Long partId;
    private int quantity;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public Order(Long id,
                 String customerName,
                 String deviceModel,
                 Long partId,
                 int quantity,
                 BigDecimal totalPrice,
                 OrderStatus status,
                 LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.deviceModel = deviceModel;
        this.partId = partId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %d | Клиент: %s | Устройство: %s | ID запчасти: %d | Кол-во: %d | Сумма: %s | Статус: %s | Создан: %s",
                id,
                customerName,
                deviceModel,
                partId,
                quantity,
                totalPrice,
                status,
                createdAt
        );
    }
}
