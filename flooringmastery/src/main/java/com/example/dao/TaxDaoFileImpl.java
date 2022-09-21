package com.example.dao;

import com.example.dto.Tax;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class TaxDaoFileImpl implements TaxDao {
    private Map <String, Tax> taxes = new HashMap<>();
    private final String DELIMITER = ",";
    private final String TAXES_FILE;
    
    public TaxDaoFileImpl() {
    	this.TAXES_FILE = "SampleFileData/Data/Taxes.txt";
    }
    
    //Constructor for testing
    public TaxDaoFileImpl (String taxesTextFile) {
        this.TAXES_FILE = taxesTextFile;
    }    
    
    @Override
    public List<Tax> getAllTaxes()throws PersistenceException {
        loadTaxes();
        return new ArrayList(taxes.values());
    }
    
    
    @Override 
    public Tax getTax(String stateAbbreviationInput)throws PersistenceException {
    loadTaxes();
        return taxes.get(stateAbbreviationInput);
    }
    
    private Tax unmarshallTax(String taxAsText) throws PersistenceException {
        //This line is then split at the DELIMITER (,) leaving an array of Strings,
        //stored as taxTokens, which should look like this:
    	// [0]     [1]    [2]
    	// TX      Texas   4.45
           
        
        String[] taxTokens = taxAsText.split(DELIMITER);
        String stateAbbreviation = taxTokens[0];
        
        Tax taxFromFile = new Tax(stateAbbreviation);
        
        taxFromFile.setStateName(taxTokens[1]);
        taxFromFile.setTaxRate(new BigDecimal(taxTokens[2]));
        return taxFromFile;
    }
    
    private void loadTaxes() throws PersistenceException {
        Scanner scanner;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(TAXES_FILE)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException(
            "-_- Could not load tax data into memory.", e);
        }
        String currentLine;
        Tax currentTax;
        
        int iteration = 0;
        
        while (scanner.hasNextLine()) {
            //om the first iteration do not try to unmarshall tax, as the first
            //line is heading
            if (iteration == 0) {
                String headings = scanner.nextLine();
                iteration ++;
                continue;
            }
        
            currentLine = scanner.nextLine();
            currentTax = unmarshallTax(currentLine);
            
            
            taxes.put(currentTax.getStateAbbr(), currentTax);
            iteration++;
        }
        //Clean up/close file
        scanner.close();
        }

    
    
    
}