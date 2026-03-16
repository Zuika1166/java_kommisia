package com.servicecenter.app;

import com.servicecenter.app.config.DatabaseInitializer;
import com.servicecenter.app.dao.impl.OrderDaoImpl;
import com.servicecenter.app.dao.impl.PartDaoImpl;
import com.servicecenter.app.service.OrderService;
import com.servicecenter.app.service.PartService;
import com.servicecenter.app.ui.ConsoleMenu;

public class App {

    public static void main(String[] args) {
        DatabaseInitializer databaseInitializer = new DatabaseInitializer();
        databaseInitializer.initializeDatabase();

        PartDaoImpl partDao = new PartDaoImpl();
        OrderDaoImpl orderDao = new OrderDaoImpl();

        PartService partService = new PartService(partDao);
        OrderService orderService = new OrderService(orderDao, partDao);

        ConsoleMenu consoleMenu = new ConsoleMenu(partService, orderService);
        consoleMenu.start();
    }
}
