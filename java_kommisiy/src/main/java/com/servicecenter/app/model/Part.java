package com.servicecenter.app.model;

import java.math.BigDecimal;

public class Part {

    private Long id;
    private String name;
    private String manufacturer;
    private BigDecimal price;
    private int quantity;

    public Part(Long id, String name, String manufacturer, BigDecimal price, int quantity) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %d | Запчасть: %s | Производитель: %s | Цена: %s | Остаток: %d",
                id,
                name,
                manufacturer,
                price,
                quantity
        );
    }
}
