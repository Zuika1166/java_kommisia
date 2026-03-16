package com.servicecenter.app.dao;

import com.servicecenter.app.model.Part;

import java.util.List;
import java.util.Optional;

public interface PartDao {

    Part create(Part part);

    Optional<Part> findById(long id);

    List<Part> findAll();

    boolean update(Part part);

    boolean delete(long id);

    List<Part> searchByName(String name);
}
