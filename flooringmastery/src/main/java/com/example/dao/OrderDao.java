package com.example.dao;

import com.example.dto.Order;
import com.example.dto.Product;
import com.example.dto.Tax;
import com.example.service.OrderFileNotExistException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OrderDao {
    
    List <Order> getAllOrdersForADate(String orderFile)throws PersistenceException;
    
    List<Integer> getAllOrderNumsForADate(String orderFile) throws PersistenceException;
    
    Order getOrder (String orderFile, int orderNum) throws PersistenceException;
    
    Order addOrderToExistingFile(String orderFile, int orderNumber, Order order) throws PersistenceException;
    
    Order addOrderToNewFile(String orderFile, int orderNumber, Order order) throws PersistenceException;
    
    Order removeOrder(String orderFile, int orderNumber) throws PersistenceException;

    String [] listAllOrderFiles();
    
    List<Order> getAllOrders() throws PersistenceException;

    List<Integer> getAllOrderNums() throws PersistenceException;
    
    Order editOrder(String orderFile, Order updatedOrder)throws PersistenceException;
    
    Map<String,List<Order>> getExportData() throws PersistenceException;
    
    void exportAllData() throws PersistenceException;
    
    public void deleteOrderFile(String fileName)throws OrderFileNotExistException;
    
}
