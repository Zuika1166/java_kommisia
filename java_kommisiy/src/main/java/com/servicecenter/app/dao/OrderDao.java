package com.servicecenter.app.dao;

import com.servicecenter.app.model.Order;
import com.servicecenter.app.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Order create(Order order);

    Optional<Order> findById(long id);

    List<Order> findAll();

    boolean update(Order order);

    boolean delete(long id);

    List<Order> findByStatus(OrderStatus status);
}
