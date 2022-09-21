package com.example.dao;

import com.example.dto.Product;
import java.util.List;

public interface ProductDao {
    
    List<Product> getAllProducts()throws PersistenceException;
    
    Product getProduct (String productType) throws PersistenceException;
}