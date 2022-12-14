package com.example.service;

import com.example.dao.PersistenceException;
import com.example.dto.Order;
import com.example.dto.Product;
import com.example.dto.Tax;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.dao.OrderDao;
import com.example.dao.ProductDao;
import com.example.dao.TaxDao;

public class ServiceLayerImpl implements ServiceLayer {
    //The service layer is responsible for the business logic of an application.
    //It sits between the DAOs and the controller.
    
    private OrderDao orderDao;
    private ProductDao productDao;
    private TaxDao taxDao;
    
    private final String customerNamePlaceHolder = "#*~";

    public ServiceLayerImpl(OrderDao orderDao, ProductDao productDao, TaxDao taxDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
    }
    
    //********************************************* 1. LIST ORDERS *********************************************
    
    @Override
    public String createOrderFileNameFromDate(LocalDate date){
        //date will be in the format YYYY-MM-DD
        //Need to convert this to the format: MMDDYYYY
        
        //Make a formatter with the required format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy");
        String dateFormatted = date.format(formatter);
        
        return "Orders_"+dateFormatted+".txt";
    }
    
    @Override
    public void checkOrderFileExists (String orderFileName)throws NoOrdersException{
        //Checks if there are any files in the Orders folder that have the  
        //given file name
        //Will throw exception if not found and will return void if no file is found.
        String [] orderFiles = orderDao.listAllOrderFiles();
        String orderfile = null;
        //compare the order file names to the file name given
        for (String orderFile : orderFiles){
            if (orderFileName.equals(orderFile)) {
                //if it is found, set name to the orderfile name
                orderfile = orderFileName;
                if (orderfile!=null) {
                    break;
                }
            }
        }
        //if the order file does not exist, the order file name is null, exception thrown.
        if (orderfile == null){
            throw new NoOrdersException (
            "ERROR: no orders exist for that date.");
        }
    }
        
    @Override
    public List <Order> getAllOrders (String fileWithDate) throws PersistenceException {  
        //Lists all the orders from the file specified.
        return orderDao.getAllOrdersForADate(fileWithDate);
    }

    @Override
    public List<Order> getOrderList (LocalDate wantedOrderDate) throws NoOrdersException, PersistenceException{
        String fileWithDate = createOrderFileNameFromDate(wantedOrderDate);
        checkOrderFileExists(fileWithDate);
        return getAllOrders(fileWithDate);
        
    }
    
    //********************************************* 2. ADD AN ORDER ************************************************    
    //---------------------------------------------- order date -----------------------------------------------------
    @Override
    public LocalDate checkDateIsInFuture(LocalDate orderDate) throws DateErrorException{
        //Check if the date is in the future
        //Get the date for now
        LocalDate dateNow = LocalDate.now();
        //If the order date is before the date now
        if (orderDate.compareTo(dateNow)<0){
            throw new DateErrorException (
            "ERROR: Date must be in the future.");
        }
        return orderDate;
    }    
    
    //---------------------------------------------- customer name -----------------------------------------------------
    
    @Override
    public void validateCustomerName(String customerNameInput)throws CustomerNameErrorException {
        //Checks first that field is null or have whitespace/empty
        if (customerNameInput.isBlank()  || customerNameInput.isEmpty()) {
            throw new CustomerNameErrorException (
                    "ERROR: customer name cannot be blank.");
                }
    }
    
    @Override 
    public String getCustomerNamePlaceHolder(String customerNameInput) {
        //If the name contains any comma's, swap them out for #*~ 
        return customerNameInput.replace(",", customerNamePlaceHolder);
    }
    
    //------------------------------------------------- state -----------------------------------------------------------
    @Override
    public void checkStateAgainstTaxFile(String stateAbbreviationInput)throws PersistenceException, StateNotFoundException {
        List<Tax> taxesList = taxDao.getAllTaxes();
        String stateAbbreviation = null;
       for (Tax tax:taxesList) {
           if (tax.getStateAbbr().equalsIgnoreCase(stateAbbreviationInput)) {
               stateAbbreviation = tax.getStateAbbr();
               if (stateAbbreviation!=null){
                   break;
               }
           }
       }
       if (stateAbbreviation == null) {
           throw new StateNotFoundException (
           "ERROR: we cannot sell to " + stateAbbreviation + ".");
       }
    }
    //------------------------------------------------- product ----------------------------------------------------------
    @Override
    public List <Product> getAllProducts()throws PersistenceException {
        return productDao.getAllProducts();
    }
    
    @Override
    public void checkProductTypeAgainstProductsFile (String productTypeInput) throws PersistenceException, ProductTypeNotFoundException {
        List<Product> productList = productDao.getAllProducts();
        String productType = null;
        
        for (Product product:productList) {
            if (product.getProductType().equalsIgnoreCase(productTypeInput)) {
                productType = product.getProductType();
            }
        }
        if (productType == null) {
            throw new ProductTypeNotFoundException (
                    "ERROR: " + productTypeInput + " is not in the product list.");
        }
    }
    
    @Override 
    public Product getProduct (String productType) throws PersistenceException{
        return productDao.getProduct(productType);
    }
    
    //--------------------------------------------------- calculations ---------------------------------------------------
    @Override
    public BigDecimal calculateMaterialCost(BigDecimal area, BigDecimal costPerSquareFoot) {
        return area.multiply(costPerSquareFoot).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateLaborCost(BigDecimal area, BigDecimal laborCostPerSquareFoot) {
        return area.multiply(laborCostPerSquareFoot).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTax(BigDecimal materialCost, BigDecimal laborCost, BigDecimal taxRate) {
        return (materialCost.add(laborCost)).multiply(taxRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotal(BigDecimal materialCost, BigDecimal laborCost, BigDecimal tax) {
        return materialCost.add(laborCost).add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    //------------------------------------------------- area ----------------------------------------------------------------
    @Override
    public void checkAreaOverMinOrder (BigDecimal areaInput) throws AreaBelowMinException {
        //if the area input is greater less than 100 sq ft, throw error
        if (areaInput.compareTo(new BigDecimal("100"))<0) {
            throw new AreaBelowMinException (
            "ERROR: the area is below the minimum order");
        }
     }
    
    //------------------------------------------------- tax ---------------------------------------------------------------
    
    @Override
    public Tax getTax(String stateAbbreviationInput)throws PersistenceException {
        return taxDao.getTax(stateAbbreviationInput);
    }
    
    //--------------------------------- create new order and add to in memory storage --------------------------------------
    @Override
    public int generateNewOrderNum() throws PersistenceException {
//        check if there is a file for todays date, if there is get the last order number and add one to it. If there isn't a file for 
//        todays date then create one
        //Export all data 
        //then use the exported data to generate a list/map of the order numbers, obtain the last one and add one.
        List<Integer> orderNums = orderDao.getAllOrderNums();
        int maxOrderNum = Collections.max(orderNums);
        return maxOrderNum + 1;
    }

    //will return a new order if required, null if not.
   @Override 
    public Order createNewOrderIfRequired (String verifyOrder, LocalDate orderDateInput, int orderNumber, String customerNameInput, String stateAbbreviationInput, BigDecimal taxRate, String productTypeInput,
                    BigDecimal areaInput, BigDecimal CostPerSquareFoot, BigDecimal laborCostPerSquareFoot, BigDecimal materialCost, BigDecimal laborCost, BigDecimal tax, BigDecimal total) 
    throws PersistenceException {
       //If the user replies Y to whether they want to place the order, then create a new order object using the information given.
        Order newOrder;
       if (verifyOrder.equalsIgnoreCase("Y")){
           newOrder = new Order(orderNumber);
           newOrder.setCustomerName(customerNameInput);
           newOrder.setStateAbbr(stateAbbreviationInput);
           newOrder.setTaxRate(taxRate);
           newOrder.setProductType(productTypeInput);
           newOrder.setArea(areaInput);
           newOrder.setCostPerSquareFoot(CostPerSquareFoot);
           newOrder.setLaborCostPerSquareFoot(laborCostPerSquareFoot);
           newOrder.setMaterialCost(materialCost);
           newOrder.setLaborCost(laborCost);
           newOrder.setTax(tax);
           newOrder.setTotal(total);
           //create the order file name using the date
           String newOrderFileName = createOrderFileNameFromDate(orderDateInput);
           //see if the order file exists or not
           String [] orderFiles = orderDao.listAllOrderFiles();
           //if it does exist, orderfile will be set equal to the newOrderFileName. If
           //it doesn't, orderfile will still be null
           String fileExists = null;
           //compare the order file names to the file name newOrderFileName
           for (String orderFile : orderFiles){
               if (newOrderFileName.equals(orderFile)) {
                  //if it is found, add the order to that file
                fileExists = newOrderFileName;
                Order orderCreated = orderDao.addOrderToExistingFile(fileExists, orderNumber, newOrder);
                return orderCreated;          
                }
            }
        //if the order file does not exist, the order file should be created.
        if (fileExists == null){
            Order orderCreated = orderDao.addOrderToNewFile(newOrderFileName, orderNumber, newOrder);
            return orderCreated;
        }
        }
        //If the user did not want to save the order, return null order
        return null;
       }
    
     
//********************************************* 3. EDIT AN ORDER ************************************************          
    @Override
    public String checkForEdit (String updatedInfo) {
        //If the user entered nothing, then return null
        if (updatedInfo == null 
                || updatedInfo.trim().length()==0
                || updatedInfo.isEmpty()
                || updatedInfo.isBlank()) {
            return null;
        } else {
            //the user entered new information, return that
            return updatedInfo;
        }
    }
    
    @Override
    public BigDecimal checkForEditBigDecimal (String updatedInfo) {
        //If the user entered nothing, then return null
        if (updatedInfo == null 
                || updatedInfo.trim().length()==0
                || updatedInfo.isEmpty()
                || updatedInfo.isBlank()) {
            return null;
        } else {
            //the user entered new information, return that
            return new BigDecimal(updatedInfo);
        }
    }
    
    @Override
    public Order updateOrderCustomerNameIfRequired(String updatedCustomerName, Order orderToEdit){
        //If the updatedCustomerName string is null, then return the order object unedited
        if (updatedCustomerName == null) {
            return orderToEdit;
        } else {
            //If the updatedCustomerName contains new info, then update the order and return it
            orderToEdit.setCustomerName(updatedCustomerName);
            return orderToEdit;
        }
    }
    @Override
    public Order updateOrderStateIfRequired(String updatedState, Order orderToEdit){
        //If the updatedCustomerName string is null, then return the order object unedited
        if (updatedState == null) {
            return orderToEdit;
        } else {
            //If the updatedCustomerName contains new info, then update the order and return it
            orderToEdit.setStateAbbr(updatedState);
            return orderToEdit;
        }
    }

    @Override
    public Order updateOrderProductTypeIfRequired(String updatedProductType, Order orderToEdit){
        //If the updatedCustomerName string is null, then return the order object unedited
        if (updatedProductType == null) {
            return orderToEdit;
        } else {
            //If the updatedCustomerName contains new info, then update the order and return it
            orderToEdit.setProductType(updatedProductType);
            return orderToEdit;
        }
    }    
    
    @Override
    public Order updateOrderAreaIfRequired(BigDecimal updatedArea, Order orderToEdit){
        //If the updatedCustomerName string is null, then return the order object unedited
        if (updatedArea == null) {
            return orderToEdit;
        } else {
            //If the updatedCustomerName contains new info, then update the order and return it
            orderToEdit.setArea(updatedArea);
            return orderToEdit;
        }
    }
    
    @Override
    public Order updateOrderCalculations(Order editedOrder) throws PersistenceException {
        //get the updated state, product type and area and use them to obtain and update the other values.
        //if the values haven't actually been updated, they will just return the same old value.
        BigDecimal updatedTaxRate = null;
        BigDecimal updatedCostPerSquareFoot = null;
        BigDecimal updatedLaborCostPerSquareFoot = null;
        
        //if the state is not null, then get the new tax rate
        String updatedStateAbbreviation = editedOrder.getStateAbbr();
        if (updatedStateAbbreviation != null) {
            Tax updatedTaxObj = taxDao.getTax(updatedStateAbbreviation);
            updatedTaxRate = updatedTaxObj.getTaxRate();
        }
        
        String updatedProductType = editedOrder.getProductType();
        if (updatedProductType != null) {
            Product updatedProduct = productDao.getProduct(updatedProductType);
            updatedCostPerSquareFoot = updatedProduct.getCostPerSquareFoot();
            updatedLaborCostPerSquareFoot = updatedProduct.getLaborCostPerSquareFoot();
            BigDecimal updatedArea = editedOrder.getArea();
            //Get the updated calculated values
            BigDecimal updatedMaterialCost = this.calculateMaterialCost(updatedArea, updatedCostPerSquareFoot);
            BigDecimal updatedLaborCost = this.calculateLaborCost(updatedArea, updatedLaborCostPerSquareFoot);
            BigDecimal updatedTax = this.calculateTax(updatedMaterialCost, updatedLaborCost, updatedTaxRate);
            BigDecimal updatedTotal = this.calculateTotal(updatedMaterialCost, updatedLaborCost, updatedTax);
            editedOrder.setMaterialCost(updatedMaterialCost);
            editedOrder.setLaborCost(updatedLaborCost);
            editedOrder.setTax(updatedTax);
            editedOrder.setTotal(updatedTotal);
        }
       //Update the orders calculated values
       return editedOrder;
    }
    
    @Override
    public Order editOrderIfConfirmed(String toBeEdited, String orderFile, Order updatedOrder) throws PersistenceException{
        //if edit confirmation is Y, edit the order in memory and return the edited order.
        if (toBeEdited.equalsIgnoreCase("Y")) {
            Order editedOrder = orderDao.editOrder(orderFile, updatedOrder);
            return editedOrder;
        } 
        //if edit confirmation is anything other than y or Y, return null
        return null;
    }

    //********************************************* 4. REMOVE AN ORDER ************************************************      
    @Override
    public Order removeOrderIfConfirmed(String removeConfirmation, String orderFile, int orderNumber) throws PersistenceException, OrderFileNotExistException {
        //if remove confirmation is Y, remove the order and return the removed order,
        if (removeConfirmation.equalsIgnoreCase("Y")) {
            Order removedOrder = orderDao.removeOrder(orderFile, orderNumber);
            //Check if the file is empty, if it is, remove it.
            removeFileIfEmpty(orderFile);
            return removedOrder;
        }
        //if remove confirmation is anything other than Y or y, return null 
        return null;
    }
    
    private void removeFileIfEmpty (String orderFile) throws PersistenceException, OrderFileNotExistException {
        //Get all the orders from the file
        ArrayList<Order> orderList = (ArrayList<Order>) this.getAllOrders(orderFile);
        //if its empty, this is a file with only headings, delete the file.
        if (orderList.isEmpty()) {
            orderDao.deleteOrderFile(orderFile);
        }
    }
    
    @Override
    public int checkOrderNumExists(String orderFileName, int orderNumberInput) throws PersistenceException, NoOrderNumException{
        //Checks if there are any orders in the order file that have the  
        //given order number input
        //Will throw exception if not found and will return null if no order is found.
        
        //Get a list of all the order nums for specified order file name
        List<Integer> orderNums = orderDao.getAllOrderNumsForADate(orderFileName);
        
        int orderNumFound = 0;
        //Compare the order number inputted to all order numbers in the list
        for (Integer orderNum : orderNums){
            if (orderNumberInput == orderNum) {
                //if it is found, set orderNumFound to the order number
                orderNumFound=orderNum;
                return orderNumFound;
            }
        }
        //if the order number hasn't been found, the order num will still be 0.
        // and exception thrown.
        if (orderNumFound == 0) {
            throw new NoOrderNumException (
            "ERROR: no orders exist for that order number.");
    }
        return 0;
    }
    
    @Override
    public Order getOrder(String fileName, int orderNum) throws PersistenceException {
        return orderDao.getOrder(fileName, orderNum);
    }
      
//********************************************* 5. EXPORT ALL DATA ************************************************ 


    @Override
    public void exportAllData() throws PersistenceException {
        orderDao.exportAllData();
    }

        
    }
    
    

    
