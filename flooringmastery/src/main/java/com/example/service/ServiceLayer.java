package com.example.service;

import com.example.dao.PersistenceException;
import com.example.dto.Order;
import com.example.dto.Product;
import com.example.dto.Tax;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ServiceLayer {
    
    BigDecimal calculateMaterialCost(BigDecimal area,BigDecimal costPerSquareFoot);
    
    BigDecimal calculateLaborCost(BigDecimal area,BigDecimal laborCostPerSquareFoot);
    
    BigDecimal calculateTax(BigDecimal materialCost, BigDecimal laborCost,BigDecimal taxRate);
    
    BigDecimal calculateTotal(BigDecimal materialCost, BigDecimal laborCost, BigDecimal tax);
    
    String createOrderFileNameFromDate(LocalDate date);
    
    void checkOrderFileExists (String orderfileName)throws NoOrdersException;
    
    List <Order> getAllOrders (String fileWithDate) throws PersistenceException;
    
    List<Order> getOrderList (LocalDate wantedOrderDate) throws NoOrdersException, PersistenceException;
    
    LocalDate checkDateIsInFuture(LocalDate orderDate) throws DateErrorException;
    
    void validateCustomerName(String customerNameInput)throws CustomerNameErrorException;
    
    void checkStateAgainstTaxFile(String stateAbbreviationInput)throws PersistenceException, StateNotFoundException;
    
     List <Product> getAllProducts() throws PersistenceException;
    
     void checkProductTypeAgainstProductsFile (String productTypeInput) throws PersistenceException, ProductTypeNotFoundException;
     
     void checkAreaOverMinOrder (BigDecimal areaInput) throws AreaBelowMinException;
     
     Product getProduct (String productType) throws PersistenceException;
     
     Tax getTax(String stateAbbreviationInput)throws PersistenceException;
     
     Order createNewOrderIfRequired (String verifyOrder, LocalDate orderDateInput, int orderNumber, String customerNameInput, String stateAbbreviationInput, BigDecimal taxRate, String productTypeInput,
                    BigDecimal areaInput, BigDecimal CostPerSquareFoot, BigDecimal laborCostPerSquareFoot, BigDecimal materialCost, BigDecimal laborCost, BigDecimal tax, BigDecimal total)
             throws PersistenceException ;
     
     int generateNewOrderNum()throws PersistenceException;
     
     //void addOrder(String orderFile, int orderNumber, Order order)throws PersistenceException;
     
     
     Order removeOrderIfConfirmed(String removeConfirmation, String orderFile, int orderNumber) throws PersistenceException, OrderFileNotExistException;
     
     int checkOrderNumExists(String orderFileName, int orderNumberInput) throws PersistenceException, NoOrderNumException;
     
     Order getOrder(String fileName, int orderNum) throws PersistenceException ;
     
      String checkForEdit (String updatedInfo);
      
      Order updateOrderCustomerNameIfRequired(String updatedCustomerName, Order orderToEdit);
      
      Order updateOrderStateIfRequired(String updatedState, Order orderToEdit);
      
      Order updateOrderProductTypeIfRequired(String updatedProductType, Order orderToEdit);
      
      Order updateOrderAreaIfRequired(BigDecimal updatedArea, Order orderToEdit);
      
      BigDecimal checkForEditBigDecimal (String updatedInfo);
      
      Order updateOrderCalculations(Order editedOrder) throws PersistenceException;
      
      Order editOrderIfConfirmed(String toBeEdited, String orderFile, Order updatedOrder) throws PersistenceException;
      
      String getCustomerNamePlaceHolder(String customerNameInput);
      
      void exportAllData() throws PersistenceException;
}

