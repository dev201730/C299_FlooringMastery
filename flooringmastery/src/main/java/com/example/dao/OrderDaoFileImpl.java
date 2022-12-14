package com.example.dao;

import com.example.dto.Order;
import com.example.service.OrderFileNotExistException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class OrderDaoFileImpl implements OrderDao {
    private Map <Integer, Order> orders = new HashMap<>();
    private final String DELIMITER = ",";
    private final String DATA_EXPORT_FILE;
    private final String customerNamePlaceHolder = "#*~";
    private final String orderFolder;
    
    public OrderDaoFileImpl() {
    	this.DATA_EXPORT_FILE = "SampleFileData/Backup/DataExport.txt";
        this.orderFolder = "SampleFileData/Orders";
    }
    
    //Constructor for testing
    public OrderDaoFileImpl (String dataExportFile, String orderFolder) {
        this.DATA_EXPORT_FILE = dataExportFile;
        this.orderFolder = orderFolder;
    }
    
    @Override
    public List<Order> getAllOrdersForADate(String orderFile) throws PersistenceException {
        loadOrders(orderFile);
        List<Order> allOrdersForADate = new ArrayList(orders.values());
        this.orders.clear();
        return allOrdersForADate;
    }
    
    @Override
    public List<Integer> getAllOrderNumsForADate(String orderFile) throws PersistenceException {
        loadOrders(orderFile);
        Set<Integer> orderNums = orders.keySet();
        List<Integer> listOfOrderNums = new ArrayList<>(orderNums);
        this.orders.clear();
        return listOfOrderNums;
    }
    
    @Override 
    public List<Integer> getAllOrderNums() throws PersistenceException {
        //Get all order file names that exist
        String [] allOrderFiles= listAllOrderFiles();
        
        List<Integer> allOrderNums = new ArrayList<>();
        //Loop through all existing order files, append the keysets to a new list
        for (String orderFile : allOrderFiles) {
            List<Integer> orderNumsPerFile = getAllOrderNumsForADate(orderFile);
            orderNumsPerFile.forEach(orderNum -> {
                allOrderNums.add(orderNum);
            });  
        }

        orders.clear();
        return allOrderNums;
        
    }

    @Override
    public Order getOrder (String orderFile, int orderNum) throws PersistenceException {
        loadOrders(orderFile);
        Order orderToGet = orders.get(orderNum);
        orders.clear();
        return orderToGet;
    }
    
    @Override
    public List<Order> getAllOrders() throws PersistenceException { 
        //Get a list of all existing order files
        String [] allOrderFiles = listAllOrderFiles();
        //Go through the list one by one to obtain the order file names, then use getAllOrdersForADate to add the orders
        //to the orders list. 
        List<Order> allOrders = new ArrayList<>();
        
        for (String orderFile : allOrderFiles) {
            List<Order> ordersForADate = getAllOrdersForADate(orderFile);
            ordersForADate.forEach(order -> {
                allOrders.add(order);
            });
        }
        return allOrders;
    }

    @Override
    public Order addOrderToExistingFile(String orderFile, int orderNumber, Order order) throws PersistenceException {
        loadOrders(orderFile);
        Order newOrder = orders.put(orderNumber, order);
        writeOrders(orderFile);
        orders.clear();
        return newOrder;
    }
    
    @Override
    public Order addOrderToNewFile(String orderFile, int orderNumber, Order order) throws PersistenceException {
        //Don't need to load a file, as we are adding an order to a new one
        Order newOrder = orders.put(orderNumber, order);
        //Writes all the orders in the orders hashmap to the order file
        writeOrders(orderFile);
        orders.clear();
        return newOrder;
    }
    
    
    @Override 
    public Order editOrder(String orderFile, Order updatedOrder) throws PersistenceException{
        loadOrders(orderFile);
        Order orderToEdit = orders.get(updatedOrder.getOrderNumber());
        orderToEdit.setCustomerName(updatedOrder.getCustomerName());
        orderToEdit.setStateAbbr(updatedOrder.getStateAbbr());
        orderToEdit.setTaxRate(updatedOrder.getTaxRate());
        orderToEdit.setProductType(updatedOrder.getProductType());
        orderToEdit.setArea(updatedOrder.getArea());
        orderToEdit.setCostPerSquareFoot(updatedOrder.getCostPerSquareFoot());
        orderToEdit.setLaborCostPerSquareFoot(updatedOrder.getLaborCostPerSquareFoot());
        orderToEdit.setMaterialCost(updatedOrder.getMaterialCost());
        orderToEdit.setLaborCost(updatedOrder.getLaborCost());
        orderToEdit.setTax(updatedOrder.getTax());
        orderToEdit.setTotal(updatedOrder.getTotal());
        writeOrders(orderFile);
        return orderToEdit;
    }

    @Override
    public Order removeOrder(String orderFile, int orderNumber) throws PersistenceException {
        loadOrders(orderFile);
        Order orderToRemove = orders.remove(orderNumber);
        writeOrders(orderFile);
        return orderToRemove;
    }

    public String getDateFromOrderFileName(String orderFile) throws PersistenceException {
        //Takes in an ordeFile name in the form: "Order_MMDDYY.txt" and converts it to 'mm-dd'yyyy'
        
        //Split the order file name at _ into two strings: 'Order' & 'MMDDYY.txt'
        String [] fileNameTokens = orderFile.split("_");
        //String with index [1] will have the date in the format 'MMDDYYYY.txt'
        //need to split again at the '.'
        String [] dateTokens = fileNameTokens[1].split("\\.");
        //String with index [0] will have the date in the format 'MMDDYYYY'
        //Need to convert this into mm-dd-yyyy
        String date = dateTokens[0];
        //Get the indiviual dd, mm and yyyy
        String mm = date.substring(0, 2);   //second number is up to but not including hence go up to the index + 1
        String dd = date.substring(2, 4);
        String yyyy = date.substring(4, 8);
        
        //combine to get format mm-dd-yyyy
        return mm+"-"+dd+"-"+yyyy;
    }
    
    @Override
    public Map<String,List<Order>> getExportData() throws PersistenceException  {
        Map<String,List<Order>> exportDataMap = new HashMap<>();
        //Get a list of all the orders from all files
        String [] allOrderFiles = this.listAllOrderFiles();
        
        for (String orderFile: allOrderFiles) {
            //Get the order date from the file name
            String dateString = this.getDateFromOrderFileName(orderFile);
            //Get the order list from the file
            List<Order> allOrdersForFile = this.getAllOrdersForADate(orderFile);
            //Add to the map the date and orders
            exportDataMap.put(dateString,allOrdersForFile);
        }
        return exportDataMap;
    }
    
    @Override
    public void exportAllData() throws PersistenceException {
        this.writeDataExport();
    }

   @Override
    public String [] listAllOrderFiles() {
        //creates an array in which we will store the names of files (& directories if there were any)
        FilenameFilter filter = (file, fileName) -> {
        return fileName.contains(".");
        };

        String [] orderFiles = new File(orderFolder).list(filter);
        return orderFiles;
    }
    
    @Override
    public void deleteOrderFile(String fileName) throws OrderFileNotExistException {
        Path pathOfFile = Paths.get(this.orderFolder + "/" + fileName);
        try {
            Files.deleteIfExists(pathOfFile);
        } catch (IOException e) {
            throw new OrderFileNotExistException(
            "Could not delete order file.",e);
          
        }
        
    }  


    /**
     * marshallOrder organises the order information from an in memory object into a
     * line of text, so it is in an appropriate format for writing it to permanent storage.
     * @param aOrder an order object in memory 
     * @return a String consisting of the format 1,Ada Lovelace,CA,25.00,Tile,249.00,3.50,4.15,871.50,1033.35,476.21,2381.06 
     */   

    private String marshallOrder(Order aOrder) {
        //Get order number and convert int to a string
        String orderAsText = String.valueOf(aOrder.getOrderNumber()) + DELIMITER;
        orderAsText += aOrder.getCustomerName() + DELIMITER;
        orderAsText += aOrder.getStateAbbr() + DELIMITER;
        //Get tax rate number and convert BigDecimal to a string
        orderAsText += aOrder.getTaxRate().toString() + DELIMITER;
        orderAsText += aOrder.getProductType() + DELIMITER;
        //Get all BigDec and convert to string
        orderAsText += aOrder.getArea().toString() + DELIMITER;
        orderAsText += aOrder.getCostPerSquareFoot().toString() + DELIMITER;
        orderAsText += aOrder.getLaborCostPerSquareFoot().toString() + DELIMITER;
        orderAsText += aOrder.getMaterialCost().toString() + DELIMITER;
        orderAsText += aOrder.getLaborCost().toString() + DELIMITER;
        orderAsText += aOrder.getTax().toString() + DELIMITER;
        orderAsText += aOrder.getTotal().toString();
        return orderAsText;
    }
    
    /**
     * UnmarshallOrder translates a line of text into a order object. 
     * @param 
     * @return 
     */
    private Order unmarshallOrder(String orderAsText) {
       
    	//System.out.println("EN PRINT: orderAsText "+ orderAsText);
        String [] orderTokens = orderAsText.split(DELIMITER);
        //System.out.println("EN PRINT: order no: "+ orderTokens[0]);
        int orderNumber = Integer.parseInt(orderTokens[0]);
        Order orderFromFile = new Order(orderNumber);
        
        String customerName = orderTokens[1];
        //remove the placeholder
        customerName = customerName.replace(customerNamePlaceHolder, ",");
        
        orderFromFile.setCustomerName(customerName);
        orderFromFile.setStateAbbr(orderTokens[2]);
        orderFromFile.setTaxRate(new BigDecimal(orderTokens[3]));
        orderFromFile.setProductType(orderTokens[4]);
        orderFromFile.setArea(new BigDecimal (orderTokens[5]));
        orderFromFile.setCostPerSquareFoot(new BigDecimal(orderTokens[6]));
        orderFromFile.setLaborCostPerSquareFoot(new BigDecimal(orderTokens[7]));
        orderFromFile.setMaterialCost(new BigDecimal(orderTokens[8]));
        orderFromFile.setLaborCost(new BigDecimal(orderTokens[9]));
        orderFromFile.setTax(new BigDecimal(orderTokens[10]));
        orderFromFile.setTotal(new BigDecimal(orderTokens[11]));

        return orderFromFile;
    }
    
    private void loadOrders(String orderFile) throws PersistenceException {
    	
        //Open File:
        Scanner scanner;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(this.orderFolder+"/"+orderFile)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException("-_- Could not load order data into memory",e);
        }
        
        //Read from file
        String currentLine; //holds the most recent line read from the file
        Order currentOrder;  //holds the most recent unmarshalled order
        
        while (scanner.hasNextLine()) {
            //get the next line in the file
            currentLine = scanner.nextLine();
            
            //if the line begins with OrderNumber then this is the heading, ignore this iteration.
            if (currentLine.startsWith("OrderNumber")) {
                continue;
            }
            //unmarshall the line into an order
            currentOrder = unmarshallOrder(currentLine);
            
            orders.put(currentOrder.getOrderNumber(), currentOrder);
        }
        //Clean up/close file
        scanner.close();
    }
    
    private void writeOrders(String orderFile) throws PersistenceException {
        PrintWriter out;
        
        try {
            out = new PrintWriter(new FileWriter(this.orderFolder+"/"+orderFile));
        } catch (IOException e) {
            throw new PersistenceException("Could not save order data",e);
        }
        out.println("OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total");
        String orderAsText;
        List <Order> orderList = this.getAllOrdersForADate(orderFile);
        for (Order currentOrder : orderList) {
            orderAsText = marshallOrder(currentOrder);
            out.println(orderAsText);
            out.flush();
        }
        //Clean up
        out.close();
    }
    
    
    private void writeDataExport() throws PersistenceException {
        PrintWriter out;
        
        try {
            out = new PrintWriter(new FileWriter(DATA_EXPORT_FILE));
        } catch (IOException e) {
            throw new PersistenceException("Could not save order data",e);
        }
        out.println("OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total,Date");

        //Map <String date, ArrayList of Orders>
        Map<String, List<Order>> exportData = getExportData();

        //Loop through every date (key) and create a new string in the form of:
        //marshalled order + "," + date
        //print this out to the file.
        String exportDataAsText;
        
        for (String date : exportData.keySet()) {
            List<Order> currentOrderList = exportData.get(date);
            for (Order order : currentOrderList) {
                //marshall each order and concatenate the date onto it
                exportDataAsText = marshallOrder(order) + "," + date;
                out.println(exportDataAsText);
                out.flush();
            }
        }
        out.close();
    }
    
}



    
    
   