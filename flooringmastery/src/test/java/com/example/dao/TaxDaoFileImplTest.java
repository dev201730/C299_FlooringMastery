package com.example.dao;

import com.example.dto.Tax;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class TaxDaoFileImplTest {
	String testTaxesFile = "SampleFileDataTest/DataTest/TaxesTest.txt";
    TaxDao testTaxDao = new TaxDaoFileImpl(testTaxesFile);
    
    public TaxDaoFileImplTest() {
    }
    
    @BeforeAll
    public static void setUpClass() throws IOException {    
        
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testAddGetAllTaxes() throws PersistenceException {
    	//ARRANGE 
        //Create tax objects
        Tax kentucky = new Tax("KY");
        kentucky.setStateName("Kentucky");
        kentucky.setTaxRate(new BigDecimal("6.00"));
        
        Tax washington = new Tax("WA");
        washington.setStateName("Washington");
        washington.setTaxRate(new BigDecimal("9.25"));
        
        Tax texas = new Tax("TX");
        texas.setStateName("Texas");
        texas.setTaxRate(new BigDecimal("4.45"));
        
        Tax fakeState = new Tax("FS");
        fakeState.setStateName("FakeState");
        fakeState.setTaxRate(new BigDecimal("0.01"));

        //ACT
        //Get all taxes from the test taxes file
        List<Tax> taxes = testTaxDao.getAllTaxes();
        
        //ASSERT
        assertEquals(taxes.size(),4,"The all taxes list should contain 4 tax objects.");
        assertTrue(taxes.contains(kentucky) && taxes.contains(washington)&& taxes.contains(texas),"The all taxes list should contain kentucky,"
                + "washington and texas");
        assertFalse(taxes.contains(fakeState),"The all taxes list should not contain fake state");

    }
    
    @Test
    public void testGetTax() throws PersistenceException {
        //ARRANGE 
        //Create Tax objects
        Tax kentucky = new Tax("KY");
        kentucky.setStateName("Kentucky");
        kentucky.setTaxRate(new BigDecimal("6.00"));
        
        Tax washington = new Tax("WA");
        washington.setStateName("Washington");
        washington.setTaxRate(new BigDecimal("9.25"));
        
        //ACT
        Tax retrievedTax = testTaxDao.getTax("KY");
        
        //ASSERT
        assertEquals(retrievedTax, kentucky, "The retrieved tax object should be equal to the kentucky tax object");
        
        
    }
    
    
}
