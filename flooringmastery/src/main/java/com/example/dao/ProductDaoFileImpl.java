package com.example.dao;

import com.example.dto.Product;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProductDaoFileImpl implements ProductDao{
    private Map<String, Product> products = new HashMap<>();
    private final String DELIMITER = ",";
    private final String PRODUCTS_FILE;
    
    
    public ProductDaoFileImpl() {
    	this.PRODUCTS_FILE = "SampleFileData/Data/Products.txt";
    }
               
    //Constructor for testing
    public ProductDaoFileImpl (String productsTextFile) {
        this.PRODUCTS_FILE = productsTextFile;
    }    
    
    @Override
    public List<Product> getAllProducts()throws PersistenceException {
        loadProducts();
        return new ArrayList(products.values());
    }    
    
    @Override 
    public Product getProduct (String productType) throws PersistenceException {
        loadProducts();
        return products.get(productType);
    }
    
    
    private String marshallProduct(Product aProduct) {
        //Get product type, cost per sq ft and labor cost per sq ft.
        String productAsText = aProduct.getProductType();
        productAsText += aProduct.getCostPerSquareFoot().toString();
        productAsText += aProduct.getLaborCostPerSquareFoot().toString();
        return productAsText;
    }
    
    /**
     * UnmarshallTax translates a line of text into a order object. 
     * @param 
     * @return 
     */
    private Product unmarshallProduct(String productAsText) {
        //This line is then split at the DELIMITER (,) leaving an array of Strings,
        //stored as orderTokens, which should look like this:
    	// [0]     [1]    [2]
    	// Carpet  2.25   2.10
        
        String [] productTokens = productAsText.split(DELIMITER);
        
        String productType = productTokens[0];
        Product productFromFile = new Product(productType);
        
        productFromFile.setCostPerSquareFoot(new BigDecimal(productTokens[1]));
        productFromFile.setLaborCostPerSquareFoot(new BigDecimal(productTokens[2]));

        return productFromFile;
    }
    
    private void loadProducts() throws PersistenceException {
        //Open File:
        Scanner scanner;
        try {
            scanner = new Scanner(
            new BufferedReader(
            new FileReader(PRODUCTS_FILE)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException(
            "-_- Could not load product data into memory",e);
        }
        
        //Read from file
        String currentLine; //holds the most recent line read from the file
        Product currentProduct;  //holds the most recent unmarshalled order
        
        int iteration = 0;
        
        while (scanner.hasNextLine()) {
            //on the first iteration do not try to unmarshall order, as the first 
            //line is the headings.
            if (iteration == 0) {
                String headings = scanner.nextLine();
                iteration ++;
                continue;
            }
            //get the next line in the file
            currentLine = scanner.nextLine();
            //unmarshall the line into an order
            currentProduct = unmarshallProduct(currentLine);
            
            products.put(currentProduct.getProductType(), currentProduct);
            iteration++;
        }
        //Clean up/close file
        scanner.close();
    }
    
    
}