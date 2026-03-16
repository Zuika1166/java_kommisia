package com.servicecenter.app.service;

import com.servicecenter.app.dao.PartDao;
import com.servicecenter.app.model.Part;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PartService {

    private final PartDao partDao;

    public PartService(PartDao partDao) {
        this.partDao = partDao;
    }

    public Part addPart(String name, String manufacturer, BigDecimal price, int quantity) {
        validatePartData(name, manufacturer, price, quantity);
        Part part = new Part(null, name.trim(), manufacturer.trim(), price, quantity);
        return partDao.create(part);
    }

    public List<Part> getAllParts() {
        return partDao.findAll();
    }

    public Optional<Part> getPartById(long id) {
        return partDao.findById(id);
    }

    public boolean updatePart(long id, String name, String manufacturer, BigDecimal price, int quantity) {
        validatePartData(name, manufacturer, price, quantity);

        Optional<Part> existingPart = partDao.findById(id);
        if (existingPart.isEmpty()) {
            return false;
        }

        Part part = existingPart.get();
        part.setName(name.trim());
        part.setManufacturer(manufacturer.trim());
        part.setPrice(price);
        part.setQuantity(quantity);
        return partDao.update(part);
    }

    public boolean deletePart(long id) {
        return partDao.delete(id);
    }

    public List<Part> searchByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название для поиска не должно быть пустым.");
        }
        return partDao.searchByName(name);
    }

    private void validatePartData(String name, String manufacturer, BigDecimal price, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название запчасти не должно быть пустым.");
        }
        if (manufacturer == null || manufacturer.isBlank()) {
            throw new IllegalArgumentException("Производитель не должен быть пустым.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Количество на складе не может быть отрицательным.");
        }
    }
}
