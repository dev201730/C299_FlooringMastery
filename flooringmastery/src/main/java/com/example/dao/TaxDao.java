package com.example.dao;

import com.example.dto.Tax;
import java.util.List;

public interface TaxDao {
    List<Tax> getAllTaxes()throws PersistenceException;
    
    Tax getTax(String stateAbbreviationInput) throws PersistenceException;
}